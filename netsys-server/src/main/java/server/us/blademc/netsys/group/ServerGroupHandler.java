package server.us.blademc.netsys.group;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.group.GroupHandler;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.scheduler.WaterdogScheduler;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServerGroupHandler extends GroupHandler<BedrockServerInfo> {

    protected final Map<String, BedrockServerInfo> servers = new HashMap<>();

    public ServerGroupHandler(NetSys netSys) {
        super(netSys);
    }

    public void init() {
        WaterdogScheduler.getInstance().scheduleRepeating(() -> {
            if (netSys.isDebug()) netSys.getLogger().debug("Preparing to remove offline servers...");

            List<String> queueRemove = new ArrayList<>();

            servers.forEach((id, server) -> {
                if (server.getPlayers().size() > 0) return;

                try {
                    server.ping(5, TimeUnit.SECONDS).get();
                } catch (InterruptedException | ExecutionException e) {
                    queueRemove.add(id);
                    netSys.getLogger().warn("§6" + id + " NetSys-Client will be removed because it did not answer the ping!");
                }
            });

            queueRemove.forEach(id -> removeServer(id, "Do not respond to ping"));
            if (netSys.isDebug()) netSys.getLogger().debug("Removed all offline servers!");
        }, 20 * 60);
    }

    @Override
    public Map<String, BedrockServerInfo> getServers() {
        return servers;
    }

    @Override
    public void storeServer(BedrockServerInfo cache) {
        servers.putIfAbsent(cache.getServerName(), cache);
    }

    @Override
    public void removeServer(String serverName, String reason) {
        ProxyServer.getInstance().removeServerInfo(serverName);
        netSys.getLogger().warn("§cNetSys-Client " + serverName + " disconnected by " + reason);
    }

    @Override
    public BedrockServerInfo getServer(String serverName) {
        return servers.get(serverName);
    }

    @Override
    public List<BedrockServerInfo> filterServers(Predicate<? super BedrockServerInfo> predicate) {
        return servers.values()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public List<BedrockServerInfo> getLobbyServers() {
        return filterServers(server -> server.getServerName().startsWith("lobby-") ||
                server.getServerName().startsWith("hub-")
        );
    }

    @Override
    public List<BedrockServerInfo> getSortedServers(List<BedrockServerInfo> serverList) {
        return serverList.stream().
                sorted(Comparator.comparingInt(server -> server.getPlayers().size())).
                collect(Collectors.toList());
    }
}
