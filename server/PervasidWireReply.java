package com.pervasid.rfid.server;

import java.nio.ByteBuffer;

public class PervasidWireReply extends PervasidWireReplyHeader {
	/* This must be kept in sync with reader/include/wire.h 
	 *
	 * FIXME: generate the constants in this file and wire.h from a common source.
	 *        outside the sope of a short project.
	 */

	/* constants */
	private static final int FLAGS_NONE              = 0;
	private static final int FLAGS_SETTING_AVAILABLE = 0;

	/* unpacked data */
	private byte [] data;

	public byte [] getBytes() {
		byte [] bytes = new byte[REPLY_HEADER_SIZE + data_length];
		ByteBuffer b = ByteBuffer.wrap(bytes);
		b.order(java.nio.ByteOrder.LITTLE_ENDIAN);

		b.put(super.getBytes());

		if (data != null) {
			b.put(data);
		}

		return bytes;
	}

	public PervasidWireReply(int return_value, int command, byte [] data) {
		super(return_value, command, data.length);
		this.data = data;

		if (data != null)
			this.data_length = data.length;
	}
	
}