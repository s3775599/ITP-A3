import javax.swing.JOptionPane;

/*
 * This application allows a user to record, display, and sort a list of
 * transactions that they have made. Transactions include information like
 * sender, recipient, amount etc. The data is maintained in a .csv file.
 */

// APPLICATION CLASS
public class WalletTracker {

	private String file; // The name of the file storing transactions

	/*
	 * The Wallet tracker class is the application class, and sequences events,
	 * creates menus, and takes actions resulting from the user's choice. For the
	 * most part it does not prompt users, take user inputs or perform validation,
	 * those functions are mainly handled by menu or transaction objects as
	 * appropriate.
	 */

	public static void main(String[] args) {
		// Create a new instance of the WalletTracker class and include the transactions
		// file's name
		WalletTracker wt = new WalletTracker("transactions.csv");
		return;
	}

	// CONSTRUCTOR
	public WalletTracker(String file) {
		// Call the main menu method
		this.file = file;
		mainMenu();
		return;
	}

	////////////// MENUS
	// This section handles the creation of menus, and the events after the user
	////////////// selects an option.

	public void mainMenu() { // COMMENT REVIEW COMPLETE
		/*
		 * This method creates the main menu object, displays it to the user, then acts
		 * on their validated selection by calling the relevant method (to enter a
		 * transaction, view a transaction, or view all transactions).
		 * 
		 * If the user cancels this menu they exit the program.
		 */

		// Create an array to store menu options/dialog
		// This is then passed to the menu constructor
		String menuArray[] = new String[3];

		// Add menu options to the array
		menuArray[0] = "1. Enter a new transaction\n";
		menuArray[1] = "2. View a previous transaction by entering a transaction number\n";
		menuArray[2] = "3. View all previous transactions\n";

		// Create the menu with no header
		Menu mm = new Menu(menuArray, "");

		// display the menu, and let the user choose an option
		mm.displayMenu();

		// Check if the User clicked cancel
		while (mm.getSelection() != null) { // Start of menu loop

			// Check what the user has entered.
			if (mm.getSelection().contentEquals("1")) { // Enter a new transaction
				while (enterTransactionMenu() == true) {
					// calls enterTransaction method until user cancels prompt
				}
				mm.displayMenu(); // display the menu, and let the user choose an option

			} else if (mm.getSelection().contentEquals("2")) { // View a previous transaction
				viewTransactionMenu();
				mm.displayMenu(); // display the menu, and let the user choose an option

			} else if (mm.getSelection().contentEquals("3")) { // View all previous transactions
				while (viewTransactionsMenu() == true) {
					// calls viewTransactions method until user cancels prompt
				}
				mm.displayMenu(); // display the menu, and let the user choose an option
			}
		} // end of menu loop

		return;
	}

	public boolean enterTransactionMenu() { // COMMENT REVIEW COMPLETE
		/*
		 * This method creates a transaction object with values provided by the user,
		 * and creates the enter transaction menu object. The transaction details are
		 * then displayed to the user along with the menu. This allows the user to
		 * review the transaction before deciding to submit (write to file) or re-enter
		 * the transaction (repeat this method).
		 * 
		 * This method returns true if it needs to be repeated (if the user wants to
		 * enter another transaction)
		 */

		// Declare/initialise the variable used to track whether to repeat method
		boolean repeat = false; // by default we don't want to repeat the method

		// Create a transaction and initialise it with input from the user by passing
		// an empty array.
		String[] transactionData = new String[0];
		Transaction txn = new Transaction(transactionData, this.file);

		// declare / initialise a new menuArray
		String menuArray[] = new String[2];

		// Add menu options to the array
		menuArray[0] = "1. Submit this transaction\n";
		menuArray[1] = "2. Re-enter the transaction\n";

		// Create the menu, include the transaction details as a header
		Menu etm = new Menu(menuArray, txn.getTransactionDetails(true, false, "\n") + "\n\n");

		/*
		 * Check the txn.getComplete() value - if the user clicked cancel while
		 * initialising the transaction, the transaction.complete value will be false.
		 * If it's false we return to the main menu, otherwise show the enter
		 * transaction menu
		 */
		if (txn.getComplete() == true) {
			// User entered a complete transaction, display it along with menu options
			etm.displayMenu();

			// Check if the User clicked cancel
			while (etm.getSelection() != null) { // Start of Menu Loop
				/*
				 * This loop will repeat until the user clicks cancel, or userSelection is set
				 * to null as a result of their selecting a valid option. If userSelection is
				 * not null, check what has been entered and take the corresponding action.
				 */

				// check what the user inputed
				if (etm.getSelection().contentEquals("1")) {
					// user wants to submit the transaction.
					if (txn.submitTransaction() == true) { // this method writes the transaction to a file.
						JOptionPane.showMessageDialog(null, "Transaction submitted"); // alert user to success
						etm.setSelection(null); // set condition to exit loop

					} else { // there was an error during .submitTransaction
						JOptionPane.showMessageDialog(null, "Error submitting transaction"); // alert user to failure
					}
					etm.setSelection(null); // set condition to exit loop and return to main menu

				} else { // etm.getSelection() is "2", the only other value a user can enter.
					// user wants to re-enter the transaction
					etm.setSelection(null); // set condition to exit loop and return to main menu
					repeat = true; // repeat this method upon returning to main menu
				}
			} // End of Menu Loop
		}
		return repeat; // return repeat value to calling method
	}

	public void viewTransactionMenu() { // COMMENT REVIEW COMPLETE
		/*
		 * This method creates a transactions object holding all of the transactions in
		 * the transactions file.
		 * 
		 * This method creates a view Transaction menu containing no menu options, which
		 * means the menu will not display specific options or perform validation, and
		 * the user can enter any value. It then prompts the user for a transaction
		 * number, and if valid, displays the relevant transaction from the
		 * transactions.transaction array, along with a prompt to enter another
		 * transaction number. This repeats until the user clicks cancel.
		 * 
		 * Unlike other menus, validation for user inputs is handled inside this method
		 * rather than by the menu object.
		 */

		/*
		 * Create a local int to store a transaction number inputed by the user. A local
		 * variable is used as validation is being done inside this method.
		 */
		int byTransactionNumber = 0;

		// Create a transactions object, which will include an array containing all
		// transactions in the transactions file.
		Transactions txns = new Transactions(this.file);

		/*
		 * Create an empty menu array to pass to the menu constructor. This means there
		 * will be no options presented, only the header message ("enter transaction
		 * number").
		 */
		String menuArray[] = new String[0];

		// Create a menu object with no options, just the header
		Menu vtm = new Menu(menuArray, "Enter a transaction number");

		// Show the menu and get user input
		vtm.displayMenu();

		// Check if the user clicked cancel
		while (vtm.getSelection() != null) { // Start menu loop
			// validate the user's input using try/catch so that we avoid data type errors
			// if the user has inputed a value that is not an int
			try {
				// Get the transaction number entered
				byTransactionNumber = Integer.parseInt(vtm.getSelection());
				// Check that it's a valid transaction number (between 1 and the max transaction
				// number)
				while (byTransactionNumber < 1 || byTransactionNumber > txns.getSize()) {
					// usewr entered an invalid transaction number, recreate the menu object with a
					// new prompt
					vtm = new Menu(menuArray, "Invalid transaction number, please try again");
					// Display the updated menu and get user input
					vtm.displayMenu();
					// Get the new transaction number entered
					byTransactionNumber = Integer.parseInt(vtm.getSelection());
				}
				/*
				 * Transaction number entered is valid.
				 * 
				 * Recreate the menu object with a new prompt, prefixed with the transaction
				 * details. This allows the user to view the specified transaction, and enter
				 * another transaction number if they want to view another. Alternatively they
				 * click cancel to exit.
				 * 
				 * Note the offset of -1 to byTransactionNumber when using it as an array index,
				 * as transaction number 1 is stored at index 0, etc.
				 */
				vtm = new Menu(menuArray,
						txns.getTransaction(byTransactionNumber - 1).getTransactionDetails(true, true, "\n") + "\n\n"
								+ "Enter a transaction number to view another \nor cancel to return to main menu");
				// Display the updated menu and get user input.
				vtm.displayMenu();

			} catch (Exception e) { // error generated
				// regardless of the error, the user input was invalid. Recreate the menu object
				// with a new prompt
				vtm = new Menu(menuArray, "Invalid transaction number, please try again");
				// Display the updated menu and get user input.
				vtm.displayMenu();
			}
		} // End of menu loop
		return;
	}

	public boolean viewTransactionsMenu() { // COMMENT REVIEW COMPLETE

		/*
		 * This method reads all of the transactions from transactions file into an
		 * array, and then creates a menu object with options for the user to navigate
		 * the array. The first transaction is then displayed to the user along with the
		 * menu options. The user can then view the next transaction, the previous
		 * transaction, or opt to sort the transactions on a transaction field of their
		 * choosing.
		 */

		// Declare/initialise a return variable which tells the calling method whether
		// this method should be called again.
		boolean repeat = false;

		// Create a new transactions object, which will be populated by data from the
		// transactions.csv file
		Transactions txns = new Transactions(this.file);

		// declare / initialise a new menu array for the view transactions menu
		String vtmMenuArray[] = new String[3];

		// Add menu options to the view transactions array
		vtmMenuArray[0] = "1. View next transaction\n";
		vtmMenuArray[1] = "2. View previous transaction\n";
		vtmMenuArray[2] = "3. Sort transactions\n";

		// Declare / initialise a variable to track the current index of the
		// txns.transactions array
		int index = 0;

		// Create the view transactions menu, include the first transaction details as a
		// header

		Menu vtm = new Menu(vtmMenuArray, "Record number: " + (index + 1) + "\n\n"
				+ txns.getTransaction(index).getTransactionDetails(true, true, "\n") + "\n\n");

		// Show the view transactions menu and get input from the user
		vtm.displayMenu();

		// check if user clicked cancel
		while (vtm.getSelection() != null) { // Start view transactions menu loop

			if (vtm.getSelection().contentEquals("1")) { // user chose next transaction
				if (index == txns.getSize() - 1) { // check if there are any more transactions
					JOptionPane.showMessageDialog(null, "There are no more transactions to display!");
					vtm.displayMenu(); // show view transactions menu
				} else {
					index += 1; // increment counter
					// recreate view transactions menu with next transaction details as a header
					vtm = new Menu(vtmMenuArray, "Record number: " + (index + 1) + "\n\n"
							+ txns.getTransaction(index).getTransactionDetails(true, true, "\n") + "\n\n");
					vtm.displayMenu();
				}

			} else if (vtm.getSelection().contentEquals("2")) { // user chose previous transaction
				if (index == 0) {
					JOptionPane.showMessageDialog(null, "There are no previous transactions to display!");
					vtm.displayMenu(); // show view transactions menu
				} else {
					index -= 1;
					// recreate view transactions menu with previous transaction details as a header
					vtm = new Menu(vtmMenuArray, "Record number: " + (index + 1) + "\n\n"
							+ txns.getTransaction(index).getTransactionDetails(true, true, "\n") + "\n\n");

					// show the updated view transactions menu
					vtm.displayMenu();
				}

			} else if (vtm.getSelection().contentEquals("3")) { // user chose sort transactions

				/*
				 * Call the sort transactions method, which takes the txns array and an int
				 * value to sort on. The int value is determined by calling the
				 * sortTransactionsMenu method which prompts the user to enter a number
				 * representing the field that they want to sort by. Unlike other menu methods,
				 * the sortTransactionsMenu method returns an int value that represents the
				 * field used to sort the transactions array.
				 */
				txns.sortTransactions(sortTransactionsMenu());

				// Reset the transactions array index so that the first transaction from the
				// newly sorted array will be displayed.
				index = 0;

				// recreate view transactions menu with transaction details as a header
				vtm = new Menu(vtmMenuArray, "Record number: " + (index + 1) + "\n\n"
						+ txns.getTransaction(index).getTransactionDetails(true, true, "\n") + "\n\n");

				// show the updated view transactions menu
				vtm.displayMenu();
			}
		}
		return repeat;
	}

	public int sortTransactionsMenu() { // COMMENT REVIEW COMPLETE
		/*
		 * This method is a sub menu of the view transactions menu.
		 * 
		 * Unlike other menu methods, it returns an int identifying which field the user
		 * wants to use as a sort key.
		 */

		// declare / initialise the sort transactions menu array
		String stmMenuArray[] = new String[10];

		// Add menu options to the sort transactions menu array
		stmMenuArray[0] = "1: Transaction number (default)\n";
		stmMenuArray[1] = "2: Transaction type\n";
		stmMenuArray[2] = "3: Sender\n";
		stmMenuArray[3] = "4: Sender's account number or wallet address\n";
		stmMenuArray[4] = "5: Recipient\n";
		stmMenuArray[5] = "6: Recipient's account number or wallet address\n";
		stmMenuArray[6] = "7: Amount\n";
		stmMenuArray[7] = "8: Currency type\n";
		stmMenuArray[8] = "9: Date / time\n";
		stmMenuArray[9] = "10: Note\n";

		// declare a menu object for the sort transactions menu
		Menu stm = new Menu(stmMenuArray, "\n\n Sort by which field?\n");

		// show the sort transactions menu and get input from the user
		stm.displayMenu(); // can only result in valid inputs or null

		// if user clicked cancel, default to using transaction number as a key.
		while (stm.getSelection() == null) { // user clicked cancel
			stm.setSelection("0"); // sort by transaction number
		}
		// The return value is offset by -1, as the choice (1) correlates with field (0)
		// transaction number, etc.
		return Integer.parseInt(stm.getSelection()) - 1;
	}
}
