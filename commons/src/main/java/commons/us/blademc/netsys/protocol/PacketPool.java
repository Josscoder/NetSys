package commons.us.blademc.netsys.protocol;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.packet.DataPacket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PacketPool {

    protected final NetSys netSys;
    protected final Map<Byte, Class<? extends DataPacket>> registeredPackets = new HashMap<>();

    public PacketPool(NetSys netSys) {
        this.netSys = netSys;
    }

    public void init() {
        netSys.getLogger().warn("Registering default packets...");

        //TODO: register default packets here

        netSys.getLogger().warn("Packets registered successfully!");
    }

    public void registerPacket(DataPacket ...packets) {
        Arrays.stream(packets).forEach(packet ->
                registeredPackets.putIfAbsent(packet.getPid(), packet.getClass())
        );
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
