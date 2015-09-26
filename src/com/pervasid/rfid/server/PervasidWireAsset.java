package com.pervasid.rfid.server;

public class PervasidWireAsset  {
	/* This must be kept in sync with reader/include/wire.h 
	 *
	 * FIXME: generate the constants in this file and wire.h from a common source.
	 *        outside the sope of a short project.
	 */

	/* constants */
	private static final int MAX_EPC_DATA_LEN = 64;

	/* unpacked data */
    private long timestamp;
    private long event_type;
	private int epcLength;
	private short [] epc_data;


	public long getTimeStamp() {
		return timestamp;
	}

	public long getEventType() {
		return event_type;
	}

	public String getEPC() {
		StringBuffer out = new StringBuffer();
		long epcLength = this.epcLength;

		if (epcLength > (MAX_EPC_DATA_LEN)) {
			System.out.printf("Warning: epc length exceeds data allocation length\n");
			epcLength = MAX_EPC_DATA_LEN;
		}

		for (int i = 0; i < epcLength; i++) {		
			out.append(String.format("%02x", epc_data[i]));
		}

		return out.toString();
	}


	public PervasidWireAsset(byte []data) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data);		

		int type   = du.readUint16();

		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		timestamp = Utils.wince_to_unix_timestamp(timestamp_h, timestamp_l);

		assert(type == PervasidWireHeader.DATA_TYPE_ASSET_EVENT);

		event_type = du.readUint32();
		epcLength = du.readUint16();
		epc_data  = du.readBytes(MAX_EPC_DATA_LEN);
	}
	
}
