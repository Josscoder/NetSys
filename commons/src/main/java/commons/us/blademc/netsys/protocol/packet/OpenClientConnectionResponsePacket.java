package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.protocol.ProtocolInfo;

public class OpenClientConnectionResponsePacket extends DataPacket {

    public boolean accepted;
    public String clientID;
    public String serverID;

    public OpenClientConnectionResponsePacket() {
        super(ProtocolInfo.OPEN_CLIENT_CONNECTION_RESPONSE_PACKET);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeBoolean(accepted);
        output.writeUTF(clientID);
        output.writeUTF(serverID);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        accepted = input.readBoolean();
        clientID = input.readUTF();
        serverID = input.readUTF();
    }
}
