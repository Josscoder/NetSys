package client.us.blademc.netsys;

import commons.us.blademc.netsys.IPacketHandler;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.ProtocolInfo;
import commons.us.blademc.netsys.protocol.packet.DataPacket;
import commons.us.blademc.netsys.protocol.packet.OpenClientConnectionResponsePacket;
import commons.us.blademc.netsys.protocol.packet.ServerDisconnectPacket;

public class ClientPacketHandler implements IPacketHandler {

    @Override
    public void handle(DataPacket packet) {
        NetSysClient netSysClient = NetSysClient.getInstance();
        NetSys netSys = netSysClient.sync();
        ClientServiceInfo serviceInfo = netSysClient.getServiceInfo();

        switch (packet.getPid()) {
            case ProtocolInfo.OPEN_CLIENT_CONNECTION_RESPONSE_PACKET:
                OpenClientConnectionResponsePacket openClientConnectionResponsePacket = (OpenClientConnectionResponsePacket) packet;
                if (!openClientConnectionResponsePacket.clientID.equalsIgnoreCase(serviceInfo.getID())) return;

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
        }
    }
}
