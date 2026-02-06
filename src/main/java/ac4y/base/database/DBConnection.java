package ac4y.base.database;

import ac4y.base.Ac4yException;
import ac4y.base.ExternalPropertyHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	public DBConnection(String aDriver, String aConnectionString,  String aHost, String aUser, String aPassword) throws ClassNotFoundException, SQLException {

		setDriver(aDriver);
		setHost(aHost);
		setUser(aUser);
		setPassword(aPassword);
		setConnectionString(aConnectionString);
		
		setConnection();

	} // DBConnection

	public DBConnection() throws ClassNotFoundException, SQLException, IOException, Ac4yException {

		setProperties(new ExternalPropertyHandler().getPropertiesFromClassPath("ac4y.properties"));

		setAttributesFromProperties(getProperties());
		
		setConnection();

	} // DBConnection

	public DBConnection(String PropertiesName) throws ClassNotFoundException, SQLException, IOException, Ac4yException {

		System.out.println("getPropertiesFromClassPath");
		setProperties(new ExternalPropertyHandler().getPropertiesFromClassPath(PropertiesName));

		setAttributesFromProperties(getProperties());
		
		setConnection();

	} // DBConnection

	public DBConnection(Properties aProperties) throws ClassNotFoundException, SQLException {

		setAttributesFromProperties(aProperties);
		
		setConnection();

	} // DBConnection

	public void setAttributesFromProperties(Properties aProperties){

		driver 				= aProperties.getProperty("driver");
		connectionString 	= aProperties.getProperty("connectionString");
		user 				= aProperties.getProperty("dbuser");
		password 			= aProperties.getProperty("dbpassword");
		
	} // setAttributesFromProperties
	
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	private String driver;
	private String host;
	private String user;
	private String password;
	private String connectionString;
	private Connection connection;
	private Properties properties;

	public void setConnection() throws ClassNotFoundException, SQLException {

		Class.forName(driver);

		connection = DriverManager.getConnection(connectionString, user, password);

	} // setConnection

	public Connection getConnection() {

		return connection;

	} // getConnection

} // DBConnection