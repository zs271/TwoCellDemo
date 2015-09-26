package com.pervasid.rfid.server;

import java.nio.ByteBuffer;

public class SettingsReply 	
	extends PervasidWireReplyHeader
	 { 
		 private long inventory_period;
		 private long start_q;	
		 private long min_q;
		 private long max_q;
		 private long retry_count;   
		 private long threshold_multiplier;
		 private long power_level;
		 private int session;
		 private int target;
		 private long profile;
		 private long switch_states;
		 private long freq_dwell_time;
		 private int mode;
		 private int asset_period;
		 private int prev_states;
		 private int trans_packets;
		 private int hop_rate;

	private static int PACKET_SIZE = 68;

	public SettingsReply(int return_value, InventorySettings settings) {
		super(return_value, 0, PACKET_SIZE);

		this.inventory_period = settings.inventory_period;
		this.start_q = settings.start_q;	
		this.min_q = settings.min_q;
		this.max_q = settings.max_q;
		this.retry_count = settings.retry_count;   
		this.threshold_multiplier = settings.threshold_multiplier;
		this.power_level = settings.power_level;
		this.session = settings.session;
		this.target = settings.target;
		this.profile = settings.profile;
		this.switch_states = settings.switch_states;
		this.freq_dwell_time = settings.freq_dwell_time;
		this.mode = settings.mode;
		this.asset_period = settings.asset_period;
		this.prev_states = settings.previous_states;
		this.trans_packets = settings.trans_packets;
		this.hop_rate = settings.hop_rate;
	}

	public byte [] getBytes() {
		byte [] bytes = new byte[super.getDataLength()];
		ByteBuffer b = ByteBuffer.wrap(bytes);
		b.order(java.nio.ByteOrder.LITTLE_ENDIAN);

		b.put(super.getBytes());
		b.putInt((int) inventory_period);
		b.putInt((int) start_q);	
		b.putInt((int) min_q);
		b.putInt((int) max_q);
		b.putInt((int) retry_count);   
		b.putInt((int) threshold_multiplier);
		b.putInt((int) power_level);
		b.putInt(session);
		b.putInt(target);
		b.putInt((int) profile);
		b.putInt((int) switch_states);
		b.putInt((int) freq_dwell_time);
		b.putInt(mode);
		b.putInt(asset_period);
		b.putInt(prev_states);
		b.putInt(trans_packets);
		b.putInt(hop_rate);

		return bytes;	
	}

	
};