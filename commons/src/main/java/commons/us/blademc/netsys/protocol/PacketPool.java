package commons.us.blademc.netsys.protocol;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.packet.*;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class PacketPool {

    protected final NetSys netSys;
    protected final Map<Byte, Class<? extends DataPacket>> registeredPackets = new HashMap<>();

    public void init() {
        registerPacket(
                new OpenClientConnectionRequestPacket(),
                new OpenClientConnectionResponsePacket(),
                new CloseClientConnectionPacket(),
                new ServerDisconnectPacket()
        );
    }

    public void registerPacket(DataPacket ...packets) {
        Arrays.stream(packets).forEach(packet -> {
            registeredPackets.putIfAbsent(packet.getPid(), packet.getClass());
            if (netSys.isDebug()) netSys.getLogger().debug("Packet " + packet.getClass().getName() + " registered!");
        });
    }

    public DataPacket getPacket(byte pid) {
        Class<? extends DataPacket> classInstance = registeredPackets.get(pid);
        if (classInstance == null) return null;

        try {
            return classInstance.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
