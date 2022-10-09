package client.us.blademc.netsys;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import commons.us.blademc.netsys.service.ClientServiceInfo;

public class WhereAImCommand extends Command {

    public WhereAImCommand() {
        super("whereaim",
                "Provide information about the server you are on",
                "",
                new String[]{"server"}
        );
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        ClientServiceInfo serviceInfo = NetSysClient.getInstance().getServiceInfo();
        sender.sendMessage("§aYou are currently online on " + serviceInfo.toString());
        return true;
    }
}
