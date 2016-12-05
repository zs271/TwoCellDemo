package com.pervasid.rfid.experiment;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class DatabaseAlert {
	private Connection con;
	
	public DatabaseAlert(final Connection con){
		this.con=con;
	}
	
	public void sendDBAlert(String tag_id,int zone_id,long reader_id,String alert_type,String desc){
		
		PreparedStatement pstmt = null;
		try {
			int i = 1;
			
			
			pstmt = con.prepareStatement("INSERT INTO alert (tag_id, zone_id,reader_id, alert_type,description) VALUES (?, ?,?, ?, ?)");
			
	
			pstmt.setString(i++, tag_id);
			pstmt.setInt(i++, zone_id);
			pstmt.setLong(i++, reader_id);
			pstmt.setString(i++, alert_type);
			pstmt.setString(i++, desc);
			
			pstmt.executeUpdate();

	        pstmt.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}

		}
		
	
	public static void main(){
		
	}
		
		
}
