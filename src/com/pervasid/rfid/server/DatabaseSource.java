package com.pervasid.rfid.server;

import java.sql.Connection;
		
public class DatabaseSource {
	private static volatile DatabaseSource instance = null;

	private com.mysql.jdbc.jdbc2.optional.MysqlDataSource ds;
 
	private DatabaseSource(PervasidServerSettings settings) {
		this.ds = new com.mysql.jdbc.jdbc2.optional.MysqlDataSource();

		ds.setServerName(settings.db_location);
		ds.setPortNumber(settings.db_port);
		ds.setDatabaseName(settings.db_name);
		ds.setUser(settings.db_username);
		ds.setPassword(settings.db_password);
									   
	}

	public synchronized Connection getConnection() throws java.sql.SQLException {
		return ds.getConnection();
	}
 
	public static DatabaseSource getInstance(PervasidServerSettings settings) 
		throws java.sql.SQLException {

		if (instance == null) {
			synchronized (DatabaseSource .class){
				if (instance == null) {
					instance = new DatabaseSource(settings);
				}
			}
		}
		return instance;
	}
}