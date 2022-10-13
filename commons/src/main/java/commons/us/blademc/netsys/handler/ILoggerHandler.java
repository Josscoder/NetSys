package commons.us.blademc.netsys.handler;

public interface ILoggerHandler {
    void info(String message);
    void warn(String message);
    void debug(String message);
    void error(String message);
}
