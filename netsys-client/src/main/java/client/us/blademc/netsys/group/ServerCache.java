package client.us.blademc.netsys.group;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServerCache {

    private final String name;
    private List<String> players = new ArrayList<>();

    public int getOnlinePlaying() {
        return players.size();
    }

    public boolean isPlaying(String playerName) {
        return players.contains(playerName);
    }
}
