package com.pervasid.rfid.server;

import java.nio.ByteBuffer;

public class PervasidWireHeader {
	/* This must be kept in sync with reader/include/wire.h 
	 *
	 * FIXME: generate the constants in this file and wire.h from a common source.
	 *        outside the sope of a short project.
	 */

	/* constants */
	public static final  int HEADER_SIZE = 14;
	private static final  int HWADDR_SIZE = 8;

	/* flags */
	private static final int FLAGS_COMPRESSED = (1 << 0);

	/* PACKET TYPES */
	public static final int DATA_TYPE_INVENTORY = 0;
	public static final int DATA_TYPE_END_DIAGS = 1;
	public static final int DATA_TYPE_ASSET_EVENT = 2;
	public static final int DATA_TYPE_BEGIN_DIAGS = 3;
	public static final int DATA_TYPE_COMMAND_END = 4;

	//is this still right?
	public static final int WIRE_PACKET_SIZE = 106;

	public enum TransmissionType { DATA, PING, REQUEST , MAC_REQUEST};
	private static final PervasidWireHeader.TransmissionType[] packetTypeVals = PervasidWireHeader.TransmissionType.values();

	/* unpacked data */
	private long flags;
	private int  transmission_type;
	private short[] hwaddr;

	boolean isCompressed() {
		return (flags & FLAGS_COMPRESSED) > 0;
	}

	TransmissionType getTransmissionType() {
		return packetTypeVals[transmission_type];
	}

	public long getHwaddr() {
		long a = 
			((0xffL & (long)hwaddr[0]) <<  0) |
			((0xffL & (long)hwaddr[1]) <<  8) |
			((0xffL & (long)hwaddr[2]) << 16) |
			((0xffL & (long)hwaddr[3]) << 24) |
			((0xffL & (long)hwaddr[4]) << 32) |
			((0xffL & (long)hwaddr[5]) << 40) |
			((0xffL & (long)hwaddr[6]) << 48) |
			((0xffL & (long)hwaddr[7]) << 56) ;

		return a;
	}

	public String getHwaddrAsString() {
		StringBuffer out = new StringBuffer();

		for (int i = 0; i < HWADDR_SIZE; i++) {		
			out.append(String.format("%02x", hwaddr[i]));
		}

		return out.toString();
	}


	public PervasidWireHeader(byte []data) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data);

		assert(data.length == HEADER_SIZE);

		flags = du.readUint32();
		transmission_type = du.readUint16();
		hwaddr = du.readBytes(HWADDR_SIZE);

	}
	
}