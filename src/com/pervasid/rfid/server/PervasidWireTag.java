package com.pervasid.rfid.server;


public class PervasidWireTag  {
	/* This must be kept in sync with reader/include/wire.h 
	 *
	 * FIXME: generate the constants in this file and wire.h from a common source.
	 *        outside the sope of a short project.
	 */

	/* constants */
	//public static final  int PACKET_SIZE = 45;

	private static final int PHASE_LEN = 8;
	private static final int INV_DATA_LEN = 64;

	/* unpacked data */

    private long timestamp;

	/* diagnostics */
    private long plldivmult;
	private int chan;
	private short [] phase;
	private short switch_state;
	private short rx_switch_state;

	/* inventory packet fields */
	private long ms_ctr;
	private short nb_rssi;
	private short wb_rssi;
	private int ana_ctrl1;
	private int rssi;
	//private int res0;
	private short channel;
	private short rx_phase;

	/* Variable length inventory data (i.e., PC, EPC, and CRC)              */
	/* extended to 128 bits to allow 96bit id as well as padding / CRC etc. */
	private int inv_data_length;
	private int tidLength; 

	/* See data sheets for how to interpret this data. */
	private short [] inv_data;


	public long getTimeStamp() {
		return timestamp;
	}

	public long getPllDivMult() {
		return plldivmult;
	}

	public long getMsCtr() {
		return ms_ctr;
	}

	public short getNbRssi() {
		return nb_rssi;
	}

	public short getWbRssi() {
		return wb_rssi;
	}

	public int getAnaCtrl() {
		return ana_ctrl1;
	}

	public int getRssi() {
		return rssi;
	}

	public short getchannel() {
		return channel;
	}
	public short getrxphase() {
		return rx_phase;
	}


	public short getSwitchState() {
		return switch_state;
	}
	
	public short getRxSwitchState() {
		return rx_switch_state;
	}
	
	public int getChan() {
		return chan;
	}

	public short [] getPhase() {
		return phase;
	}


	public String getEPC() {
		StringBuffer out = new StringBuffer();
		int epcLength = inv_data_length - tidLength - 4;

		if (epcLength > (INV_DATA_LEN - tidLength - 4)) {
			System.out.printf("Warning: epc length exceeds data allocation length\n");
			epcLength = INV_DATA_LEN - tidLength - 4;
		}

		for (int i = 0; i < epcLength; i++) {		
			out.append(String.format("%02x", inv_data[i + 2]));
		}

		return out.toString();
	}


	public String getCRC() {
		StringBuffer out = new StringBuffer();
		int epcLength = inv_data_length - tidLength - 4;

		out.append(String.format("%02x", inv_data[epcLength + 2 + 0]));
		out.append(String.format("%02x", inv_data[epcLength + 2 + 1]));

		return out.toString();
	}

	public String getPC() {
		StringBuffer out = new StringBuffer();

		out.append(String.format("%02x", inv_data[0]));
		out.append(String.format("%02x", inv_data[1]));

		return out.toString();
	}



	public PervasidWireTag(byte []data) throws DataQuantityException {
		DataUnpacker du = new DataUnpacker(data);		

		//assert(data.length == PACKET_SIZE);

		int type   = du.readUint16();


		long timestamp_l  = du.readUint32();
		long timestamp_h  = du.readUint32();

		timestamp = Utils.wince_to_unix_timestamp(timestamp_h, timestamp_l);

		assert(type == PervasidWireHeader.DATA_TYPE_INVENTORY);

		plldivmult   = du.readUint32();
		chan         = du.readUint16();
		phase        = du.readBytes(8);
		switch_state = du.readUint8();
		rx_switch_state = du.readUint8();

		ms_ctr    = du.readUint32();
		nb_rssi   = du.readUint8();
		wb_rssi   = du.readUint8();
		ana_ctrl1 = du.readUint16();
		rssi      = du.readUint16();
		rx_phase   = du.readUint8();
		channel  = du.readUint8();

		inv_data_length = du.readUint16();
		tidLength       = du.readUint16();
		inv_data        = du.readBytes(INV_DATA_LEN);

		if (false /*DEBUG REMOVE*/) {
			for (int i = 0; i < data.length; i++) {
				System.out.printf("%02x", data[i]);
			}
			System.out.printf("\n");
		
			for (int i = 0; i < inv_data.length; i++) {
				System.out.printf("%02x", inv_data[i]);
			}
			System.out.printf("\n");
		}
	}
	
}
