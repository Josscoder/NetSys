package commons.us.blademc.netsys.service;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.packet.CloseConnectionPacket;
import commons.us.blademc.netsys.protocol.packet.OpenConnectionRequestPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class ClientServiceInfo {

    private final NetSys netSys;
    private final String uuid = UUID.randomUUID().toString();
    private final String type;
    private final String region;
    private final String branch;
    private final InetSocketAddress address;
    private final InetSocketAddress publicAddress;
    private String serverID = "NONE";
    private boolean logged = false;

    public void login() {
        if (logged) return;

        OpenConnectionRequestPacket packet = new OpenConnectionRequestPacket();
        packet.id = getID();
        packet.uuid = uuid;
        packet.type = type;
        packet.region = region;
        packet.branch = branch;
        packet.publicAddress = (publicAddress == null ? address : publicAddress);

        netSys.getRedisPool().dataPacket(packet);
    }

    public String getID() {
        return String.format("%s-%s-%s",
                region,
                type,
                uuid.substring(0, 16)
        );
    }

    public void disconnect() {
        disconnect("Request");
    }

    public void disconnect(String reason) {
        if (!logged) return;

        CloseConnectionPacket packet = new CloseConnectionPacket();
        packet.id = getID();
        packet.reason = reason;

        netSys.getRedisPool().dataPacket(packet);
    }

    @Override
    public String toString() {
        return String.format("§e%s §a(§e%s§a)", getID(), serverID);
    }
}
