package report.us.blademc.netsys;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.UUID;

public class ReportCommand extends Command {

    public ReportCommand() {
        super("report",
                "Report a player",
                "/report [player] <reason>"
        );
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length != 2) {
            commandSender.sendMessage(new TranslationContainer("commands.generic.usage", usageMessage));
            return false;
        }

        String playerName = args[0];
        String reasonOutput = args[1];

        NetSysReports.getInstance().sync().getRedisPool().dataPacket(new ReportPacket(){{
            id = UUID.randomUUID().toString().substring(0, 4);
            server = NetSysReports.getInstance().sync().getClient().getName();
            sender = commandSender.getName();
            target = playerName;
            reason = reasonOutput;
        }});

        commandSender.sendMessage(NetSysReports.getInstance().getPrefix()
                + TextFormat.GREEN + "Your report was sent!"
        );
        return true;
    }
}
