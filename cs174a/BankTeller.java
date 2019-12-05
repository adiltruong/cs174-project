package cs174a;

import java.sql.*;
import java.util.*;
import oracle.jdbc.OracleConnection;
import java.io.*;
import cs174a.Transactions;

public class BankTeller{
    private OracleConnection _connection;
    //Statement stmt;
    
    Transactions t;
    public BankTeller(OracleConnection _connection){//constructor
        this._connection = _connection;
        this.t = new Transactions(_connection);
    }
    //main functions
    public String checkTransaction(String accountID, double amount){
        this.t.writeCheck(accountID, amount);
        return "0";
    }
    public String generateMonthlyStatement(String taxID){
        String statement = "";
        double totalMonthlyBalance = 0.00;
        try{
            Statement stmt = _connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT a_id, balance "+ 
                            "FROM Account " +
                            "WHERE primaryOwner = '" + taxID + "'");
            String [] account = parseRsAsString(rs, "a_id");
            rs = stmt.executeQuery("SELECT a_id, balance "+ 
                            "FROM Account " +
                            "WHERE primaryOwner = '" + taxID + "'");
            double [] balance = parseRsAsDouble(rs, "balance");
            for(int i = 0; i < account.length; i++){
                statement += "Account ID: " + account[i] + "\n";
                totalMonthlyBalance += balance[i];
                ResultSet owns = stmt.executeQuery("SELECT C.address " + 
                                                        "FROM Customer C, Owns O " +
                                                        "WHERE O.a_id = '" +account[i]+ "' AND C.taxID = O.taxID");
                ResultSet owns1 = stmt.executeQuery("SELECT C.name " + 
                                                        "FROM Customer C, Owns O " +
                                                        "WHERE O.a_id = '" +account[i]+ "' AND C.taxID = O.taxID");
                statement+=ownsList(owns1, owns);
                ResultSet pos_transactions = stmt.executeQuery("SELECT type, t_date, amount " +
                                                                    "FROM Transaction "+
                                                                    "WHERE rec_id='"+account[i]+"' "+
                                                                    "AND EXTRACT(month from t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                System.out.println("oof4");
                ResultSet neg_transactions = stmt.executeQuery("SELECT type, t_date, amount " +
                                                                    "FROM Transaction " + 
                                                                    "WHERE send_id='"+account[i]+"' " +
                                                                    "AND EXTRACT(month FROM t_date) = (SELECT MAX(EXTRACT(month FROM globaldate)) FROM GlobalDate)");
                
                
                System.out.println("oof5");
                double inital = calculateInitialBalance(pos_transactions, neg_transactions, balance[i]);
               System.out.println(inital);
                statement+= String.format("Initial Balance: $%.2f \n", inital);

                statement+="Positive Transactions:\n";
                statement+=getTransactionList(pos_transactions);
                statement+="Negative Transactions:\n";
                statement+=getTransactionList(neg_transactions);

                statement+=String.format("Final Balance: $%.2f \n",totalMonthlyBalance);


                statement+="\n";   
            }


        }catch(SQLException e){
            //System.out.println(e);
            e.printStackTrace();
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
        return "0";
    }
    public String generateDTER(){
        return "0";
    }
    public String customerReport(String taxID){
        return "0";
    }
    public String addInterest(){
        return "0";
    }
    public String createAccount(){//use app to make specific accounts/new customers
        return "0";
    }
    public String deleteClosedAccountsCustomers(){
        return "0";
    }
    public String deleteTransactions(){
        return "0";
    }

    //helper functions
    public String[] parseRsAsString(ResultSet rs, String key){
        try{
            ArrayList<String> rsStringOutput = new ArrayList<String>();     
   
            while(rs.next()){
                String id = rs.getString(key);
                System.out.println(id);
                rsStringOutput.add(id.trim());        
            }

            //rs.beforeFirst();
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
            System.out.println("parserD: " + e);
        }
        double[] temp = {1.1};
        return temp;
    }

    private String ownsList(ResultSet owners, ResultSet owners1){
        
        String list = "Owners:\n";
        String [] names = parseRsAsString(owners, "name");
        String [] address = parseRsAsString(owners1, "address");
        for(int i=0; i<names.length; i++){
            list += names[i] + " : " + address[i] + "\n";
        }
        list+="\n";
        return list;
    }

    private double calculateInitialBalance(ResultSet pos_transactions, ResultSet neg_transactions, double inital){
        double [] pos = parseRsAsDouble(pos_transactions, "amount");
        for(double p : pos){
            inital -= p;
        }
        double [] neg = parseRsAsDouble(neg_transactions, "amount");
        for(double n : neg){
            inital += n;
        }
        return inital;
    }
    private String getTransactionList(ResultSet transactions){
        String list = "";
        String [] types = parseRsAsString(transactions, "a_type");
        String [] dates = parseRsAsString(transactions, "globaldate");
        double [] amounts = parseRsAsDouble(transactions, "amount");
        for(int i=0;i<types.length;i++){
            list += dates[i] + "\t$" + String.format("%.2f",amounts[i])+ "\t" + types[i] + "\n";
        }
        list+="\n";
        return list;
    }

}