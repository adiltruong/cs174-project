package cs174a;                         // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// DO NOT REMOVE THIS IMPORT.
import cs174a.Testable.*;
import java.util.Scanner;

/**
 * This is the class that launches your application.
 * DO NOT CHANGE ITS NAME.
 * DO NOT MOVE TO ANY OTHER (SUB)PACKAGE.
 * There's only one "main" method, it should be defined within this Main class, and its signature should not be changed.
 */
public class Main
{
	/**
	 * Program entry point.
	 * DO NOT CHANGE ITS NAME.
	 * DON'T CHANGE THE //!### TAGS EITHER.  If you delete them your program won't run our tests.
	 * No other function should be enclosed by the //!### tags.
	 */
	//!### COMENZAMOS
	public static void main( String[] args )
	{
		App app = new App();                        // We need the default constructor of your App implementation.  Make sure such
													// constructor exists.
		//ATM atm = new ATM();
		String r = app.initializeSystem();          // We'll always call this function before testing your system.
		if( r.equals( "0" ) )
		{
			//app.exampleAccessToDB();                // Example on how to connect to the DB.
			app.dropTables();
			app.createTables();
			app.populate_tables();
			r = app.setDate(2011,3,1);
			System.out.println(r);

			// Example tests.  We'll overwrite your Main.main() function with our final tests.
			r = app.listClosedAccounts();
			System.out.println( r );

			// Another example test.
			r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "account1", 1234.56, "theTaxID", "Im YoungMing", "Known" );
			System.out.println( r );

			Transactions transactions = new Transactions(app.getConnection());
			BankTeller bankTeller = new BankTeller(app.getConnection());
			Customer customer = new Customer("taxID", app.getConnection());

			r = app.createPocketAccount( "account2", "account1", 30.0, "theTaxID");
			System.out.println( r );

			r = app.setInterestRate( AccountType.INTEREST_CHECKING, 3.5);
			System.out.println(r);

			r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "account3", 12340000.56, "someID", "oof", "haha" );
			System.out.println( r );

			r = app.createPocketAccount( "account4", "account3", 50.0, "someID");
			System.out.println( r );

			System.out.println("payFriend: ");
			r = transactions.payFriend("account4", "account2", 20.0);
			System.out.println( r );

			System.out.println("createCustomer: ");
			r = app.createCustomer("account1", "taxID", "George", "Joe Mama");
			System.out.println( r );

			r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "account5", 1234.56, "taxID", "Im YoungMing", "Known" );
			System.out.println( r );

			r = app.showBalance("account5");
			System.out.println("BALANCE for 5:" + r);
						
			r = app.showBalance("account1");
			System.out.println("BALANCE for 1:" + r);

			r = transactions.writeCheck("account5", 20.0);
			System.out.println( r );

			r = bankTeller.checkTransaction("account5", 20.0);
			System.out.println( r );

			r = bankTeller.checkTransaction("account5", 50.0);
			System.out.println( r );


			r = app.showBalance("account5");
			System.out.println("BALANCE for 5:" + r);

			r = app.showBalance("account1");
			System.out.println("BALANCE for 1:" + r);

			
			//System.out.println(customer.setPIN("1717","2727"));
			System.out.println("TESTING>>>>>>>>>>>>>>>>>>>>>>>>>>\n\n");
			//System.out.println(bankTeller.generateMonthlyStatement("taxID"));
			//System.out.println(bankTeller.listClosedAccounts());
			r = app.showBalance("account3");
			System.out.println("BALANCE for 3:" + r);
			r = app.showBalance("account5");
			System.out.println("BALANCE for 5:" + r);
			r = transactions.wire("account3","account5", 100000);
			System.out.println("wire: " + r);
			r = app.showBalance("account5");
			System.out.println("BALANCE for 5:" + r);
			r = app.showBalance("account3");
			System.out.println("BALANCE for 3:" + r);
			System.out.println("Wire: " + r);
			System.out.println(bankTeller.generateDTER());
			

		boolean systemOn = true;
		Scanner myObj = new Scanner(System.in);

		while(systemOn) {
			System.out.println();
			System.out.println("Welcome to Debts R Us! What would you like to do today?");
			System.out.println();
			System.out.println("0  ATM interface");
			System.out.println("1  Bank teller");
			System.out.println("2 Set system date");
			System.out.println("3 Set interest rates");
			System.out.println("4 Leave bank of debt");
			int loginTries = 0;
			int option = myObj.nextInt();
			String taxID = "";
			String pin = "";


			if (option == 0) { //ATM Interface
				System.out.println();
				System.out.println("Booting up ATM...");
				System.out.println();
				boolean rr = false;
				boolean atmON = true;
				
				while (loginTries < 3 && !rr) {
					System.out.println();
					System.out.println("Welcome to Debts R Us ATM");
					System.out.println("Please log in...");
					System.out.println();
					System.out.println("TaxID:");
					
					String tempTaxID = myObj.next();
					taxID = tempTaxID;
					System.out.println();
					System.out.println("PIN:");					
					String tempPin = myObj.next();
					pin = tempPin;
					System.out.println();
					rr = app.login(taxID, pin);
					loginTries++;
					if(!rr)
						System.out.println("big oof you goofed, Try again.");
				}

				if(rr) {
					while (atmON) {
						Customer cust = new Customer(taxID, app.getConnection());
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

						if (input == 0) {
							System.out.println();
							System.out.println("Which account would you like to deposit to?");
							System.out.println();
							String account = myObj.next();
							System.out.println();
							System.out.println("How much would you like to deposit?");
							System.out.println();
							double amount = myObj.nextDouble();

						}

						if (input == 1) {

						}



						if (input == 8) {
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

						if (input == 9) {
							System.out.println();
							System.out.println("Goodbye...");
							System.out.println();
							atmON = false;
						}
					}
				}

				if(loginTries == 3) {
					System.out.println();
					System.out.println("3 failed attempts... Rerouting to main menu");
					System.out.println();
				}
			}

			if (option == 1) { //Bank Teller Interface
				System.out.println("Hello, I am the Bank Teller");
				System.out.println("Tell me what you'd like me to do for you!");

				System.out.println();
				System.out.println();




// "Write Check","Monthly Statement","List Closed Accounts",
//         "DTER", "Customer Report", "Add Interest", "Create New Account",
//         "Delete Closed Accounts and Customers", "Delete Transactions", 


				
			}

			if (option == 2) {
				System.out.println();
				System.out.println("What date would you like to set the system to?");
				System.out.println("Year? YYYY");

				int year = myObj.nextInt();

				System.out.println("Month?");
				int month = myObj.nextInt();

				System.out.println("Day?");
				int day = myObj.nextInt();

				r = app.setDate(year, month, day);

				System.out.println("Date set!");
				System.out.println(app.getDate());
				System.out.println();
			}

			if (option == 3) {
				System.out.println();
				System.out.println("Which account type would you like to change the Interest rate for?");
				System.out.println();

				System.out.println("1 Interest-Checking");
				System.out.println("2 Savings");

				int acc = myObj.nextInt();

				if(acc == 1) {
					System.out.println();
					System.out.println("What rate would like to set it as? (Enter double)");
					System.out.println();
					double rate = myObj.nextDouble();
					r = app.setInterestRate(AccountType.INTEREST_CHECKING, rate);
					if (r == "0") {
						System.out.println("SUCCESS Interest-Checking rate changed to "+rate);
					}
					else {
						System.out.println("Invalid rate. Returning to Main Menu");
					}
				}

				else if(acc == 2) {
					System.out.println();
					System.out.println("What rate would like to set it as? (Enter double)");
					System.out.println();
					double rate = myObj.nextDouble();
					r = app.setInterestRate(AccountType.SAVINGS, rate);
					if (r == "0") {
						System.out.println("SUCCESS Savings rate changed to "+rate);
					} else {
						System.out.println("Invalid rate. Returning to Main Menu");
					}
				}

				else {
					System.out.println("Invalid option. Returning to Main Menu");
				}
			}

				//String input = myObj.nextLine();

				// input = input.toUpperCase();

				// if (input == "INTEREST_CHECKING" || input == "SAVINGS") {
				// 	AccountType acc = AccountType.valueOf(input);
				// 	System.out.println("What's the new rate?");
				// 	double rate = myObj.nextDouble();
				// 	System.out.println();
				// 	r = app.setInterestRate(acc, rate);
				// 	System.out.println(r);

				// }

			if (option == 4) {
				System.out.println();
				System.out.println("Goodbye...");
				System.out.println();
				systemOn = false;
			}

			}
		 }
		 
	//!### FINALIZAMOS
		}
}