package server.us.blademc.netsys;

import commons.us.blademc.netsys.NetSys;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.scheduler.WaterdogScheduler;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BedrockServerPool {

    protected final NetSys netSys;

    protected final Map<String, BedrockServerInfo> storedServers = new HashMap<>();

    public void init() {
        WaterdogScheduler.getInstance().scheduleRepeating(new Runnable() {
            private final List<String> queueRemove = new ArrayList<>();

            @Override
            public void run() {
                if (netSys.isDebug()) netSys.getLogger().debug("Preparing to remove offline servers...");

                storedServers.forEach((id, server) -> {
                    if (server.getPlayers().size() > 0) return;

                    try {
                        server.ping(5, TimeUnit.SECONDS).get();
                    } catch (InterruptedException | ExecutionException e) {
                        queueRemove.add(id);
                        netSys.getLogger().warn("§6" + id + " NetSys-Client was added to the queue to be removed because it did not answer the ping!");
                    }
                });

                queueRemove.forEach(serverName -> removeServer(serverName, "Do not respond to ping"));
                queueRemove.clear();

                if (netSys.isDebug()) netSys.getLogger().debug("Removed all offline servers!");
            }
        }, 20 * 60);
    }

    public void storeServer(BedrockServerInfo serverInfo) {
        storedServers.putIfAbsent(serverInfo.getServerName(), serverInfo);
    }

    public void removeServer(String serverName, String reason) {
        ProxyServer.getInstance().removeServerInfo(serverName);
        netSys.getLogger().warn("§cNetSys-Client " + serverName + " disconnected by " + reason);
    }

    public BedrockServerInfo getServer(String serverName) {
        return storedServers.get(serverName);
    }

    public List<BedrockServerInfo> filterServers(Predicate<? super BedrockServerInfo> predicate) {
        return storedServers.values()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<BedrockServerInfo> getLobbyServers() {
        return filterServers(serverInfo -> serverInfo.getServerName().startsWith("lobby-") ||
                serverInfo.getServerName().startsWith("hub-")
        );
    }

    public List<BedrockServerInfo> getSortedServers(List<BedrockServerInfo> serverList) {
        return serverList.stream().
                sorted(Comparator.comparingInt(serverInfo -> serverInfo.getPlayers().size())).
                collect(Collectors.toList());
    }

    public BedrockServerInfo getBalancedServer(List<BedrockServerInfo> serverList) {
        return serverList.size() > 0 ? getSortedServers(serverList).get(0) : null;
    }

    public BedrockServerInfo getBalancedLobbyServer() {
        return getBalancedServer(getLobbyServers());
    }
}
