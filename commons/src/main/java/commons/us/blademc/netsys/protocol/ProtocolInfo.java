package commons.us.blademc.netsys.protocol;

public interface ProtocolInfo {
    byte
            OPEN_CONNECTION_REQUEST_PACKET = 0X01,
            OPEN_CONNECTION_RESPONSE_PACKET = 0x02,
            CLOSE_CONNECTION_PACKET = 0x03,
            SERVER_DISCONNECT_PACKET = 0x04;
}
