package commons.us.blademc.netsys;

import com.google.common.base.Preconditions;
import commons.us.blademc.netsys.protocol.PacketPool;
import commons.us.blademc.netsys.redis.RedisPool;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetSys {

    private ILoggerHandler logger = null;
    private IPacketHandler packetHandler = null;
    private RedisPool redisPool = null;

    private final PacketPool packetPool;

    public NetSys() {
        packetPool = new PacketPool(this);
    }

    public void start() {
        Preconditions.checkNotNull(logger,
                "No ILoggerHandler class found to handle messages"
        );
        Preconditions.checkNotNull(packetHandler,
                "No IPacketHandler class found to handle packets"
        );
        Preconditions.checkNotNull(redisPool, "Configure your redisPool");

        packetPool.init();
        redisPool.login();
    }

    public void stop() {
        if (redisPool != null) redisPool.close();
    }
}
