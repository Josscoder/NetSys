package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.protocol.ProtocolInfo;
import commons.us.blademc.netsys.protocol.packet.types.PacketHelper;

import java.net.InetSocketAddress;

public class OpenConnectionRequestPacket extends DataPacket {

    public String id;
    public String uuid;
    public String type;
    public String region;
    public String branch;
    public InetSocketAddress publicAddress;

    public OpenConnectionRequestPacket() {
        super(ProtocolInfo.OPEN_CONNECTION_REQUEST_PACKET);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeUTF(id);
        output.writeUTF(uuid);
        output.writeUTF(type);
        output.writeUTF(region);
        output.writeUTF(branch);
        PacketHelper.writeAddress(output, publicAddress);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        id = input.readUTF();
        uuid = input.readUTF();
        type = input.readUTF();
        region = input.readUTF();
        branch = input.readUTF();
        publicAddress = PacketHelper.readAddress(input);
    }
}
