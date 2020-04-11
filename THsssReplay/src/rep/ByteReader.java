package rep;

public class ByteReader {
	public byte[] fileByte;
	public int position = 0;

	public ByteReader(byte[] bs) {
		fileByte = bs;
	}

	public int readUShort() {
        return 0xffff & (fileByte[position++] & 0xff | (fileByte[position++] & 0xff) << 8);
    }
}

