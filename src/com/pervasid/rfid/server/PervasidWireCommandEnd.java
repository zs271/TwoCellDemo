package com.pervasid.rfid.server;

public class PervasidWireCommandEnd {
	
	/* This must be kept in sync with reader/include/wire.h 
	 *
	 * FIXME: generate the constants in this file and wire.h from a common source.
	 *        outside the sope of a short project.
	 */

	/* constants */
	

	/* unpacked data */

	long timestamp;

	/* diagnostics */
  private long ms_ctr;
  private long status;
  

	public long getTimeStamp() {
		return timestamp;
	}

	public long getMsCtr() {
		return ms_ctr;
	}

	public long getStatus() {
		return status;
	}
	
	

	public PervasidWireCommandEnd(byte []data) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data);		

		int type   = du.readUint16();
		assert(type == PervasidWireHeader.DATA_TYPE_COMMAND_END);

		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		//timestamp = (timestamp_h << 32) | timestamp_l;
		//timestamp /= 10000;
		//timestamp -= 11644473600000l;
		
		timestamp = Utils.wince_to_unix_timestamp(timestamp_h, timestamp_l);

		ms_ctr = du.readUint32();
		status = du.readUint32();
		}


	public PervasidWireCommandEnd(byte []data, int start_index) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data, start_index);		

		int type   = du.readUint16();
		assert(type == PervasidWireHeader.DATA_TYPE_COMMAND_END);

		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		timestamp = Utils.wince_to_unix_timestamp(timestamp_h, timestamp_l);

		ms_ctr = du.readUint32();
		status = du.readUint32();
	}
	


}
