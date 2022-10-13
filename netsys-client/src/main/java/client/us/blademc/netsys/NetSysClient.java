package client.us.blademc.netsys;

import client.us.blademc.netsys.command.HubCommand;
import client.us.blademc.netsys.command.TransferCommand;
import client.us.blademc.netsys.command.WhereAImCommand;
import client.us.blademc.netsys.group.ClientGroupHandler;
import client.us.blademc.netsys.listener.ClientDataUpdateListener;
import client.us.blademc.netsys.logger.ClientLogger;
import client.us.blademc.netsys.protocol.ClientPacketHandler;
import client.us.blademc.netsys.service.ClientServiceInfo;
import cn.nukkit.Player;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.network.protocol.TransferPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.redis.RedisPool;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
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
        handleNetSysServerConnection();

        registerCommands();

        getServer().getPluginManager().registerEvents(new ClientDataUpdateListener(), this);

        groupHandler = new ClientGroupHandler(netSys);
    }

    private ClientGroupHandler groupHandler;

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
                serviceSection.getString("id"),
                serviceSection.getString("type"),
                serviceSection.getString("region"),
                serviceSection.getString("branch"),
                new InetSocketAddress(getServer().getIp(), getServer().getPort()),
                serviceSection.getString("publicAddress").isEmpty() ? null :
                        new InetSocketAddress(serviceSection.getString("publicAddress"), getServer().getPort()
                )
        );
    }

    private ClientServiceInfo serviceInfo = null;

    private void handleNetSysServerConnection() {
        getServer().getScheduler().scheduleRepeatingTask(() -> {
            if (!serviceInfo.isLogged()) {
                netSys.getLogger().info("§eTrying to connect to a NetSys-Server...");
                serviceInfo.login();
            }
        }, 20 * 10);
    }

    private void registerCommands() {
        SimpleCommandMap map = getServer().getCommandMap();
        map.register("whereaim", new WhereAImCommand());
        map.register("transfer", new TransferCommand());
        map.register("hub", new HubCommand());
    }

    public void transferPlayer(Player player, String serverID) {
        TransferPacket packet = new TransferPacket();
        packet.address = serverID;
        packet.port = 0;
        player.dataPacket(packet);
    }

    @Override
    public void onDisable() {
        if (serviceInfo != null) serviceInfo.disconnect("Plugin disabled");
        if (netSys != null) netSys.stop();
    }
}
