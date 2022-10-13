package report.us.blademc.netsys;

import client.us.blademc.netsys.packetHandler.ClientPacketHandler;
import cn.nukkit.Server;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;
import commons.us.blademc.netsys.protocol.packet.DataPacket;

public class ReportPacketHandler extends ClientPacketHandler {

    @Override
    public void handle(DataPacket packet) {
        if (!(packet instanceof ReportPacket)) {
            super.handle(packet);
            return;
        }

        ReportPacket reportPacket = (ReportPacket) packet;

        String output = NetSysReports.getInstance().getPrefix() + TextFormat.RED + String.format("(%s-%s) %s was reported by %s for %s!",
                reportPacket.uuid.toUpperCase(),
                reportPacket.server,
                reportPacket.target,
                reportPacket.sender,
                reportPacket.reason
        );

        Server.getInstance().getOnlinePlayers().values()
                .stream()
                .filter(player -> player.hasPermission(NetSysReports.getInstance().getPermission()))
                .forEach(player -> {
                    player.getLevel().addSound(player, Sound.RANDOM_TOTEM, 1, 1, player);
                    player.sendMessage(output);
                });

        NetSysReports.getInstance().getLogger().info(output);
    }
}
