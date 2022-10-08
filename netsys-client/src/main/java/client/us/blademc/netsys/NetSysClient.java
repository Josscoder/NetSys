package client.us.blademc.netsys;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import commons.us.blademc.netsys.ServiceInfo;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.redis.RedisPool;
import lombok.Getter;

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

        netSys = new NetSys();

        Config config = getConfig();

        ConfigSection redisSection = config.getSection("redis");
        RedisPool redisPool = new RedisPool(netSys)
                .host(redisSection.getString("host"))
                .password(redisSection.getString("password"));

        ConfigSection serviceInfoSection = config.getSection("serviceInfo");
        ServiceInfo serviceInfo = new ServiceInfo(
                serviceInfoSection.getString("name"),
                serviceInfoSection.getString("type"),
                serviceInfoSection.getString("region"),
                serviceInfoSection.getString("branch")
        );

        netSys
                .packetHandler(new ClientPacketHandler())
                .logger(new ClientLogger())
                .redisPool(redisPool)
                .serviceInfo(serviceInfo)
                .debug(config.getBoolean("debug", false))
                .start();
    }

    @Override
    public void onDisable() {
        if (netSys != null) netSys.stop();
    }
}
