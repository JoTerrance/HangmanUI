import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)   
class HangmanUITest {
	
	HangmanLogic logic;
	
	@Test
	void testInitFrame() {
		 // given
	    HangmanUI hangmanUI = spy(new HangmanUI());
	    hangmanUI.setLOGIC(logic);
	    JFrame frameMock = mock(JFrame.class);

	    doReturn(frameMock).when(hangmanUI).createFrame();
	    doNothing().when(hangmanUI).requestSecret();

	    // when
	    hangmanUI.init();

	    // then
	    verify(hangmanUI).requestSecret();
	    verify(frameMock).setVisible(true);
	    verify(frameMock, times(2))
	            .add(any(Component.class), anyString());
	}
	

    private HangmanUI ui;
    
    
    @Test
    void testProcessGuess_correctLetter_updatesLabelsAndDisablesOnWin() {
        // Given
        HangmanUI ui = new HangmanUI();
        HangmanLogic logic = mock(HangmanLogic.class);
        ui.setLOGIC(logic);

        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);

        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));

        when(inputField.getText()).thenReturn("a");
        when(logic.guessLetter("a")).thenReturn(true);
        when(logic.getCurrentWordState()).thenReturn("a _ _ a");
        when(logic.getSecret()).thenReturn("a _ _ a");

        // Mock JOptionPane
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {

            // When
            ui.processGuess(inputField, submitButton);

            // Then
            verify(inputField).setText("");
            verify(ui.getLettersGuessedLabel()).setText("a _ _ a");
            verify(submitButton, never()).setEnabled(false); // No ganó todavía
            verify(ui.getImageLabel()).setIcon(any(ImageIcon.class));
        }
    }
    
    @Test
    void testProcessGuess_incorrectLetter_triggersLoss() {
    	//given
        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);

        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));

        when(inputField.getText()).thenReturn("x");
        when(logic.guessLetter("x")).thenReturn(false);

        ui.setFails(7); // próximo fallo = 8

        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // when
            ui.processGuess(inputField, submitButton);

            // then
            assertEquals(8, ui.getFails());
            verify(submitButton).setEnabled(false);
            pane.verify(() -> JOptionPane.showMessageDialog(null, "You lost. The secret word: " + logic.getSecret()));
        }
    }

    @Test
    void testCheckWin_returnsTrue_whenWordIsCompleted() {
         
        when(logic.getCurrentWordState()).thenReturn("c a s a");
        when(logic.getSecret()).thenReturn("casa");

        assertTrue(ui.checkWin());
    }

    @Test
    void testCheckWin_returnsFalse_whenWordIsIncomplete() {
        when(logic.getCurrentWordState()).thenReturn("c _ s a");
        when(logic.getSecret()).thenReturn("casa");

        assertFalse(ui.checkWin());
    }

    @Test
    void testConfigureFrame_setsCorrectProperties() {
        JFrame frame = mock(JFrame.class);

        ui.configureFrame(frame);

        verify(frame).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        verify(frame).setLayout(any(BorderLayout.class));
    }

    @Test
    void testGuessPanel_containsTextFieldAndButton() {
        JPanel panel = ui.guessPanel();

        assertEquals(2, panel.getComponentCount());
        assertTrue(panel.getComponent(0) instanceof JTextField);
        Component jbuttonTest = panel.getComponent(1);
		assertTrue(jbuttonTest instanceof JButton);
        assertTrue(((JButton)jbuttonTest).getBackground().equals(java.awt.Color.white));
        assertTrue(panel.getBackground().equals(java.awt.Color.white));
    }

    @Test
    void testHangmanStatusPanel_usesCurrentWordStateFromLogic() {
        //given
    	when(logic.getCurrentWordState()).thenReturn("_ a _ a");
    	//when
        ui.hangmanStatusPanel();
        //then
        assertEquals("_ a _ a", ui.getLettersGuessedLabel().getText());
    }

    @Test
    void testRequestSecret_setsSecretInLogic() {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() ->
                    JOptionPane.showInputDialog(any(), anyString()))
                    .thenReturn("Casa");

            ui.requestSecret();

            verify(logic).setSecret("casa");
        }
    }

    @Test
    void testEndGameMessage_win_showsWinMessage() {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            ui.endGameMessage(true);

            mockedPane.verify(() ->
                    JOptionPane.showMessageDialog(null, "You won!"));
        }
    }

    @Test
    void testEndGameMessage_loss_showsSecretWord() {
        when(logic.getSecret()).thenReturn("java");

        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            ui.endGameMessage(false);

            mockedPane.verify(() ->
                    JOptionPane.showMessageDialog(
                            null,
                            "You lost. The secret word: java"));
        }
    }
    
    
    @Test
    void testProcessGuess_invalidInput_multipleLetters_showsErrorMessage() {
        // Given
        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);
        
        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));
        
        when(inputField.getText()).thenReturn("abc");
        
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // When
            ui.processGuess(inputField, submitButton);
            
            // Then
            verify(inputField).setText("");
            verify(logic, never()).guessLetter(anyString());
            pane.verify(() -> JOptionPane.showMessageDialog(null, "Please enter exactly one letter."));
        }
    }
    
    @Test
    void testProcessGuess_invalidInput_emptyString_showsErrorMessage() {
        // Given
        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);
        
        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));
        
        when(inputField.getText()).thenReturn("");
        
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // When
            ui.processGuess(inputField, submitButton);
            
            // Then
            verify(inputField).setText("");
            verify(logic, never()).guessLetter(anyString());
            pane.verify(() -> JOptionPane.showMessageDialog(null, "Please enter exactly one letter."));
        }
    }
    
    @Test
    void testProcessGuess_invalidInput_number_showsErrorMessage() {
        // Given
        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);
        
        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));
        
        when(inputField.getText()).thenReturn("5");
        
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // When
            ui.processGuess(inputField, submitButton);
            
            // Then
            verify(inputField).setText("");
            verify(logic, never()).guessLetter(anyString());
            pane.verify(() -> JOptionPane.showMessageDialog(null, "Please enter exactly one letter."));
        }
    }
    
    @Test
    void testProcessGuess_correctLetter_userWins_disablesInputsAndShowsWinMessage() {
        // Given
        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);
        
        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));
        
        when(inputField.getText()).thenReturn("a");
        when(logic.guessLetter("a")).thenReturn(true);
        when(logic.getCurrentWordState()).thenReturn("c a s a");
        when(logic.getSecret()).thenReturn("casa");
        
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // When
            ui.processGuess(inputField, submitButton);
            
            // Then
            verify(inputField).setText("");
            verify(ui.getLettersGuessedLabel()).setText("c a s a");
            verify(submitButton).setEnabled(false);
            verify(inputField).setEnabled(false);
            pane.verify(() -> JOptionPane.showMessageDialog(null, "You won!"));
        }
    }
    
    @Test
    void testProcessGuess_withAccentedLetter_processesCorrectly() {
        // Given
        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);
        
        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));
        
        when(inputField.getText()).thenReturn("á");
        when(logic.guessLetter("á")).thenReturn(true);
        when(logic.getCurrentWordState()).thenReturn("_ á _ _");
        when(logic.getSecret()).thenReturn("pájá");
        
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // When
            ui.processGuess(inputField, submitButton);
            
            // Then
            verify(logic).guessLetter("á");
            verify(ui.getLettersGuessedLabel()).setText("_ á _ _");
            pane.verify(() -> JOptionPane.showMessageDialog(any(), anyString()), never());
        }
    }
    
    @Test
    void testProcessGuess_incorrectLetter_incrementsFails() {
        // Given
        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);
        
        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));
        ui.setFails(3);
        
        when(inputField.getText()).thenReturn("z");
        when(logic.guessLetter("z")).thenReturn(false);
        when(logic.getCurrentWordState()).thenReturn("_ _ _ _");
        
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // When
            ui.processGuess(inputField, submitButton);
            
            // Then
            assertEquals(4, ui.getFails());
            verify(submitButton, never()).setEnabled(false);
        }
    }
    
    @Test
    void testRequestSecret_withInvalidInput_retriesUntilValid() {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), anyString()))
                    .thenReturn("123")       // primer intento: números
                    .thenReturn("abc123")    // segundo intento: letras y números
                    .thenReturn("válido");   // tercer intento: válido con acento
            
            // When
            ui.requestSecret();
            
            // Then
            verify(logic).setSecret("válido");
            mockedPane.verify(() -> JOptionPane.showInputDialog(any(), anyString()), times(3));
        }
    }
    
    @Test
    void testRequestSecret_convertsToLowerCase() {
        try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
            mockedPane.when(() -> JOptionPane.showInputDialog(any(), anyString()))
                    .thenReturn("MAYÚSCULAS");
            
            // When
            ui.requestSecret();
            
            // Then
            verify(logic).setSecret("mayúsculas");
        }
    }
    
    @Test
    void testGettersAndSetters_fails() {
        // When
        ui.setFails(5);
        
        // Then
        assertEquals(5, ui.getFails());
    }
    
    @Test
    void testGettersAndSetters_frame() {
        // Given
        JFrame mockFrame = mock(JFrame.class);
        
        // When
        ui.setFrame(mockFrame);
        
        // Then
        assertEquals(mockFrame, ui.getFrame());
    }
    
    @Test
    void testGettersAndSetters_imageLabel() {
        // Given
        JLabel mockLabel = mock(JLabel.class);
        
        // When
        ui.setImageLabel(mockLabel);
        
        // Then
        assertEquals(mockLabel, ui.getImageLabel());
    }
    
    @Test
    void testGettersAndSetters_lettersGuessedLabel() {
        // Given
        JLabel mockLabel = mock(JLabel.class);
        
        // When
        ui.setLettersGuessedLabel(mockLabel);
        
        // Then
        assertEquals(mockLabel, ui.getLettersGuessedLabel());
    }
    
    @Test
    void testGettersAndSetters_logic() {
        // Given
        HangmanLogic mockLogic = mock(HangmanLogic.class);
        
        // When
        ui.setLOGIC(mockLogic);
        
        // Then
        assertEquals(mockLogic, ui.getLOGIC());
    }
    
    @Test
    void testCheckWin_caseInsensitive_returnsTrue() {
        // Given
        when(logic.getCurrentWordState()).thenReturn("C A S A");
        when(logic.getSecret()).thenReturn("casa");
        
        // When/Then
        assertTrue(ui.checkWin());
    }
    
    @Test
    void testHangmanStatusPanel_createsImageWithCorrectInitialStage() {
        // Given
        when(logic.getCurrentWordState()).thenReturn("_ _ _ _");
        
        // When
        JPanel panel = ui.hangmanStatusPanel();
        
        // Then
        assertNotNull(panel);
        assertNotNull(ui.getImageLabel());
        assertNotNull(ui.getImageLabel().getIcon());
    }
    
    @Test
    void testProcessGuess_updatesImagePath_basedOnFailCount() {
        // Given
        JTextField inputField = mock(JTextField.class);
        JButton submitButton = mock(JButton.class);
        
        ui.setLettersGuessedLabel(mock(JLabel.class));
        ui.setImageLabel(mock(JLabel.class));
        ui.setFails(2);
        
        when(inputField.getText()).thenReturn("x");
        when(logic.guessLetter("x")).thenReturn(false);
        when(logic.getCurrentWordState()).thenReturn("_ _ _ _");
        
        try (MockedStatic<JOptionPane> pane = mockStatic(JOptionPane.class)) {
            // When
            ui.processGuess(inputField, submitButton);
            
            // Then
            assertEquals(3, ui.getFails());
            verify(ui.getImageLabel()).setIcon(any(ImageIcon.class));
        }
    }

    
    @BeforeAll
    static void setUpHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeEach
    void setUp() {
    	ui = new HangmanUI();
    	logic = mock(HangmanLogic.class);
        ui.setLOGIC(logic);
    }
}


