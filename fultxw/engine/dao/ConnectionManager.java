package ifs.fultxw.engine.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static ifs.fultxw.engine.servlet.FulltextInitServlet.classString;
import static ifs.fultxw.engine.servlet.FulltextInitServlet.connectionString;
import static ifs.fultxw.engine.servlet.FulltextInitServlet.userName;
import static ifs.fultxw.engine.servlet.FulltextInitServlet.password;

public class ConnectionManager {
	//TODO  DBCP
	static {
		try {
			Class.forName( classString);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
public static Connection getConnection(){
	Connection conn = null;
	try {
		conn = DriverManager.getConnection( connectionString, userName, password);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return conn;

}

public static void main(String[] args) {
	Connection conn = getConnection();
	System.out.println(conn);
}

}
