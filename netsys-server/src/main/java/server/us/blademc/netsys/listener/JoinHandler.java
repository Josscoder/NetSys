package server.us.blademc.netsys.listener;

import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IJoinHandler;
import server.us.blademc.netsys.NetSysServer;

public class JoinHandler implements IJoinHandler {

    @Override
    public ServerInfo determineServer(ProxiedPlayer proxiedPlayer) {
        return NetSysServer.getInstance().getGroupHandler().getBalancedLobbyServer();
    }
}
