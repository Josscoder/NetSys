package report.us.blademc.netsys;

import client.us.blademc.netsys.ClientPacketHandler;
import client.us.blademc.netsys.NetSysClient;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import commons.us.blademc.netsys.NetSys;
import commons.us.blademc.netsys.protocol.packet.DataPacket;
import lombok.Getter;

public class NetSysReports extends PluginBase {

    @Getter
    private static NetSysReports instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        setupConfig();
        handleNetSys();
        getServer().getCommandMap().register("report", new ReportCommand());
    }

    private void setupConfig() {
        saveDefaultConfig();
        prefix = getConfig().getString("prefix");
        permission = getConfig().getString("permission");
    }

    @Getter
    private String prefix;
    private String permission;

    private void handleNetSys() {
        sync().getPacketPool().registerPacket(new ReportPacket());
        sync().packetHandler(new ClientPacketHandler() {
            @Override
            public void handle(DataPacket packet) {
                if (!(packet instanceof ReportPacket)) {
                    super.handle(packet);
                    return;
                }
                ReportPacket reportPacket = (ReportPacket) packet;

                String output = prefix + TextFormat.RED + String.format("(%s) %s was reported by %s for %s!",
                        reportPacket.id,
                        reportPacket.target,
                        reportPacket.sender,
                        reportPacket.reason
                );

                getServer().getOnlinePlayers().values()
                        .stream()
                        .filter(player -> player.hasPermission(permission))
                        .forEach(player -> player.sendMessage(output));
                getLogger().info(output);
            }
        });
    }

    public NetSys sync() {
        return NetSysClient.getInstance().sync();
    }
}
