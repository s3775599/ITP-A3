import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JOptionPane;

public class Transactions {
	/*
	 * The Transactions class holds the transactions that are in the transactions
	 * file, and provides display and sorting functionality
	 */

	private String file; // the name of the transactions file including the name
	private Transaction[] transactions; // an array to store transactions
	private int size; // holds the size of the array (# transactions) for easy reference

	// CONSTRUCTOR
	public Transactions(String file) { // comment review completed

		/*
		 * The transactions constructor uses the getNumberOfTransactions method to get
		 * the number of transactions in the transactions file and store it in the
		 * size variable. It then calls the readTransactions method to initialise the
		 * transactions array with transactions from the transactions file.
		 */
		this.file = file; // initialise the file variable
		this.size = getNumberOfTransactions(); // initialise the size variable
		readTransactions(); // initialise transactions array
		return;
	}

	public int getNumberOfTransactions() { // comment review completed

		// Initialise a counter to count lines in the file
		int counter = 0;
		try {
			// Create a file reader object.
			BufferedReader transactionFileReader = new BufferedReader(new FileReader(file));

			// Initialise a string variable to store the last line read, and read the first
			// line (we know the first is a header row) without incrementing counter.
			String readLine = transactionFileReader.readLine();

			// Read each line in the file and increment the counter
			while (readLine != null) {
				readLine = transactionFileReader.readLine(); // Get the next line
				if (readLine != null) { // check that the line isn't null,
					counter += 1; // if so increment counter
				}
			}
			// cleanup
			transactionFileReader.close();

		} catch (Exception e) {
			// an error occurred, display the error
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return counter;
	}

	public void readTransactions() { // comment review completed
		/*
		 * This reads transaction data line by line from the transactions file, and
		 * creates an array of transactions with the corresponding transaction details
		 * in the file.
		 */

		// Declare/Initialise variables
		// Size the transactions array to match the number of transactions in the
		// transactions file.
		this.transactions = new Transaction[size];

		String transactionData[]; // array used to split each line as it is read from the file
		String readLine; // string to store the value of each line as it's read from the file
		int index = 0; // int to track the current index of the transactions array
		boolean fileOpened = false; // flag to check if file has opened without an error
		boolean allTransactionsRead = false; // flag to check if file has closed without an error

		// Create a file reader object.
		BufferedReader transactionFileReader;

		try {
			// This try..catch block creates a file reader object to open the
			// transactions file, and then reads each transaction into the transactions
			// array. It then closes the file reader object.

			// Initialise the file reader object
			transactionFileReader = new BufferedReader(new FileReader(file));

			// Initialise readLine with the first line of the file. We know it is a header
			// row and don't want to store it in the transactions array.
			readLine = transactionFileReader.readLine();

			fileOpened = true; // The file has opened without error, set flag.

			while (index < this.size) {
				// This block loops through each index of the transactions array

				// read the next line in the file
				readLine = transactionFileReader.readLine();

				// Read each line in the file, split it with the ',' delimiter, and store each
				// value in the transactionData array
				transactionData = readLine.split(",");

				this.transactions[index] = new Transaction(transactionData, this.file);

				// Increment the index
				index += 1;
			}

			// all transactions have been read without error, set flag.
			allTransactionsRead = true;

			// cleanup, close file
			transactionFileReader.close();

		} catch (Exception e) {
			// error occurred, alert user
			if (fileOpened == false) { // error occured without file opening
				JOptionPane.showMessageDialog(null,
						e.getMessage() + "\nAn error occured opening the " + file + " file: ");
			} else if (allTransactionsRead == true) { // error occurred while closing file
				JOptionPane.showMessageDialog(null,
						e.getMessage() + "\nAn error occured closing the " + file + " file");
			} else { // error occurred after file was opened, but before all transactions were read.
				JOptionPane.showMessageDialog(null, e.getMessage() + "\nAn error occured reading transaction " + index);
			}
		}
		return;
	}

	public void sortTransactions(int byField) { // comment review completed

		/*
		 * Sorting is done by String.compareTo for most values, by a numeric comparison
		 * (>) for transactionNumber or Amount, and by using a custom method for dates.
		 * 
		 * The if condition for the comparison is a little hard to read, so to summarise
		 * it:
		 * 
		 * The left item is transactions[counter], the right is transactions[counter+1]
		 * 
		 * if (data type is numeric, AND left > right) OR (data type is a date AND left
		 * date occurs later than right date) OR (left .compareTo(right) > 0), then ...
		 */

		int counter = 0; // loop counter
		Transaction swap; // initialise a transaction used to swap values around

		// SORT
		while (counter < this.size - 1) { // -1 so we don't check a row outside the array
			// Compare the current row to next row, and see if they're in the correct order.
			// if (data type is numeric, and left > right) OR (left .compareTo(right) > 0),
			// then ...
			if (((byField == 0 || byField == 6)
					&& Double.parseDouble(this.transactions[counter].getTransactionElement(byField)) > Double
							.parseDouble(this.transactions[counter + 1].getTransactionElement(byField)))
					|| (byField == 8 && compareDate(this.transactions[counter].getTransactionElement(byField),
							this.transactions[counter + 1].getTransactionElement(byField)) == true)
					|| (byField != 0 && byField != 6 && byField != 8)
							&& this.transactions[counter].getTransactionElement(byField).compareToIgnoreCase(
									this.transactions[(counter + 1)].getTransactionElement(byField)) > 0) {
				// The current row is larger than the next row, move current row array to
				// the swap array

				swap = this.transactions[counter];
				this.transactions[counter] = this.transactions[(counter + 1)]; // move next row to current row
				this.transactions[(counter + 1)] = swap; // move swap to next row

				if (counter > 0) { // make sure we don't move outside the array's lower boundary
					counter -= 1; // next comparison will be current and previous row
				}
			} else { // current and previous row were in the correct order
				if (counter < this.size) { // make sure we don't move outside the array's upper boundary
					counter += 1; // current and previous row were in correct order, move up a row
				}
			}
		}
		return;
	}

	public boolean compareDate(String date1, String date2) {
		// This method compares dates accurately, returns true if date 1 occurs after
		// date 2 (i.e. date 1 > date 2)

		// declare a return variable
		boolean compareDate;

		// initialise an array to store day/month/year elements of supplied dates
		String[] date1Array = new String[0];
		String[] date2Array = new String[0];

		// populate the array using .split function, with / as a delimiter
		// index 0 = day
		// index 1 = month
		// index 2 = year
		date1Array = date1.split("/");
		date2Array = date2.split("/");

		// put the day/month/year values into ints so they can be compared using '<' and
		// '>'
		int day1 = Integer.parseInt(date1Array[0]);
		int month1 = Integer.parseInt(date1Array[1]);
		int year1 = Integer.parseInt(date1Array[2]);

		int day2 = Integer.parseInt(date2Array[0]);
		int month2 = Integer.parseInt(date2Array[1]);
		int year2 = Integer.parseInt(date2Array[2]);

		// compare years first, if years are equal compare months, if months are equal
		// compare days.
		if (year1 > year2) {
			// the first year is later than the second year
			// :. date 1 > date 2
			compareDate = true;

		} else if (year1 < year2) {
			// the first year is earlier than the second year
			// :. date 1 < date 2
			compareDate = false;

		} else if (month1 > month2) {
			// same year
			// the first month is later than the second month
			// :. date 1 > date 2
			compareDate = true;

		} else if (month1 < month2) {
			// same year
			// the first month is earlier than the second month
			// :. date 1 < date 2
			compareDate = false;

		} else if (day1 > day2) {
			// same year
			// same month
			// the first day is later than the second day
			// :. date 1 > date 2
			compareDate = true;

		} else if (day1 < day2) {
			// same year
			// same month
			// the first day is earlier than the second day
			// :. date 1 < date 2
			compareDate = false;

		} else {
			// same year
			// same month
			// same day
			// return false as first date is not > than the second date.
			compareDate = false;
		}
		return compareDate;
	}

	public int getSize() {
		return this.size;
	}
	
	public Transaction getTransaction(int index) {
		Transaction transaction = this.transactions[index];
		return transaction;
	}

}
