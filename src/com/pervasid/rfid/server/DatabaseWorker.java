package com.pervasid.rfid.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

import com.pervasid.rfid.server.DataUnpacker;

public class DatabaseWorker {
	private DatabaseSource ds;

	public DatabaseWorker(PervasidServerSettings settings)
		throws java.sql.SQLException {		

		this.ds = DatabaseSource.getInstance(settings);
	}

	public void storeTag(PervasidWireHeader header, PervasidWireTag tag) 
		throws SQLException {

		Connection con = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;

		try {
			int i = 1;

			con = ds.getConnection();
			
			pstmt = con.prepareStatement("INSERT INTO tag_reads (reader_id, time_stamp, tag_id, ms_ctr, nb_rssi, wb_rssi, ana_ctrl, rssi, res0, switch_state, rx_switch_state, phase0, phase1, phase2, phase3, phase4, phase5, phase6, phase7, channel, processed) VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			pstmt.setLong(i++, header.getHwaddr());
			pstmt.setLong(i++, tag.getTimeStamp());
			pstmt.setString(i++, tag.getEPC());
			pstmt.setLong(i++, tag.getMsCtr());
			pstmt.setShort(i++, tag.getNbRssi());
			pstmt.setShort(i++, tag.getWbRssi());
			pstmt.setInt(i++, tag.getAnaCtrl());
			pstmt.setInt(i++, (short)tag.getRssi());
			pstmt.setShort(i++, tag.getrxphase());
			pstmt.setShort(i++, tag.getSwitchState());
			pstmt.setShort(i++, tag.getRxSwitchState());
			pstmt.setShort(i++, tag.getPhase()[0]);
			pstmt.setShort(i++, tag.getPhase()[1]);
			pstmt.setShort(i++, tag.getPhase()[2]);
			pstmt.setShort(i++, tag.getPhase()[3]);
			pstmt.setShort(i++, tag.getPhase()[4]);
			pstmt.setShort(i++, tag.getPhase()[5]);
			pstmt.setShort(i++, tag.getPhase()[6]);
			pstmt.setShort(i++, tag.getPhase()[7]);
			pstmt.setShort(i++, tag.getchannel());
			pstmt.setBoolean(i++, false);
			
			pstmt.executeUpdate();

            pstmt.close();
			
            i=1;
			pstmt2 = con.prepareStatement("INSERT INTO tag_reads_simple (reader_id, time_stamp, tag_id, ms_ctr, nb_rssi, wb_rssi, ana_ctrl, rssi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			
			pstmt2.setLong(i++, header.getHwaddr());
			pstmt2.setLong(i++, tag.getTimeStamp());
			pstmt2.setString(i++, tag.getEPC());
			pstmt2.setLong(i++, tag.getMsCtr());
			pstmt2.setShort(i++, tag.getNbRssi());
			pstmt2.setShort(i++, tag.getWbRssi());
			pstmt2.setInt(i++, tag.getAnaCtrl());
			pstmt2.setInt(i++, (short)tag.getRssi());
            
            pstmt2.executeUpdate();

            pstmt2.close();

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
	
	public void storeTagVector(PervasidWireHeader header, Vector<PervasidWireTag> vtag) 
	throws SQLException {

	Connection con = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;

    int n;
    
	try {
		con = ds.getConnection();
		
		pstmt = con.prepareStatement("INSERT INTO tag_reads (reader_id, time_stamp, tag_id, ms_ctr, nb_rssi, wb_rssi, ana_ctrl, rssi, res0, switch_state,rx_switch_state, phase0, phase1, phase2, phase3, phase4, phase5, phase6, phase7, channel, processed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		pstmt2 = con.prepareStatement("INSERT INTO tag_reads_simple (reader_id, time_stamp, tag_id, ms_ctr, nb_rssi, wb_rssi, ana_ctrl, rssi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		
		for(PervasidWireTag tag : vtag)
		{
			int i = 1;
	
				pstmt.setLong(i++, header.getHwaddr());
			pstmt.setLong(i++, tag.getTimeStamp());
			pstmt.setString(i++, tag.getEPC());
			pstmt.setLong(i++, tag.getMsCtr());
			pstmt.setShort(i++, tag.getNbRssi());
			pstmt.setShort(i++, tag.getWbRssi());
			pstmt.setInt(i++, tag.getAnaCtrl());
			pstmt.setInt(i++, (short)tag.getRssi());
			pstmt.setShort(i++, tag.getrxphase());
			pstmt.setShort(i++, tag.getSwitchState());
			pstmt.setShort(i++, tag.getRxSwitchState());
			pstmt.setShort(i++, tag.getPhase()[0]);
			pstmt.setShort(i++, tag.getPhase()[1]);
			pstmt.setShort(i++, tag.getPhase()[2]);
			pstmt.setShort(i++, tag.getPhase()[3]);
			pstmt.setShort(i++, tag.getPhase()[4]);
			pstmt.setShort(i++, tag.getPhase()[5]);
			pstmt.setShort(i++, tag.getPhase()[6]);
			pstmt.setShort(i++, tag.getPhase()[7]);
			pstmt.setShort(i++, tag.getchannel());
			pstmt.setBoolean(i++, false);
			
			pstmt.addBatch();		
			
	        i=1;
			pstmt2.setLong(i++, header.getHwaddr());
			pstmt2.setLong(i++, tag.getTimeStamp());
			pstmt2.setString(i++, tag.getEPC());
			pstmt2.setLong(i++, tag.getMsCtr());
			pstmt2.setShort(i++, tag.getNbRssi());
			pstmt2.setShort(i++, tag.getWbRssi());
			pstmt2.setInt(i++, tag.getAnaCtrl());
			pstmt2.setInt(i++, (short)tag.getRssi());
			
			pstmt2.addBatch();
		}
		pstmt.executeBatch();
		
        pstmt.close();
        
        pstmt2.executeBatch();

        pstmt2.close();

	} finally {
		if (con != null) {
			con.close();
		}
	}
}
	
	

	public void storeAssetEvent(PervasidWireHeader header, PervasidWireAsset asset) 
		throws SQLException {

		Connection con = null;
        PreparedStatement pstmt = null;

		try {
			int i = 1;

			con = ds.getConnection();
			
			pstmt = con.prepareStatement("INSERT INTO asset_events (asset_id, time_stamp, reader_id, event) VALUES (?, ?, ?, ?)");

			pstmt.setString(i++, asset.getEPC());
			pstmt.setLong(i++, asset.getTimeStamp());
			pstmt.setLong(i++, header.getHwaddr());
			pstmt.setLong(i++, asset.getEventType());

			pstmt.executeUpdate();

            pstmt.close();

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	public void storeStatus(PervasidWireHeader header, PervasidWirePing ping) 
		throws SQLException {

		Connection con = null;
        PreparedStatement pstmt = null;

		try {
			int i = 1;

			con = ds.getConnection();
			
			pstmt = con.prepareStatement("INSERT INTO reader_status (reader_id, time_stamp, status_code) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE  time_stamp=?, status_code=?");

			pstmt.setLong(i++, header.getHwaddr());
			pstmt.setLong(i++, ping.getTimeStamp());
			pstmt.setLong(i++, ping.getStatus());
			pstmt.setLong(i++, ping.getTimeStamp());
			pstmt.setLong(i++, ping.getStatus());
			
			pstmt.executeUpdate();

            pstmt.close();

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
	
	public void updateStatus(PervasidWireHeader header,int code, long time_stamp) 
	throws SQLException {

	Connection con = null;
    PreparedStatement pstmt = null;

	try {
		int i = 1;

		con = ds.getConnection();
		
		pstmt = con.prepareStatement("UPDATE reader_status SET status_code=?, time_stamp=? WHERE reader_id=?");

		pstmt.setInt(i++,code);
		pstmt.setLong(i++, time_stamp);
		pstmt.setLong(i++, header.getHwaddr());		
		pstmt.executeUpdate();

        pstmt.close();

	} finally {
		if (con != null) {
			con.close();
		}
	}
}

	public void storeDiags(PervasidWireHeader header, PervasidWireDiags diags) 
		throws SQLException {

		Connection con = null;
        PreparedStatement pstmt = null;

		try {
			int i = 1;

			con = ds.getConnection();			
			pstmt = con.prepareStatement("INSERT INTO round_diags (reader_id, time_stamp, ms_ctr, querys, rn16_rx, rn16_to, epc_to, good_reads, crc_failures) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");


			pstmt.setLong(i++, header.getHwaddr());
			pstmt.setLong(i++, diags.getTimeStamp());
			pstmt.setLong(i++, diags.getMsCtr());
			pstmt.setLong(i++, diags.getQuerys());
			pstmt.setLong(i++, diags.getRn16Rcv());
			pstmt.setLong(i++, diags.getRn16To());
			pstmt.setLong(i++, diags.getEpcTo());
			pstmt.setLong(i++, diags.getGoodReads());
			pstmt.setLong(i++, diags.getCrcFailures());
		
			
			pstmt.executeUpdate();

            pstmt.close();

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
	
	public void storeDiagsVector(PervasidWireHeader header, Vector<PervasidWireDiags> vdiags) 
	throws SQLException {

	Connection con = null;
    PreparedStatement pstmt = null;

	try {
		con = ds.getConnection();			
		pstmt = con.prepareStatement("INSERT INTO round_diags (reader_id, time_stamp, ms_ctr, querys, rn16_rx, rn16_to, epc_to, good_reads, crc_failures) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");


		for(PervasidWireDiags diags : vdiags)
		{
		int i = 1;
		
		pstmt.setLong(i++, header.getHwaddr());
		pstmt.setLong(i++, diags.getTimeStamp());
		pstmt.setLong(i++, diags.getMsCtr());
		pstmt.setLong(i++, diags.getQuerys());
		pstmt.setLong(i++, diags.getRn16Rcv());
		pstmt.setLong(i++, diags.getRn16To());
		pstmt.setLong(i++, diags.getEpcTo());
		pstmt.setLong(i++, diags.getGoodReads());
		pstmt.setLong(i++, diags.getCrcFailures());
		pstmt.addBatch();
		}
		
		pstmt.executeBatch();

        pstmt.close();

	} finally {
		if (con != null) {
			con.close();
		}
	}
}

	public void storeBeginDiags(PervasidWireHeader header, PervasidWireBeginDiags diags) 
	throws SQLException {

	Connection con = null;
    PreparedStatement pstmt = null;

	try {
		int i = 1;

		con = ds.getConnection();			
		pstmt = con.prepareStatement("INSERT INTO round_begin_diags (reader_id, time_stamp, ms_ctr, sing_params, switch_state, rx_switch_state, phase0, phase1, phase2, phase3, phase4, phase5, phase6, phase7) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? )");


		pstmt.setLong(i++, header.getHwaddr());
		pstmt.setLong(i++, diags.getTimeStamp());
		pstmt.setLong(i++, diags.getMsCtr());
		pstmt.setLong(i++, diags.getSingParams());
		pstmt.setShort(i++, diags.getSwitchState());
		pstmt.setShort(i++, diags.getRxSwitchState());	
		pstmt.setShort(i++, diags.getPhase()[0]);
		pstmt.setShort(i++, diags.getPhase()[1]);
		pstmt.setShort(i++, diags.getPhase()[2]);
		pstmt.setShort(i++, diags.getPhase()[3]);
		pstmt.setShort(i++, diags.getPhase()[4]);
		pstmt.setShort(i++, diags.getPhase()[5]);
		pstmt.setShort(i++, diags.getPhase()[6]);
		pstmt.setShort(i++, diags.getPhase()[7]);
		
	
		
		pstmt.executeUpdate();

        pstmt.close();

	} finally {
		if (con != null) {
			con.close();
		}
	}
}
	
	public void storeBeginDiagsVector(PervasidWireHeader header, Vector<PervasidWireBeginDiags> vdiags) 
	throws SQLException {

	Connection con = null;
    PreparedStatement pstmt = null;

	try {
		con = ds.getConnection();			
		pstmt = con.prepareStatement("INSERT INTO round_begin_diags (reader_id, time_stamp, ms_ctr, sing_params, switch_state, rx_switch_state, phase0, phase1, phase2, phase3, phase4, phase5, phase6, phase7) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? )");

		for(PervasidWireBeginDiags diags : vdiags)
		{
		int i = 1;

		pstmt.setLong(i++, header.getHwaddr());
		pstmt.setLong(i++, diags.getTimeStamp());
		pstmt.setLong(i++, diags.getMsCtr());
		pstmt.setLong(i++, diags.getSingParams());
		pstmt.setShort(i++, diags.getSwitchState());
		pstmt.setShort(i++, diags.getRxSwitchState());
		pstmt.setShort(i++, diags.getPhase()[0]);
		pstmt.setShort(i++, diags.getPhase()[1]);
		pstmt.setShort(i++, diags.getPhase()[2]);
		pstmt.setShort(i++, diags.getPhase()[3]);
		pstmt.setShort(i++, diags.getPhase()[4]);
		pstmt.setShort(i++, diags.getPhase()[5]);
		pstmt.setShort(i++, diags.getPhase()[6]);
		pstmt.setShort(i++, diags.getPhase()[7]);
		pstmt.addBatch();
		}
			
		pstmt.executeBatch();

        pstmt.close();

	} finally {
		if (con != null) {
			con.close();
		}
	}
}
	
	public void storeCommandEnd(PervasidWireHeader header, PervasidWireCommandEnd cmd_end) 
	throws SQLException {

	Connection con = null;
    PreparedStatement pstmt = null;

	try {
		int i = 1;

		con = ds.getConnection();			
		pstmt = con.prepareStatement("INSERT INTO command_end (reader_id, time_stamp, ms_ctr, status) VALUES (?, ?, ?, ? )");


		pstmt.setLong(i++, header.getHwaddr());
		pstmt.setLong(i++, cmd_end.getTimeStamp());
		pstmt.setLong(i++, cmd_end.getMsCtr());
		pstmt.setLong(i++, cmd_end.getStatus());
				
		pstmt.executeUpdate();

        pstmt.close();

	} finally {
		if (con != null) {
			con.close();
		}
	}
}

	public int storeDiagsBulk(PervasidWireHeader header, byte []packet_data, int s_index) 
		throws SQLException, DataQuantityException {

		Connection con = null;
        PreparedStatement pstmt = null;
		int n_dealt_with = 0;

		try {

			con = ds.getConnection();			
			pstmt = con.prepareStatement("INSERT INTO round_diags (reader_id, time_stamp, ms_ctr, querys, rn16_rx, rn16_to, epc_to, good_reads, crc_failures) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

			int type;
			{
				/* peak at the packet type */
				DataUnpacker du = new DataUnpacker(packet_data, s_index);
				type = du.readUint16();
			}

			if (type == PervasidWireHeader.DATA_TYPE_END_DIAGS) {
				
				do {
					PervasidWireDiags diags = new PervasidWireDiags(packet_data, 
																	s_index);

					int i = 1;


					pstmt.setLong(i++, header.getHwaddr());
					pstmt.setLong(i++, diags.getTimeStamp());
					pstmt.setLong(i++, diags.getMsCtr());
					pstmt.setLong(i++, diags.getQuerys());
					pstmt.setLong(i++, diags.getRn16Rcv());
					pstmt.setLong(i++, diags.getRn16To());
					pstmt.setLong(i++, diags.getEpcTo());
					pstmt.setLong(i++, diags.getGoodReads());
					pstmt.setLong(i++, diags.getCrcFailures());

					pstmt.addBatch();

					if ((n_dealt_with + 1) % 1000 == 0) {
						pstmt.executeBatch(); // Execute every 1000 items.
				    }

					n_dealt_with++;

					s_index = s_index +  PervasidWireHeader.WIRE_PACKET_SIZE;
					
					if (s_index >= packet_data.length) {
						break;
					}


					{
						/* peak at the packet type */
						DataUnpacker du = new DataUnpacker(packet_data, s_index);
						type = du.readUint16();
					}
				} while(type  == PervasidWireHeader.DATA_TYPE_END_DIAGS);

				pstmt.executeBatch();
			}
		
			
            pstmt.close();

		} finally {
			if (con != null) {
				con.close();
			}
		}

		return n_dealt_with;
	}
	
	
	public int storeBeginDiagsBulk(PervasidWireHeader header, byte []packet_data, int s_index) 
	throws SQLException, DataQuantityException {

	Connection con = null;
    PreparedStatement pstmt = null;
	int n_dealt_with = 0;

	try {

		con = ds.getConnection();			
		pstmt = con.prepareStatement("INSERT INTO round_begin_diags (reader_id, time_stamp, ms_ctr, sing_params, switch_state, phase0, phase1, phase2, phase3, phase4, phase5, phase6, phase7) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? )");

		int type;
		{
			/* peak at the packet type */
			DataUnpacker du = new DataUnpacker(packet_data, s_index);
			type = du.readUint16();
		}

		if (type == PervasidWireHeader.DATA_TYPE_BEGIN_DIAGS) {
			
			do {
				PervasidWireBeginDiags diags = new PervasidWireBeginDiags(packet_data, 
																s_index);

				int i = 1;


				pstmt.setLong(i++, header.getHwaddr());
				pstmt.setLong(i++, diags.getTimeStamp());
				pstmt.setLong(i++, diags.getMsCtr());
				pstmt.setLong(i++, diags.getSingParams());
				pstmt.setShort(i++, diags.getSwitchState());
				pstmt.setShort(i++, diags.getPhase()[0]);
				pstmt.setShort(i++, diags.getPhase()[1]);
				pstmt.setShort(i++, diags.getPhase()[2]);
				pstmt.setShort(i++, diags.getPhase()[3]);
				pstmt.setShort(i++, diags.getPhase()[4]);
				pstmt.setShort(i++, diags.getPhase()[5]);
				pstmt.setShort(i++, diags.getPhase()[6]);
				pstmt.setShort(i++, diags.getPhase()[7]);

				pstmt.addBatch();

				if ((n_dealt_with + 1) % 1000 == 0) {
					pstmt.executeBatch(); // Execute every 1000 items.
			    }

				n_dealt_with++;

				s_index = s_index +  PervasidWireHeader.WIRE_PACKET_SIZE;
				
				if (s_index >= packet_data.length) {
					break;
				}


				{
					/* peak at the packet type */
					DataUnpacker du = new DataUnpacker(packet_data, s_index);
					type = du.readUint16();
				}
			} while(type  == PervasidWireHeader.DATA_TYPE_BEGIN_DIAGS);

			pstmt.executeBatch();
		}
	
		
        pstmt.close();

	} finally {
		if (con != null) {
			con.close();
		}
	}

	return n_dealt_with;
}



	public int getCommand(PervasidWireHeader header) 
		throws SQLException {

		Connection con = null;
		PreparedStatement pstmt = null;
		int command = 0;
			
		try {
			/* get command */
			{
				int i = 1;
				int n = 0;
				
				con = ds.getConnection();			
				pstmt = con.prepareStatement("SELECT command, processed from reader_commands WHERE reader_id=?");
				
				pstmt.setLong(i++, header.getHwaddr());				
				ResultSet rs = pstmt.executeQuery();
				
				while (rs.next()) {
					int processed = rs.getInt("processed");

					if (processed == 0) {
						command = rs.getInt("command");
					}
				}
				
				assert(n == 1);
				
				pstmt.close();
			}

			/* set processed */
			{
				int i = 1;
				pstmt = con.prepareStatement("UPDATE reader_commands SET processed=? WHERE reader_id=?");
				pstmt.setLong(i++, 1);
				pstmt.setLong(i++, header.getHwaddr());
				pstmt.executeUpdate();
				pstmt.close();
			}
		} finally {
			if (con != null) {
				con.close();
			}
		}

		return command;
	}

	public InventorySettings getSettings(PervasidWireHeader header) 
		throws SQLException {

		Connection con = null;
		PreparedStatement pstmt = null;
		InventorySettings is = null;
			
		try {
			int i = 1;
			int n = 0;
				
			con = ds.getConnection();			
			pstmt = con.prepareStatement("SELECT * from reader_settings JOIN interface_settings WHERE reader_id = ?");
				
			pstmt.setLong(i++, header.getHwaddr());				
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				is = new InventorySettings(rs.getLong("inventory_period"),
										   rs.getLong("start_q"),
										   rs.getLong("min_q"),
										   rs.getLong("max_q"),
										   rs.getLong("retry_count"),
										   rs.getLong("threshold_multiplier"),
										   rs.getLong("power_level"),
										   rs.getInt("session"),
										   rs.getInt("target"),
										   rs.getLong("profile"),
										   rs.getLong("switch_states"),
										   rs.getLong("freq_dwell_time"),
										   rs.getInt("mode"),
										   rs.getInt("asset_period"),
										   rs.getInt("previous_states"),
										   rs.getInt("trans_packets"),
										   rs.getInt("hop_rate")
											);

				n++;
			}

			assert(n == 1);
				
			pstmt.close();
		} finally {
			if (con != null) {
				con.close();
			}
		}


		return is;
	}
	
	public Vector<MacSettings> getMACSettings(PervasidWireHeader header) 
	throws SQLException {

	Connection con = null;
	PreparedStatement pstmt = null;
	Vector<MacSettings> v_ms;
	MacSettings ms =null;
		
	v_ms = new Vector<MacSettings>();
	
	try {
		int i = 1;
					
		con = ds.getConnection();			
		pstmt = con.prepareStatement("SELECT * from mac_settings WHERE reader_id = ? AND processed = 0");
			
		pstmt.setLong(i++, header.getHwaddr());				
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			ms = new MacSettings(rs.getShort("type"),
							     rs.getShort("address"),
							     rs.getShort("bank"),
							     rs.getInt("data"));

			
			v_ms.add(ms);
		}
		

		pstmt.close();
		pstmt = con.prepareStatement("UPDATE mac_settings SET processed = 1 WHERE reader_id = ?");
		pstmt.setLong(1,header.getHwaddr());
		pstmt.execute();
		
	} finally {
		if (con != null) {
			con.close();
		}
	}


	return v_ms;
}
	
	public void setDefaultSettings(PervasidWireHeader header) 
	throws SQLException {

	Connection con = null;
	PreparedStatement pstmt = null;
	
		
	try {
		int i = 1;
		
			
		con = ds.getConnection();
		
		
		pstmt = con.prepareStatement("INSERT INTO interface_settings (interface_settings_id, asset_period, reader_period)" +
				"VALUES (1, 20000, 5000) ON DUPLICATE KEY UPDATE asset_period = 20000, reader_period = 5000 ");
		pstmt.executeUpdate();

		
		pstmt.close();			
		
		pstmt = con.prepareStatement("INSERT INTO reader_settings (reader_id, inventory_period, start_q, min_q, max_q, retry_count, threshold_multiplier, power_level, session, target, profile, switch_states, freq_dwell_time,mode,previous_states,trans_packets,hop_rate) " +
										"VALUES (?, 3000, 7, 3, 9, 40, 8, 160, 2, 0, 1, 7,100,1,0,7,0)");
			
		pstmt.setLong(i++, header.getHwaddr());				
		pstmt.executeUpdate();
		pstmt.close();
		
	} finally {
		if (con != null) {
			con.close();
		}
	}


	
}
	


}