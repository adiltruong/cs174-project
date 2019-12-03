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

			r = app.setDate(2011,3,1);
			System.out.println(r);

			// Example tests.  We'll overwrite your Main.main() function with our final tests.
			r = app.listClosedAccounts();
			System.out.println( r );

			// Another example test.
			r = app.createCheckingSavingsAccount( AccountType.INTEREST_CHECKING, "account1", 1234.56, "theTaxID", "Im YoungMing", "Known" );
			System.out.println( r );

			r = app.createPocketAccount( "account2", "account1", 30.0, "theTaxID");
			System.out.println( r );

			r = app.setInterestRate( AccountType.INTEREST_CHECKING, 3.5);
			System.out.println(r);

			r = app.createCheckingSavingsAccount( AccountType.STUDENT_CHECKING, "account3", 1234.56, "someID", "oof", "haha" );
			System.out.println( r );

			r = app.createPocketAccount( "account4", "account3", 50.0, "someID");
			System.out.println( r );

			System.out.println("payFriend: ");
			r = app.payFriend("account4", "account2", 20.0);
			System.out.println( r );

			System.out.println("createCustomer: ");
			r = app.createCustomer("account1", "taxID", "George", "Joe Mama");
			System.out.println( r );

			r = app.showBalance("account1");
			System.out.println("BALANCE for r:" + r);

			r = app.deposit("account1", 200.00);
			System.out.println( r );

			r = app.showBalance("account1");
			System.out.println("BALANCE for r:" + r);

			r = app.topUp("account2", 300.00);
			System.out.println( r );

			r = app.showBalance("account2");
			System.out.println( r );

			r = app.withdraw("account1", 300.0);
			System.out.println(r);

			r = app.showBalance("account1");
			System.out.println( r );

			r = app.purchase("account2", 3.0);
			System.out.println(r);

			r = app.showBalance("account2");
			System.out.println( r );

			r = app.collect("account2", 30.00);
			System.out.println( r );

			r = app.showBalance("account2");
			System.out.println( r );

			r = app.showBalance("account1");
			System.out.println( r );


			

		boolean systemOn = true;
		Scanner myObj = new Scanner(System.in);

		while(systemOn) {
			System.out.println();
			System.out.println("Welcome to Debts R Us! What would you like to do today?");
			System.out.println();
			System.out.println("0  ATM interface");
			System.out.println("1  Bank teller");
			System.out.println("2 Set system date");
			System.out.println("3 Leave bank of debt");
			int loginTries = 0;
			int option = myObj.nextInt();

			if (option == 0) {
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
					String name = myObj.nextLine();
					String pin = myObj.nextLine();
					rr = app.login(name, pin);
					loginTries++;
					if(!rr)
						System.out.println("big oof you goofed");
				}
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
			int input = myObj.nextInt();
			//if(input = )

					
			//}

			if (option == 1) {
				System.out.println();
				System.out.println("");


				
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
				System.out.println("Goodbye...");
				systemOn = false;
			}

			}
		}
		}
	//!### FINALIZAMOS
}
}