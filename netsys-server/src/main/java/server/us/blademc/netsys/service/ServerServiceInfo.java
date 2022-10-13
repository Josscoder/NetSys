package server.us.blademc.netsys.service;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.packet.ServerDisconnectPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ServerServiceInfo {

    private final NetSys netSys;

    private final String id;
    private final String type;
    private final String region;
    private final String branch;

    public void disconnect() {
        ServerDisconnectPacket packet = new ServerDisconnectPacket();
        packet.serverID = getID();
        netSys.getRedisPool().dataPacket(packet);
    }

    public String getID() {
        return String.format("%s-%s", region, id);
    }

    @Override
    public String toString() {
        return "§e" + getID();
    }
}
