package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.protocol.ProtocolInfo;

public class OpenConnectionResponsePacket extends DataPacket {

    public boolean accepted;
    public String client;
    public String server;

    public OpenConnectionResponsePacket() {
        super(ProtocolInfo.OPEN_CONNECTION_RESPONSE_PACKET);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeBoolean(accepted);
        output.writeUTF(client);
        output.writeUTF(server);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        accepted = input.readBoolean();
        client = input.readUTF();
        server = input.readUTF();
    }
}
