package client.us.blademc.netsys.logger;

import client.us.blademc.netsys.NetSysClient;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.TextFormat;
import commons.us.blademc.netsys.ILoggerHandler;

public class ClientLogger implements ILoggerHandler {

    private final PluginLogger pluginLogger = NetSysClient.getInstance().getLogger();

    @Override
    public void info(String message) {
        pluginLogger.info(TextFormat.GREEN + message);
    }

    @Override
    public void warn(String message) {
        pluginLogger.warning(TextFormat.AQUA + message);
    }

    @Override
    public void debug(String message) {
        pluginLogger.info(TextFormat.DARK_BLUE + "[DEBUG] " + TextFormat.WHITE + message);
    }

    @Override
    public void error(String message) {
        pluginLogger.error(message);
    }
}
