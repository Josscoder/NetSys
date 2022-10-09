package server.us.blademc.netsys;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.packet.ServerDisconnectPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ServerServiceInfo {

    private final NetSys netSys;
    private final String uuid = UUID.randomUUID().toString();
    private final String name;
    private final String type;
    private final String region;
    private final String branch;

    public void disconnect() {
        ServerDisconnectPacket packet = new ServerDisconnectPacket();
        packet.serverID = getID();
        netSys.getRedisPool().dataPacket(packet);
    }

    public String getShortUUID() {
        return uuid.substring(0, 5);
    }

    public String getID() {
        return String.format("%s-%s", region, name);
    }

    @Override
    public String toString() {
        return "§e" + getID();
    }
}
