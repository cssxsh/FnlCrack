package cssxsh.fnlfont.ByteTools;

import java.io.*;

public class FnaInputStream extends DataInputStream implements DataInput {
    public FnaInputStream(InputStream in) {
        super(in);
    }
    public final int readUnsignedShortRight () throws IOException {
        int ch0 = in.read();
        int ch1 = in.read();
        if ((ch0 | ch1) < 0) {
            throw new EOFException();
        }
        return ((ch0 << 0x00) | (ch1 << 0x08));
    }
    public final long readUnsignedIntRight () throws IOException {
        long ch0 = in.read();
        long ch1 = in.read();
        long ch2 = in.read();
        long ch3 = in.read();
        if ((ch0 | ch1 | ch2 | ch3) < 0) {
            throw new EOFException();
        }
        return ((ch0 << 0x00) | (ch1 << 0x08) | (ch2 << 0x10) | (ch3 << 0x18));
    }
}
