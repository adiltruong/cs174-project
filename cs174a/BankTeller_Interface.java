package cs174a;

import java.util.*;
import java.io.*;
import java.sql.*;
import oracle.jdbc.OracleConnection;

public class BankTeller_Interface extends App{
    private OracleConnection _connection;
    private BankTeller bankTeller;
    public BankTeller_Interface(OracleConnection _connection){
        this._connection = _connection;
        this.bankTeller = new BankTeller(_connection);

    }
    public void tellerInterace(Scanner myObj, boolean tellerOn){
        while(tellerOn){
            System.out.println("Bank Teller Inferface Options");
            System.out.println("");
            System.out.println("0: Enter Check Transaction");
            System.out.println("1: Generate Monthly Statement");
            System.out.println("2: List Closed Accounts");
            System.out.println("3: Generate Drug and Tax Evasion Report");
            System.out.println("4: Customer Report");
            System.out.println("5: Add Interest");
            System.out.println("6: Create Account");
            System.out.println("7: Delete Closed Accounts and Customers");
            System.out.println("8: Delete Transactions");
            System.out.println("9: Leave Bank Teller Interface");
            System.out.println();
            int input = myObj.nextInt();
            if(input == 0){//enter check transaction
                System.out.println();
                System.out.println("Enter check transaction information: ");
                System.out.println();
                System.out.println("Account:");
                String account = myObj.next();
                System.out.println();
                System.out.println("Amount");
                System.out.println();
                double amount = myObj.nextDouble();
                System.out.println("Processing Code: " + bankTeller.checkTransaction(account, amount) + '\n');
            }else if(input == 1){//Generate Monthly Statement
                System.out.println();
                System.out.println("Select account (taxID) to generate statement for: ");
                System.out.println();
                String taxID = myObj.next();
                System.out.println();
                System.out.println(bankTeller.generateMonthlyStatement(taxID));
                System.out.println();
            }else if(input == 2){//List Closed Accounts
                System.out.println();
                System.out.println(bankTeller.listClosedAccounts());
                System.out.println();
            }else if(input == 3){//Genereate DTER
                System.out.println();
                System.out.println(bankTeller.generateDTER());
                System.out.println();
            }else if(input == 4){//Customer Report
                System.out.println();
                System.out.println("Which customer do you want to generate a report for?");
                String taxID = myObj.next();
                System.out.println();
                System.out.println(bankTeller.customerReport(taxID));
                System.out.println();
            }else if(input == 5){//Add Interest
                System.out.println();
                System.out.println("Add Interest to all accounts processing code: " + bankTeller.addInterest());
                System.out.println();
            }else if(input == 6){//Create account
                System.out.println();
                System.out.println("Create account:\n");
                System.out.println();
                System.out.println("Type:");
                String accountType = myObj.next();
                boolean incorrectType = true;
                while(incorrectType){
                    if(accountType.equals("STUDENT_CHECKING") || accountType.equals("INTEREST_CHECKING") || accountType.equals("SAVING") || accountType.equals("POCKET")){
                        incorrectType = false;
                    }else{
                        System.out.println("Incorrect type...\nType:\n");
                        accountType = myObj.next();
                    }
                }
                if(accountType.equals("POCKET")){
                    System.out.println("\nId for Pocket:");
                    String id = myObj.next();
                    System.out.println("\nId for Linked Account:");
                    String linkedId = myObj.next();
                    System.out.println("\nInitial Top Up:");
                    double initialTopUp = myObj.nextDouble();
                    System.out.println("\nTax ID Number:");
                    String tin = myObj.next();
                    System.out.println("Pocket Account Processing Code: " + bankTeller.createPocketAccount(id, linkedId, initialTopUp, tin) + '\n');                   
                }else{
                    System.out.println("\nId:");
                    String id = myObj.next();
                    System.out.println("\nInitial Balance:");
                    double initialBalance = myObj.nextDouble();
                    System.out.println("\nTax ID Number:");
                    String tin = myObj.next();
                    System.out.println("\nName:");
                    String name = myObj.next();
                    System.out.println("\nAddress:");
                    String address = myObj.next();
                    System.out.println();
                    switch(accountType){
                        case "STUDENT_CHECKING":
                            System.out.println("Account Creation Processing Code: " + bankTeller.createAccount(AccountType.STUDENT_CHECKING, id, initialBalance, tin, name, address) + '\n');
                            break;
                        case "INTEREST_CHECKING":
                            System.out.println("Account Creation Processing Code: " + bankTeller.createAccount(AccountType.INTEREST_CHECKING, id, initialBalance, tin, name, address) + '\n');
                            break;
                        case "SAVINGS":
                            System.out.println("Account Creation Processing Code: " + bankTeller.createAccount(AccountType.SAVINGS, id, initialBalance, tin, name, address) + '\n');
                            break;
                        default:
                            System.out.println("Incorrect Type...\n");
                    }
                }    
            }else if(input == 7){//Delete closed acc and customers
                System.out.println();
                System.out.println("Deleting all closed accounts and associated customers...");
                System.out.println(bankTeller.deleteClosedAccountsCustomers());
                System.out.println();
            }else if(input == 8){//Delete transactions
                System.out.println();
                System.out.println("Deleting transactions...");
                System.out.println(bankTeller.deleteTransactions());
                System.out.println();
            }else if(input == 9){//Leave interface
                System.out.println();
                System.out.println("Leaving Bank Teller Interface...\nGoodbye...\n");
                tellerOn = false;
            }else{
                System.out.println();
                System.out.println("Error 404: Input not found...");
                System.out.println();
            }
        }
    }
}