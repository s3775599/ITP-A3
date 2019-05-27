import javax.swing.JOptionPane;

public class Menu {

	/*
	 * The Menu class provides functionality to to display menu options, derive
	 * validation rules and validate inputs, and make the user's selection available
	 * to the creator of the object.
	 *
	 * The menu class does not take any actions resulting from a user's menu choice,
	 * for example - displaying a transaction, or writing transaction data to a file
	 * are both actions that are outside the scope of the menu class.
	 */

	private String menuMessage;
	private String validation[]; // stores validation rules in the form of acceptable values
	private String selection; // the user's selection
	private boolean validated; // a flag indicating whether the user selection is validated

	// CONSTRUCTOR
	public Menu(String menuArray[], String header) {
		/*
		 * The menu constructor method takes 2 arguments - an array holding menu
		 * options, and a header that is prefixed to the menu message.
		 * 
		 * It uses the menu array to create a menu message, and to create an array
		 * holding valid values for each menu option, which is used to validate user
		 * input when the menu is displayed and user input requested (in .displayMenu)
		 */

		//// Create the menu message
		// Add the header to the menuMessage
		this.menuMessage = header;

		// If the menuArray has options .length will be > 0, and we add text asking the
		// user to select an option. If the menuArray holds no options it means any
		// value can be entered and that the header will be the only prompt displayed to
		// the user.
		if (menuArray.length > 0) {
			this.menuMessage += "Please Select an Option \n";
		}

		//// Create the validation array
		// Declare/ Initialise a loop counter
		int counter = 0;

		// size the validation array to match the size of menuArray
		this.validation = new String[menuArray.length];

		// Loop through the menuArray and build the menu message, and validation array.
		while (counter < menuArray.length) {
			this.menuMessage += menuArray[counter];
			this.validation[counter] = "" + (counter + 1); // all menu selections must be 1,2,3 etc.
			counter += 1;
		}
		
		this.selection = null; // initialise selection default to null (cancel)
		this.validated = false; // initialise validated default to false
		return;
	}

	public void displayMenu() {
		/*
		 * This method displays the menu message, accepts a user input, and then
		 * validates the user input by checking whether the value entered is found in
		 * the validation array.
		 * 
		 * If the validation array has length 0, it means there is no validation and all
		 * values are considered valid for return by this method.
		 * 
		 * If the validation check fails, the user is prompted to try again. If the
		 * validation check passes, the member variable 'validated' is set to true.
		 */

		if (this.validation.length == 0) {
			this.validated = true;
		}

		this.selection = JOptionPane.showInputDialog(this.menuMessage); // Display menu and get input

		int counter = 0; // initialise a counter for the validation loop

		// Start the validation loop
		while (this.validated == false) { // check if validation has passed

			// loop until the end of the array, or until validation passes (no need to
			// iterate through remaining values once a validation check succeeds)
			// & check if inputed value is found at the current index
			while (counter < this.validation.length && validated == false) {
				if (this.selection == null) { // check if user clicked cancel
					this.validated = true; // cancel is always a valid option
				} else if (this.selection.contentEquals(this.validation[counter])) {
					this.validated = true; //
				}
				counter += 1;
			}
			if (this.validated == false) {
				// the user input is not found in the validation array and is therefore invalid
				JOptionPane.showMessageDialog(null, "Invalid choice, please try again!"); // alert user
				counter = 0; // reset counter
				this.selection = JOptionPane.showInputDialog(this.menuMessage); // Display menu and get new input
			}
		}
		return;
	}

	////////// GET/SET METHODS

	// only selection needs a get method, all other member variables are accessed
	// inside the menu class.
	public String getSelection() {
		// return the value of the 'selection' member variable
		return this.selection;
	}

	// only selection needs a set method, all other member variables are accessed
	// inside the menu class.
	public void setSelection(String selection) {
		this.selection = selection;
		return;
	}

}
