package com.pervasid.rfid.server;

/**
 * *mutter* ... unsigned types ... *mutter* ... java ... *mutter* *curse*.
 */

public class DataUnpacker {
	byte [] data;
	int index;


	public DataUnpacker(byte [] data) {
		index = 0;
		this.data = data;		
	}

	public DataUnpacker(byte [] data, int start_index) {
		index = start_index;
		this.data = data;		
	}


	public long readUint32() throws DataQuantityException {
		final int data_size = 4;
		long out = 0;

		if ((data.length - index) < data_size) {
			throw new DataQuantityException("Not enough data to compete operation");
		}

		out |= (0xff & (int)data[index + 0]) << 0;
		out |= (0xff & (int)data[index + 1]) << 8;
		out |= (0xff & (int)data[index + 2]) << 16;
		out |= (0xff & (int)data[index + 3]) << 24;

		out = out & 0xFFFFFFFFL;

		index += data_size;

		return out;
	}

	public int readUint16() throws DataQuantityException {
		final int data_size = 2;
		int out = 0;

		if ((data.length - index) < data_size) {
			throw new DataQuantityException("Not enough data to compete operation");
		}

		out |= (0xff & (int)data[index + 0]) << 0;
		out |= (0xff & (int)data[index + 1]) << 8;

		out = out & 0xFFFF;

		index += data_size;

		return out;
	}

	public short readUint8() throws DataQuantityException {
		final int data_size = 1;
		short out = 0;

		if ((data.length - index) < data_size) {
			throw new DataQuantityException("Not enough data to compete operation");
		}

		out |= (0xff & (int)data[index + 0]) << 0;
		index += data_size;

		return out;
	}

	public long readInt64() throws DataQuantityException {
		final int data_size = 8;
		long out = 0;
		java.nio.ByteBuffer b;

		if ((data.length - index) < data_size) {
			throw new DataQuantityException("Not enough data to compete operation");
		}

		b = java.nio.ByteBuffer.wrap(data, index, data_size);
		b.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		out = b.getLong();

		return out;
	}

	public short [] readBytes(int n) throws DataQuantityException {
		if ((data.length - index) < n) {
			throw new DataQuantityException("Not enough data to compete operation");
		}

		short [] bytes = new short[n]; 

		if (false /*DEBUG REMOVE*/) {
			System.out.printf("READBYTES\n");
		}

		for (int i = 0; i < n; i++) {
			if (false /*DEBUG REMOVE*/) {
				System.out.printf("%02x\n", data[index + 1]);
			}
			
			int a = ((int)0xff & ((int)data[index + i]));
			bytes[i] = (short)a;
		}
		

		index += n;

		return bytes;
	}
}