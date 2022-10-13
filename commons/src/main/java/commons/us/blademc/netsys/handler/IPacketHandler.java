package commons.us.blademc.netsys.handler;

import commons.us.blademc.netsys.protocol.packet.DataPacket;

public interface IPacketHandler {
    void handle(DataPacket packet);
}
