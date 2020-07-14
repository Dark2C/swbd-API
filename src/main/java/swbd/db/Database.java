package swbd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Database {
	public static Connection Get_Connection() throws Exception {
		Context env = (Context) new InitialContext().lookup("java:comp/env");
		DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
		String connectionURL = (String) env.lookup("dbConn");
		Connection connection = null;
		connection = DriverManager.getConnection(connectionURL, (String) env.lookup("dbUser"),
				(String) env.lookup("dbPass"));
		return connection;
	}
}
