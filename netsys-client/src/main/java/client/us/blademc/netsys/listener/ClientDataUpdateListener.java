package client.us.blademc.netsys.listener;

import client.us.blademc.netsys.NetSysClient;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import commons.us.blademc.netsys.protocol.packet.ClientUpdateDataPacket;

import java.util.stream.Collectors;

public class ClientDataUpdateListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        handleUpdate();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        handleUpdate();
    }

    private void handleUpdate() {
        ClientUpdateDataPacket packet = new ClientUpdateDataPacket();
        packet.id = NetSysClient.getInstance().getServiceInfo().getID();
        packet.players = Server.getInstance().getOnlinePlayers().values()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        NetSysClient.getInstance().sync().getRedisPool().dataPacket(packet);
    }
}
