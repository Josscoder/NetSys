package client.us.blademc.netsys.command;

import client.us.blademc.netsys.NetSysClient;
import client.us.blademc.netsys.group.ServerCache;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class HubCommand extends Command {

    public HubCommand() {
        super("hub",
                "Return to the lobby",
                "",
                new String[]{"leave", "l", "lobby", "vestibulo", "centro", "exit", "salir"}
        );
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] label) {
        if (!sender.isPlayer()) return false;
        Player player = (Player) sender;

        NetSysClient netSysClient = NetSysClient.getInstance();

        ServerCache lobbyServer = netSysClient.getGroupHandler().getBalancedLobbyServer();
        if (lobbyServer == null) {
            player.sendMessage("§cThere are no rotating lobby servers!");
            return false;
        }

        netSysClient.transferPlayer(player, lobbyServer.getName());
        return true;
    }
}
