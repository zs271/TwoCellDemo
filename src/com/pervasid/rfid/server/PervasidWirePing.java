package com.pervasid.rfid.server;

public class PervasidWirePing {
	/* This must be kept in sync with reader/include/wire.h 
	 *
	 * FIXME: generate the constants in this file and wire.h from a common source.
	 *        outside the sope of a short project.
	 */

	/* unpacked data */
	long timestamp;
    private int  status;

	public long getTimeStamp() {
		return timestamp;
	}

	public long getStatus() {
		return status;
	}


	public PervasidWirePing(byte []data) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data);		
		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		timestamp = Utils.wince_to_unix_timestamp(timestamp_h, timestamp_l);

		status = du.readUint16();
	}

}
