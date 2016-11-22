
package com.pervasid.rfid.experiment;

import com.pervasid.rfid.server.InventorySettings;

public class ExperimentSettings extends InventorySettings{
	public long expt_number;
	public long reader_id;
	public long duration;
	
	public ExperimentSettings(){
		super();
		
	}
	
	public ExperimentSettings(long expt_number, long reader_id, long duration,
							 long inventory_period, long start_q,
							 long min_q, long max_q, long retry_count, 
							 long threshold_multiplier, long power_level, 
							 int session, int target, long profile, 
							 long switch_states, long freq_dwell_time,
							 int mode, int asset_period, int previous_states, 
							 int trans_packets, int hop_rate) {
		
		super(inventory_period, start_q,min_q, max_q, retry_count, threshold_multiplier, power_level, session, target, profile,	
				switch_states, freq_dwell_time, mode, asset_period, previous_states, trans_packets, hop_rate);
		this.expt_number = expt_number;
		this.reader_id = reader_id;
		this.duration = duration;
	}
	
}

