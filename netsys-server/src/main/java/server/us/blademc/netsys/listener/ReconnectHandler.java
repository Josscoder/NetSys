package server.us.blademc.netsys.listener;

import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IReconnectHandler;
import server.us.blademc.netsys.NetSysServer;

public class ReconnectHandler implements IReconnectHandler {

    @Override
    public ServerInfo getFallbackServer(ProxiedPlayer proxiedPlayer, ServerInfo serverInfo, String reason) {
        String proxyId = NetSysServer.getInstance().getServiceInfo().getID();

        proxiedPlayer.sendMessage("§cServer down report" +
                "\n"+
                "§fThe server you were previously on went down caused by §4" + reason.toUpperCase() +
                "\n" +
                "§fUnexpected? Report this (" + serverInfo.getServerName() + "-" + proxyId + ")" +
                "\n" +
                "§aWe'll connect you to a lobby shortly..."
        );

        return NetSysServer.getInstance().getBedrockServerPool().getBalancedLobbyServer();
    }
}
