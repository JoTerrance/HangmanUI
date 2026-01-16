import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // Create logic
        HangmanLogic logic = new HangmanLogic();

        // Create UI
        SwingUtilities.invokeLater(() -> {
            new HangmanUI(logic);
        });
    }
}