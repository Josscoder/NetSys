package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.protocol.ProtocolInfo;
import commons.us.blademc.netsys.protocol.packet.types.PacketHelper;

import java.net.InetSocketAddress;

public class OpenClientConnectionRequestPacket extends DataPacket {

    public String id;
    public String type;
    public String region;
    public String branch;
    public InetSocketAddress publicAddress;

    public OpenClientConnectionRequestPacket() {
        super(ProtocolInfo.OPEN_CLIENT_CONNECTION_REQUEST_PACKET);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeUTF(id);
        output.writeUTF(type);
        output.writeUTF(region);
        output.writeUTF(branch);
        PacketHelper.writeAddress(output, publicAddress);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        id = input.readUTF();
        type = input.readUTF();
        region = input.readUTF();
        branch = input.readUTF();
        publicAddress = PacketHelper.readAddress(input);
    }
}
