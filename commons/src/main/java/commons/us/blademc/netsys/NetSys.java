package commons.us.blademc.netsys;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import commons.us.blademc.netsys.protocol.PacketPool;
import commons.us.blademc.netsys.redis.RedisPool;
import lombok.Getter;

@Getter
public class NetSys {

    private final PacketPool packetPool;

    public NetSys() {
        packetPool = new PacketPool(this);
    }

    @CanIgnoreReturnValue
    public NetSys logger(ILoggerHandler logger) {
        this.logger = logger;
        return this;
    }

    private ILoggerHandler logger = null;

    @CanIgnoreReturnValue
    public NetSys packetHandler(IPacketHandler packetHandler) {
        this.packetHandler = packetHandler;
        return this;
    }

    private IPacketHandler packetHandler = null;

    @CanIgnoreReturnValue
    public NetSys redisPool(RedisPool redisPool) {
        this.redisPool = redisPool;
        return this;
    }

    private RedisPool redisPool = null;

    @CanIgnoreReturnValue
    public NetSys debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    private boolean debug = false;

    public void start() {
        startTime = System.currentTimeMillis();

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

    private double startTime;

    public void stop() {
        if (redisPool != null) redisPool.close();
    }
}
