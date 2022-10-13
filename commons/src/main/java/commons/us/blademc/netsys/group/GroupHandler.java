package commons.us.blademc.netsys.group;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.handler.IGroupHandler;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class GroupHandler<T> implements IGroupHandler<T> {
    protected final NetSys netSys;

    @Override
    public T getBalancedServer(List<T> serverList) {
        return serverList.size() > 0 ? getSortedServers(serverList).get(0) : null;
    }

    @Override
    public T getBalancedLobbyServer() {
        return getBalancedServer(getLobbyServers());
    }
}
