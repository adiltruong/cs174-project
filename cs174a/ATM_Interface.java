package cs174a;

import java.io.*;
import java.util.*;
import java.sql.*;
import oracle.jdbc.OracleConnection;
import cs174a.*;

public class ATM_Interface{
    private OracleConnection _connection;
    private Transactions transaction;
    private BankTeller bankTeller;
    //private Transactions transaction;
    public ATM_Interface(OracleConnection _connection){
        this._connection = _connection;
        transaction = new Transactions(_connection);
        bankTeller = new BankTeller(_connection);
    }
    private OracleConnection getConnection(){
        return this._connection;
    }
    public void startATM(Scanner myObj, String taxID, boolean atmOn){
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
                System.out.println(transaction.deposit(account, amount));

            }
            if(input == 1){//Top-up
                System.out.println();
                System.out.println("Which account would you like to top up with?");
                System.out.println();
                String account = myObj.next();
                System.out.println();
                System.out.println("How much would you like to top up with?");
                System.out.println();
                double amount = myObj.nextDouble();
                System.out.println(transaction.topUp(account, amount));
            }
            if(input == 2){//Withdrawl
                System.out.println();
                System.out.println("Which account would you like to withdraw from?");
                System.out.println();
                String account = myObj.next();
                System.out.println();
                System.out.println("How much would you like to withdraw?");
                System.out.println();
                double amount = myObj.nextDouble();
                System.out.println(transaction.withdraw(account, amount));
            }
            if(input == 3){//Purchase
                System.out.println();
                System.out.println("Which account would you like to purchase with?");
                System.out.println();
                String account = myObj.next();
                System.out.println();
                System.out.println("How much would you like to use?");
                System.out.println();
                double amount = myObj.nextDouble();
                System.out.println(transaction.purchase(account, amount));
            }
            if(input == 4){//Transfer
                System.out.println();
                System.out.println("Which account would you like to transfer from?");
                System.out.println();
                String from = myObj.next();
                System.out.println();
                System.out.println("Which account would you like to transfer to?");
                System.out.println();
                String to = myObj.next();
                System.out.println();
                System.out.println("How much would you like to transfer?");
                System.out.println();
                double amount = myObj.nextDouble();
                System.out.println(transaction.transfer(from, to, amount));
            }
            if(input == 5){//Collect
                System.out.println();
                System.out.println("Which account would you collect from?");
                System.out.println();
                String account = myObj.next();
                System.out.println();
                System.out.println("How much would you like to collect?");
                System.out.println();
                double amount = myObj.nextDouble();
                System.out.println(transaction.collect(account, amount));
            }
            if(input == 6){//Wire
                System.out.println();
                System.out.println("Which account would you like to wire money from?");
                System.out.println();
                String from = myObj.next();
                System.out.println();
                System.out.println("Which account would you like to wire money to?");
                System.out.println();
                String to = myObj.next();
                System.out.println();
                System.out.println("How much would you like to wire?");
                System.out.println();
                double amount = myObj.nextDouble();
                System.out.println(transaction.wire(from, to, amount));
            }
            if(input == 7){//Pay Friend
                System.out.println();
                System.out.println("Which pocket account would you like to pay from?");
                System.out.println();
                String from = myObj.next();
                System.out.println();
                System.out.println("Which pocket account would you like to pay?");
                System.out.println();
                String to = myObj.next();
                System.out.println();
                System.out.println("How much would you like to pay?");
                System.out.println();
                double amount = myObj.nextDouble();
                System.out.println(transaction.payFriend(from, to, amount));
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
                System.out.println("Exiting ATM...\nGoodbye...");
                System.out.println();
                atmOn = false;
            }
        }
    }
}