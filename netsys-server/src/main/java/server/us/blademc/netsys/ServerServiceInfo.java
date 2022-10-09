package server.us.blademc.netsys;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ServerServiceInfo {

    private final String uuid = UUID.randomUUID().toString();
    private final String name;
    private final String type;
    private final String region;
    private final String branch;

    public String getID() {
        return String.format("%s-%s", region, name);
    }

    @Override
    public String toString() {
        return "§e" + getID();
    }
}
