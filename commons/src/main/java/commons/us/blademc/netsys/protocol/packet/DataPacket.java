package commons.us.blademc.netsys.protocol.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class DataPacket {
    private final byte pid;

    public abstract void encode(ByteArrayDataOutput output);
    public abstract void decode(ByteArrayDataInput input);
}
