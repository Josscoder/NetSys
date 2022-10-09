package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.protocol.ProtocolInfo;

public class ServerDisconnectPacket extends DataPacket {

    public String serverID;

    public ServerDisconnectPacket() {
        super(ProtocolInfo.SERVER_DISCONNECT_PACKET);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeUTF(serverID);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        serverID = input.readUTF();
    }
}
