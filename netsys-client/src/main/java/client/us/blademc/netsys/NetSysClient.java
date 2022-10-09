package client.us.blademc.netsys;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.redis.RedisPool;
import lombok.Getter;

import java.net.InetSocketAddress;

public class NetSysClient extends PluginBase {

    @Getter
    private static NetSysClient instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    public NetSys sync() {
        return netSys;
    }

    @Getter
    private NetSys netSys = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        handleNetSys();
        handleService();
        handleLoginSequence();

        getServer().getCommandMap().register("whereaim", new WhereAImCommand());
    }

    private void handleNetSys() {
        netSys = new NetSys();

        Config config = getConfig();

        ConfigSection redisSection = config.getSection("redis");
        RedisPool redisPool = new RedisPool(netSys)
                .host(redisSection.getString("host"))
                .password(redisSection.getString("password"));

        netSys
                .packetHandler(new ClientPacketHandler())
                .logger(new ClientLogger())
                .redisPool(redisPool)
                .debug(config.getBoolean("debug", false))
                .start();
    }

    private void handleService() {
        ConfigSection serviceSection = getConfig().getSection("serviceInfo");
        serviceInfo = new ClientServiceInfo(
                netSys,
                serviceSection.getString("type"),
                serviceSection.getString("region"),
                serviceSection.getString("branch"),
                new InetSocketAddress(getServer().getIp(), getServer().getPort()),
                serviceSection.getString("publicAddress").isEmpty() ? null :
                        new InetSocketAddress(serviceSection.getString("publicAddress"), getServer().getPort()
                )
        );
    }

    @Getter
    private ClientServiceInfo serviceInfo;

    private void handleLoginSequence() {
        getServer().getScheduler().scheduleRepeatingTask(() -> {
            if (!serviceInfo.isLogged()) {
                netSys.getLogger().info("§eStarting login sequence...");
                serviceInfo.login();
            }
        }, 20 * 10);
    }

    @Override
    public void onDisable() {
        if (serviceInfo != null) serviceInfo.disconnect();
        if (netSys != null) netSys.stop();
    }
}
