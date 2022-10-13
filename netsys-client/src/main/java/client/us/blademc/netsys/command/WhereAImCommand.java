package client.us.blademc.netsys.command;

import client.us.blademc.netsys.NetSysClient;
import client.us.blademc.netsys.service.ClientServiceInfo;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class WhereAImCommand extends Command {

    public WhereAImCommand() {
        super("whereaim",
                "Provide information about the server you are on",
                "/whereaim",
                new String[]{"connection"}
        );
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        ClientServiceInfo serviceInfo = NetSysClient.getInstance().getServiceInfo();
        sender.sendMessage("§aYou are currently online on " + serviceInfo.toString());
        return true;
    }
}
