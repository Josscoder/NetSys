package commons.us.blademc.netsys.redis;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.packet.DataPacket;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;

/*
Part of this code was taken and adapted from: https://github.com/aabstractt/AbstractPractice
 */

@Getter
@Setter
public class RedisPool {

    protected final NetSys netSys;

    private byte[] filterID = "NetSysPacketChannel".getBytes(StandardCharsets.UTF_8);

    public RedisPool(NetSys netSys) {
        this.netSys = netSys;
    }

    @CanIgnoreReturnValue
    public RedisPool host(String host) {
        String[] split = host.split(":");
        if (split.length > 1) {
            this.host = split[0];
            this.port = Integer.parseInt(split[1]);
        } else {
            this.host = host;
        }
        return this;
    }

    private String host = Protocol.DEFAULT_HOST;
    private int port = Protocol.DEFAULT_PORT;

    @CanIgnoreReturnValue
    public RedisPool password(String password) {
        this.password = password;
        return this;
    }

    private String password = null;

    public void login() {
        connection = new JedisPool(new JedisPoolConfig(),
                host,
                port,
                30_000,
                password,
                0,
                null
        );

        connected = connection.getResource().isConnected();
        if (connected) {
            netSys.getLogger().info("The connection was established, synchronizing...");
            sync();
        } else {
            netSys.getLogger().error("A connection could not be established because something in the credentials is wrong");
        }
    }

    private JedisPool connection = null;

    private void sync() {
        ForkJoinPool.commonPool().execute(this::handlePackets);
        netSys.getLogger().warn(String.format("Synchronization is done (%sms)!",
                ((System.currentTimeMillis() - netSys.getStartTime()) / 1000.0D))
        );
    }

    private void handlePackets() {
        execute(jedis -> {
            jedis.subscribe(packetPubSub = new BinaryJedisPubSub(){
                @Override
                public void onMessage(byte[] channel, byte[] message) {
                    handlePacketDecoding(message);
                }
            }, filterID);
        });
    }

    private BinaryJedisPubSub packetPubSub = null;

    private void handlePacketDecoding(byte[] message) {
        ByteArrayDataInput input = ByteStreams.newDataInput(message);

        DataPacket packet = netSys.getPacketPool().getPacket(input.readByte());
        if (packet == null) {
            if (netSys.isDebug()) netSys.getLogger().debug("Packet received is null");
            return;
        }

        packet.decode(input);
        netSys.getPacketHandler().handle(packet);

        if (netSys.isDebug()) netSys.getLogger().debug("Packet " + packet.getClass().getName() + " decoded and handled!");
    }

    public void dataPacket(DataPacket packet) {
        CompletableFuture.runAsync(() -> handlePacketEncoding(packet));
    }

    private void handlePacketEncoding(DataPacket packet) {
        execute(jedis -> {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();

            output.writeByte(packet.getPid());
            packet.encode(output);

            jedis.publish(filterID, output.toByteArray());

            if (netSys.isDebug()) netSys.getLogger().debug("Packet " + packet.getClass().getName() + " encoded and sent!");
        });
    }

    public boolean isConnected() {
        return connected && connection != null && !connection.isClosed();
    }

    private boolean connected = false;

    public <T> T execute(Function<Jedis, T> action) {
        if (!isConnected()) throw new RuntimeException("Redis disconnected");
        try (Jedis jedis = connection.getResource()) {
            if (password != null && !password.isEmpty()) jedis.auth(password);
            return action.apply(jedis);
        }
    }

    public void execute(Consumer<Jedis> action) {
        if (isConnected()) {
            try (Jedis jedis = connection.getResource()) {
                if (password != null && !password.isEmpty()) jedis.auth(password);
                action.accept(jedis);
            }
        } else {
            throw new RuntimeException("Redis disconnected");
        }
    }

    public void close() {
        if (connection != null) connection.close();
        if (packetPubSub != null) packetPubSub.unsubscribe();
    }
}
