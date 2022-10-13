package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.protocol.ProtocolInfo;

import java.util.LinkedList;
import java.util.List;

public class ClientUpdateDataPacket extends DataPacket {

    public String id;
    public List<String> players = new LinkedList<>();

    public ClientUpdateDataPacket() {
        super(ProtocolInfo.CLIENT_UPDATE_DATA_PACKET);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeUTF(id);
        output.writeInt(players.size());
        players.forEach(output::writeUTF);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        id = input.readUTF();

        int playersSize = input.readInt();

        if (playersSize <= 0) {
            players = new LinkedList<>();
            return;
        }

        for (int i = 0; i < playersSize; i++) players.add(input.readUTF());
    }
}
