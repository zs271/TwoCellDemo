package com.pervasid.rfid.server;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.util.Vector;

import com.pervasid.rfid.server.DataUnpacker;

public class ConnectionThread implements Runnable {
	private SSLSocket ssl_socket;

	private final PervasidServerSettings settings;

	public ConnectionThread(SSLSocket ssl_socket,
			PervasidServerSettings settings) {
		this.ssl_socket = ssl_socket;
		this.settings = settings;
	}

	public PervasidWireReplyHeader handleDataCompressed(
			PervasidWireHeader header, DataInputStream datainputstream)
			throws java.util.zip.DataFormatException, DataQuantityException,
			IOException {

		int nbytes;
		byte[] in_buffer = new byte[16384];
		byte[] out_buffer = new byte[16384];
		Inflater decompresser = new Inflater();
		byte[] data_buffer = new byte[0];

		do {
			nbytes = datainputstream.read(in_buffer);
			decompresser.setInput(in_buffer, 0, nbytes);

			do {
				int count = decompresser.inflate(out_buffer);

				/* Ugly but build the data into a single buffer */
				byte[] new_data_buffer = new byte[data_buffer.length + count];

				System.arraycopy(data_buffer, 0, new_data_buffer, 0,
						data_buffer.length);

				System.arraycopy(out_buffer, 0, new_data_buffer,
						data_buffer.length, count);

				data_buffer = new_data_buffer;
			} while (!decompresser.needsInput());
		} while (!decompresser.finished());

		return handleData(header, data_buffer);
	}

	public int handleTagPacket(PervasidWireHeader header, PervasidWireTag tag) {

		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			dw.storeTag(header, tag);
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

	public int handleDiagsPackets(PervasidWireHeader header, byte[] diags,
			int index) throws DataQuantityException {
		int rv;

		/* this is a little unclean, but it gets us bulk inserts quickly */

		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			rv = dw.storeDiagsBulk(header, diags, index);
		} catch (java.sql.SQLException e) {
			System.out.printf("Failed to store bulk diags.\n" + e);
			return -1;
		}

		return rv;
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

	public int handleBeginDiagsPackets(PervasidWireHeader header, byte[] diags,
			int index) throws DataQuantityException {
		int rv;

		/* this is a little unclean, but it gets us bulk inserts quickly */

		try {
			DatabaseWorker dw = new DatabaseWorker(settings);
			rv = dw.storeBeginDiagsBulk(header, diags, index);
		} catch (java.sql.SQLException e) {
			System.out.printf("Failed to store bulk begin diags.\n" + e);
			return -1;
		}

		return rv;
	}

	public PervasidWireReplyHeader handleData(PervasidWireHeader header,
			byte[] data) throws DataQuantityException {

		PervasidWireReplyHeader reply = null;

		switch (header.getTransmissionType()) {
		case PING: {
			PervasidWirePing ping = new PervasidWirePing(data);
			System.out.println("GOT PING! \n");
			reply = new PervasidWireReply(PervasidWireReply.RETURN_OK, 0,
						new byte[0]);
			try {
				DatabaseWorker dw = new DatabaseWorker(settings);
				dw.storeStatus(header, ping);

				

				// check for the existence of settings and insert if we don't
				// find them
				InventorySettings is = dw.getSettings(header);
				if (is == null) {
					dw.setDefaultSettings(header);
				}

			} catch (java.sql.SQLException e) {
				System.out.printf("Failed To Save Status: %s\n", e);
				reply = new PervasidWireReply(PervasidWireReply.RETURN_FAIL, 0,
						new byte[0]);
			}
			break;
		}
		case REQUEST:
			try {
				DatabaseWorker dw = new DatabaseWorker(settings);

				InventorySettings is = dw.getSettings(header);

				if (is != null) {
					reply = new SettingsReply(PervasidWireReply.RETURN_OK, is);
				} else {
					reply = new PervasidWireReply(
							PervasidWireReply.RETURN_FAIL, 0, new byte[0]);
				}
			} catch (java.sql.SQLException e) {
				reply = new PervasidWireReply(PervasidWireReply.RETURN_FAIL, 0,
						new byte[0]);
			}

			break;
		case MAC_REQUEST:
				try {
					DatabaseWorker dw = new DatabaseWorker(settings);

					Vector<MacSettings> v_ms = dw.getMACSettings(header);

					if (v_ms != null && !v_ms.isEmpty()) {
						reply = new MacSettingsReply(PervasidWireReply.RETURN_OK, v_ms);
					} else {
						reply = new PervasidWireReply(
								PervasidWireReply.RETURN_FAIL, 0, new byte[0]);
					}
				} catch (java.sql.SQLException e) {
					reply = new PervasidWireReply(PervasidWireReply.RETURN_FAIL, 0,
							new byte[0]);
				}

			break;
		case DATA:
			// write data in separate thread so we can get a quick response out
			Thread dbt = new Thread(new DatabaseThread(header, data, settings));
			dbt.start();
			reply = new PervasidWireReply(PervasidWireReply.RETURN_OK, 0,
					new byte[0]);

		default:
			/* Shouldn't happen. */
		}

		return reply;

	}

	public PervasidWireReplyHeader readData(DataInputStream datainputstream)
			throws DataQuantityException {
		PervasidWireReplyHeader reply = null;

		try {

			int data_length = datainputstream.readInt();
			byte[] data = new byte[PervasidWireHeader.HEADER_SIZE];

			datainputstream.readFully(data, 0, PervasidWireHeader.HEADER_SIZE);

			PervasidWireHeader header = new PervasidWireHeader(data);

			if (header.isCompressed()) {
				try {
					reply = handleDataCompressed(header, datainputstream);
				} catch (java.util.zip.DataFormatException e) {
					System.out.println("DataFormat Exception! " + e);
				}
			} else {
				data = new byte[data_length];
				datainputstream.readFully(data, 0, data_length);

				reply = handleData(header, data);
			}

			try {
				DatabaseWorker dw = new DatabaseWorker(settings);
				reply.setCommand(dw.getCommand(header));
			} catch (java.sql.SQLException e) {
				System.out.println("Error getting reader commands\n" + e);
			}

		} catch (javax.net.ssl.SSLException e) {
			System.out.println("Detected non SSL connection. Closing.");
			System.out.printf("\t%s\n", e.getMessage());
			e.printStackTrace();
		} catch (java.io.IOException e) {
			System.out.println("IO exception: " + e);
			e.printStackTrace();
		}

		return reply;
	}

	public void run() {

		try {
			System.out.println("New Socket Opened");
			/* Incomming. */
			while (!ssl_socket.isClosed()) {
				InputStream inputstream = ssl_socket.getInputStream();
				DataInputStream datainputstream = new DataInputStream(
						inputstream);

				/* Out going. */
				OutputStream outputstream = ssl_socket.getOutputStream();
				ByteArrayOutputStream b = new ByteArrayOutputStream();

				try {
					PervasidWireReplyHeader reply;

					reply = readData(datainputstream);
					byte[] reply_bytes = reply.getBytes();

					b.write(reply_bytes, 0, reply_bytes.length);
					//System.out.printf("sending data %d",reply_bytes.length);
					b.writeTo(outputstream);
				} catch (com.pervasid.rfid.server.DataQuantityException e) {
					System.out.println("data quantity exception");
				}

			}

		} catch (javax.net.ssl.SSLException e) {
			System.out.println("Detected non SSL connection. Closing. ");
			System.out.printf("\t%s\n", e.getMessage());

		} catch (java.io.IOException e) {
			System.out.println("IO exception: " + e);
			e.printStackTrace();

		} finally {
			try {
				ssl_socket.close();
				System.out.println("Socket closed");
			} catch (java.io.IOException e) {
				// shrug
				System.out.println("IO exception: " + e);
				e.printStackTrace();
			}
		}

	}

}
