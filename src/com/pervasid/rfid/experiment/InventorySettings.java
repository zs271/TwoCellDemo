
package com.pervasid.rfid.experiment;

public class InventorySettings {
	public long inventory_period;
	public long start_q;	
	public long min_q;
	public long max_q;
	public long retry_count;   
	public long threshold_multiplier;
	public long power_level;
	public int session;
	public int target;
	public long profile;
	public long switch_states;
	public long freq_dwell_time;
	public int mode;
	public int asset_period;
	public int previous_states;
	public int trans_packets;
	public int hop_rate;
	
	public InventorySettings(){}

	public InventorySettings(long inventory_period, long start_q,
							 long min_q, long max_q, long retry_count, 
							 long threshold_multiplier, long power_level, 
							 int session, int target, long profile, 
							 long switch_states, long freq_dwell_time,
							 int mode, int asset_period, int previous_states, 
							 int trans_packets, int hop_rate) {
		this.inventory_period = inventory_period;
		this.start_q = start_q;	
		this.min_q = min_q;
		this.max_q = max_q;
		this.retry_count = retry_count;   
		this.threshold_multiplier = threshold_multiplier;
		this.power_level = power_level;
		this.session = session;
		this.target = target;
		this.profile = profile;
		this.switch_states = switch_states;
		this.freq_dwell_time = freq_dwell_time;
		this.mode = mode;
		this.asset_period = asset_period;
		this.previous_states = previous_states;
		this.trans_packets = trans_packets;
		this.hop_rate = hop_rate;
	}
}
