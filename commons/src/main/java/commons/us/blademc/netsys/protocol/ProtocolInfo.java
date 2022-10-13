package commons.us.blademc.netsys.protocol;

public interface ProtocolInfo {
    byte
            OPEN_CLIENT_CONNECTION_REQUEST_PACKET = 0X01,
            OPEN_CLIENT_CONNECTION_RESPONSE_PACKET = 0x02,
            CLOSE_CLIENT_CONNECTION_PACKET = 0x03,
            SERVER_DISCONNECT_PACKET = 0x04,
            CLIENT_UPDATE_DATA_PACKET = 0x05;
}
