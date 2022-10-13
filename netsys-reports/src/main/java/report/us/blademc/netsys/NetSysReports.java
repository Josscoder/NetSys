package report.us.blademc.netsys;

import client.us.blademc.netsys.NetSysClient;
import cn.nukkit.plugin.PluginBase;
import commons.us.blademc.netsys.NetSys;
import lombok.Getter;
import report.us.blademc.netsys.command.ReportCommand;
import report.us.blademc.netsys.protocol.packet.ReportPacket;
import report.us.blademc.netsys.protocol.ReportPacketHandler;

@Getter
public class NetSysReports extends PluginBase {

    public static byte REPORT_PACKET = 0x53;

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

    private String prefix;
    private String permission;

    private void handleNetSys() {
        sync().getPacketPool().registerPacket(new ReportPacket());
        sync().packetHandler(new ReportPacketHandler());
    }

    public NetSys sync() {
        return NetSysClient.getInstance().sync();
    }
}
