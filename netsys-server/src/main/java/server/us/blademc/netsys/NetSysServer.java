package server.us.blademc.netsys;

import commons.us.blademc.netsys.Client;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.redis.RedisPool;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import lombok.Getter;

public class NetSysServer extends Plugin {

    @Getter
    private static NetSysServer instance;

    public NetSys sync() {
        return netSys;
    }

    @Getter
    private NetSys netSys = null;

    @Override
    public void onEnable() {
        loadConfig();

        instance = this;

        netSys = new NetSys();

        Configuration config = getConfig();

        RedisPool redisPool = new RedisPool(netSys)
                .host(config.getString("redis.host"))
                .password(config.getString("redis.password"));

        Client client = new Client(config.getString("client.name"),
                config.getString("client.type")
        );

        netSys
                .packetHandler(new ServerPacketHandler())
                .logger(new ServerLogger())
                .redisPool(redisPool)
                .client(client)
                .debug(config.getBoolean("debug", false))
                .start();
    }

    @Override
    public void onDisable() {
        if (netSys != null) netSys.stop();
    }
}
