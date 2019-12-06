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
		ATM_Interface atm_Interface = new ATM_Interface(app.getConnection());
		BankTeller_Interface bankTeller_Interface = new BankTeller_Interface(app.getConnection());
		while(systemOn) {
			System.out.println();
			System.out.println("Welcome to Debts R Us! What would you like to do today?");
			System.out.println();
			System.out.println("0 ATM interface");
			System.out.println("1 Bank teller");
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
						System.out.println("Incorrect login information. Please try again.");
				}
				if(rr) {
					atm_Interface.startATM(myObj, taxID);
				}
			}
			if(option == 1){ //Bank teller interface
				bankTeller_Interface.tellerInterace(myObj, true);
			}
			if(option == 2){ //set system date
				System.out.println("\nSet System Date:\n");
				System.out.println("Year:\n");
				int year = myObj.nextInt();
				System.out.println("\nMonth:\n");
				int month = myObj.nextInt();
				System.out.println("\nDay:\n");
				int day = myObj.nextInt();
				app.setDate(year, month, day);
			}
			if(option == 3){ //Set Interest Rate
				System.out.println("\nAccount Type:\n");
				String accountType = myObj.next();
				System.out.println("\nRate:");
				double rate = myObj.nextDouble();
				if(accountType == "INTEREST_CHECKING")
					System.out.println("\nInterest Rate Code: " + app.setInterestRate(AccountType.INTEREST_CHECKING, rate));
				else if(accountType == "SAVING")
					System.out.println("\nInterest Rate Code: " + app.setInterestRate(AccountType.SAVINGS, rate));
				else
					System.out.println("\nInvalid Input... Exiting...\n");
			}
			if(option == 4){
				System.out.println("\nGoodbye...\n");
				systemOn = false;
			}
	//!### FINALIZAMOS
		}
	}
}
}
