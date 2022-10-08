package commons.us.blademc.netsys;

import commons.us.blademc.netsys.protocol.packet.DataPacket;

public interface IPacketHandler {
    void handle(DataPacket packet);
}
