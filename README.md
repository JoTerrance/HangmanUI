# Hangman Game (Java Swing)

This project is a simple **Hangman game** implemented in Java using **Swing** for the user interface. Players can guess letters to uncover a secret word, with visual feedback provided through a Hangman image that progresses with incorrect guesses.

---

## Features

- **Graphical User Interface (GUI)** using Java Swing.
- Enter guesses via a text field and submit button (or press ENTER).
- Dynamic Hangman images update with each incorrect guess.
- Input validation ensures only single alphabet letters are accepted.
- End-of-game pop-ups announce **win** or **loss**.
- Fully modular logic separated from the UI for maintainability.

---

## Files

- `HangmanUI.java` – Contains the Swing GUI implementation and user interaction logic.
- `HangmanLogic.java` – Handles the core game logic, secret word storage, and letter guessing.
- `/stages/0.png` to `/stages/8.png` – Images representing Hangman stages for incorrect guesses.

## Build & Run with Maven

Prerequisites: JDK 17+ and Maven 3.9+ in your PATH.

```bash
mvn clean package
mvn exec:java
```

The exec plugin is configured to start the Swing UI from `Main` and will load hangman images from `src/main/resources/stages`.
