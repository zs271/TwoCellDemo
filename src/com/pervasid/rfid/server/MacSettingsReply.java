package com.pervasid.rfid.server;

import java.nio.ByteBuffer;
import java.util.Vector;

public class MacSettingsReply 
	extends PervasidWireReplyHeader
{
	 private Vector<MacSettings> vsettings;

	 private int PACKET_SIZE;

public MacSettingsReply(int return_value, Vector<MacSettings> vsettings) {
	super(return_value, 0, 12*vsettings.size());

	this.vsettings = vsettings;
}

public byte [] getBytes() {
	byte [] bytes = new byte[super.getDataLength()];
	ByteBuffer b = ByteBuffer.wrap(bytes);
	b.order(java.nio.ByteOrder.LITTLE_ENDIAN);

	b.put(super.getBytes());
	
	for(MacSettings setting : vsettings)
	{
		b.putInt((int) setting.type);
		b.putShort((short) setting.address);	
		b.putShort((short) setting.block);
		b.putInt((int) setting.data);
	}
	
	return bytes;	
}
}
