package commons.us.blademc.netsys.protocol.packet.types;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.net.InetSocketAddress;

public class PacketHelper {

    public static void writeAddress(ByteArrayDataOutput output, InetSocketAddress address) {
        String hostAddress = address.getAddress().getHostAddress();
        output.writeUTF(hostAddress);
        output.writeInt(address.getPort());
    }

    public static InetSocketAddress readAddress(ByteArrayDataInput input) {
        return new InetSocketAddress(input.readUTF(), input.readInt());
    }
}
