package server.us.blademc.netsys;

import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.redis.RedisPool;
import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import lombok.Getter;
import server.us.blademc.netsys.command.GotoCommand;
import server.us.blademc.netsys.command.TransferCommand;
import server.us.blademc.netsys.listener.JoinHandler;
import server.us.blademc.netsys.listener.ReconnectHandler;
import server.us.blademc.netsys.logger.ServerLogger;
import server.us.blademc.netsys.packetHandler.ServerPacketHandler;
import server.us.blademc.netsys.service.ServerServiceInfo;

@Getter
public class NetSysServer extends Plugin {

    @Getter
    private static NetSysServer instance;

    public NetSys sync() {
        return netSys;
    }

    private NetSys netSys = null;

    @Override
    public void onEnable() {
        loadConfig();

        instance = this;

        handleNetSys();

        handleService();

        bedrockServerPool = new BedrockServerPool(netSys);
        bedrockServerPool.init();

        rewriteHandlers();
        registerCommands();
        subscribeEvents();
    }

    private BedrockServerPool bedrockServerPool;

    private void handleNetSys() {
        netSys = new NetSys();

        Configuration config = getConfig();

        RedisPool redisPool = new RedisPool(netSys)
                .host(config.getString("redis.host"))
                .password(config.getString("redis.password"));

        netSys
                .packetHandler(new ServerPacketHandler())
                .logger(new ServerLogger())
                .redisPool(redisPool)
                .debug(config.getBoolean("debug", false))
                .start();
    }

    private void handleService() {
        Configuration config = getConfig();
        serviceInfo = new ServerServiceInfo(netSys,
                config.getString("serviceInfo.id"),
                config.getString("serviceInfo.type"),
                config.getString("serviceInfo.region"),
                config.getString("serviceInfo.branch")
        );
        netSys.getLogger().info("§aService Info: " + serviceInfo.toString());
    }

    private void rewriteHandlers() {
        getProxy().setJoinHandler(new JoinHandler());
        getProxy().setReconnectHandler(new ReconnectHandler());
    }

    private void registerCommands() {
        getProxy().getCommandMap().registerCommand(new GotoCommand());
        getProxy().getCommandMap().registerCommand(new TransferCommand());
    }

    private void subscribeEvents() {
        getProxy().getEventManager().subscribe(ProxyQueryEvent.class, this::onQuery);
        getProxy().getEventManager().subscribe(ProxyPingEvent.class, this::onPing);
        getProxy().getEventManager().subscribe(PreTransferEvent.class, this::onTransfer);
    }

    public void onQuery(ProxyQueryEvent event) {
        event.setMaximumPlayerCount(event.getPlayerCount() + 10);
    }

    public void onPing(ProxyPingEvent event) {
        event.setMaximumPlayerCount(event.getPlayerCount() + 10);
    }

    public void onTransfer(PreTransferEvent event) {
        event.getPlayer().sendMessage("§7Connecting you to " + event.getTargetServer().getServerName());
    }

    @Getter
    private ServerServiceInfo serviceInfo = null;

    @Override
    public void onDisable() {
        if (serviceInfo != null) serviceInfo.disconnect();
        if (netSys != null) netSys.stop();
    }
}
