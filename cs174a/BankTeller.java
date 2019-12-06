package cs174a;

import java.sql.*;
import java.util.*;
import oracle.jdbc.OracleConnection;
import java.io.*;
import cs174a.Transactions;

public class BankTeller extends App{
    private OracleConnection _connection;
    //Statement stmt;
    
    Transactions t;
    private int [] daysInMonthRegular = {31,28,31,30,31,30,31,31,30,31,30,31};
    private int [] daysInMonthLeap = {31,29,31,30,31,30,31,31,30,31,30,31};

    public BankTeller(OracleConnection _connection){//constructor
        this._connection = _connection;
        this.t = new Transactions(_connection);
    }
    //main functions
    public String checkTransaction(String accountID, double amount){
        return this.t.writeCheck(accountID, amount);
             
    }
    public String generateMonthlyStatement(String taxID){
        String statement = "";
        double totalMonthlyBalance = 0.00;
        try{
            ResultSet rs = this.executeQ("SELECT a_id, balance "+ 
                            "FROM Account " +
                            "WHERE primaryOwner = '" + taxID + "'");
            String [] account = parseRsAsString(rs, "a_id");
            rs = this.executeQ("SELECT a_id, balance "+ 
                            "FROM Account " +
                            "WHERE primaryOwner = '" + taxID + "'");
            double [] balance = parseRsAsDouble(rs, "balance");
            for(int i = 0; i < account.length; i++){
                statement += "Account ID: " + account[i] + "\n";
                totalMonthlyBalance += balance[i];
                ResultSet ownsNames = this.executeQ("SELECT C.name " + 
                                                        "FROM Customer C, Owns O " +
                                                        "WHERE O.a_id = '" +account[i]+ "' AND C.taxID = O.taxID");
                ResultSet ownsAddress = this.executeQ("SELECT C.address " + 
                                                        "FROM Customer C, Owns O " +
                                                        "WHERE O.a_id = '" +account[i]+ "' AND C.taxID = O.taxID");
                statement+=ownsList(ownsNames, ownsAddress);
                ResultSet posTransType = this.executeQ("SELECT type " +
                                                                    "FROM Transaction "+
                                                                    "WHERE rec_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                ResultSet posTransDate = this.executeQ("SELECT t_date " +
                                                                    "FROM Transaction "+
                                                                    "WHERE rec_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                ResultSet posTransAmount = this.executeQ("SELECT amount " +
                                                                    "FROM Transaction "+
                                                                    "WHERE rec_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                ResultSet negTransType = this.executeQ("SELECT type " +
                                                                    "FROM Transaction "+
                                                                    "WHERE send_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                ResultSet negTransDate = this.executeQ("SELECT t_date " +
                                                                    "FROM Transaction "+
                                                                    "WHERE send_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                ResultSet negTransAmount = this.executeQ("SELECT amount " +
                                                                    "FROM Transaction "+
                                                                    "WHERE send_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                double inital = calculateInitialBalance(posTransAmount, negTransAmount, balance[i]);
                statement+= String.format("Initial Balance: $%.2f \n", inital);
                posTransAmount = this.executeQ("SELECT amount " +
                                                                    "FROM Transaction "+
                                                                    "WHERE rec_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                negTransAmount = this.executeQ("SELECT amount " +
                                                                    "FROM Transaction "+
                                                                    "WHERE send_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                statement+="Positive Transactions:\n";
                statement+=getTransactionList(posTransAmount, posTransDate, posTransType);
                statement+="Negative Transactions:\n";
                statement+=getTransactionList(negTransAmount, negTransDate, negTransType);

                statement+=String.format("Final Balance: $%.2f \n",totalMonthlyBalance);


                statement+="\n";   
            }


        }catch(Exception e){
            System.out.println(e);
            return "1";
        }
 
        if(totalMonthlyBalance > 100000.00){
            statement += "WARNING: Total monthly balance is greater than $100,000. Insurance limit has been reached...\n";
        }
        if (statement == "")
            statement += "No accounts found for " + taxID + '\n';
        return "0\n" + statement;
    }

    public String listClosedAccounts(){
        try{
            ResultSet closedAccounts = this.executeQ("SELECT a_id " +
                                                    "FROM Account "+
                                                    "WHERE isClosed='1' "+
                                                    "AND EXISTS(SELECT a_id FROM Transaction WHERE (send_id = a_id OR rec_id = a_id) " +
                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate))");
            String[] listOfClosedAcc = this.parseRsAsString(closedAccounts, "a_id");
            String closedAcc = "List of Closed Accounts: \n";
            for(String s : listOfClosedAcc){
                closedAcc += s + '\n';
            }
            if(listOfClosedAcc.length == 0)
                closedAcc += "none\n";
            return closedAcc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "1";
    }
    public String generateDTER(){
        try{
            String dter = "Government Drug and Tax Evasion Report:\n";
            ResultSet customers = this.executeQ("SELECT O.taxID " +
                                        "FROM Owns O " +
                                        "WHERE (SELECT sum(T.amount) " +
                                            "FROM Transaction T " +
                                            "WHERE O.a_id = T.rec_id " +
                                                    "AND EXTRACT(month FROM t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) " +
                                                                                           "FROM GlobalDate)) > 10000");
            String [] dterID = parseRsAsString(customers, "taxID");
            if(dterID.length == 0)
                return dter + "No accounts found\n";
            else{
                for(String s : dterID){
                    dter += "ID: " + s + '\n';
                }
            }
            return dter;
        }catch(Exception e ){
            e.printStackTrace();
        }
        return "0";
    }
    public String customerReport(String taxID){
        try{
            ResultSet customerAcc = this.executeQ("SELECT A.a_id " +
                                                    "FROM Account A, Owns O " +
                                                    "WHERE taxID = '" + taxID + "' AND A.a_id = O.a_id" );
            ResultSet isAccClosed = this.executeQ("SELECT A.isClosed " +
                                                    "FROM Account A, Owns O " +
                                                    "WHERE taxID = '" + taxID + "' AND A.a_id = O.a_id" );        
            String [] customerID = parseRsAsString(customerAcc, "a_id");
            String [] isClosed = parseRsAsString(isAccClosed, "isClosed");
            String report = "Customer Report for: " + taxID + "\n";
            for(int i = 0; i < customerID.length; i++){
                if(isClosed[i] == "1")
                    report += "ID: " + customerID[i] + ", Is Closed: True\n";
                else
                    report += "ID: " + customerID[i] + ", Is Closed: False\n";
            }
            report += "\n";
            return report;
        }catch(Exception e){
            e.printStackTrace();
        }
        return "0";
    }
    public String addInterest(){
        if (!isLastDay()){
            return "1";
        }

        ResultSet openAcc_id = this.executeQ("SELECT a_id "+
                                                    "FROM Account "+
                                                    "WHERE isClosed = 0");

        ResultSet openAcc_bal = this.executeQ("SELECT balance "+
                                                    "FROM Account "+
                                                    "WHERE isClosed = 0");
        ResultSet openAcc_type = this.executeQ("SELECT a_type "+
                                                    "FROM Account "+
                                                    "WHERE isClosed = 0");
        
        String [] openAccounts = parseRsAsString(openAcc_id, "a_id");
        double [] balances = parseRsAsDouble(openAcc_bal, "balance");
        String [] types = parseRsAsString(openAcc_type, "a_type");

        ResultSet interest_type = this.executeQ("SELECT type "+
                                                    "FROM Interest");
        ResultSet interest_rate = this.executeQ("SELECT int_rate "+
                                                    "FROM Interest");

        String [] type = parseRsAsString(interest_type, "type");
        double [] rate = parseRsAsDouble(interest_rate, "int_rate");
        Map<String, Double> monthlyRates = new HashMap<String, Double>();

        for(int i=0; i<type.length; i++){
            monthlyRates.put(type[i], rate[i]/12.0);
        }

        for(int i=0;i<openAccounts.length;i++){
            //double interest = calculateInterest(openAccounts[i], balances[i], monthlyRates.get(types[i]));
            double interest = 0.0;

            if(interest > 0){
                this.executeQ("UPDATE Account SET balance = balance + "+interest+" WHERE a_id = '"+openAccounts[i]+"'");
                this.executeQ("INSERT INTO Transaction VALUES ( "+interest+", TO_DATE('"+getDate()+"', 'YYYY-MM-DD HH24:MI:SS'), 'accrue-interest', '"+generateRandomChars(9)+"', NULL,'"+openAccounts[i]+"', NULL)");
            }
        }

        return "0";
    }
    public String createAccount(AccountType accountType, String id, double initialBalance, String tin, String name, String address){//use app to make specific accounts/new customers
        this.createCheckingSavingsAccount(accountType, id, initialBalance, tin, name, address);
        return "0";
    }
    public String deleteClosedAccountsCustomers(){
        try{
            this.executeQ("DELETE FROM Account " + 
                          "WHERE isClosed = 1 ");
            this.executeQ("DELETE FROM Owns " + 
                          "WHERE EXISTS(SELECT * FROM Account " + 
                                        "WHERE Owns.a_id = Account.a_id ");
            this.executeQ("DELETE FROM Customer " +
                          "WHERE NOT EXISTS(SELECT * FROM Owns " +
                                            "WHERE Customer.taxID = Customer.taxID");
            return "0 All closed accounts and customers without accounts deleted...";
        }catch(Exception e){    
            e.printStackTrace();
        }
        return "1";
    }
    public String deleteTransactions(){
        return "0 Deleted all transactions...";
    }

    //helper functions
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
            //rs.beforeFirst();
            double[] temp = new double[rsDoubleOutput.size()];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = rsDoubleOutput.get(i);
            }
            return temp;
        }catch(Exception e){
            System.out.println("parserD: "+ key + " " + e);
        }
        double[] temp = {1.1111};
        return temp;
    }

    private String ownsList(ResultSet ownsNames, ResultSet ownsAddress){
        String list = "Owners:\n";
        String [] names = parseRsAsString(ownsNames, "name");
        String [] address = parseRsAsString(ownsAddress, "address");
        for(int i=0; i<names.length; i++){
            list += names[i] + " : " + address[i] + "\n";
        }
        list+="\n";
        return list;
    }

    private double calculateInitialBalance(ResultSet posAmount, ResultSet negAmount, double inital){
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
    private String getTransactionList(ResultSet tAmount,ResultSet tDate,ResultSet tType){
        String list = "";
        String [] types = parseRsAsString(tType, "type");
        String [] dates = parseRsAsString(tDate, "t_date");
        double [] amounts = parseRsAsDouble(tAmount, "amount");
        for(int i=0;i<types.length;i++){
            list += dates[i] + "\t$" + String.format("%.2f",amounts[i])+ "\t" + types[i] + "\n";
        }
        list+="\n";
        return list;
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

    public boolean isLastDay() {
        String currentDate = t.getDate();
        int year = Integer.parseInt(currentDate.substring(0,4));
        int month = Integer.parseInt(currentDate.substring(5,7));
        int day = Integer.parseInt(currentDate.substring(8,10));

        if (year%4 == 0) { //leap year
            if (day == daysInMonthLeap[month-1]){
                return true;
            } else {
                return false;
            }
        }

        else {
            if (day == daysInMonthRegular[month-1]){
                return true;
            } else {
                return false;
            }
        }
    }

}