package swbd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.naming.Context;
import javax.naming.InitialContext;
/**
 * Classe per gestire connessione con il database.
 *
 */
public class Database {
	private static Connection connection = null;
	public static Connection Get_Connection() throws Exception {
		if(null == connection) {
			Context env = (Context) new InitialContext().lookup("java:comp/env");
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
			String connectionURL = (String) env.lookup("dbConn");
			connection = DriverManager.getConnection(connectionURL, (String) env.lookup("dbUser"),
				(String) env.lookup("dbPass"));
		}
		return connection;
	}
}
