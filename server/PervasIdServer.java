package com.pervasid.rfid.server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.ini4j.Ini;

public class PervasIdServer {

	private PervasidServerSettings settings;
	private String settings_path;

	public class SettingsException extends Exception {
		public SettingsException(String msg) {
			super(msg);
		}
	}

	public PervasIdServer(String settings_path) {
			this.settings = new PervasidServerSettings();
			this.settings_path = settings_path;
	}

	public void loadSettings(String config_path) 
		throws SettingsException {

		try {
			Ini ini = new Ini(new java.io.FileInputStream(
								  new java.io.File(config_path)));

			settings.db_location = ini.get("database", "location");
			settings.db_password = ini.get("database", "password");
			settings.db_username = ini.get("database", "user");
			settings.db_name     = ini.get("database", "name");
			settings.db_port     = ini.get("database", "port", int.class);
			
			settings.listen_port = ini.get("server", "port", int.class);

		} catch (java.io.FileNotFoundException e) {
			throw new SettingsException("Cannot open config file. (FileNotFoundException)");
		} catch (java.io.IOException e) {
			throw new SettingsException("Error reading config file. (IOException)");
		} catch (java.lang.IllegalArgumentException e) {
			throw new SettingsException("Error converting value while parseing config file. (IllegalArgument)");
		}
	
		try {
			DatabaseSource ds = DatabaseSource.getInstance(settings);
		} catch (java.sql.SQLException e) {
			System.out.printf("Error creating SQL data source\n");
			throw new SettingsException("Error creating SQL data source\n");
		}

	}


	public void serverLoop() {
        try {
            SSLServerSocketFactory ssl_sock_factory =
				(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket socket =
				(SSLServerSocket)ssl_sock_factory.createServerSocket(settings.listen_port);
           


			while (true) {
				
				Thread connectionThread;
				SSLSocket sslsocket = (SSLSocket)socket.accept();
			   
				connectionThread = new Thread(
					new ConnectionThread(sslsocket, settings));
				connectionThread.start();
				} 
        } catch (Exception exception) {
            exception.printStackTrace();
        } 
        
	}

	public void run() {
		try {
			this.loadSettings(this.settings_path);
		} catch (SettingsException e) {
			System.out.printf("Error loading settings:\n");
			System.out.printf("\t%s\n", e.getMessage());

			System.exit(1);
		}

		this.serverLoop();
	}

    public static void main(String[] args) {
		String conf_location = "conf/pervasid-settings.ini";

		if (args.length > 0) {
			System.out.printf("From commandline args.\n");
			conf_location = args[0];
		}

		PervasIdServer server = new PervasIdServer(conf_location);
		server.run();
    }
}
