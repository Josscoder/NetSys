package report.us.blademc.netsys.command;

import client.us.blademc.netsys.NetSysClient;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import report.us.blademc.netsys.NetSysReports;
import report.us.blademc.netsys.protocol.packet.ReportPacket;

import java.util.Arrays;
import java.util.UUID;

public class ReportCommand extends Command {

    public ReportCommand() {
        super("report",
                "Report a player",
                "/report [player] <reason>"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", usageMessage));
            return false;
        }

        String playerName = args[0];
        String reasonOutput = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        ReportPacket packet = new ReportPacket();
        packet.uuid = UUID.randomUUID().toString().substring(0, 3);
        packet.server = NetSysClient.getInstance().getServiceInfo().getID();
        packet.sender = sender.getName();
        packet.target = playerName;
        packet.reason = reasonOutput;
        NetSysReports.getInstance().sync().getRedisPool().dataPacket(packet);

        sender.sendMessage(NetSysReports.getInstance().getPrefix()
                + TextFormat.GREEN + "Your report was sent!"
        );
        return true;
    }
}
