package server.us.blademc.netsys;

import commons.us.blademc.netsys.IPacketHandler;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.ProtocolInfo;
import commons.us.blademc.netsys.protocol.packet.CloseConnectionPacket;
import commons.us.blademc.netsys.protocol.packet.DataPacket;
import commons.us.blademc.netsys.protocol.packet.OpenConnectionRequestPacket;
import commons.us.blademc.netsys.protocol.packet.OpenConnectionResponsePacket;
import commons.us.blademc.netsys.service.ServerServiceInfo;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;

import java.net.InetSocketAddress;

public class ServerPacketHandler implements IPacketHandler {

    @Override
    public void handle(DataPacket packet) {
        NetSysServer netSysServer = NetSysServer.getInstance();
        NetSys netSys = netSysServer.sync();
        ServerServiceInfo serviceInfo = netSysServer.getServiceInfo();

        ProxyServer proxy = ProxyServer.getInstance();

        switch (packet.getPid()) {
            case ProtocolInfo.OPEN_CONNECTION_REQUEST_PACKET:
                OpenConnectionRequestPacket openConnectionRequestPacket = (OpenConnectionRequestPacket) packet;
                String prefix = openConnectionRequestPacket.id;

                OpenConnectionResponsePacket openConnectionResponsePacket = new OpenConnectionResponsePacket();
                openConnectionResponsePacket.client = prefix;

                InetSocketAddress socketAddress = openConnectionRequestPacket.branch.toLowerCase().startsWith("dev")
                        ? new InetSocketAddress("127.0.0.1", openConnectionRequestPacket.publicAddress.getPort())
                        : openConnectionRequestPacket.publicAddress;

                BedrockServerInfo bedrockServerInfo = new BedrockServerInfo(
                        prefix,
                        socketAddress,
                        socketAddress
                );

                boolean registered = proxy.registerServerInfo(bedrockServerInfo);

                if (registered) {
                    openConnectionResponsePacket.accepted = true;
                    openConnectionResponsePacket.server = serviceInfo.getID();
                    netSys.getRedisPool().dataPacket(openConnectionResponsePacket);
                    netSys.getLogger().warn("§a" + prefix + " Authentication accepted, new registered server!");
                    return;
                }

                openConnectionResponsePacket.accepted = false;
                netSys.getRedisPool().dataPacket(openConnectionResponsePacket);
                netSys.getLogger().warn("§c" + prefix + " Authentication failed by duplicate server, automatically disconnected!");
                break;
            case ProtocolInfo.CLOSE_CONNECTION_PACKET:
                CloseConnectionPacket closeConnectionPacket = (CloseConnectionPacket) packet;
                proxy.removeServerInfo(closeConnectionPacket.id);
                netSys.getLogger().warn("§aServer " + closeConnectionPacket.id + " disconnected by " + closeConnectionPacket.reason);
                break;
        }
    }
}
