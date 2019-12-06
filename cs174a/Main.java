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
		String r = app.initializeSystem();          // We'll always call this function before testing your system.
		if( r.equals( "0" ) )
		{
			//app.exampleAccessToDB();                // Example on how to connect to the DB.
			app.dropTables();
			app.createTables();
			app.populate_tables();

			// Example tests.  We'll overwrite your Main.main() function with our final tests.

			// Another example test

			Transactions transactions = new Transactions(app.getConnection());
			BankTeller bankTeller = new BankTeller(app.getConnection());
			Customer customer = new Customer("taxID", app.getConnection());
						

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
						System.out.println("Please log in...\nEnter to quit\n");
						System.out.println();
						System.out.println("TaxID:");
						String tempTaxID = myObj.next();
						if(tempTaxID != ""){
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
					bankTeller.setDateQ(year, month, day);
				}
				if(option == 3){ //Set Interest Rate
					System.out.println("\nAccount Type:\n");
					String accountType = myObj.next();
					System.out.println("\nRate:");
					double rate = myObj.nextDouble();
					if(accountType.equals("INTEREST_CHECKING"))
						System.out.println("\nInterest Rate Code: " + app.setInterestRate(AccountType.INTEREST_CHECKING, rate));
					else if(accountType.equals("SAVINGS"))
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