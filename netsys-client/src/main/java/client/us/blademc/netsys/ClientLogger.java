package client.us.blademc.netsys;

import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.TextFormat;
import commons.us.blademc.netsys.ILoggerHandler;

public class ClientLogger implements ILoggerHandler {

    private final PluginLogger pluginLogger = NetSysClient.getInstance().getLogger();

    @Override
    public void info(String message) {
        pluginLogger.info(TextFormat.AQUA + message);
    }

    @Override
    public void warn(String message) {
        pluginLogger.warning(TextFormat.GOLD + message);
    }

    @Override
    public void debug(String message) {
        pluginLogger.info(TextFormat.DARK_BLUE + "[DEBUG] " + TextFormat.WHITE + message);
    }
}
