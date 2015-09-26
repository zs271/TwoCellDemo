package com.pervasid.rfid.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.zip.Inflater;


public class DatabaseThread implements Runnable{
	private PervasidWireHeader header;
	private byte []data;
	private PervasidServerSettings settings;
	
	public DatabaseThread(PervasidWireHeader header,byte []data, PervasidServerSettings settings){
		this.header = header;
		this.data = data;
		this.settings = settings;
	}
	

	
	public int handleTagPacket(PervasidWireHeader header, 
							   PervasidWireTag tag) {

		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			dw.storeTag(header, tag);
		} catch (java.sql.SQLException e) {
			System.out.printf("Failed to store tag.\n" + e);
			return -1;
		}
		
		
		return 0;
	}
	
	public int handleTagVector(PervasidWireHeader header, 
			   Vector<PervasidWireTag> vtag) {

	try {
	DatabaseWorker dw = new DatabaseWorker(settings);
	dw.storeTagVector(header, vtag);
	} catch (java.sql.SQLException e) {
	System.out.printf("Failed to store tag.\n" + e);
	return -1;
	}
	
	
	return 0;
	}


	public int handleAssetPacket(PervasidWireHeader header, 
							   PervasidWireAsset asset) {

		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			dw.storeAssetEvent(header, asset);
		} catch (java.sql.SQLException e) {
			System.out.printf("Failed to store tag.\n" + e);
			return -1;
		}
		
		
		return 0;
	}

	
	public int handleDiagsPacket(PervasidWireHeader header, 
								 PervasidWireDiags diags) {
		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			dw.storeDiags(header, diags);
		} catch (java.sql.SQLException e) {
			System.out.printf("Failed to store diags.\n" + e);
			return -1;
		}
		
		
		return 0;
	}
	
	public int handleDiagsVector(PervasidWireHeader header, 
			 Vector<PervasidWireDiags> diags) {
	try {
	DatabaseWorker dw = new DatabaseWorker(settings);
	dw.storeDiagsVector(header, diags);
	} catch (java.sql.SQLException e) {
	System.out.printf("Failed to store diags.\n" + e);
	return -1;
	}
	
	
	return 0;
	}

	
	public int handleBeginDiagsPacket(PervasidWireHeader header, 
			 PervasidWireBeginDiags diags) {
		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			dw.storeBeginDiags(header, diags);
		} catch (java.sql.SQLException e) {
			System.out.printf("Failed to store begin diags.\n" + e);
		return -1;
		}
		
		
		return 0;
	}
	
	public int handleBeginDiagsVector(PervasidWireHeader header, 
			 Vector<PervasidWireBeginDiags> diags) {
		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			dw.storeBeginDiagsVector(header, diags);
		} catch (java.sql.SQLException e) {
			System.out.printf("Failed to store begin diags.\n" + e);
		return -1;
		}
		
		
		return 0;
	}
	
	public int handleCommandEndPacket(PervasidWireHeader header, 
			 PervasidWireCommandEnd cmd_end) {
		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			dw.storeCommandEnd(header, cmd_end);
		} catch (java.sql.SQLException e) {
			System.out.printf("Failed to store command end.\n" + e);
		return -1;
		}
		
		
		return 0;
	}

	
	
	public void StoreData(PervasidWireHeader header, byte []data)
	throws DataQuantityException{
		int n_packets = data.length / PervasidWireHeader.WIRE_PACKET_SIZE;
		assert((data.length % PervasidWireHeader.WIRE_PACKET_SIZE) == 0);
		System.out.printf("Got: %d packets\n", n_packets);

		int rv = 0;			
		long time_stamp = 0;
		int status_code = 1;
		
		//vectorise diags and tags for bulk inserts
		Vector<PervasidWireTag> vtag = new Vector<PervasidWireTag>();
		Vector<PervasidWireDiags> vdiags = new Vector<PervasidWireDiags>();
		Vector<PervasidWireBeginDiags> vbegindiags = new Vector<PervasidWireBeginDiags>();
		
		
		for (int i = 0; i < n_packets; i++) {
			int s_index = PervasidWireHeader.WIRE_PACKET_SIZE * i;
			int e_index = s_index + PervasidWireHeader.WIRE_PACKET_SIZE;

			int type;

			byte [] packet_data = Arrays.copyOfRange(data, s_index, e_index);

			{
				/* peak at the packet type */
				DataUnpacker du = new DataUnpacker(packet_data);
				type = du.readUint16();
			}

			switch (type) {
			case PervasidWireHeader.DATA_TYPE_INVENTORY:
				PervasidWireTag tag = new PervasidWireTag(packet_data);
				vtag.add(tag);
				
			
				time_stamp=tag.getTimeStamp();
	
				break;
			case PervasidWireHeader.DATA_TYPE_ASSET_EVENT:
				PervasidWireAsset asset = new PervasidWireAsset(packet_data);
				System.out.printf("Event: %d %s\n", asset.getEventType(), asset.getEPC());

				rv = handleAssetPacket(header, asset);
				time_stamp=asset.getTimeStamp();
	
				break;
			case PervasidWireHeader.DATA_TYPE_END_DIAGS:
				
				PervasidWireDiags diags = new PervasidWireDiags(data, 
						s_index);
				vdiags.add(diags);
				time_stamp=diags.getTimeStamp();
			
				break;
			case PervasidWireHeader.DATA_TYPE_BEGIN_DIAGS:
				PervasidWireBeginDiags diags2 = new PervasidWireBeginDiags(data, 
						s_index);
				vbegindiags.add(diags2);
				time_stamp=diags2.getTimeStamp();

				
				break;
			case PervasidWireHeader.DATA_TYPE_COMMAND_END:
				PervasidWireCommandEnd cmd_end = new PervasidWireCommandEnd(packet_data);
				rv = handleCommandEndPacket(header, cmd_end);
				time_stamp=cmd_end.getTimeStamp();
				if(cmd_end.getStatus()!=0)
					{
					status_code = 2;
					System.out.printf("Command End Error: %x \n",cmd_end.getStatus());
					}
							
				break;
			default:
				assert(0 == 1);
				time_stamp = 0;
				/* shouldn't happen */
			}
			
		}
		try{
			if(!vtag.isEmpty())
				rv = handleTagVector(header,vtag);
			if(!vdiags.isEmpty())
				rv = handleDiagsVector(header,vdiags);
			if(!vbegindiags.isEmpty())
				rv = handleBeginDiagsVector(header,vbegindiags);
				
			DatabaseWorker dw = new DatabaseWorker(settings);
			dw.updateStatus(header, status_code,time_stamp);
			} catch (java.sql.SQLException e) {
				System.out.printf("Failed To Update Status: %s\n", e);
				}
	return;
	}
	
	public void run() {
		try{
			//System.out.println("New DB Thread");
			StoreData(header, data);
			return;
		}catch (com.pervasid.rfid.server.DataQuantityException e){
			System.out.println("data quantity exception");
		} 
	
	}
	
	
	
}
