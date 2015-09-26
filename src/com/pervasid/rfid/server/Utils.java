package com.pervasid.rfid.server;

public class Utils {

	public static long wince_to_unix_timestamp(long timestamp_h, long timestamp_l) {
		long timestamp;

		/* convert a wince time stamp, but in *milliseconds* rather
		 * than nanoseconds
		 */
		
		timestamp = (timestamp_h << 32) | timestamp_l;
		timestamp -= 11644473600000l;

		return timestamp;
	}

}