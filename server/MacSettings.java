package com.pervasid.rfid.server;

public class MacSettings {
	public int type;
	public short address;
	public short block;
	public int data;

	public MacSettings(short type, short address, short block, int data) {
		this.type = type;
		this.address = address;
		this.block = block;
		this.data = data;
	}

}
