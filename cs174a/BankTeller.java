package cs174a;

import java.sql.*;
import java.util.*;
import oracle.jdbc.OracleConnection;
import java.io.*;
import cs174a.Transactions;

public class BankTeller{
    private OracleConnection _connection;
    Statement stmt;
    ResultSet rs;
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
            stmt = _connection.createStatement();
            rs = stmt.executeQuery("SELECT a_id, balance "+ 
                            "FROM Accounts" +
                            "WHERE primaryOwner = " + taxID);
            String [] account = parseRsAsString(rs, "a_id");
            double [] balance = parseRsAsDouble(rs, "balance");
            for(int i = 0; i < balance.length; i++){
                statement += "Account ID: " + account[i] + "\n";
                totalMonthlyBalance += balance[i];
            }
        }catch(Exception e){
            System.out.println(e);
            return "1";
        }
        if (statement == "")
            statement += "No accounts found for " + taxID;
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
            while(rs.next()) {
                String id = rs.getString(key);
                rsStringOutput.add(id.trim());
            }
            rs.beforeFirst();
            String[] a = new String[rsStringOutput.size()];
            rsStringOutput.toArray(a);
            return a;
        }catch(Exception e){
            System.out.println(e);
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
            System.out.println(e);
        }
        double[] temp = {1.1};
        return temp;
    }

}