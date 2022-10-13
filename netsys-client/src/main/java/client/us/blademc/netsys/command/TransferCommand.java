package client.us.blademc.netsys.command;

import client.us.blademc.netsys.NetSysClient;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class TransferCommand extends Command {

    public TransferCommand() {
        super("transfer",
                "Transfer to a specific server",
                "/transfer <server id>"
        );
        setPermission("netsys.transfer.command");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.isPlayer() || !testPermission(sender)) return false;

        Player player = (Player) sender;

        NetSysClient netSysClient = NetSysClient.getInstance();

        if (args.length <= 0) {
            player.sendMessage("§aServers list: ");
            netSysClient.getGroupHandler().getServers().values().forEach(server -> player.sendMessage(
                    (netSysClient.getServiceInfo().getID().equals(server.getName())
                            ? "§6"
                            : "§a") +
                            "- " + server.getName())
            );
            return true;
        }

        String serverName = args[0];

        if (!netSysClient.getGroupHandler().containsServer(serverName)) {
            player.sendMessage("§cThat server is currently not in rotation");
            return true;
        }

        netSysClient.transferPlayer(player, serverName);
        return true;
    }
}
