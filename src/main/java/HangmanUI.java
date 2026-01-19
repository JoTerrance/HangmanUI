import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

public class HangmanUI {
	// Constants
	public HangmanLogic LOGIC;

	// private static final Color BG_COLOR = new Color(141, 69, 220);
	private static final Color BG_COLOR = Color.white;
	private int fails = 0;

	private JLabel imageLabel;
	private JLabel lettersGuessedLabel;
	private JFrame frame;

	public HangmanUI() {
		// Main frame
		// for testing purpose
	}

	// Constructor
	public HangmanUI(HangmanLogic logic) {
		super();
		setLOGIC(logic);
		init();
	}

	public void init() {
		frame = createFrame();
		// Ask for the word to guess
		requestSecret();

		// Guess panel
		frame.add(guessPanel(), BorderLayout.NORTH);

		// Image panel
		frame.add(hangmanStatusPanel(), BorderLayout.SOUTH);

		frame.setSize(400, 600);

//        frame.pack();
		frame.setVisible(true);
	}

	public JFrame createFrame() {
		JFrame frame = new JFrame();
		configureFrame(frame);
		frame.setTitle("MasterMind");
		return frame;
	}

	void configureFrame(JFrame frame) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
	}

	// Input fields to enter guesses
	public JPanel guessPanel() {
		JPanel guessPanel = new JPanel();
		guessPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		guessPanel.setBackground(BG_COLOR);

		// Input field (10 columns wide)
		JTextField inputField = new JTextField(10);

		// Submit button
		JButton submitButton = new JButton("Submit");
		submitButton.setPreferredSize(new Dimension(80, 30));
		submitButton.setBackground(Color.WHITE);
		submitButton.setContentAreaFilled(true);
		submitButton.setBorderPainted(false);
		submitButton.setFocusPainted(false);

		// Submit button
		submitButton.addActionListener(e -> processGuess(inputField, submitButton));

		// ENTER key triggers submit
		inputField.addActionListener(e -> processGuess(inputField, submitButton));

		// Add components
		guessPanel.add(inputField);
		guessPanel.add(submitButton);

		return guessPanel;
	}

	// Image panel for hangman stages
	public JPanel hangmanStatusPanel() {
		JPanel hangmanPanel = new JPanel(new BorderLayout());
		hangmanPanel.setBackground(BG_COLOR);

		// Image
		setImageLabel(new JLabel(new ImageIcon(getClass().getResource("/stages/0.png"))));

		// Status
		setLettersGuessedLabel(new JLabel(LOGIC.getCurrentWordState()));
		// Center the text
		getLettersGuessedLabel().setHorizontalAlignment(SwingConstants.CENTER);

		// Adding
		hangmanPanel.add(getImageLabel(), BorderLayout.SOUTH);
		hangmanPanel.add(getLettersGuessedLabel(), BorderLayout.NORTH);

		return hangmanPanel;
	}

	// Pop-up to request a word to guess
	public void requestSecret() {
		String secretWord = JOptionPane.showInputDialog(null, "Enter the word to guess:").toLowerCase();
		while (!secretWord.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+")) {
			secretWord = JOptionPane.showInputDialog(null, "Enter a valid word (letters only):").toLowerCase();
		}
		LOGIC.setSecret(secretWord);

	}

	// Process user's guess
	// Esto permite que los tests en el mismo paquete puedan llamarlo.
	void processGuess(JTextField inputField, JButton submitButton) {
		String currentGuess = inputField.getText().toLowerCase();
		inputField.setText("");

		// To make sure user enters only one letter
		if (currentGuess.length() != 1 || !currentGuess.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+")) {
			JOptionPane.showMessageDialog(null, "Please enter exactly one letter.");
			return;
		}

		boolean guessed = LOGIC.guessLetter(currentGuess);
		getLettersGuessedLabel().setText(LOGIC.getCurrentWordState());

		// If the guess is wrong
		if (!guessed) {
			setFails(getFails() + 1);
			if (getFails() == 8) {
				endGameMessage(false);
				submitButton.setEnabled(false);
			}
		}
		// If user won
		else if (checkWin()) {
			endGameMessage(true);
			submitButton.setEnabled(false);
			inputField.setEnabled(false);
		}

		// Update hangman image
		String imagePath = "/stages/" + getFails() + ".png";
		getImageLabel().setIcon(new ImageIcon(getClass().getResource(imagePath)));
	}

	// Pop up at the end of the game
	public void endGameMessage(boolean win) {
		String message = "";
		if (win) {
			message = "You won!";
		} else {
			message = "You lost. The secret word: " + LOGIC.getSecret();
		}
		JOptionPane.showMessageDialog(null, message);

	}

	// Check is user guessed the complete word
	public boolean checkWin() {
		String currentState = LOGIC.getCurrentWordState().replace(" ", "");
		String secret = LOGIC.getSecret();

		if (currentState.equalsIgnoreCase(secret)) {
			return true;
		}
		return false;

	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	JLabel getLettersGuessedLabel() {
		return lettersGuessedLabel;
	}

	public HangmanLogic getLOGIC() {
		return LOGIC;
	}

	public void setLOGIC(HangmanLogic logic) {
		this.LOGIC = logic;
	}

	public int getFails() {
		return fails;
	}

	public void setFails(int fails) {
		this.fails = fails;
	}

	public void setLettersGuessedLabel(JLabel lettersGuessedLabel) {
		this.lettersGuessedLabel = lettersGuessedLabel;
	}

	public JLabel getImageLabel() {
		return imageLabel;
	}

	public void setImageLabel(JLabel imageLabel) {
		this.imageLabel = imageLabel;
	}

}