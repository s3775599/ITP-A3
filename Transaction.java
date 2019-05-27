import javax.swing.JOptionPane;
import java.io.*;

public class Transaction {
	/*
	 * The transaction class contains discrete details of a transaction, such as
	 * sender, amount, recipient etc.
	 * 
	 * It also provides functionality to input, validate, display, and write
	 * transactions to the transactions.csv file.
	 */

	private String file; // name of file storing transactions, the 'transaction file'
	private String transactionElements[]; // array of transaction elements
	private boolean complete; // flag to indicate if a transaction is complete

	// DATA STRUCTURE - transactionElements[]
	// [0] Transaction number
	// [1] Transaction type - W, D, T (withdrawal, deposit, transfer)
	// [2] Sender
	// [3] Sender's account number or wallet address (optional)
	// [4] Recipient
	// [5] Recipient's account number or wallet address (optional)
	// [6] Amount
	// [7] Currency type
	// [8] Date / time
	// [9] Note

	// CONSTRUCTOR
	public Transaction(String[] transactionData, String file) { // COMMENT REVIEW COMPLETE 21/4
		/*
		 * When this method is called, it is passed an array which is either empty, or
		 * contains values for each of its member variables. If the array is not empty,
		 * the values in the array are used to create the transaction. If the array is
		 * empty then the transaction is initialised using the initialiseTransaction
		 * method, which gets input from the user.
		 */

		// initialise the transactions file's name
		this.file = file;
		
		// initialise the transaction elements' array
		this.transactionElements = new String[10];

		// Check whether the supplied array is empty
		if (transactionData.length == 0) {
			// call the method to get user inputs that are used to create the transaction
			// initialises the transaction elements array and the .complete variable
			initialiseTransaction(); 
		} else {
			// Supplied array is populated, use the values to create the transaction.
			this.transactionElements = transactionData;
			this.complete = true;
		}
		return;
	}

	/////////////////////////////////////// FUNCTIONAL METHODS
	public void initialiseTransaction() { // COMMENT REVIEW COMPLETED 21/4
		/*
		 * This method collects inputs from the user for each transaction element,
		 * validates the user input against specific criteria for that element, and if
		 * the user input is valid it stores the value in the transactionElements array.
		 */

		/*
		 * Create a boolean variable to track whether a user clicks cancel to any of the
		 * prompts. If they click cancel they will be returned to the previous menu and
		 * the transaction flagged as incomplete (.complete = false)
		 */
		boolean cancel = false;
		// Create a variable to store user input
		String input = "";

		// initialise a loop counter, start at 1 because 0 is for transaction number and
		// that value is not supplied by the user.
		int counter = 1;

		/*
		 * use a loop to prompt the user to input a value for each of the transaction
		 * elements (transaction type, sender, sender wallet/account, recipient,
		 * recipient wallet/account, amount, currency type, date, note).
		 * 
		 * Lookup methods are used to identify the correct variable for the purposes of
		 * prompts and validation.
		 */
		while (counter < 10 && cancel != true) {
			// Get the transaction type and ensure it is valid
			input = getInput(getTransactionVariablePrompt(counter));
			while (input != null && !validateTransactionElementValue(counter, input)) {
				// data validation check failed, alert user to error
				JOptionPane.showMessageDialog(null, "Invalid choice, please try again.");
				// prompt for new input
				input = getInput(getTransactionVariablePrompt(counter));
			}
			if (input == null) { // check for cancel click
				cancel = true; // user clicked cancel
			} else { // user didn't click cancel
				// validation passed, store the user's input in the transactionElements array at
				// the current index.
				this.transactionElements[counter] = input;
			}
			counter += 1;
		}

		// if the user clicked cancel at any point, this method will return an
		// incomplete object, and the calling method needs to handle it appropriately.
		// The transaction class has a .complete boolean variable to track its
		// 'completed' status.
		if (cancel == true) {
			// user clicked cancel to a prompt for information, return an incomplete
			// transaction
			complete = false;
		} else {
			// return a completed transaction
			complete = true;
		}
		return;
	}

	public String getTransactionDetails(boolean includeFieldNames, boolean includeTransactionNumber, String delimiter) { // COMMENT
																															// REVIEW
																															// COMPLETE
																															// 21/4
		// This method returns a string which contains all of the details of a
		// transaction. This string is used either for display purposes, or as a value
		// to write to a file.
		//
		// The method takes 3 arguments:
		// 1) includeFieldNames - (yes for display on a message dialog window, no for
		// insertion into a .csv file).
		// 2) includeTransactionNumber (only if a transaction has been read from, or is
		// being written to, a csv file).
		// 3) The delimiter to be used ("\n" for display on a message dialog window, and
		// "," for insertion into a .csv file).

		// declare a string to store the transaction details
		String transactionDetails = "";

		// initialise a loop counter
		int counter = 0;

		// if transaction number is included, counter starts at 0. If not it starts at
		// 1.
		if (includeTransactionNumber == false) { // check if transaction number is included
			counter = 1; // start counter at 1 so transaction number is skipped
		}

		while (counter < 10) {
			// loop through the transactionElements array and output the names if required,
			// and values, and a delimiter
			if (includeFieldNames == true) {
				transactionDetails += getTransactionVariableName(counter) + ": ";
			}
			transactionDetails += this.transactionElements[counter] + delimiter;
			counter += 1;
		}
		// remove the trailing delimiter from the complete transaction string
		transactionDetails = transactionDetails.substring(0, transactionDetails.length() - delimiter.length());

		return transactionDetails; // return the completed string
	}

	public boolean submitTransaction() { // COMMENT REVIEW COMPLETE 21/4
		/*
		 * This method writes transaction data to a file, and returns true (success) or
		 * false (error)
		 */
		// The steps taken to write the transaction details are as follows:
		// 1) Check that an output file exists.
		// 2) If no file exists, create one and insert headers and the transaction
		// details with transaction number 1
		// 3) If file exists, read the file to get the last transaction number, insert
		// the transaction details into the first empty line with an incremented
		// transaction number.

		// Declare / initialise a return value which stores the return value
		boolean success = true; // true for now

		// 1) Check that an output file exists.
		// Create an instance of the file object so we can check whether file exists
		File checkFile = new File(this.file);

		try {
			// create a file writer object. Declaration is here as it's used for both of the
			// true/false if scenarios below
			BufferedWriter transactionFileWriter = null;
			if (checkFile.createNewFile()) { // returns true if file doesn't exist
				// no file exists, create one and insert headers and the transaction
				// details with transaction number 1
				transactionFileWriter = new BufferedWriter(new FileWriter("transactions.csv"));
				transactionFileWriter.write(
						"Transaction number, Transaction type, Sender, Sender's account number or wallet address (optional), Recipient, Recipient's account number or wallet address (optional), Amount, Currency type, Date / time, Note");
				this.transactionElements[0] = "1";
				transactionFileWriter.write("\n" + getTransactionDetails(false, true, ","));

			} else {
				// Create a file reader object.
				BufferedReader transactionFileReader = new BufferedReader(new FileReader("transactions.csv"));
				// Initialise an int variable to store last the transaction number
				int transactionNumber = 0;
				// Initialise a string variable to store the last line read, and read the first
				// 2 lines (we know the first is a header row).
				String readLine = transactionFileReader.readLine();
				readLine = transactionFileReader.readLine();
				// Read each line in the file and parse the string for the transaction number
				while (readLine != null) {
					transactionNumber = Integer.parseInt(readLine.substring(0, readLine.indexOf(",")));
					readLine = transactionFileReader.readLine(); // Get the next line
				}

				// Increment and set the transaction number
				this.transactionElements[0] = "" + (transactionNumber + 1);

				// Write the new line
				transactionFileWriter = new BufferedWriter(new FileWriter("transactions.csv", true));
				transactionFileWriter.write("\n" + getTransactionDetails(false, true, ","));

				// cleanup the reader
				transactionFileReader.close();
				transactionFileReader = null;
			}
			// cleanup the writer
			transactionFileWriter.close(); // close the file
			transactionFileWriter = null; // we don't need this anymore

		} catch (Exception e) {
			// an error occurred, display the error
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		// clean up the file object we used to check if file existed
		checkFile = null; // we don't need this anymore
		return success;
	}

	public String getInput(String prompt) { // COMMENT REVIEW COMPLETE 21/4
		// this method takes a String, shows it to the user with an input dialog, and
		// returns the user's input.
		String input = JOptionPane.showInputDialog(prompt);
		return input;
	}

	/////////////////////////////////// LOOKUP METHODS
	// The following methods are lookup methods which take an index (int value) to
	// identify the corresponding name, prompt, or validation method for an element
	// in the transactionElements array.
	//
	// DATA STRUCTURE - transactionElements[]
	// [0] Transaction number
	// [1] Transaction type - W, D, T (withdrawal, deposit, transfer)
	// [2] Sender
	// [3] Sender's account number or wallet address (optional)
	// [4] Recipient
	// [5] Recipient's account number or wallet address (optional)
	// [6] Amount
	// [7] Currency type
	// [8] Date / time
	// [9] Note

	public String getTransactionVariableName(int index) { // COMMENT REVIEW COMPLETE 21/4
		/*
		 * This method takes an index number and returns an element's name to be used
		 * when displaying values to the user.
		 */
		String name = "";

		// using the supplied int, return the name for the corresponding array element
		if (index == 0) {
			name = "Transaction number";
		} else if (index == 1) {
			name = "Transaction type";
		} else if (index == 2) {
			name = "Sender";
		} else if (index == 3) {
			name = "Sender's wallet / account";
		} else if (index == 4) {
			name = "Recipient";
		} else if (index == 5) {
			name = "Recipients wallet / account";
		} else if (index == 6) {
			name = "Amount";
		} else if (index == 7) {
			name = "Currency type";
		} else if (index == 8) {
			name = "Date";
		} else if (index == 9) {
			name = "Note";
		} else {
			name = "Invalid reference: " + index;
		}
		return name;
	}

	public String getTransactionVariablePrompt(int index) { // COMMENT REVIEW COMPLETE 21/4
		/*
		 * This method takes an index number and returns a prompt to be used when asking
		 * a user to input values for the corresponding array element
		 */

		// initialise a string to store the user input
		String prompt = "";

		// using the supplied int, return the prompt for the corresponding array element
		if (index == 0) {
			// there is no prompt for transaction number, it's procedurally generated.
		} else if (index == 1) {
			prompt = "Is this a (W)ithdrawal, (T)ransfer, or (D)eposit?";
		} else if (index == 2) {
			prompt = "Who is sending this transaction?";
		} else if (index == 3) {
			prompt = "Please enter the sender's account number or wallet address if known (Optional)";
		} else if (index == 4) {
			prompt = "Who is receiving this transaction?";
		} else if (index == 5) {
			prompt = "Please enter the destination account number or wallet address for this transaction?";
		} else if (index == 6) {
			prompt = "Please enter the amount for this transaction?";
		} else if (index == 7) {
			prompt = "Please enter the currency type for this transaction?";
		} else if (index == 8) {
			prompt = "Please enter the date for this transaction? (DD/MM/YYYY)";
		} else if (index == 9) {
			prompt = "Please enter any notes or details relevant to this transaction. (Optional)";
		} else {
			prompt = "Invalid reference: " + index;
		}
		return prompt;
	}

	public boolean validateTransactionElementValue(int index, String value) { // COMMENT REVIEW COMPLETE 21/4
		/*
		 * This method takes an index number and calls the validation method for the
		 * corresponding array element, passing the supplied value to be validated. If
		 * the validation method returns true, this method also returns true. Likewise
		 * if the validation method returns false, this method also returns false.
		 */

		// initialise a boolean to store the return value
		boolean check = false;

		// use the supplied index to call the validation method for the corresponding
		// array element, passing the argument 'value'. Assign the returned validation
		// result to 'check'
		if (index == 0) {
			check = validateTransactionNumber(value);
		} else if (index == 1) {
			check = validateTransactionType(value);
		} else if (index == 2) {
			check = validateSender(value);
		} else if (index == 3) {
			check = validateSenderWallet(value);
		} else if (index == 4) {
			check = validateRecipient(value);
		} else if (index == 5) {
			check = validateRecipientWallet(value);
		} else if (index == 6) {
			check = validateAmount(value);
		} else if (index == 7) {
			check = validateCurrencyType(value);
		} else if (index == 8) {
			check = validateDate(value);
		} else if (index == 9) {
			check = validateNote(value);
		}
		return check;
	}

	/////////////////////////////////////// GET METHODS
	public boolean getComplete() {
		// return the value of the member variable
		return this.complete;
	}

	public String getTransactionElement(int byField) { // COMMENT REVIEW COMPLETE 21/4
		/*
		 * This method takes an int that represents an index in the transactionElements
		 * array, and uses it to return the corresponding value in the array.
		 */
		String transactionElement = this.transactionElements[byField];
		return transactionElement;
	}

	/////////////////////////////////////// VALIDATION METHODS
	//
	// The following methods store validation rules for each element in the
	// transactionElements array. When called, they check the supplied argument
	// against the validation rules and return true if validation passes, or false
	// if validation fails.
	//
	// Not all elements have validation rules, but all elements have a validation
	// method in case validation is added. Where there are no validation rules, the
	// method will always return true.

	public boolean validateTransactionNumber(String transactionNumber) { // COMMENT REVIEW COMPLETE 21/4
		// Rules:
		// Must be numeric

		// declare / initialise a return variable
		boolean check;
		try {
			// If .parseDouble fails on the transactionNumber, it's not valid.
			Double.parseDouble(transactionNumber);
			check = true;
		} catch (Exception e) {
			check = false; // invalid value, return false.
		}
		return check;
	}

	public boolean validateTransactionType(String transactionType) { // COMMENT REVIEW COMPLETE 21/4
		// Rules:
		// Must be 'w', 'd', or 't'

		// declare / initialise a return variable
		boolean check = false;
		if (transactionType.equalsIgnoreCase("w") || transactionType.equalsIgnoreCase("d")
				|| transactionType.equalsIgnoreCase("t")) {
			check = true;
		}
		return check;
	}

	public boolean validateSender(String sender) { // COMMENT REVIEW COMPLETE 21/4
		// Rules: none

		// declare / initialise a return variable
		boolean check = true;
		return check;
	}

	public boolean validateSenderWallet(String senderWallet) { // COMMENT REVIEW COMPLETE 21/4
		// Rules: none

		// declare / initialise a return variable
		// initialise a variable to store result of data validation check
		boolean check = true;
		return check;
	}

	public boolean validateRecipient(String recipient) { // COMMENT REVIEW COMPLETE 21/4
		// Rules: none

		// declare / initialise a return variable
		boolean check = true;
		return check;
	}

	public boolean validateRecipientWallet(String recipientWallet) { // COMMENT REVIEW COMPLETE 21/4
		// Rules: none

		// declare / initialise a return variable
		boolean check = true;
		return check;
	}

	public boolean validateAmount(String amount) { // COMMENT REVIEW COMPLETE 21/4
		// Rules: must be a valid double

		// declare / initialise a return variable
		boolean check;
		try {
			// If .parseDouble fails on the transactionNumber, it's not valid.
			Double.parseDouble(amount);
			check = true;
		} catch (Exception e) {
			check = false; // invalid value, return false.
		}
		return check;
	}

	public boolean validateCurrencyType(String currencyType) { // COMMENT REVIEW COMPLETE 21/4
		// Rules: cannot be ""

		// declare / initialise a return variable
		boolean check = false;
		if (currencyType.contentEquals("")) {
			// validation fail
			check = false;
		} else {
			check = true;
		}
		return check;
	}

	public boolean validateDate(String date) { // COMMENT REVIEW COMPLETE 21/4
		// Rules:
		// a date is valid if it has
		// 1) 10 chars,
		// 2) the third and sixth characters are "/"
		// 3) the remaining values are all positive ints.

		// declare / initialise a return variable
		boolean check = false;

		if (date.length() == 10) { // check if the date has 10 chars
			// date has 10 chars
			if (date.substring(2, 3).contentEquals("/") && date.substring(5, 6).contentEquals("/")) {
				// the third and sixth characters are "/"
				// so far so good, use a loop check each character is an int
				// set the value of 'check' to true
				// if the loop finds a non-int, change it back to false and exit the loop.
				check = true;
				int counter = 0; // declare a loop counter
				while (counter < 10 && check == true) {
					// check if the value is an int, and then check if it's positive
					// exclude the "/" at positions 2 and 5 of the string.
					if (counter != 2 && counter != 5) {
						if (checkIfStringIsInt(date.substring(counter, counter + 1)) == false
								|| Integer.parseInt(date.substring(counter, counter + 1)) < 0) {
							// found an invalid value, exit loop and return false.
							check = false;
						}
					}
					counter += 1;
				}
			}
		}
		return check;
	}

	public boolean validateNote(String note) { // COMMENT REVIEW COMPLETE 21/4
		// Rules: None

		// declare / initialise a return variable
		boolean check = true;
		return check;
	}

	public boolean checkIfStringIsInt(String toCheck) { // COMMENT REVIEW COMPLETE 21/4
		// This method is passed a string, checks whether it is an int, and returns true
		// (int) or false (not an int). This is used for checking valid dates.
		boolean check = false;
		try {
			Integer.parseInt(toCheck); // see if we cause an error trying to parse an int
			check = true; // no error, value is an int
		} catch (Exception e) {
			// we caused an error trying to parse an int, so the string is not an int
			// no action required, return default false value
		}
		return check;
	}
}
