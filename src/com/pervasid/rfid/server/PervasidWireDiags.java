package com.pervasid.rfid.server;

public class PervasidWireDiags {
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
    private long querys;
    private long rn16rcv;
    private long rn16to;
    private long epcto;
    private long good_reads;
    private long crc_failures;

	public long getTimeStamp() {
		return timestamp;
	}

	public long getMsCtr() {
		return ms_ctr;
	}

	public long getQuerys() {
		return querys;
	}

	public long getRn16Rcv() {
		return rn16rcv;
	}

	public long getRn16To() {
		return rn16to;
	}

	public long getEpcTo() {
		return epcto;
	}

	public long getGoodReads() {
		return good_reads;
	}

	public long getCrcFailures() {
		return crc_failures;
	}


	public PervasidWireDiags(byte []data) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data);		

		int type   = du.readUint16();
		assert(type == PervasidWireHeader.DATA_TYPE_END_DIAGS);

		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		timestamp = (timestamp_h << 32) | timestamp_l;
		timestamp /= 10000;
		timestamp -= 11644473600000l;

		ms_ctr = du.readUint32();
		querys = du.readUint32();
		rn16rcv = du.readUint32();
		rn16to = du.readUint32();
		epcto = du.readUint32();
		good_reads = du.readUint32();;
		crc_failures = du.readUint32();;
	}


	public PervasidWireDiags(byte []data, int start_index) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data, start_index);		

		int type   = du.readUint16();
		assert(type == PervasidWireHeader.DATA_TYPE_END_DIAGS);

		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		timestamp = Utils.wince_to_unix_timestamp(timestamp_h, timestamp_l);

		ms_ctr = du.readUint32();
		querys = du.readUint32();
		rn16rcv = du.readUint32();
		rn16to = du.readUint32();
		epcto = du.readUint32();
		good_reads = du.readUint32();;
		crc_failures = du.readUint32();;
	}
	
}
