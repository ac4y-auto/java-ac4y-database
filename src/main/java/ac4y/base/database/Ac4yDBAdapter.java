package ac4y.base.database;

import java.sql.Connection;

public class Ac4yDBAdapter {

	public Ac4yDBAdapter(){
	}
	
	public Ac4yDBAdapter(Connection aConnection){
		setConnection(aConnection); 
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	private Connection connection;

} // Ac4yDBAdapter