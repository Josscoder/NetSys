package server.us.blademc.netsys.listener;

import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IReconnectHandler;
import server.us.blademc.netsys.NetSysServer;

public class ReconnectHandler implements IReconnectHandler {

    @Override
    public ServerInfo getFallbackServer(ProxiedPlayer player, ServerInfo serverInfo, String reason) {
        String proxyID = NetSysServer.getInstance().getServiceInfo().getID();

        player.sendMessage(
                "§8Unexpected? Report this §7(" + proxyID + "-" + serverInfo.getServerName() + "): §c" + reason +
                "\n" +
                "§aWe will connect you to a lobby shortly..."
        );

        return NetSysServer.getInstance().getGroupHandler().getBalancedLobbyServer();
    }
}
