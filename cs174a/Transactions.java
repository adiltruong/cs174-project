package cs174a;

import java.sql.*;
import java.util.*;
import oracle.jdbc.OracleConnection;
import java.io.*;
import cs174a.App;


public class Transactions extends App{
    private OracleConnection _connection;
    public Transactions(OracleConnection _connection){
        this._connection = _connection;
    }

	private int [] daysInMonthRegular = {31,28,31,30,31,30,31,31,30,31,30,31};
    private int [] daysInMonthLeap = {31,29,31,30,31,30,31,31,30,31,30,31};
    //functions
    public String deposit(String accountId, double amount){
        //in app.java
        double oldBal = 0.0;
		double newBal = 0.0;
		
		if (checkClosed(accountId)) {
			return "1";
		}
		try {
			Statement stmt = _connection.createStatement();

			try {
				ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId)+" AND a_type = 'Pocket'");
				if (rs.next()){
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
						stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'deposit', '"+generateRandomChars(9)+"', "+parse(accountId)+", NULL, NULL)");
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
    public String topUp( String accountId, double amount ){
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
						return "1";
					}
					stmt.executeQuery("UPDATE Account SET balance = balance +"+l_amount+" WHERE a_id = "+parse(accountId));
					stmt.executeQuery("UPDATE Account SET balance = balance -"+amount+" WHERE a_id = "+parse(linkedId));
					stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'top-up', '"+generateRandomChars(9)+"', "+parse(accountId)+", "+parse(linkedId)+", NULL)");
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
    public String payFriend( String from, String to, double amount ){
		//check if both friends have a pocket account
		//check if both friends have a valid balance
		//from balance = balance - amount
		//to balance = balance + amount

		if(checkClosed(from)){
			return "1";
		}
    	if(checkClosed(to)){
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
				stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'pay-friend', '"+generateRandomChars(9)+"', "+parse(to)+", "+parse(from)+", NULL)");
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

	//check if accountType is not POCKET
	//balance Too Low, cant use
	//

	public String withdraw(String accountId, double amount){
		try {
			Statement stmt = _connection.createStatement();

			try {
				ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId)+" AND a_type = 'Pocket'");
				if (rs.next()){
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
    			if (rs.next()){
        			if(balTooLow(accountId, amount)){
            		return "1";
        			}
        		stmt.executeQuery("UPDATE Account SET balance = balance -"+amount+" WHERE a_id = "+parse(accountId));
        		stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'withdraw', '"+generateRandomChars(9)+"', NULL, "+parse(accountId)+", NULL)");
    			}
    			closeAccountBalanceCheck(accountId);
			} catch(Exception e) {
    			System.out.println(e);
    			return "1";
			}

		} catch (Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1";
		}

		return "0";
	}

	public String purchase(String accountId, double amount) {
		try {
			Statement stmt = _connection.createStatement();

			try {
    			ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId)+" AND a_type = 'POCKET'");
    			if (rs.next()){
					if (isFirstTransactionOfMonth(accountId)) {
						amount = amount+5;
					}
        			if(balTooLow(accountId, amount)){
            			return "1";
        			}
        			stmt.executeQuery("UPDATE Account SET balance = balance -"+amount+" WHERE a_id = "+parse(accountId));
        			stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'withdraw', '"+generateRandomChars(9)+"', NULL, "+parse(accountId)+", NULL)");
    				closeAccountBalanceCheck(accountId);
				}
			} catch(Exception e) {
    			System.out.println(e);
    			return "1";
			}

		} catch (Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1";
		}
		return "0";
	}

	public String transfer(String from, String to, double amount){
		if(amount > 2000){
			return "1";
		}

		if(checkClosed(from)){
			return "1";
		}
    	if(checkClosed(to)){
			return "1";
		}

      	try{
			Statement stmt = _connection.createStatement();
        	ResultSet rs = stmt.executeQuery("SELECT * FROM Owns O1, Owns O2, Account A1, Account A2 "+
			 									"WHERE O1.taxID = O2.taxID "+ 
												 "AND O1.a_id = "+parse(from)+" AND O1.a_id = A1.a_id "+ 
												 "AND (A1.a_type = 'STUDENT_CHECKING' OR A1.a_type = 'INTEREST_CHECKING' OR A1.a_type = 'SAVINGS') "+ 
												 "AND O2.a_id = "+parse(to)+" AND O2.a_id = A2.a_id "+ 
												 "AND (A2.a_type = 'STUDENT_CHECKING' OR A2.a_type = 'INTEREST_CHECKING' OR A2.a_type = 'SAVINGS')"
											); 
			
        	if(rs.next()) {
          		if(balTooLow(from, amount)){
            		return "1";
          		}
          		stmt.executeQuery("UPDATE Account SET balance = balance +"+amount+" WHERE a_id = "+parse(to));
				stmt.executeQuery("UPDATE Account SET balance = balance -"+amount+" WHERE a_id = "+parse(from));
				stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'transfer', '"+generateRandomChars(9)+"', "+parse(to)+", "+parse(from)+", NULL)");
				closeAccountBalanceCheck(from);
        		closeAccountBalanceCheck(to);
				return "0";
        	}

			
      	}catch(Exception e){
			System.out.println(e);
        	return "1";
      	}

		return "1";
	}

	public String collect(String accountId, double amount) {
		if (checkClosed(accountId)){
			return "1";
		}
		double linkedNewBalance = 0.0;
		double pocketNewBalance = 0.0;
		double p_amount = amount + 0.03*amount;
		String linkedId = "";
		try {
			Statement stmt = _connection.createStatement();

			try {
				if (isFirstTransactionOfMonth(accountId)) {
					p_amount = amount-5;
				}
				ResultSet rs = stmt.executeQuery("SELECT * FROM Account WHERE a_id = "+parse(accountId)+" AND a_type = 'POCKET'");
				if (rs.next()) {
					linkedId = rs.getString("linked_id");
					if (balTooLow(accountId, p_amount)){
						return "1";
					}
					stmt.executeQuery("UPDATE Account SET balance = balance -"+p_amount+" WHERE a_id = "+parse(accountId));
					stmt.executeQuery("UPDATE Account SET balance = balance +"+amount+" WHERE a_id = "+parse(linkedId));
					stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'collect', '"+generateRandomChars(9)+"', "+parse(linkedId)+", "+parse(accountId)+", NULL)");
					closeAccountBalanceCheck(linkedId);
				}
			} catch(Exception e) {
				System.out.println("Couldn't execute operations");
				System.out.println(e);
				return "1";
			}

		} catch(Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return "1";
		}
		return "0 ";
	}
	//3%fee

	public String wire(String from, String to, double amount) {
		double fee_amount = amount + 0.02*amount;
		if(checkClosed(from)){
			return "1";
		}
    	if(checkClosed(to)){
			return "1";
		}

      	try{
			Statement stmt = _connection.createStatement();
        	ResultSet rs = stmt.executeQuery("SELECT * FROM Account A1, Account A2 " +
											"WHERE A1.a_id = "+parse(from)+" " + 
											"AND (A1.a_type='INTEREST_CHECKING' OR A1.a_type='STUDENT_CHECKING' OR A1.a_type='SAVINGS') " +
											"AND A2.a_id= "+parse(to)+" " + 
											"AND (A2.a_type='INTEREST_CHECKING' OR A2.a_type='STUDENT_CHECKING' OR A2.a_type='SAVINGS')");
			if(rs.next()) {
          		if(balTooLow(from, fee_amount)){
            		return "1";
          		}
          		stmt.executeQuery("UPDATE Account SET balance = balance +"+amount+" WHERE a_id = "+parse(to));
				stmt.executeQuery("UPDATE Account SET balance = balance -"+fee_amount+" WHERE a_id = "+parse(from));
				stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'wire', '"+generateRandomChars(9)+"', "+parse(to)+", "+parse(from)+", NULL)");
			}
        	closeAccountBalanceCheck(from);
        	closeAccountBalanceCheck(to);
      	}catch(SQLException e){
			System.out.println(e);
			e.printStackTrace();
        	return "1";
      	}

		return "0";
	}
	//Customer must be owner of both
	//2% fee

	public String writeCheck(String accountId, double amount) {
		try {
			Statement stmt = _connection.createStatement();
			try {
					ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId)+" AND (a_type = 'Pocket' OR a_type = 'SAVINGS')");
					if (rs.next()){
						return "1";
					}
					rs.close();
			}catch(Exception e){
				System.out.println("Couldn't select from account for type");
				System.out.println(e);
				return "1";
			}
				
			ResultSet rs = stmt.executeQuery("SELECT a_id FROM Account WHERE a_id = "+parse(accountId));
			if (rs.next()){
				if(balTooLow(accountId, amount)){
					return "1";
				}
				stmt.executeQuery("UPDATE Account SET balance = balance -"+amount+" WHERE a_id = "+parse(accountId));
				stmt.executeQuery("INSERT INTO Transaction VALUES ( "+amount+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'write_check', '"+generateRandomChars(9)+"', NULL, "+parse(accountId)+", '" + generateRandomChars(20) + "')");
				closeAccountBalanceCheck(accountId);
			}
		
		}catch(Exception e){
			System.out.println("Couldn't connect to database");
			System.out.println(e);
			return "1";
		}


		
		return "0";
	}

	public double accrueInterest(String a_id, double endBalance, double monthlyRate) {
        ResultSet pos_transactions_days = this.executeQ("SELECT EXTRACT(DAY FROM t_date) AS day FROM Transaction WHERE rec_id = '"+a_id+"' AND EXTRACT(MONTH FROM t_date) = (SELECT MAX(EXTRACT(MONTH FROM GlobalDate)) FROM GlobalDate)");
        ResultSet pos_transactions_amounts = this.executeQ("SELECT AMOUNT FROM Transaction WHERE rec_id = '"+a_id+"' AND EXTRACT( MONTH FROM t_date) = (SELECT MAX(EXTRACT(MONTH from globalDate)) FROM GlobalDate)");
        ResultSet neg_transactions_days = this.executeQ("SELECT EXTRACT(DAY FROM t_date) AS day FROM Transaction WHERE send_id = '"+a_id+"' AND EXTRACT(MONTH FROM t_date) = (SELECT MAX(EXTRACT(MONTH FROM GlobalDate)) FROM GlobalDate)");
        ResultSet neg_transactions_amounts = this.executeQ("SELECT AMOUNT FROM Transaction WHERE send_id = '"+a_id+"' AND EXTRACT( MONTH FROM t_date) = (SELECT MAX(EXTRACT(MONTH from globalDate)) FROM GlobalDate)");
        double initial = calculateInitialBalance(pos_transactions_amounts, neg_transactions_amounts, endBalance);

        int currentMonth = Integer.parseInt(getDate().substring(5,7));
        int year = Integer.parseInt(getDate().substring(0,4));



        if(year % 4==0) {
            double total = 0.0;
            total+=intHelper(pos_transactions_amounts, pos_transactions_days, 1, currentMonth);
            total+=intHelper(neg_transactions_amounts, neg_transactions_days, -1, currentMonth);
            total+=initial*daysInMonthLeap[currentMonth-1];
            return total*(monthlyRate/100.0)/daysInMonthLeap[currentMonth-1];
        }

        else {
            double total = 0.0;
            total+=intHelper(pos_transactions_amounts, pos_transactions_days, 1, currentMonth);
            total+=intHelper(neg_transactions_amounts, neg_transactions_days, -1, currentMonth);
            total+=initial*daysInMonthRegular[currentMonth-1];
            return total*(monthlyRate/100.0)/daysInMonthRegular[currentMonth-1];
        }
    }

    public double intHelper(ResultSet transactions_amounts, ResultSet transactions_days, int sign, int currentMonth){
        double total=0.0;
        double [] amounts = parseRsAsDouble(transactions_amounts, "amount");
        double [] days = parseRsAsDouble(transactions_days, "day");
        int year = Integer.parseInt(getDate().substring(0,4));
		if (year % 4 == 0) {
				for(int i=0;i<amounts.length;i++){

					total+=sign*amounts[i]*(daysInMonthLeap[currentMonth-1] - days[i]);

				}
		} else {
				for(int i=0;i<amounts.length;i++){

					total+=sign*amounts[i]*(daysInMonthRegular[currentMonth-1] - days[i]);

				}
		}
        return total;
    }

    //helper functions

	public double calculateInitialBalance(ResultSet posAmount, ResultSet negAmount, double inital){
        double [] pos = parseRsAsDouble(posAmount, "amount");
        for(double p : pos){
            inital -= p;
        }
        double [] neg = parseRsAsDouble(negAmount, "amount");
        for(double n : neg){
            inital += n;
        }
        return inital;
    }

	public ResultSet executeQ(String query){
        ResultSet rs = null;
        try{
            Statement stmt = this._connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(query);
        }
        catch(Exception e){
            System.out.print("Something went wrong querying the server");
            e.printStackTrace();
        }
        return rs;
    }

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
			return "1 "+s;
		} 

		else if (month < 1 || month > 12) {
			return "1 "+s;
		}

		else if (day < 1 || day >31) {
			return "1 " +s;
		}

		else {
			if (month == 2 && year%4 ==0) {
				if (day > 29){
					return "1 "+s;
				}
			}

			else if (month == 2) {
				if (day > 28) {
					return "1 "+s;
				}
			}

			else if (month == 4) {
				if (day > 30) {
					return "1 "+s;
				}
			}

			else if (month == 6) {
				if (day > 30) {
					return "1 "+s;
				}
			}

			else if (month == 9) {
				if (day > 30) {
					return "1 "+s;
				}
			}

			else if (month == 11) {
				if (day >30) {
					return "1 "+s;
				}
			}
		}

		try {

				Statement stmt = _connection.createStatement();
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
    public static String generateRandomChars(int length) {
    	String candidateChars  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    	StringBuilder sb = new StringBuilder();
    	Random random = new Random();
    	for (int i = 0; i < length; i++) {
        	sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
    	}

    	return sb.toString();
  	}

	public boolean checkClosed(String a_id) {
		try {
			Statement stmt = _connection.createStatement();

			try {
				ResultSet rs = stmt.executeQuery("SELECT isClosed FROM Account WHERE a_id = "+parse(a_id));
				if (rs.next()){
					int closed = rs.getInt("isClosed");
					return closed > 0;
				}
				rs.close();
			} catch (Exception e) {
				System.out.println("Couldn't select closed");
				System.out.println(e);
			}
		} catch (Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
			return false;
		}
		return false;
	}

	public void closeAccountBalanceCheck(String a_id) {
		try {
			Statement stmt = _connection.createStatement();
			try {
				stmt.executeQuery("UPDATE Account SET isClosed = 1 WHERE balance <= 0.01 AND a_id="+parse(a_id));
			} catch (Exception e) {
				System.out.println("Couldn't update");
			}
		} catch(Exception e) {
			System.out.println("Couldn't connect to DB");
			System.out.println(e);
		}

	}

	public boolean balTooLow(String a_id, double amount) {
		double bal = 0.0;
		try(Statement stmt = _connection.createStatement()){

			String sql = "SELECT balance " +
					"FROM Account " +
					"WHERE a_id = " + parse(a_id);
			ResultSet r = stmt.executeQuery(sql);
			if (r.next()) {
				bal = r.getDouble("balance");
			}
		} catch(Exception e) {
			System.out.println("Couldn't select balance");
			return false;
		}
		return amount > bal;
	}

	public boolean isFirstTransactionOfMonth(String a_id){
    	try{
			Statement stmt = _connection.createStatement();
      		ResultSet rs = stmt.executeQuery("SELECT * FROM Transaction T WHERE (T.rec_id = "+parse(a_id)+" OR T.send_id = "+parse(a_id)+") AND extract(month FROM T.t_date) = (select MAX(extract(month FROM C.globalDate)) FROM globalDate C)");
      		if(rs.next()){
        		return false;
      		}
    	} catch(Exception e){
			System.out.println(e);
    	}
    	return true;
  	}

    public static String parse(String s){
        return "'" + s.replace("'", "''") + "'";
    }

    public static String parseNULL(String s){
        if(s ==null || s.isEmpty()){
            return "NULL";
        }else{
            return parse(s);
        }
  	}

	public String[] parseRsAsString(ResultSet rs, String key){
        try{
            ArrayList<String> rsStringOutput = new ArrayList<String>();     
   
            while(rs.next()){
                String id = rs.getString(key);
                rsStringOutput.add(id.trim());        
            }
            rs.beforeFirst();
            String[] a = new String[rsStringOutput.size()];
            rsStringOutput.toArray(a);
            rs.close();
            return a;
        }catch(Exception e){
            System.out.println("parserS: " + key + " " + e );
        } 
        String[] temp = {"no data"};
        return temp;
    }

    public double[] parseRsAsDouble(ResultSet rs, String key){
        try{
            ArrayList<Double> rsDoubleOutput = new ArrayList<Double>();
            while(rs.next()) {
                Double id = rs.getDouble(key);
                rsDoubleOutput.add(id);
            }
            rs.beforeFirst();
            double[] temp = new double[rsDoubleOutput.size()];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = rsDoubleOutput.get(i);
            }
            return temp;
        }catch(Exception e){
            System.out.println("parserD: "+ key + " " + e);
        }
        double[] temp = {1.1};
        return temp;
    }

}
