package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.protocol.ProtocolInfo;

public class CloseClientConnectionPacket extends DataPacket {

    public String id;
    public String reason;

    public CloseClientConnectionPacket() {
        super(ProtocolInfo.CLOSE_CLIENT_CONNECTION_PACKET);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeUTF(id);
        output.writeUTF(reason);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        id = input.readUTF();
        reason = input.readUTF();
    }
}
