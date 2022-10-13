package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.cache.ServerCache;
import commons.us.blademc.netsys.protocol.ProtocolInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NetworkUpdateDataPacket extends DataPacket {

    public List<ServerCache> serverList = new LinkedList<>();

    public NetworkUpdateDataPacket() {
        super(ProtocolInfo.NETWORK_UPDATE_DATA);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeInt(serverList.size());
        serverList.forEach(serverCache -> {
            output.writeUTF(serverCache.getName());
            output.writeInt(serverCache.getOnlinePlaying());
            serverCache.getPlayers().forEach(output::writeUTF);
        });
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        int serversSize = input.readInt();

        if (serversSize <= 0) {
            serverList = new LinkedList<>();
            return;
        }

        for (int i = 0; i < serversSize; i++) {
            String id = input.readUTF();

            List<String> players = new ArrayList<>();
            int playerList = input.readInt();

            for (int ij = 0; ij < playerList; ij++) players.add(input.readUTF());

            ServerCache cache = new ServerCache(id);
            cache.setPlayers(players);

            serverList.add(cache);
        }
    }
}
