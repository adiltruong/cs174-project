package cs174a;

import java.io.*;
import java.util.*;
import java.sql.*;
import oracle.jdbc.OracleConnection;
import cs174a.*;

public class ATM_Interface{
    private OracleConnection _connection;
    private boolean atmOn = true;
    //private Transactions transaction;
    public ATM_Interface(OracleConnection _connection){
        this._connection = _connection;

    }
    private OracleConnection getConnection(){
        return this._connection;
    }
    public void startATM(Scanner myObj, String taxID){
        while(atmOn){
            Customer customer = new Customer(taxID, this.getConnection());
            System.out.println("ATM Inferface Options");
            System.out.println("");
            System.out.println("0: Deposit");
            System.out.println("1: Top-Up");
            System.out.println("2: Withdrawal");
            System.out.println("3: Purchase");
            System.out.println("4: Transfer");
            System.out.println("5: Collect");
            System.out.println("6: Wire");
            System.out.println("7: Pay-Friend");
            System.out.println("8: Set Pin");
            System.out.println("9: Leave ATM");
            System.out.println();
            int input = myObj.nextInt();

            if(input == 0){//Deposit
                System.out.println();
                System.out.println("Which account would you like to deposit to?");
                System.out.println();
                String account = myObj.next();
                System.out.println();
                System.out.println("How much would you like to deposit?");
                System.out.println();
                double amount = myObj.nextDouble();

            }
            if(input == 1){//Top-up
            }
            if(input == 2){//Withdrawl
            }
            if(input == 3){//Purchase
            }
            if(input == 4){//Transfer
            }
            if(input == 5){//Collect
            }
            if(input == 6){//Wire
            }
            if(input == 7){//Pay Friend
            }
            if(input == 8){//Set Pin
                System.out.println();
                System.out.println("Verify PIN first:");
                System.out.println();
                String oldPin = myObj.next();
                if (customer.verifyPIN(oldPin) == "0"){
                    System.out.println("Enter new PIN:");
                    System.out.println();
                    String newPin = myObj.next();
                    customer.setPIN(oldPin, newPin);
                    System.out.println("Success!\n");
                }else{
                    System.out.println("Invalid PIN...\nReturning to ATM menu...\n");
                }
            }
            if (input == 9) {//Leave ATM
                System.out.println();
                System.out.println("Goodbye...");
                System.out.println();
                atmOn = false;
            }
        }
    }
}