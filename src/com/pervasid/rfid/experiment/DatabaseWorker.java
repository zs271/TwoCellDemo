package com.pervasid.rfid.experiment;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.sql.SQLException;


import java.util.Vector;
import java.util.HashMap;
import java.util.zip.ZipOutputStream;

import com.pervasid.rfid.experiment.InventorySettings;



public class DatabaseWorker {
	private DatabaseSource ds;
	private Connection con = null;
	private boolean transaction = false;

	public Connection getConnection() throws SQLException {
		if (con == null) {
			con = ds.getConnection();

			if (transaction) {
				con.setAutoCommit(false);
			}
		}

		return con;
	}

	public void closeConnection() throws SQLException {
		if (con != null && !transaction) {
			con.close();
			con = null;
		}
	}

	public void startTransaction() throws SQLException {
		transaction = true;		
	}

	public void endTransaction() throws SQLException {
		transaction = false;

		if (con != null) {
			con.commit();
		}
		closeConnection();
	}

	public DatabaseWorker(PervasidServerSettings settings)
		throws java.sql.SQLException {		

		this.ds = DatabaseSource.getInstance(settings);
	}

	public Vector<ExperimentSettings> getExptSettings() 
	throws SQLException {

	Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
	Vector<ExperimentSettings> ExpSetVec = new Vector<ExperimentSettings>();
	
		
	try {
		int i = 1;
		int n = 0;
			
		con = ds.getConnection();			
		pstmt = con.prepareStatement("SELECT * from expt_settings");
			
					
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			is = new ExperimentSettings();
			is.expt_number = rs.getLong("expt_number");
			is.reader_id = 	rs.getLong("reader_id");
			is.duration = rs.getLong("duration");
			is.inventory_period = rs.getLong("inventory_period");
			is.start_q = rs.getLong("start_q");
			is.min_q = rs.getLong("min_q");
			is.max_q = rs.getLong("max_q");
			is.retry_count = rs.getLong("retry_count");
			is.threshold_multiplier = rs.getLong("threshold_multiplier");
			is.power_level = rs.getLong("power_level");
			is.session = rs.getInt("session");
			is.target = rs.getInt("target");
			is.profile = rs.getLong("profile");
			is.switch_states = rs.getLong("switch_states");
			is.freq_dwell_time = rs.getLong("freq_dwell_time");
			is.mode = rs.getInt("mode");
			is.previous_states = rs.getInt("previous_states");
			is.trans_packets = rs.getInt("trans_packets");
			is.hop_rate = rs.getInt("hop_rate");
			ExpSetVec.add(is);
			n++;
		};

			
			
			
		pstmt.close();
	} finally {
		if (con != null) {
			con.close();
		}
		
	}


	return ExpSetVec;
}
	
	public int setSettings(ExperimentSettings expt_settings) 
	throws SQLException {

	Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
		
	try {
		int i = 1;
		int n = 0;
			
		con = ds.getConnection();			
		pstmt = con.prepareStatement("REPLACE INTO reader_settings SET " +
										"reader_id = ?," +
										"inventory_period = ?," +
										"start_q = ?," +
										"min_q =?," +
										"max_q=?," +
										"retry_count = ?," +
										"threshold_multiplier = ?," +
										"power_level = ?," +
										"session = ?," +
										"target = ?," +
										"profile = ?," +
										"switch_states = ?," +
										"freq_dwell_time = ?," +
										"mode = ?," +
										"previous_states = ?," +
										"trans_packets = ?," +
										"hop_rate = ? ");
			
		pstmt.setLong(i++, expt_settings.reader_id);
		pstmt.setLong(i++, expt_settings.inventory_period);
		pstmt.setLong(i++, expt_settings.start_q);
		pstmt.setLong(i++, expt_settings.min_q);
		pstmt.setLong(i++, expt_settings.max_q);
		pstmt.setLong(i++, expt_settings.retry_count);
		pstmt.setLong(i++, expt_settings.threshold_multiplier);
		pstmt.setLong(i++, expt_settings.power_level);
		pstmt.setLong(i++, expt_settings.session);
		pstmt.setLong(i++, expt_settings.target);
		pstmt.setLong(i++, expt_settings.profile);
		pstmt.setLong(i++, expt_settings.switch_states);
		pstmt.setLong(i++, expt_settings.freq_dwell_time);
		pstmt.setLong(i++, expt_settings.mode);
		pstmt.setLong(i++, expt_settings.previous_states);
		pstmt.setLong(i++, expt_settings.trans_packets);
		pstmt.setLong(i++, expt_settings.hop_rate);
		
		pstmt.executeUpdate();
			
		pstmt.close();
	} finally {
		if (con != null) {
			con.close();
		}
	}


	return 0;
}
	
	public int AddExptStartSettings(long exptNumber) 
	throws SQLException {

	Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
		
	try {
		int i = 1;
		int n = 0;
			
		con = ds.getConnection();			
		pstmt = con.prepareStatement("INSERT expt_records (reader_id, inventory_period, start_q, min_q, max_q, retry_count," +
									 "threshold_multiplier,power_level,session, target, profile, switch_states, freq_dwell_time, mode, previous_states, " +
									 "trans_packets, hop_rate,duration, start_time, start_tag_read_id, start_cmd_end_id," +
									 " start_round_begin_diags_id,start_round_diags_id) " +
									 "SELECT reader_id, inventory_period, start_q, min_q, max_q, retry_count," +
									 "threshold_multiplier,power_level,session, target, profile, switch_states, freq_dwell_time, mode, previous_states, " +
									 "trans_packets, hop_rate,duration, NOW() as start_time," +
									 "(SELECT COALESCE(MAX(tag_read_id),0) FROM tag_reads)+1 as start_tag_read_id," +
									 "(SELECT COALESCE(MAX(cmd_end_id),0) FROM command_end)+1 as start_cmd_end_id, " +
									 "(SELECT COALESCE(MAX(round_begin_diags_id),0) FROM round_begin_diags)+1 as start_round_begin_diags_id, " +
									 "(SELECT COALESCE(MAX(round_diags_id),0) FROM round_diags)+1 as start_round_diags_id " +
									 "FROM expt_settings WHERE expt_number = ?");
			
		pstmt.setLong(i++, exptNumber);
		
		pstmt.executeUpdate();
			
		pstmt.close();
	} finally {
		if (con != null) {
			con.close();
		}
	}


	return 0;
}
	
	public int AddExptEndSettings(long exptNumber) 
	throws SQLException {

	Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
		
	try {
		int i = 1;
		int n = 0;
			
		con = ds.getConnection();			
		pstmt = con.prepareStatement("UPDATE expt_records SET end_time=NOW(), " +
										"end_tag_read_id = (SELECT COALESCE(MAX(tag_read_id),0) from tag_reads), " +
										"end_cmd_end_id = (SELECT COALESCE(MAX(cmd_end_id),0) FROM command_end), " +
										"end_round_begin_diags_id = (SELECT COALESCE(MAX(round_begin_diags_id),0) FROM round_begin_diags)," +
										"end_round_diags_id = (SELECT COALESCE(MAX(round_diags_id),0) FROM round_diags)" +
										" ORDER BY expt_id DESC LIMIT 1");
		
		pstmt.executeUpdate();
			
		pstmt.close();
	} finally {
		if (con != null) {
			con.close();
		}
	}


	return 0;
}

	
	
	public Connection returncon() 
	throws SQLException {

	//Connection con = null;
				
		con = ds.getConnection();			
		
	   return con;
	}
	
	
	
	public int SetStartInventory(long reader_id, Connection con) 
	throws SQLException {

	//Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
		
	
		int i = 1;
		int n = 0;
			
		//con = ds.getConnection();			
		pstmt = con.prepareStatement("INSERT INTO reader_commands (reader_id, command, processed) VALUES (? , 1, 0) ON DUPLICATE KEY UPDATE command=1, processed=0");
			
		pstmt.setLong(i++, reader_id);
		
		
		pstmt.executeUpdate();
			
	  




	return 0;
}
	
	public String GetReaderTime(long reader_id,Connection con) 
			throws SQLException {

			//Connection con = null;
			PreparedStatement pstmt = null;
			ExperimentSettings is = null;
			String current_time;
				
				int i = 1;
				int n = 0;
					
				//con = ds.getConnection();			
				pstmt = con.prepareStatement("SELECT server_timestamp FROM reader_status WHERE reader_id = ?");
				
				pstmt.setLong(i++, reader_id);
					
					
				ResultSet rs = pstmt.executeQuery();
				rs.next();
				current_time = rs.getString("server_timestamp");
					
				pstmt.close();
				
//				String[] reader_time_s=current_time.split("\\s+");
//				current_time=reader_time_s[1];
				

			return current_time;
		}
	

	

	public int SetStopInventory(long reader_id,Connection con) 
	throws SQLException {

	//Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
		
		int i = 1;
		int n = 0;
			
		//con = ds.getConnection();			
		pstmt = con.prepareStatement("UPDATE reader_commands SET command = 2, processed = 0 WHERE reader_id = ?");
			
		pstmt.setLong(i++, reader_id);
		
		pstmt.executeUpdate();
			
		//pstmt.close();



	return 0;
}
	
	
	public int ResetReader(long reader_id,Connection con) 
	throws SQLException {

	//Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
		
		int i = 1;
		int n = 0;
			
		//con = ds.getConnection();			
		pstmt = con.prepareStatement("UPDATE reader_commands SET command = 3, processed = 0 WHERE reader_id = ?");
			
		pstmt.setLong(i++, reader_id);
		
		pstmt.executeUpdate();
			
		//pstmt.close();



	return 0;
}
	
	
	public int ResetAllReaders(Connection con) 
	throws SQLException {

	//Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
		
		int i = 1;
		int n = 0;
			
		//con = ds.getConnection();			
		pstmt = con.prepareStatement("UPDATE reader_commands SET command = 3, processed = 0 WHERE 1");
			
		//pstmt.setLong(i++);
		
		pstmt.executeUpdate();
			
		//pstmt.close();



	return 0;
}
	
	
	
	
	
	
	
	
	public int SetUpdateMAC(long reader_id) 
			throws SQLException {

			Connection con = null;
			PreparedStatement pstmt = null;
			ExperimentSettings is = null;
				
			try {
				int i = 1;
				int n = 0;
					
				con = ds.getConnection();			
				pstmt = con.prepareStatement("UPDATE reader_commands SET command = 5, processed = 0 WHERE reader_id = ?");
					
				pstmt.setLong(i++, reader_id);
				
				pstmt.executeUpdate();
					
				pstmt.close();
			} finally {
				if (con != null) {
					con.close();
				}
			}


			return 0;
		}
	
	public int SetMAC(MacSetting setting) 
			throws SQLException {

			Connection con = null;
			PreparedStatement pstmt = null;
			ExperimentSettings is = null;
				
			try {
				int i = 1;
				int n = 0;
					
				con = ds.getConnection();			
				pstmt = con.prepareStatement("INSERT INTO mac_settings VALUES (?, ?, ?, ?, ?, 0) ON DUPLICATE KEY UPDATE  data = ?, processed = 0 ");
					
				pstmt.setLong(i++, setting.reader_id);
				pstmt.setShort(i++, setting.type);
				pstmt.setShort(i++, setting.address);
				pstmt.setShort(i++, setting.bank);
				pstmt.setInt(i++, setting.data);
				pstmt.setInt(i++, setting.data);

				pstmt.executeUpdate();
					
				pstmt.close();
			} finally {
				if (con != null) {
					con.close();
				}
			}


			return 0;
		}
	
		public int ClearTables() 
			throws SQLException {

			Connection con = null;
			PreparedStatement pstmt = null;
			
				
			try {
				
					
				con = ds.getConnection();			
				pstmt = con.prepareStatement("TRUNCATE tag_reads");	
				pstmt.executeUpdate();
				pstmt = con.prepareStatement("TRUNCATE tag_reads_simple");	
				pstmt.executeUpdate();
				pstmt = con.prepareStatement("TRUNCATE reader_status");	
				pstmt.executeUpdate();
				pstmt = con.prepareStatement("TRUNCATE reader_settings");	
				pstmt.executeUpdate();
				pstmt = con.prepareStatement("TRUNCATE round_begin_diags");
				pstmt.executeUpdate();
				pstmt = con.prepareStatement("TRUNCATE round_diags");	
				pstmt.executeUpdate();
		
					
				pstmt.close();
			} finally {
				if (con != null) {
					con.close();
				}
			}


			return 0;
		}
		
		public long GetReader() 
				throws SQLException {

				Connection con = null;
				PreparedStatement pstmt = null;
				long reader_id=0;
					
				try {
					
						
					int i = 1;
					int n = 0;
						
					con = ds.getConnection();			
					pstmt = con.prepareStatement("SELECT * from reader_status");						
					ResultSet rs = pstmt.executeQuery();
					
					rs.next();
					reader_id = 	rs.getLong("reader_id");
						
					pstmt.close();
				} finally {
					if (con != null) {
						con.close();
					}
				}


				return reader_id;
			}
		
		public long SetReaderMac(long reader_id, String reader_barcode ) 
				throws SQLException {

				Connection con = null;
				PreparedStatement pstmt = null;
				
					
				try {
					
						
					int i = 1;
					int n = 0;
						
					con = ds.getConnection();			
					pstmt = con.prepareStatement("INSERT INTO reader_mac_id (reader_id, reader_barcode) VALUES (?, ?)");
					pstmt.setLong(i++, reader_id);
					pstmt.setString(i++, reader_barcode);
					
					pstmt.executeUpdate();
					
					
					
					
						
					pstmt.close();
				} finally {
					if (con != null) {
						con.close();
					}
				}


				return reader_id;
			}
		public int GetLastReaderMacRecord(long reader_id ) 
				throws SQLException {

				Connection con = null;
				PreparedStatement pstmt = null;
				int index;
				
					
				try {
					
						
					int i = 1;
					int n = 0;
						
					con = ds.getConnection();
					
					pstmt = con.prepareStatement("SELECT MAX(reader_mac_id_ix) as max_ix FROM reader_mac_id WHERE reader_id = ?");
					pstmt.setLong(i++, reader_id);
					ResultSet rs = pstmt.executeQuery();
					rs.next();
					index = 	rs.getInt("max_ix");
					
					
						
					pstmt.close();
				} finally {
					if (con != null) {
						con.close();
					}
				}


				return index;
			}
	
		public float GetAccuracy() 
				throws SQLException {

				Connection con = null;
				PreparedStatement pstmt = null;
				float accuracy;
					
				try {
					
						
					int i = 1;
					int n = 0;
						
					con = ds.getConnection();			
					pstmt = con.prepareStatement(("SELECT "+
				            "(SELECT COUNT(DISTINCT(tag_id)) FROM tag_reads) as distinct_tags,"+
				            "(SELECT COUNT(DISTINCT(tag_id)) FROM reference_tags) as distinct_refs,"+
				            "(SELECT COUNT(DISTINCT(a.tag_id)) FROM reference_tags as a LEFT JOIN tag_reads as b on a.tag_id=b.tag_id WHERE b.tag_id is NULL) as missing_tags,"+
				            "(SELECT 100*(1-(missing_tags/distinct_refs))) as accuracy,"+
				            "(SELECT (SELECT max(server_time) FROM reader_status LIMIT 1)-server_time FROM tag_reads ORDER BY tag_read_id LIMIT 1) as elapsed_seconds"));
				            	
					ResultSet rs = pstmt.executeQuery();
					rs.next();
					accuracy = 	rs.getFloat("accuracy");
						
					pstmt.close();
				} finally {
					if (con != null) {
						con.close();
					}
				}


				return accuracy;
			}
		
		public float SetAccuracy(int index, float accuracy) 
				throws SQLException {

				Connection con = null;
				PreparedStatement pstmt = null;
				
					
				try {
					
						
					int i = 1;
					int n = 0;
						
					con = ds.getConnection();			
					pstmt = con.prepareStatement(("UPDATE reader_mac_id SET accuracy = ? WHERE 	reader_mac_id_ix= ?"));
					pstmt.setFloat(i++, accuracy);
					pstmt.setInt(i++, index);        	
					pstmt.executeUpdate();
					
						
					pstmt.close();
				} finally {
					if (con != null) {
						con.close();
					}
				}


				return accuracy;
			}
	public int GetReaderStatus(long reader_id,Connection con) 
	throws SQLException {

	//Connection con = null;
	PreparedStatement pstmt = null;
	ExperimentSettings is = null;
	int status;
		
		int i = 1;
		int n = 0;
			
		//con = ds.getConnection();			
		pstmt = con.prepareStatement("SELECT status_code FROM reader_status WHERE reader_id = ? LIMIT 1");
			
		pstmt.setLong(i++, reader_id);
		
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		status = rs.getInt("status_code");
			
		pstmt.close();


	return status;
}

	public com.pervasid.rfid.experiment.InventorySettings getSettings(long reader_id) 
		throws SQLException {
	
		Connection con = null;
		PreparedStatement pstmt = null;
		com.pervasid.rfid.experiment.InventorySettings is = null;
			
		try {
			int i = 1;
			int n = 0;
				
			con = ds.getConnection();			
			pstmt = con.prepareStatement("SELECT * from reader_settings JOIN interface_settings WHERE reader_id = ?");
				
			pstmt.setLong(i++, reader_id);				
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
	public ZipOutputStream ReferenceTags2CSV(ZipOutputStream out) 
			throws SQLException {
		
			Connection con = null;
			PreparedStatement pstmt = null;
							
			try {
				StringBuilder sb = new StringBuilder();	
				con = ds.getConnection();			
				pstmt = con.prepareStatement("SELECT * from reference_tags");
								
				ResultSet rs = pstmt.executeQuery();
				int colunmCount = rs.getMetaData().getColumnCount();
				
				while (rs.next()) {
					for(int i=1;i<=colunmCount;i++)
                    {
                         
                        //you can update it here by using the column type but i am fine with the data so just converting 
                        //everything to string first and then saving
                        if(rs.getObject(i)!=null)
                        {
                        String data= rs.getObject(i).toString();
                        sb.append("\"");
                        sb.append(data) ;
                        sb.append("\"");
                        if(i<colunmCount)
                        	sb.append(";");
                        
                        }
                        else
                        {
                            String data= "null";
                            sb.append("\"");
                            sb.append(data) ;
                            sb.append("\"");
                            if(i<colunmCount)
                            	sb.append(";");
                            
                        }
                         
                    }
                    //new line entered after each row
                    sb.append(System.getProperty("line.separator"));
					
				}
					
				pstmt.close();
				byte[] data = sb.toString().getBytes();
			    out.write(data, 0, data.length);
			    
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (con != null) {
					con.close();
				}
			}
			return out;
		
		
			
		}
	public ZipOutputStream TagReadsSimple2CSV(ZipOutputStream out) 
			throws SQLException {
		
			Connection con = null;
			PreparedStatement pstmt = null;
							
			try {
				StringBuilder sb = new StringBuilder();	
				con = ds.getConnection();			
				pstmt = con.prepareStatement("SELECT * from tag_reads_simple");
								
				ResultSet rs = pstmt.executeQuery();
				int colunmCount = rs.getMetaData().getColumnCount();
				
				while (rs.next()) {
					for(int i=1;i<=colunmCount;i++)
                    {
                         
                        //you can update it here by using the column type but i am fine with the data so just converting 
                        //everything to string first and then saving
                        if(rs.getObject(i)!=null)
                        {
                        String data= rs.getObject(i).toString();
                        sb.append("\"");
                        sb.append(data) ;
                        sb.append("\"");
                        if(i<colunmCount)
                        	sb.append(";");
                        }
                        else
                        {
                            String data= "null";
                            sb.append("\"");
                            sb.append(data) ;
                            sb.append("\"");
                            if(i<colunmCount)
                            	sb.append(";");
                            
                        }
                         
                    }
                    //new line entered after each row
                    sb.append(System.getProperty("line.separator"));
					
				}
					
				pstmt.close();
				byte[] data = sb.toString().getBytes();
			    out.write(data, 0, data.length);
			    
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (con != null) {
					con.close();
				}
			}
			return out;
		
		
			
		}
	

	
	
}