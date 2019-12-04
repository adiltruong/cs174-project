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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
	public boolean login(String taxID, String pin){
        try{
            Statement stmt = _connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer WHERE taxID = '"+taxID+"' AND pin = '"+pin+"'");
            if (rs.next()) {
                String currentUser = rs.getString("taxID");
                return true;
            }
        }catch(Exception e){
            System.out.println("big oof: "+e);
        }
        return false;
    }

	public OracleConnection getConnection(){
		return _connection;
	}

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
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}

		dropTables();
		createTables();
		//populate_customers("cs174a/inputs/customers.csv");
		//populate_accounts("cs174a/inputs/accounts.csv");
		populate_interest("cs174a/inputs/interest.csv");
		return "0";
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
    										"globalDate DATE)";
    			statement.executeUpdate(sql);
    		} catch (Exception e) {
    			System.out.println("Failed making GlobalDate");
    			System.out.println(e);
    			return "1";
    		}

    		try {
    			System.out.println("Creating table Customer");
    			String sql = "CREATE TABLE Customer (" +
    										"name CHAR(64), " +
    										"taxID CHAR(11), " +
    										"address CHAR(64), " +
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
    										"a_id CHAR(10), " +
    										"a_type CHAR(32), " +
    										"bank_branch CHAR(64), " +
    										"primaryOwner CHAR(11), " +
    										"isClosed INTEGER, " +
    										"linked_id CHAR(10), " +
    										"balance REAL, " +
    										"PRIMARY KEY (a_id), " +
    										"FOREIGN KEY (primaryOwner) REFERENCES Customer(taxID) ON DELETE CASCADE, " +
    										"FOREIGN KEY (linked_id) REFERENCES Account(a_id) ON DELETE CASCADE, " +
    										"CONSTRAINT CHK_Balance CHECK (balance >= 0.01), " +
    										"CONSTRAINT CHK_Link CHECK ((a_type = 'POCKET' AND linked_id IS NOT NULL) OR (a_type != 'POCKET' AND linked_id IS NULL)))"; 
    			statement.executeUpdate(sql);
    		} catch (Exception e) {
    			System.out.println("Failed making table Account");
    			System.out.println(e);
    			return "1";
    		}

    		try {
    			System.out.println("Creating table Owns");
    			String sql = "CREATE TABLE Owns (" +
    										"taxID CHAR(11), " +
    										"a_id CHAR(10), " +
    										"PRIMARY KEY (taxID, a_id), " +
    										"FOREIGN KEY (taxID) REFERENCES Customer ON DELETE CASCADE, " +
    										"FOREIGN KEY (a_id) REFERENCES Account ON DELETE CASCADE )"; 
    			statement.executeUpdate(sql);
    		} catch (Exception e) {
    			System.out.println("Failed making table Owns");
    			System.out.println(e);
    			return "1";
    		}
    		
    		try {
    			System.out.println("Creating table Interest");
    			String sql = "CREATE TABLE Interest (" +
    										"type CHAR(32), " +
    										"int_rate REAL )";
    			statement.executeUpdate(sql);
    		} catch (Exception e) {
    			System.out.println("Failed making table Owns");
    			System.out.println(e);
    			return "1";
    		}

    		try {
    			System.out.println("Creating table Transaction");
    			String sql = "CREATE TABLE Transaction (" +
    										"amount REAL, " +
    										"t_date DATE, " +
    										"type CHAR(32), " +
    										"t_id CHAR(11), " +
    										"rec_id CHAR(10), " + 
    										"send_id CHAR(10), " +
											"check_no CHAR(20)," +
    										"PRIMARY KEY (t_id), " +
    										"FOREIGN KEY (rec_id) REFERENCES Account(a_id) ON DELETE CASCADE," +
    										"FOREIGN KEY (send_id) REFERENCES Account(a_id) ON DELETE CASCADE )"; 
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
			return "1 "+s;
		} 

		else if (month < 1 || month > 12) {
			System.out.println("Invalid Month");
			return "1 "+s;
		}

		else if (day < 1 || day >31) {
			System.out.println("Invalid Day");
			return "1 " +s;
		}

		else {
			if (month == 2 && year%4 ==0) {
				if (day > 29){
					System.out.println("Invalid Day Feb Leap Year");
					return "1 "+s;
				}
			}

			else if (month == 2) {
				if (day > 28) {
					System.out.println("Invalid Day Feb Not Leap Year");
					return "1 "+s;
				}
			}

			else if (month == 4) {
				if (day > 30) {
					System.out.println("Invalid Day April");
					return "1 "+s;
				}
			}

			else if (month == 6) {
				if (day > 30) {
					System.out.println("Invalid Day June");
					return "1 "+s;
				}
			}

			else if (month == 9) {
				if (day > 30) {
					System.out.println("Invalid Day June");
					return "1 "+s;
				}
			}

			else if (month == 11) {
				if (day >30) {
					System.out.println("Invalid Day Novemember");
					return "1 "+s;
				}
			}
		}

		try {

				Statement stmt = _connection.createStatement();
				System.out.println("Writing to table GlobalDate");
				try{

					String sqlDate = "DATE'"+s+"'";
					String sql = "INSERT INTO GlobalDate VALUES ("+sqlDate+")";
					stmt.executeUpdate(sql);

				} catch(Exception e) {
					System.out.println("Failed to write in GlobalDate.");
					System.out.println(e);
				}
			} catch (Exception e) {
				System.out.println("Failed to connect to DB.");
				System.out.println(e);
			}

		return "0 " + s;
	}

	public String getDate(){
    	try{
    		Statement stmt = _connection.createStatement();
      		ResultSet rs = stmt.executeQuery("SELECT MAX(globalDate) AS \"Recent Date\" FROM GlobalDate");
      		
			if(rs.next()) {
				String date = rs.getString("Recent Date");
        		System.out.println(date);
				rs.close();
        		return date;
			}
			
    	} catch(Exception e){
      		System.out.println(e);
    	}
    	return "";
  	}
	
	public String setInterestRate(AccountType accountType, double rate)
	{
		if (accountType == AccountType.POCKET || accountType == AccountType.STUDENT_CHECKING) {
			System.out.println("Can't switch interest rate by type");
			return "Type issue";
		}

		try {
			Statement stmt = _connection.createStatement();
			stmt.executeQuery("UPDATE Interest SET int_rate = "+rate+" WHERE type = '"+accountType+"' ");
		} catch(Exception e) {
			System.out.println("Couldn't update interest");
			System.out.println(e);
			return "1";
		}
		return "0";
	}
	/**
	 * Example of one of the testable functions.
	 */
	@Override
	public String listClosedAccounts()
	{
		String s = "0";

		try {
			Statement stmt = _connection.createStatement();

			try {
				String sql = "SELECT a_id " +
							 "FROM Account " +
							 "WHERE isClosed = 1";
				ResultSet r = stmt.executeQuery(sql);

				if (r==null){
					return s;
				}

				while(r.next()) {
					s += r.getString("a_id");
					s += " ";
				}
			} catch (Exception e) {
				System.out.println("Failed select a_id");
				System.out.println(e);
				return "1";
			}
		} catch (Exception e) {
			System.out.println("Failed connecting to DB");
			System.out.println(e);
		}
		return s;
	}

	/**
	 * Another example.
	 */
	/**
	 * Create a new checking or savings account.
	 * If customer is new, then their name and address should be provided.
	 * @param accountType New account's checking or savings type.
	 * @param id New account's ID.
	 * @param initialBalance Initial account balance.
	 * @param tin Account's owner Tax ID number - it may belong to an existing or new customer.
	 * @param name [Optional] If customer is new, this is the customer's name.
	 * @param address [Optional] If customer is new, this is the customer's address.
	 * @return a string "r aid type balance tin", where
	 *         r = 0 for success, 1 for error;
	 *         aid is the new account id;
	 *         type is the new account's type (see the enum codes above, e.g. INTEREST_CHECKING);
	 *         balance is the account's initial balance with 2 decimal places (e.g. 1000.34, as with %.2f); and
	 *         tin is the Tax ID of account's primary owner.
	 */
	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address )
	{
		if (initialBalance < 1000.0) { 
			System.out.println("balance too low");
			return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
		}

		if (accountType == AccountType.POCKET ) {
			System.out.println("Invalid Type");
			return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
		}

		try {
			Statement stmt = _connection.createStatement();
			try { //check if acc exists
				ResultSet rs = stmt.executeQuery("SELECT a_id FROM Owns WHERE a_id = "+parse(id));
				if (rs.next()) {
					System.out.println("Account exists");
					return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
				}
				rs.close();
			} catch(Exception e) {
				System.out.println(e);
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}
		} catch(Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
		}

		try {
			Statement stmt = _connection.createStatement();
			try {

				ResultSet ress = stmt.executeQuery("SELECT taxID FROM Customer WHERE taxID = "+parse(tin));
			
				if (ress.next()== false) {
					System.out.println("Customer does not exist");
					try {
						if(name == null || address == null) {
							System.out.println("No values to create customer");
							return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
						}
						stmt.executeQuery("INSERT INTO Customer VALUES ("+parse(name)+", "+parse(tin)+", "+parse(address)+", 1717)");
					} catch(Exception e) {
						System.out.println("Couldn't connect to Customer");
						System.out.println(e);
						return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
					}

				}

				try {
					stmt.executeQuery("INSERT INTO Account VALUES ("+parse(id)+", '"+accountType+"', 'CSIL', "+parse(tin)+", 0, NULL, "+initialBalance+")");
					System.out.println("Account linked to Customer");
				} catch(Exception e) {
					System.out.println("Couldn't add to Account");
					System.out.println(e);
					return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
				}

				try {
					stmt.executeQuery("INSERT INTO Owns VALUES ( "+parse(tin)+", "+parse(id)+")");
				} catch(Exception e) {
					System.out.println("Couldn't add to Owns");
					System.out.println(e);
					return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
				}

				try {
					stmt.executeQuery("INSERT INTO Transaction VALUES ( "+initialBalance+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'deposit', '"+generateRandomChars(9)+"', NULL, "+parse(id)+", NULL)");
				} catch(Exception e) {
					System.out.println("Couldn't add to Transactions");
					System.out.println(e);
					return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
				}

			} catch (Exception e) {
				System.out.println("Couldn't perform operations");
				System.out.println(e);
				return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
			}

		} catch(Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1 " + id + " " + accountType + " " + initialBalance + " " + tin;
		}

		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}

	@Override
	public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin )
	{
		boolean linkedIdExists = false;
		int closed = 0;
		double linkedBalance = 0.0;
		String acctype = "";
		String tid = "";
		try { //check if pocket account exists
			Statement stmt = _connection.createStatement();
			try { //check if acc exists
				ResultSet rs = stmt.executeQuery("SELECT a_id FROM Owns WHERE a_id = "+parse(id));
				if (rs.next()) {
					System.out.println("Account exists");
					return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
				}
				rs.close();
			} catch(Exception e) {
				System.out.println(e);
				return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
			}
		} catch(Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
		}

		try { //check if linked account exists
			Statement stmt = _connection.createStatement();
			try { //check if acc exists
				ResultSet rs = stmt.executeQuery("SELECT * FROM Account WHERE a_id = "+parse(linkedId));
				if (rs.next()) {
					System.out.println("linkedAccount exists");
					linkedIdExists = true;
					closed = rs.getInt("isClosed");
					linkedBalance = rs.getDouble("balance");
					acctype = rs.getString("a_type");
				}
				rs.close();
			} catch(Exception e) {

				System.out.println(e);
				return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
			}

			if (linkedIdExists == false || closed == 1 || linkedBalance - initialTopUp <= 0.01 || acctype == "POCKET") {
				return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
			}

			try {
				stmt.executeQuery("UPDATE Account SET balance = "+ (linkedBalance-initialTopUp)+" WHERE a_id  = "+parse(linkedId));
			} catch(Exception e) {
				System.out.println("Failed to update account");
				return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
			}

			try {
				stmt.executeQuery("INSERT INTO Account VALUES ("+parse(id)+", 'POCKET', 'CSIL', "+parse(tin)+", 0, "+parse(linkedId)+", "+initialTopUp+")");
				System.out.println("Inserted to Account");
			} catch(Exception e) {
				System.out.println("Couldn't add to Account");
				System.out.println(e);
				return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
			}

			try {
				stmt.executeQuery("INSERT INTO Owns VALUES ( "+parse(tin)+", "+parse(id)+")");
			} catch(Exception e) {
				System.out.println("Couldn't add to Owns");
				System.out.println(e);
				return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
			}

			try {
				stmt.executeQuery("INSERT INTO Transaction VALUES ( "+initialTopUp+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'TOPUP', '"+generateRandomChars(9)+"', "+parse(linkedId)+", "+parse(id)+", NULL)");
			} catch(Exception e) {
				System.out.println("Couldn't add to Transactions");
				System.out.println(e);
				return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
			}

		} catch(Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1 " + id + " POCKET " + initialTopUp+ " " + tin;
		}
		return "0 " + id + " POCKET " + initialTopUp+ " " + tin;
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
		try {
			Statement stmt = _connection.createStatement();
			try {
				ResultSet rs = stmt.executeQuery("SELECT taxID FROM Customer WHERE taxID = "+parse(tin));
				if(rs.next()) {
					System.out.println("Customer exists already");
					return "1";
				}
				rs.close();
			} catch(Exception e) {
				System.out.println("Couldn't select from Customer");
				System.out.println(e);
				return "1";
			}

			try {
				ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId)+" AND a_type = 'Pocket'");
				if (rs.next()){
					System.out.println("account type invalid");
					return "1";
				}
				rs.close();
			} catch(Exception e){
				System.out.println("Couldn't select from account for type");
				System.out.println(e);
				return "1";
			}

			try {
				ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId));
				if(!rs.next()) {
					System.out.println("Account doesn't exist");
					return "1";
				}
				rs.close();
			} catch(Exception e){
				System.out.println("Couldn't select from account for exist");
				System.out.println(e);
				return "1";
			}

			try {
				stmt.executeQuery("INSERT INTO Customer VALUES ("+parse(name)+", "+parse(tin)+", "+parse(address)+", 1717)");
			} catch(Exception e) {
				System.out.println("Couldn't insert to Customer");
				System.out.println(e);
			}

			try {
				stmt.executeQuery("INSERT INTO Owns VALUES ("+parse(tin)+", "+parse(accountId)+")");
			} catch(Exception e){
				System.out.println("Couldn't insert to Owns");
				System.out.println(e);
				return "1";
			}

		} catch(Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1";
		}

		return "0";
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
		double oldBal = 0.0;
		double newBal = 0.0;
		
		if (checkClosed(accountId)) {
			System.out.println("account is closed");
			return "1";
		}
		try {
			Statement stmt = _connection.createStatement();

			try {
				ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId)+" AND a_type = 'Pocket'");
				if (rs.next()){
					System.out.println("account type invalid");
					return "1";
				}
				rs.close();
			} catch(Exception e){
				System.out.println("Couldn't select from account for type");
				System.out.println(e);
				return "1";
			}

			try {
				ResultSet rs = stmt.executeQuery("SELECT balance FROM Account WHERE a_id = "+parse(accountId));
				if (rs.next()){
					oldBal = rs.getDouble("balance");
					newBal = oldBal + amount;
				}
				rs.close();
			} catch(Exception e) {
				System.out.println("Couldn't select balance");
				System.out.println(e);
				return "1";
			}

			try {
				ResultSet rs = stmt.executeQuery("UPDATE Account SET balance = balance + "+amount+" WHERE a_id= "+parse(accountId));
				if(rs.next()){
					try {
						stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'deposit', '"+generateRandomChars(9)+"', NULL, "+parse(accountId)+", NULL)");
					} catch(Exception e) {
						System.out.println("Couldn't add to Transactions");
						System.out.println(e);
						return "1 ";
					}

				}
			} catch (Exception e) {
				System.out.println("Couldn't update Account");
				System.out.println(e);
				return "1";
			}
		} catch (Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
		}
		return "0 " +oldBal+" "+newBal;
	}

	/**
	 * Show an account balance (regardless of type of account).
	 * @param accountId Account ID.
	 * @return a string "r balance", where
	 *         r = 0 for success, 1 for error; and
	 *         balance is the account balance, with up to 2 decimal places (e.g. with %.2f).
	 */

	//select from accounts
	//update balance = balance + amount

	@Override
	public String showBalance( String accountId )
	{
		//check if id exists, if not return "1";
		//check if isClosed = 1, if yes, return "0 0.00"
		//return "0"+ Double.toString();
		try(Statement stmt = _connection.createStatement()){

			String sql = "SELECT a_id, balance, isClosed " +
					"FROM Account " +
					"WHERE a_id = " + parse(accountId);
			ResultSet r = stmt.executeQuery(sql);
			if (!r.next())
				return "1";
			else if (r.getInt("isClosed") == 1)
				return "0 0.00";
			else
				return "0 " + r.getString("balance");
		}catch (Exception e) {
			System.out.println("Failed select a_id");
			System.out.println(e);
			return "1";
		}
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
		//basic functionality: check account won't close, accounts exist, $5 fee to linked account
		//pocketBalance, linkedBalance after, deny if make balance < 0 or close if balance < 0.0
		if (checkClosed(accountId)){
			return "1";
		}
		double linkedNewBalance = 0.0;
		double pocketNewBalance = 0.0;
		double l_amount = amount;
		String linkedId = "";
		try {
			Statement stmt = _connection.createStatement();

			try {
				if (isFirstTransactionOfMonth(accountId)) {
					l_amount = amount-5;
				}
				ResultSet rs = stmt.executeQuery("SELECT * FROM Account WHERE a_id = "+parse(accountId)+" AND a_type = 'POCKET'");
				if (rs.next()) {
					linkedId = rs.getString("linked_id");
					if (balTooLow(linkedId, l_amount)){
						System.out.println("Bal Too Low");
						return "1";
					}
					stmt.executeQuery("UPDATE Account SET balance = balance +"+l_amount+" WHERE a_id = "+parse(accountId));
					stmt.executeQuery("UPDATE Account SET balance = balance -"+amount+" WHERE a_id = "+parse(linkedId));
					stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'top-up', '"+generateRandomChars(9)+"', "+parse(linkedId)+", "+parse(accountId)+", NULL)");
					closeAccountBalanceCheck(accountId);
				}
			} catch(Exception e) {
				System.out.println("Couldn't execute operations");
				System.out.println(e);
				return "1";
			}

			try {
				ResultSet rs = stmt.executeQuery("SELECT balance FROM Account WHERE a_id = "+parse(accountId));
				if (rs.next()){
					pocketNewBalance = rs.getDouble("balance");
				}
				rs.close();
			} catch(Exception e) {
				System.out.println("Couldn't select balance");
				System.out.println(e);
				return "1";
			}

			try {
				ResultSet rs = stmt.executeQuery("SELECT balance FROM Account WHERE a_id = "+parse(linkedId));
				if (rs.next()){
					linkedNewBalance = rs.getDouble("balance");
				}
				rs.close();
			} catch(Exception e) {
				System.out.println("Couldn't select balance");
				System.out.println(e);
				return "1";
			}

		} catch(Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1";
		}
		return "0 "+ linkedNewBalance + " " + pocketNewBalance;
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
		//check if both friends have a pocket account
		//check if both friends have a valid balance
		//from balance = balance - amount
		//to balance = balance + amount

		if(checkClosed(from)){
			System.out.println("From is closed");
			return "1";
		}
    	if(checkClosed(to)){
			System.out.println("To is closed");
			return "1";
		}

      	try{
			Statement stmt = _connection.createStatement();
        	ResultSet rs = stmt.executeQuery("SELECT * FROM Account A1, Account A2 WHERE A1.a_id = "+parse(from)+" AND A1.a_type= 'POCKET' AND "+"A2.a_id= "+parse(to)+" AND A2.a_type= 'POCKET'");
        	if(rs.next()) {
          		if(balTooLow(from, amount)){
            		return "1";
          		}
          		stmt.executeQuery("UPDATE Account SET balance = balance +"+amount+" WHERE a_id = "+parse(to));
				stmt.executeQuery("UPDATE Account SET balance = balance -"+amount+" WHERE a_id = "+parse(from));
				stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'pay-friend', '"+generateRandomChars(9)+"', "+parse(from)+", "+parse(to)+", NULL)");
          		if(isFirstTransactionOfMonth(from)){
            		stmt.executeQuery("UPDATE Account SET balance = balance-5 WHERE a_id= "+parse(from));
          		}
          		if(isFirstTransactionOfMonth(to)){
            		stmt.executeQuery("UPDATE Account SET balance = balance-5 WHERE a_id= "+parse(to));
          		}
        	}
        	closeAccountBalanceCheck(from);
        	closeAccountBalanceCheck(to);
      	}catch(Exception e){
			System.out.println(e);
        	return "1";
      	}
		return "0";
	}

	public static String parse(String s){
    	return "'" + s.replace("'", "''") + "'";
  	}

  	public static String parseNULL(String s){
    	if(s ==null || s.isEmpty()){
      		return "NULL";
    	}
    	else{
      		return parse(s);
    	}
  	}

	void populate_customers(String filename){

    	String line="";
    	try (Statement stmt = _connection.createStatement()) {
    		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      			while ((line = br.readLine()) != null) {
        			String[] columns = line.split(",");

        			String name = parse(columns[0]);
        			String taxID = parse(columns[1]);
        			String address = parse(columns[2]);
        			String pin = parse(columns[3]);

        			String query = "INSERT INTO Customer (name, taxID, address, pin) values ("+
          			name+", " + taxID + ", " + address + ", " + pin + ")";

        			stmt.executeQuery(query);

      			}
    		}
    		catch (IOException e) {
      			e.printStackTrace();
    		}
    	}
    	catch (Exception e) {
    		System.out.println("Couldn't connect to database");
    		System.out.println(e);
    	}
  	}
  	

  	void populate_accounts(String filename){
    	String line="";
    	try (Statement stmt = _connection.createStatement()) {
    		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      			while ((line = br.readLine()) != null) {
        			String[] columns = line.split(",");

        			String id = parse(columns[0]);
        			String type = parse(columns[1]);
        			String branch = parse(columns[2]);
        			String primary_owner = parse(columns[3]);
        			String linked_id = parseNULL(columns[4]);
        			String balance = parse(columns[5]);

        			String query = "INSERT INTO Account (a_id, a_type, bank_branch, PrimaryOwner,  isClosed, linked_id, balance ) values ("+
            			id+", " + type + ", " + branch + ", " + primary_owner + ", 0, " + linked_id +", "+balance+")";


        			stmt.executeQuery(query);

      			}
    		} catch (IOException e) {
      			e.printStackTrace();
    		}
  		} catch (Exception e) {
  			System.out.println("Couldn't connect to database");
  			System.out.println(e);
  		}
	}

	void populate_owns(String filename){
		String line="";
		try (Statement stmt = _connection.createStatement()) {
    		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      			while ((line = br.readLine()) != null) {
        			String[] columns = line.split(",");

        			String tax_id = parse(columns[0]);
        			String aid = parse(columns[1]);

        			String query = "insert into Owns (taxID, a_id) values ("+
          			tax_id+", " + aid + ")";

        			stmt.executeQuery(query);

      			}
    		} catch (IOException e) {
      			e.printStackTrace();
    		}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	void populate_interest(String filename){
		String line="";
		try (Statement stmt = _connection.createStatement()) {
    		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      			while ((line = br.readLine()) != null) {
        			String[] columns = line.split(",");

        			String a_type = parse(columns[0]);
        			String rate = columns[1];

        			String query = "INSERT INTO Interest VALUES ("+
          			a_type+", " + rate + ")";

        			stmt.executeQuery(query);

      			}
    		} catch (IOException e) {
      			e.printStackTrace();
    		}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
