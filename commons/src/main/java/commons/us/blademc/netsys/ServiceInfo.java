package commons.us.blademc.netsys;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ServiceInfo {

    private final String uuid = UUID.randomUUID().toString();
    private final String name;
    private final String type;
    private final String region;
    private final String branch;

    @Override
    public String toString() {
        return "Service unique id: " +
                uuid + ", " +
                "Service name: " +
                name + ", " +
                "Service type: " +
                type + ", " +
                "Service region: " +
                region + ", " +
                "Service branch: " +
                branch;
    }
}
