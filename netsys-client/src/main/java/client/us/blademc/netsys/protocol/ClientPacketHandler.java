package client.us.blademc.netsys.protocol;

import client.us.blademc.netsys.group.ServerCache;
import client.us.blademc.netsys.service.ClientServiceInfo;
import client.us.blademc.netsys.NetSysClient;
import commons.us.blademc.netsys.handler.IPacketHandler;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.ProtocolInfo;
import commons.us.blademc.netsys.protocol.packet.*;

public class ClientPacketHandler implements IPacketHandler {

    @Override
    public void handle(DataPacket packet) {
        NetSysClient netSysClient = NetSysClient.getInstance();
        NetSys netSys = netSysClient.sync();
        ClientServiceInfo serviceInfo = netSysClient.getServiceInfo();

        switch (packet.getPid()) {
            case ProtocolInfo.OPEN_CLIENT_CONNECTION_RESPONSE_PACKET:
                OpenClientConnectionResponsePacket openClientConnectionResponsePacket = (OpenClientConnectionResponsePacket) packet;
                if (!openClientConnectionResponsePacket.clientID.equalsIgnoreCase(serviceInfo.getID())) {
                    if (!openClientConnectionResponsePacket.accepted ||
                            !openClientConnectionResponsePacket.serverID.equalsIgnoreCase(serviceInfo.getServerID())
                    ) {
                        return;
                    }

                    netSysClient.getGroupHandler().storeServer(
                            new ServerCache(openClientConnectionResponsePacket.clientID)
                    );
                    return;
                }

                if (!openClientConnectionResponsePacket.accepted) {
                    netSys.getLogger().warn("§4Failed authentication, an NetSys-Server refused the connection, restarting...");
                    netSysClient.getServer().shutdown();
                    return;
                }

                serviceInfo.setServerID(openClientConnectionResponsePacket.serverID);
                serviceInfo.setLogged(true);
                netSys.getLogger().warn("§bNetSys-client successfully connected to the NetSys-Server: " + serviceInfo.getServerID());
                netSys.getLogger().warn("§aThe currently service info is: " + serviceInfo + ", §eType the command §6/whereaim§r§e in chat to see this again!");
                break;
            case ProtocolInfo.SERVER_DISCONNECT_PACKET:
                ServerDisconnectPacket serverDisconnectPacket = (ServerDisconnectPacket) packet;
                if (!serverDisconnectPacket.serverID.equals(serviceInfo.getServerID())) return;

                serviceInfo.setServerID("NONE");
                serviceInfo.setLogged(false);

                netSys.getLogger().warn("§cThe NetSys-Server has been disconnected!");
                break;
            case ProtocolInfo.CLOSE_CLIENT_CONNECTION_PACKET:
                CloseClientConnectionPacket closeClientConnectionPacket = (CloseClientConnectionPacket) packet;
                netSysClient.getGroupHandler().removeServer(closeClientConnectionPacket.id, closeClientConnectionPacket.reason);
                break;
            case ProtocolInfo.CLIENT_UPDATE_DATA_PACKET:
                ClientUpdateDataPacket clientUpdateDataPacket = (ClientUpdateDataPacket) packet;
                if (!netSysClient.getGroupHandler().containsServer(clientUpdateDataPacket.id)) return;

                ServerCache cache = new ServerCache(clientUpdateDataPacket.id);
                cache.setPlayers(clientUpdateDataPacket.players);

                netSysClient.getGroupHandler().updateServerData(cache);
                break;
        }
    }
}
