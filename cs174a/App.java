package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
//import sun.jvm.hotspot.debugger.posix.elf.ELFSectionHeader;
import oracle.jdbc.OracleConnection;

/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection _connection;                   // Example connection object to your DB.

	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App()
	{
		// TODO: Any actions you need.
	}

	/**
	 * This is an example access operation to the DB.
	 */
	void exampleAccessToDB()
	{
		// Statement and ResultSet are AutoCloseable and closed automatically.
		try( Statement statement = _connection.createStatement() )
		{
			try( ResultSet resultSet = statement.executeQuery( "select owner, table_name from all_tables where owner = 'C##ADILTRUONG'" ) )
			{
				while( resultSet.next() )
					System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
	}

	////////////////////////////// Implement all of the methods given in the interface /////////////////////////////////
	// Check the Testable.java interface for the function signatures and descriptions.

	@Override
	public String initializeSystem()
	{
		// Some constants to connect to your DB.
		final String DB_URL = "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
		final String DB_USER = "c##adiltruong";
		final String DB_PASSWORD = "3951795";

		// Initialize your system.  Probably setting up the DB connection.
		Properties info = new Properties();
		info.put( OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER );
		info.put( OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD );
		info.put( OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20" );

		try
		{
			OracleDataSource ods = new OracleDataSource();
			ods.setURL( DB_URL );
			ods.setConnectionProperties( info );
			_connection = (OracleConnection) ods.getConnection();

			// Get the JDBC driver name and version.
			DatabaseMetaData dbmd = _connection.getMetaData();
			System.out.println( "Driver Name: " + dbmd.getDriverName() );
			System.out.println( "Driver Version: " + dbmd.getDriverVersion() );

			// Print some connection properties.
			System.out.println( "Default Row Prefetch Value is: " + _connection.getDefaultRowPrefetch() );
			System.out.println( "Database Username is: " + _connection.getUserName() );
			System.out.println();

			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}
	}

	@Override
	public String dropTables(){
		String[] tableNames = {"GLOBALDATE","CUSTOMER", "ACCOUNT","OWNS","INTEREST","TRANSACTION"};
		try{
			System.out.println("Connecting to database");
			Statement stmt = _connection.createStatement();

			for(String i : tableNames){
				try{
					String sql = "DROP TABLE " + i + " CASCADE CONSTRAINTS";
					stmt.executeUpdate(sql);
					System.out.println("Table " + i + " deleted in given database...");
				} catch (Exception e){
					System.out.println("Couldn't drop " + i);
					System.out.println(e);
					return "1";
				}
			}
		} catch( Exception e ){
			System.out.println("Couldn't drop tables");
			System.out.println(e);
			return "1";
		}
		return "0";
    }

    @Override
    public String createTables(){
    	try{
    		System.out.println("Connecting to database");
    		Statement statement = _connection.createStatement();

    		try {
    			System.out.println("Creating table GlobalDate");
    			String sql = "CREATE TABLE GlobalDate (" +
    										"did INTEGER, " +
    										"globalDate CHAR(10), " +
    										"PRIMARY KEY (did))";
    			statement.executeUpdate(sql);
    		} catch (Exception e) {
    			System.out.println("Failed making GlobalDate");
    			System.out.println(e);
    			return "1";
    		}

    		try {
    			System.out.println("Creating table Customer");
    			String sql = "CREATE TABLE Customer (" +
    										"name CHAR(20) NOT NULL, " +
    										"taxID CHAR(11) NOT NULL, " +
    										"address CHAR(20), " +
    										"pin CHAR(4) DEFAULT '1717', " +
    										"PRIMARY KEY (taxID))"; 
    			statement.executeUpdate(sql);
    		} catch (Exception e) {
    			System.out.println("Failed making table Customer");
    			System.out.println(e);
    			return "1";
    		} 

    		try {
    			System.out.println("Creating table Account");
    			String sql = "CREATE TABLE Account (" +
    										"a_type CHAR(10), " +
    										"balance DECIMAL(*,2), " +
    										"bank_branch CHAR(20), " +
    										"a_id CHAR(10), " +
    										"isClosed INTEGER, " +
    										"interest_rate DECIMAL(*,2), " +
    										"closedDate DATE," +
    										"linked_id CHAR(10), " +
    										"current_month_int_added CHAR(3), " +
    										"pocket_month_fee DECIMAL(*,2), " +
    										"PRIMARY KEY (a_id)) ";
    			statement.executeUpdate(sql);
    		} catch (Exception e) {
    			System.out.println("Failed making table Account");
    			System.out.println(e);
    			return "1";
    		}

    		try {
    			System.out.println("Creating table Owns");
    			String sql = "CREATE TABLE Owns (" +
    										"taxID CHAR(9), " +
    										"a_id CHAR(10), " +
    										"isPrimaryOwner CHAR(3), " +
    										"PRIMARY KEY (taxID, a_id)) ";
    			statement.executeUpdate(sql);
    		} catch (Exception e) {
    			System.out.println("Failed making table Owns");
    			System.out.println(e);
    			return "1";
    		}
    		

    		try {
    			System.out.println("Creating table Transaction");
    			String sql = "CREATE TABLE Transaction (" +
    										"amount DECIMAL(*,2), " +
    										"t_date DATE, " +
    										"type CHAR(32), " +
    										"t_id CHAR(10), " +
    										"check_no CHAR(10), " +
    										"a_id CHAR(10), " +
    										"c_id CHAR(10) NOT NULL, " +
    										"PRIMARY KEY (a_id, c_id)) ";
    			statement.executeUpdate(sql);

    		} catch (Exception e) {
    			System.out.println("Failed making table Transaction");
    			System.out.println(e);
    			return "1";
    		}
    	} catch (Exception e) {
    		System.out.println("Failed making tables");
    		System.out.println(e);
    		return "1";
    	}

    	return "0";
    }
	/**
	 * Set system's date.
	 * @param year Valid 4-digit year, e.g. 2019.
	 * @param month Valid month, where 1: January, ..., 12: December.
	 * @param day Valid day, from 1 to 31, depending on the month (and if it's a leap year).
	 * @return a string "r yyyy-mm-dd", where r = 0 for success, 1 for error; and yyyy-mm-dd is the new system's date, e.g. 2012-09-16.
	 */

	@Override
	public String setDate( int year, int month, int day ){
		String sYear = Integer.toString(year);
		String sMonth = Integer.toString(month);
		String sDay = Integer.toString(day);

		if(sMonth.length() < 2) {
			sMonth = "0" + sMonth;
		}
		if (sDay.length() < 2) {
			sDay = "0" + sDay;
		}
		String s = sYear + "-" + sMonth + "-" + sDay;

		if(sYear.length() != 4) { //wrong year format
			System.out.println("Invalid Year");
			return "1"+s;
		} 

		else if (month < 1 || month > 12) {
			System.out.println("Invalid Month");
			return "1"+s;
		}

		else if (day < 1 || day >31) {
			System.out.println("Invalid Day");
			return "1" +s;
		}

		else {
			if (month == 2 && year%4 ==0) {
				if (day > 29){
					System.out.println("Invalid Day Feb Leap Year");
					return "1"+s;
				}
			}

			else if (month == 2) {
				if (day > 28) {
					System.out.println("Invalid Day Feb Not Leap Year");
					return "1"+s;
				}
			}

			else if (month == 4) {
				if (day > 30) {
					System.out.println("Invalid Day April");
					return "1"+s;
				}
			}

			else if (month == 6) {
				if (day > 30) {
					System.out.println("Invalid Day June");
					return "1"+s;
				}
			}

			else if (month == 9) {
				if (day > 30) {
					System.out.println("Invalid Day June");
					return "1"+s;
				}
			}

			else if (month == 11) {
				if (day >30) {
					System.out.println("Invalid Day Novemember");
					return "1"+s;
				}
			}
		}
		return "1" + s;

	}
	/**
	 * Example of one of the testable functions.
	 */
	@Override
	public String listClosedAccounts()
	{
		return "0 it works!";
	}

	/**
	 * Another example.
	 */
	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}

	@Override
	public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin )
	{
		return "r";
	}

	/**
	 * Create a new customer and link them to an existing checking or saving account.
	 * @param accountId Existing checking or saving account.
	 * @param tin New customer's Tax ID number.
	 * @param name New customer's name.
	 * @param address New customer's address.
	 * @return a string "r", where r = 0 for success, 1 for error.
	 */
	@Override
	public String createCustomer( String accountId, String tin, String name, String address )
	{
		return "r";
	}

	/**
	 * Deposit a given amount of dollars to an existing checking or savings account.
	 * @param accountId Account ID.
	 * @param amount Non-negative amount to deposit.
	 * @return a string "r old new" where
	 *         r = 0 for success, 1 for error;
	 *         old is the old account balance, with up to 2 decimal places (e.g. 1000.12, as with %.2f); and
	 *         new is the new account balance, with up to 2 decimal places.
	 */
	@Override
	public String deposit( String accountId, double amount )
	{
		return "r";
	}

	/**
	 * Show an account balance (regardless of type of account).
	 * @param accountId Account ID.
	 * @return a string "r balance", where
	 *         r = 0 for success, 1 for error; and
	 *         balance is the account balance, with up to 2 decimal places (e.g. with %.2f).
	 */
	@Override
	public String showBalance( String accountId )
	{
		return "r";
	}

	/**
	 * Move a specified amount of money from the linked checking/savings account to the pocket account.
	 * @param accountId Pocket account ID.
	 * @param amount Non-negative amount to top up.
	 * @return a string "r linkedNewBalance pocketNewBalance", where
	 *         r = 0 for success, 1 for error;
	 *         linkedNewBalance is the new balance of linked account, with up to 2 decimal places (e.g. with %.2f); and
	 *         pocketNewBalance is the new balance of the pocket account.
	 */
	@Override
	public String topUp( String accountId, double amount )
	{
		return "r";
	}

	/**
	 * Move a specified amount of money from one pocket account to another pocket account.
	 * @param from Source pocket account ID.
	 * @param to Destination pocket account ID.
	 * @param amount Non-negative amount to pay.
	 * @return a string "r fromNewBalance toNewBalance", where
	 *         r = 0 for success, 1 for error.
	 *         fromNewBalance is the new balance of the source pocket account, with up to 2 decimal places (e.g. with %.2f); and
	 *         toNewBalance is the new balance of destination pocket account, with up to 2 decimal places.
	 */

	@Override
	public String payFriend( String from, String to, double amount )
	{
		return "r";
	}
}
