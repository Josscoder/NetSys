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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

@Getter
@Setter
public class RedisPool {

    protected final NetSys netSys;

    private String filterID = "NetSysChannel";

    public RedisPool(NetSys netSys) {
        this.netSys = netSys;
    }

    @CanIgnoreReturnValue
    public RedisPool host(String host) {
        String[] split = host.split(":");
        this.host = split[0];
        this.port = Integer.parseInt(split[1]);
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
            netSys.getLogger().warn("The connection was established, synchronizing...");
            sync();
        } else {
            netSys.getLogger().warn("A connection could not be established because something in the credentials is wrong");
        }
    }

    private JedisPool connection = null;

    private void sync() {
        handlePackets();
        handleMessages();
    }

    private void handlePackets() {
        ForkJoinPool.commonPool().execute(() -> execute(jedis -> {
            jedis.subscribe(packetPubSub = new BinaryJedisPubSub(){
                @Override
                public void onMessage(byte[] channel, byte[] message) {
                    handlePacketDecoding(message);
                }
            }, filterID.getBytes(StandardCharsets.UTF_8));
        }));
    }

    private BinaryJedisPubSub packetPubSub = null;

    private void handlePacketDecoding(byte[] message) {
        ByteArrayDataInput input = ByteStreams.newDataInput(message);

        DataPacket packet = netSys.getPacketPool().getPacket(input.readByte());
        if (packet == null && netSys.isDebug()) {
            netSys.getLogger().debug("Packet received is null");
            return;
        }

        packet.decode(input);
        netSys.getPacketHandler().handle(packet);

        if (netSys.isDebug()) {
            netSys.getLogger().debug("Packet " + packet.getClass().getName() + " decoded and handled!");
        }
    }

    public void dataPacket(DataPacket packet) {
        CompletableFuture.runAsync(() -> handlePacketEncoding(packet));
    }

    private void handlePacketEncoding(DataPacket packet) {
        execute(jedis -> {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();

            output.writeByte(packet.getPid());
            packet.encode(output);

            jedis.publish(filterID.getBytes(StandardCharsets.UTF_8), output.toByteArray());

            if (netSys.isDebug()) {
                netSys.getLogger().debug("Packet " + packet.getClass().getName() + " encoded and sent!");
            }
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

    public void publish(String channel, Object... args) {
        StringBuilder builder = new StringBuilder();
        Arrays.stream(args).forEach(arg -> builder.append(";").append(arg.toString()));
        publish(channel, builder.substring(1));
    }

    public void publish(String channel, String message) {
        CompletableFuture.runAsync(() -> execute(jedis -> {
            jedis.publish(filterID, channel + "|" + message);
            if (netSys.isDebug()) {
                netSys.getLogger().debug("A message was published on the channel " + channel);
            }
        }));
    }

    public void subscribe(String channel, MessageCallback callback) {
        callbackMap.putIfAbsent(channel, callback);

        if (netSys.isDebug()) {
            netSys.getLogger().debug("A subcription was made for the channel " + channel);
        }
    }

    private void handleMessages() {
        ForkJoinPool.commonPool().execute(() -> execute(jedis -> {
            jedis.subscribe(messagePubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    if (!channel.equals(filterID)) return;
                    String[] args = message.split(Pattern.quote("|"));

                    if (!callbackMap.containsKey(args[0])) return;
                    String[] messages = Arrays.copyOfRange(args, 1, args.length);
                    String msg = Arrays.toString(messages);
                    callbackMap.get(args[0]).onMessage(msg.substring(1).split(";"));

                    if (netSys.isDebug()) {
                        netSys.getLogger().debug("A message was decoded and handled for the channel " + args[0]);
                    }
                }
            }, filterID);
        }));
    }

    protected final Map<String, MessageCallback> callbackMap = new HashMap<>();
    private JedisPubSub messagePubSub = null;

    public void close() {
        if (connection != null) connection.close();
        if (packetPubSub != null) packetPubSub.unsubscribe();
        if (messagePubSub != null) messagePubSub.unsubscribe();
    }
}
