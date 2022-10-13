package client.us.blademc.netsys.service;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.packet.CloseClientConnectionPacket;
import commons.us.blademc.netsys.protocol.packet.OpenClientConnectionRequestPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.InetSocketAddress;

@RequiredArgsConstructor
@Getter
@Setter
public class ClientServiceInfo {

    private final NetSys netSys;

    private final String id;
    private final String type;
    private final String region;
    private final String branch;
    private final InetSocketAddress address;
    private final InetSocketAddress publicAddress;
    private String serverID = "NONE";
    private boolean logged = false;

    public void login() {
        if (logged) return;

        OpenClientConnectionRequestPacket packet = new OpenClientConnectionRequestPacket();
        packet.id = getID();
        packet.type = type;
        packet.region = region;
        packet.branch = branch;
        packet.publicAddress = (publicAddress == null ? address : publicAddress);

        netSys.getRedisPool().dataPacket(packet);
    }

    public String getID() {
        return String.format("%s-%s", type, id);
    }

    public void disconnect(String reason) {
        if (!logged) return;

        CloseClientConnectionPacket packet = new CloseClientConnectionPacket();
        packet.id = getID();
        packet.reason = reason;

        netSys.getRedisPool().dataPacket(packet);
    }

    public boolean isLobbyServer() {
        return getID().startsWith("lobby-") || getID().startsWith("hub-");
    }

    @Override
    public String toString() {
        return String.format("%s-%s-%s (%s)", region, getID(), branch, serverID);
    }
}
