package com.pervasid.rfid.server;

import java.nio.ByteBuffer;

public class PervasidWireReplyHeader implements PervasidWirePackets {
	/* constants */
	public static final  int RETURN_OK   = 0;
	public static final  int RETURN_FAIL = 1;

	public static final int REPLY_HEADER_SIZE = 12;

	/* unpacked data */
	private int return_value;
	private int command;
	protected int data_length;

	public byte [] getBytes() {
		byte [] bytes = new byte[REPLY_HEADER_SIZE];
		ByteBuffer b = ByteBuffer.wrap(bytes);
		b.order(java.nio.ByteOrder.LITTLE_ENDIAN);

		b.putInt(return_value);
		b.putInt(data_length);
		b.putInt(command);

		return bytes;
	}

	public int getDataLength() {
		return data_length;
	}

	public void setCommand(int command) {
		this.command = command;
	}


	public PervasidWireReplyHeader(int return_value, int command, int data_length) {
		this.return_value   = return_value;
		this.command = command;
		this.data_length = data_length + REPLY_HEADER_SIZE;
	}
	
}