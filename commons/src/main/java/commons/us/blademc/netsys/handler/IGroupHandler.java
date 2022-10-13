package commons.us.blademc.netsys.handler;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface IGroupHandler<T> {
    Map<String, T> getServers();
    void storeServer(T cache);
    void removeServer(String serverName, String reason);
    T getServer(String serverName);
    List<T> filterServers(Predicate<? super T> predicate);
    List<T> getLobbyServers();
    List<T> getSortedServers(List<T> serverList);
    T getBalancedServer(List<T> serverList);
    T getBalancedLobbyServer();
}
