package utils.datastructures;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class WebSocketFrame {
    private byte opcode;
    private boolean isMasked;
    private byte[] mask;
    //Unsigned byte
    private byte length;

    //Unsigned short
    private int lengthEXT1;
    private long lengthEXT2;
    private byte[] payload;
    public WebSocketFrame(InputStream in) {
        try {
            byte[] buf = new byte[2];
            in.read(buf);
            /*
            if (in.read(buf) != 2) {
                throw new RuntimeException(new Exception());
            }

             */
            opcode = buf[0];
            isMasked = buf[1] < 0;
            length = (byte)(buf[1] & 127);
            if (length <= 125) {
                payload = new byte[length];
            } else if (length == 126) {
                lengthEXT1 = in.read() << 8;
                lengthEXT1 = lengthEXT1 | in.read();
                payload = new byte[lengthEXT1];
            } else {
                byte[] b = new byte[8];
                if (in.read(b, 0, b.length) != 8) {
                    throw new RuntimeException(new Exception());
                }
                ByteBuffer bb = ByteBuffer.wrap(b);
                lengthEXT2 = bb.getLong();
                payload = new byte[(int) lengthEXT2];
            }
            if (isMasked) {
                mask = new byte[4];
                if (in.read(mask, 0, mask.length) != 4) {
                    throw new RuntimeException(new Exception());
                }
            }
            in.read(payload,0,payload.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public WebSocketFrame(byte[] payload) {
        opcode = (byte) 0b10000001;
        if (payload.length < 126) {
            length = (byte) payload.length;
        } else if (payload.length < Short.MAX_VALUE){
            length = 126;
            lengthEXT1 = payload.length;
        } else {
            length = 127;
            lengthEXT2 = payload.length;
        }
        this.payload = payload;
    }
    public void writeTo(OutputStream out) {
        try {
            out.write(opcode);
            out.write(length);
            if (length == 126) {
                out.write((byte) (lengthEXT1 >> 8));
                out.write((byte) lengthEXT1);
            } else if (length == 127) {
                out.write((int) (lengthEXT2 >> 32));
                out.write((int) lengthEXT2);
            }
            out.write(payload);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public byte[] getPayload() {
        if (isMasked) {
            for(int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (payload[i] ^ mask[i % 4]);
            }
        }
        return payload;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(opcode).append(" | ").append(length);
        sb.append(isMasked ? "Masked: " : "");
        for (byte b : payload) {
            sb.append(b).append(" | ");
        }
        return sb.toString();
    }
}
