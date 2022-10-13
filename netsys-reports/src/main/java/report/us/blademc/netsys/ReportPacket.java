package report.us.blademc.netsys;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import commons.us.blademc.netsys.protocol.packet.DataPacket;

public class ReportPacket extends DataPacket {

    public String uuid;
    public String sender;
    public String target;
    public String reason;
    public String server;

    public ReportPacket() {
        super(NetSysReports.REPORT_PACKET);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeUTF(uuid);
        output.writeUTF(sender);
        output.writeUTF(target);
        output.writeUTF(reason);
        output.writeUTF(server);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        uuid = input.readUTF();
        sender = input.readUTF();
        target = input.readUTF();
        reason = input.readUTF();
        server = input.readUTF();
    }
}
