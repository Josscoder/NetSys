package server.us.blademc.netsys.protocol;

import commons.us.blademc.netsys.handler.IPacketHandler;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.ProtocolInfo;
import commons.us.blademc.netsys.protocol.packet.CloseClientConnectionPacket;
import commons.us.blademc.netsys.protocol.packet.DataPacket;
import commons.us.blademc.netsys.protocol.packet.OpenClientConnectionRequestPacket;
import commons.us.blademc.netsys.protocol.packet.OpenClientConnectionResponsePacket;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import server.us.blademc.netsys.NetSysServer;
import server.us.blademc.netsys.service.ServerServiceInfo;

import java.net.InetSocketAddress;

public class ServerPacketHandler implements IPacketHandler {

    @Override
    public void handle(DataPacket packet) {
        NetSysServer netSysServer = NetSysServer.getInstance();
        NetSys netSys = netSysServer.sync();
        ServerServiceInfo serviceInfo = netSysServer.getServiceInfo();

        ProxyServer proxy = ProxyServer.getInstance();

        switch (packet.getPid()) {
            case ProtocolInfo.OPEN_CLIENT_CONNECTION_REQUEST_PACKET:
                OpenClientConnectionRequestPacket openClientConnectionRequestPacket = (OpenClientConnectionRequestPacket) packet;
                String prefix = openClientConnectionRequestPacket.id;

                OpenClientConnectionResponsePacket openClientConnectionResponsePacket = new OpenClientConnectionResponsePacket();
                openClientConnectionResponsePacket.clientID = prefix;

                InetSocketAddress socketAddress = openClientConnectionRequestPacket.branch.startsWith("dev")
                        ? new InetSocketAddress("127.0.0.1", openClientConnectionRequestPacket.publicAddress.getPort())
                        : openClientConnectionRequestPacket.publicAddress;

                BedrockServerInfo bedrockServerInfo = new BedrockServerInfo(
                        prefix,
                        socketAddress,
                        socketAddress
                );

                boolean registered = proxy.registerServerInfo(bedrockServerInfo);
                openClientConnectionResponsePacket.accepted = registered;

                if (registered) {
                    openClientConnectionResponsePacket.serverID = serviceInfo.getID();
                    netSys.getRedisPool().dataPacket(openClientConnectionResponsePacket);
                    netSysServer.getGroupHandler().storeServer(bedrockServerInfo);
                    netSys.getLogger().info(prefix + " Authentication accepted, new registered NetSys-Client!");
                    return;
                }

                netSys.getRedisPool().dataPacket(openClientConnectionResponsePacket);
                netSys.getLogger().warn("§c" + prefix + " Authentication failed by duplicate NetSys-Client, automatically disconnected!");
                break;
            case ProtocolInfo.CLOSE_CLIENT_CONNECTION_PACKET:
                CloseClientConnectionPacket closeClientConnectionPacket = (CloseClientConnectionPacket) packet;
                netSysServer.getGroupHandler().removeServer(
                        closeClientConnectionPacket.id,
                        closeClientConnectionPacket.reason
                );
                break;
        }
    }
}
