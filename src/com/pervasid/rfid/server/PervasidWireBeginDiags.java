package com.pervasid.rfid.server;

public class PervasidWireBeginDiags {
	/* This must be kept in sync with reader/include/wire.h 
	 *
	 * FIXME: generate the constants in this file and wire.h from a common source.
	 *        outside the sope of a short project.
	 */

	/* constants */
	private static final int PHASE_LEN = 8;
	private static final int INV_DATA_LEN = 16;

	/* unpacked data */

	long timestamp;

	/* diagnostics */
   private long ms_ctr;
   private long sing_params;
   
   private short [] phase;
   private short switch_state;
   private short rx_switch_state;
   

	public long getTimeStamp() {
		return timestamp;
	}

	public long getMsCtr() {
		return ms_ctr;
	}

	public long getSingParams() {
		return sing_params;
	}
	
	public short getSwitchState() {
		return switch_state;
	}
	
	public short getRxSwitchState() {
		return rx_switch_state;
	}

	public short [] getPhase() {
		return phase;
	}


	public PervasidWireBeginDiags(byte []data) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data);		

		int type   = du.readUint16();
		assert(type == PervasidWireHeader.DATA_TYPE_BEGIN_DIAGS);

		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		timestamp = (timestamp_h << 32) | timestamp_l;
		timestamp /= 10000;
		timestamp -= 11644473600000l;

		ms_ctr = du.readUint32();
		sing_params = du.readUint32();
		phase        = du.readBytes(8);
		switch_state = du.readUint8();
		rx_switch_state = du.readUint8();
	}


	public PervasidWireBeginDiags(byte []data, int start_index) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data, start_index);		

		int type   = du.readUint16();
		assert(type == PervasidWireHeader.DATA_TYPE_BEGIN_DIAGS);

		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		timestamp = Utils.wince_to_unix_timestamp(timestamp_h, timestamp_l);

		ms_ctr = du.readUint32();
		sing_params = du.readUint32();
		phase        = du.readBytes(8);
		switch_state = du.readUint8();
		rx_switch_state = du.readUint8();
	}
	

}
