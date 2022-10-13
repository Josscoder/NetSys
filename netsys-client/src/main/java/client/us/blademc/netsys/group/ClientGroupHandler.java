package client.us.blademc.netsys.group;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.cache.ServerCache;
import commons.us.blademc.netsys.group.GroupHandler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClientGroupHandler extends GroupHandler<ServerCache> {

    protected final Map<String, ServerCache> servers = new HashMap<>();

    public ClientGroupHandler(NetSys netSys) {
        super(netSys);
    }

    @Override
    public Map<String, ServerCache> getServers() {
        return servers;
    }

    @Override
    public void storeServer(ServerCache cache) {
        servers.putIfAbsent(cache.getName(), cache);
    }

    @Override
    public void removeServer(String serverName, String reason) {
        servers.remove(serverName);
        //reason is ignored here
    }

    public boolean containsServer(String serverName) {
        return getServer(serverName) != null;
    }

    public void clearData() {
        servers.clear();
    }

    public void updateData(List<ServerCache> serverList) {
        clearData();
        serverList.forEach(this::storeServer);
    }

    @Override
    public ServerCache getServer(String serverName) {
        return servers.get(serverName);
    }

    @Override
    public List<ServerCache> filterServers(Predicate<? super ServerCache> predicate) {
        return servers.values()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServerCache> getLobbyServers() {
        return filterServers(server -> server.getName().startsWith("lobby-") ||
                server.getName().startsWith("hub-")
        );
    }

    @Override
    public List<ServerCache> getSortedServers(List<ServerCache> serverList) {
        return serverList.stream().
                sorted(Comparator.comparingInt(ServerCache::getOnlinePlaying)).
                collect(Collectors.toList());
    }
}
