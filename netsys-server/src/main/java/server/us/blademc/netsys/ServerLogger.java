package server.us.blademc.netsys;

import commons.us.blademc.netsys.ILoggerHandler;
import dev.waterdog.waterdogpe.logger.Color;
import org.apache.logging.log4j.Logger;

public class ServerLogger implements ILoggerHandler {

    private final Logger pluginLogger = NetSysServer.getInstance().getLogger();

    @Override
    public void info(String message) {
        pluginLogger.info(Color.LIGHT_PURPLE + message);
    }

    @Override
    public void warn(String message) {
        pluginLogger.warn(Color.AQUA + message);
    }

    @Override
    public void debug(String message) {
        pluginLogger.info(Color.DARK_BLUE + "[DEBUG] " + Color.WHITE + message);
    }

    @Override
    public void error(String message) {
        pluginLogger.error(message);
    }
}
