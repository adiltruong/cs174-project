package cs174a;  

import java.sql.*;
import java.util.*;
import oracle.jdbc.OracleConnection;

public class Customer{
    private String taxId;
    private OracleConnection _connection;
    public Customer(String taxId, OracleConnection _connection){
        this.taxId = taxId;
        this._connection = _connection;
    }
    public String verifyPIN(String pin){
        try {
			Statement stmt = _connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT taxID FROM Customer WHERE taxID = '"+taxId+"' AND pin = "+pin+" ");
            if (rs.next()) {
                return "0";
            }
		} catch(Exception e) {
			System.out.println("Couldn't update Pin");
			System.out.println(e);
			return "1";
        }
        return "1";
    }

    public String setPIN(String oldPIN, String newPIN){
        try {
			Statement stmt = _connection.createStatement();
            stmt.executeQuery("UPDATE Customer SET pin = '"+newPIN+"' WHERE pin = '"+oldPIN+"' AND taxID = '"+this.taxId+"'");
            stmt.executeQuery("commit;");
            return "0";
		} catch(Exception e) {
			System.out.println("Couldn't update Pin");
			System.out.println(e);
			return "1";
        }
    }
}