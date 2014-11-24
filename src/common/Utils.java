package common;

import java.nio.ByteBuffer;

public class Utils {

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static byte[] intToBytes(int x) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE);
		buffer.putInt(0,x);
		return buffer.array();
	}
	
	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	public static long[] bytesToLongs(byte[] bytes) {
		long[] longBuf = new long[bytes.length / Long.SIZE];
		int seek = 0;
		int count = 0;
		while (seek < bytes.length) {
			byte[] tempBuf = new byte[Long.SIZE];
			System.arraycopy(bytes, seek, tempBuf, 0, Long.SIZE);
			longBuf[count++] = bytesToLong(tempBuf);
			seek += Long.SIZE;
		}
		return longBuf;
	}

	public static int bytesToInt(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();// need flip
		return buffer.getInt();
	}

	public static int[] bytesToInts(byte[] bytes) {
		int[] intBuf = new int[bytes.length / Integer.SIZE];
		int seek = 0;
		int count = 0;
		while (seek < bytes.length) {
			byte[] tempBuf = new byte[Integer.SIZE];
			System.arraycopy(bytes, seek, tempBuf, 0, Integer.SIZE);
			intBuf[count++] = bytesToInt(tempBuf);
			seek += Integer.SIZE;
		}
		return intBuf;
	}
	public static byte[] longsToBytes(long[] longs) {
		byte[] bytes = new byte[Long.SIZE * longs.length];
		int seek = 0;
		for (long num : longs) {
			byte[] byteTmp = longToBytes(num);
			System.arraycopy(byteTmp, 0, bytes, seek, Long.SIZE);
			seek += Long.SIZE;
		}
		return bytes;
	}

	//for test.
	public static void main(String[] args) {
		long[] longs = { 1L, 0L, -2L, 1000L, 1234567890L, 12345678909876L };
		byte[] buf = longsToBytes(longs);
		long[] longs2 = bytesToLongs(buf);
		for (long num : longs2)
			System.out.println(num);
	}
}
