package commons.us.blademc.netsys.redis;

@FunctionalInterface
public interface MessageCallback {
    void onMessage(String[] args);
}
