package server.us.blademc.netsys.command;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public class TransferCommand extends Command {

    public TransferCommand() {
        super("transfer", CommandSettings.builder()
                .setPermission("waterdog.command.transfer")
                .setUsageMessage("/transfer <server id>")
                .setDescription("Transfer to a specific server")
                .build()
        );
    }

    @Override
    public boolean onExecute(CommandSender sender, String s, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage("This command can only be ran by players");
            return false;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length <= 0) {
            player.sendMessage("§aServers list: ");
            player.getProxy().getServers().forEach(serverInfo -> player.sendMessage((player.getServerInfo().getServerName().equals(serverInfo.getServerName())
                    ? "§6"
                    : "§a") +
                    "- " + serverInfo.getServerName()
            ));
            return true;
        }

        ServerInfo serverInfo = player.getProxy().getServerInfo(args[0]);
        if (serverInfo == null) {
            player.sendMessage("§cThat server is currently not in rotation");
            return true;
        }

        player.connect(serverInfo);
        return true;
    }
}
