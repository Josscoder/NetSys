package commons.us.blademc.netsys;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class ServiceInfo {

    private final String uuid = UUID.randomUUID().toString();
    private final String name;
    private final String type;
    private final String region;
    private final String branch;
    private String serverServiceID = "NONE";

    public String getID() {
        return String.format("%s-%s-%s",
                region,
                type,
                uuid.substring(0, 16)
        );
    }

    @Override
    public String toString() {
        return String.format("§e%s §a(§e%s§a)", getID(), serverServiceID);
    }
}
