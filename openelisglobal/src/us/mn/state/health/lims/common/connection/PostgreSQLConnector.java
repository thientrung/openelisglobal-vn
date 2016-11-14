package us.mn.state.health.lims.common.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import us.mn.state.health.lims.common.util.SystemConfiguration;

public class PostgreSQLConnector {
	private SystemConfiguration conf = SystemConfiguration.getInstance();
	private String serverIP, serverPort, dbName, username, password;
	private Connection con = null;
	private static Log log = LogFactory.getLog(PostgreSQLConnector.class);
	
	public PostgreSQLConnector() {
		serverIP = conf.getReportPostgreSQLServerIp();
		serverPort = conf.getReportPostgreSQLServerPort();
		dbName = conf.getReportPostgreSQLServerName();
		username = "clinlims";
		password = "clinlims";
	}
	
	public boolean connect() {
		String url = "jdbc:postgresql://" + serverIP + ":" + serverPort + "/" + dbName;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, username, password);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Connection getConnection()
	{
		connect();
		return con;
	}
	
	public void closeConnection()
	{
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
