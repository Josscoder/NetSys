package client.us.blademc.netsys;

import commons.us.blademc.netsys.IPacketHandler;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.ProtocolInfo;
import commons.us.blademc.netsys.protocol.packet.DataPacket;
import commons.us.blademc.netsys.protocol.packet.OpenConnectionResponsePacket;
import commons.us.blademc.netsys.protocol.packet.ServerDisconnectPacket;
import commons.us.blademc.netsys.service.ClientServiceInfo;

public class ClientPacketHandler implements IPacketHandler {

    @Override
    public void handle(DataPacket packet) {
        NetSysClient netSysClient = NetSysClient.getInstance();
        NetSys netSys = netSysClient.sync();
        ClientServiceInfo serviceInfo = netSysClient.getServiceInfo();

        switch (packet.getPid()) {
            case ProtocolInfo.OPEN_CONNECTION_RESPONSE_PACKET:
                OpenConnectionResponsePacket openConnectionResponsePacket = (OpenConnectionResponsePacket) packet;
                if (!openConnectionResponsePacket.client.equalsIgnoreCase(serviceInfo.getID())) return;

                if (!openConnectionResponsePacket.accepted) {
                    netSys.getLogger().warn("§4Failed authentication, the proxy refused the connection");
                    return;
                }

                serviceInfo.setServerID(openConnectionResponsePacket.server);
                serviceInfo.setLogged(true);
                netSys.getLogger().info("§bClient successfully connected to the Proxy: " + serviceInfo.getServerID());
                netSys.getLogger().info("§aService Info: " + serviceInfo);
                break;
            case ProtocolInfo.SERVER_DISCONNECT_PACKET:
                ServerDisconnectPacket serverDisconnectPacket = (ServerDisconnectPacket) packet;
                if (!serverDisconnectPacket.serverID.equals(serviceInfo.getServerID())) return;

                serviceInfo.setServerID("NONE");
                serviceInfo.setLogged(false);

                netSys.getLogger().info("§cThe proxy has been disconnected!");
                break;
        }
    }
}
