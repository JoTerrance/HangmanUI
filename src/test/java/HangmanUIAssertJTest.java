import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JLabelFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;

import static org.assertj.swing.launcher.ApplicationLauncher.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@Disabled("All tests in this class are disabled")
public class HangmanUIAssertJTest {
    private FrameFixture window;
    private HangmanUI hangmanUI;
    private HangmanLogic logic;
    private MockedStatic<JOptionPane> optionPaneMock;
    

    @BeforeEach
    public void setUp() {
        // Crear la lógica y la UI en el EDT
        logic = new HangmanLogic();
        
        // Mockear JOptionPane para evitar que solicite entrada del usuario
        optionPaneMock = mockStatic(JOptionPane.class);
        optionPaneMock.when(() -> JOptionPane.showInputDialog(any(), anyString()))
                .thenReturn("hello");
        optionPaneMock.when(() -> JOptionPane.showMessageDialog(any(), anyString()))
                .thenAnswer(invocation -> null);
        
        hangmanUI = GuiActionRunner.execute(() -> {
            HangmanUI ui = new HangmanUI(logic);
            return ui;
        });
        
        // Crear el FrameFixture
        window = new FrameFixture(hangmanUI.getFrame());
        window.show();
    }

    @AfterEach
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
        if (optionPaneMock != null) {
            optionPaneMock.close();
        }
    }

    @Test
    public void shouldShowInitialWordState() {
        // Verificar que la palabra inicial está oculta
        JLabelFixture wordLabel = window.label(new GenericTypeMatcher<JLabel>(JLabel.class) {
            @Override
            protected boolean isMatching(JLabel label) {
                return label.getText() != null && label.getText().contains("_");
            }
        });
        
        wordLabel.requireVisible();
    }

    @Test
    public void shouldAcceptLetterInput() {
        // Encontrar el campo de texto
        JTextComponentFixture inputField = window.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            @Override
            protected boolean isMatching(JTextField textField) {
                return textField.getColumns() == 10;
            }
        });
        
        // Escribir una letra
        inputField.enterText("h");
        inputField.requireText("h");
    }

    @Test
    public void shouldClearInputAfterSubmit() {
        // Encontrar componentes
        JTextComponentFixture inputField = window.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            @Override
            protected boolean isMatching(JTextField textField) {
                return textField.getColumns() == 10;
            }
        });
        
        JButtonFixture submitButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return button.getText().equals("Submit");
            }
        });
        
        // Escribir y enviar
        inputField.enterText("h");
        submitButton.click();
        
        // Verificar que el campo se limpia
        inputField.requireEmpty();
    }

    @Test
    public void shouldUpdateWordStateAfterCorrectGuess() {
        // Encontrar componentes
        JTextComponentFixture inputField = window.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            @Override
            protected boolean isMatching(JTextField textField) {
                return textField.getColumns() == 10;
            }
        });
        
        JButtonFixture submitButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return button.getText().equals("Submit");
            }
        });
        
        JLabelFixture wordLabel = window.label(new GenericTypeMatcher<JLabel>(JLabel.class) {
            @Override
            protected boolean isMatching(JLabel label) {
                return label.getText() != null && label.getText().contains("_");
            }
        });
        
        // Estado inicial
        String initialState = wordLabel.text();
        
        // Adivinar una letra que está en "hello" (h)
        inputField.enterText("h");
        submitButton.click();
        
        // Verificar que el estado cambió
        String newState = wordLabel.text();
        // "h" debería aparecer en el estado
        assert newState.contains("h") : "La palabra debería contener 'h'";
    }

    @Test
    public void shouldUpdateHangmanImageAfterWrongGuess() {
        // Encontrar componentes
        JTextComponentFixture inputField = window.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            @Override
            protected boolean isMatching(JTextField textField) {
                return textField.getColumns() == 10;
            }
        });
        
        JButtonFixture submitButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return button.getText().equals("Submit");
            }
        });
        
        // Adivinar una letra incorrecta (z)
        inputField.enterText("z");
        submitButton.click();
        
        // Verificar que el contador de fallos aumentó
        assert hangmanUI.getFails() == 1 : "El contador de fallos debería ser 1";
    }

    @Test
    public void shouldShowErrorMessageForInvalidInput() {
        // Encontrar componentes
        JTextComponentFixture inputField = window.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            @Override
            protected boolean isMatching(JTextField textField) {
                return textField.getColumns() == 10;
            }
        });
        
        JButtonFixture submitButton = window.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return button.getText().equals("Submit");
            }
        });
        
        // Intentar enviar múltiples caracteres
        inputField.enterText("ab");
        submitButton.click();
        
        // Verificar que se llamó al diálogo de error
        optionPaneMock.verify(
            () -> JOptionPane.showMessageDialog(any(), eq("Please enter exactly one letter.")),
            times(1)
        );
    }

    @Test
    public void shouldDisableSubmitButtonAfterWinning() {
        // Cerrar el mock de clase para crear uno nuevo
        if (optionPaneMock != null) {
            optionPaneMock.close();
        }
        
        // Crear una lógica con palabra fácil de adivinar
        HangmanLogic customLogic = new HangmanLogic();
        customLogic.setSecret("a");
        
        try (MockedStatic<JOptionPane> localMock = mockStatic(JOptionPane.class)) {
            localMock.when(() -> JOptionPane.showInputDialog(any(), anyString()))
                    .thenReturn("a");
            localMock.when(() -> JOptionPane.showMessageDialog(any(), anyString()))
                    .thenAnswer(invocation -> null);
            
            HangmanUI customUI = GuiActionRunner.execute(() -> new HangmanUI(customLogic));
            FrameFixture customWindow = new FrameFixture(customUI.getFrame());
            customWindow.show();
            
            try {
                // Encontrar componentes
                JTextComponentFixture inputField = customWindow.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
                    @Override
                    protected boolean isMatching(JTextField textField) {
                        return textField.getColumns() == 10;
                    }
                });
                
                JButtonFixture submitButton = customWindow.button(new GenericTypeMatcher<JButton>(JButton.class) {
                    @Override
                    protected boolean isMatching(JButton button) {
                        return button.getText().equals("Submit");
                    }
                });
                
                // Adivinar la letra correcta
                inputField.enterText("a");
                submitButton.click();
                
                // Verificar que el botón se deshabilitó
                submitButton.requireDisabled();
            } finally {
                customWindow.cleanUp();
            }
        }
    }

    @Test
    public void shouldDisableSubmitButtonAfterLosing() {
        // Cerrar el mock de clase para crear uno nuevo
        if (optionPaneMock != null) {
            optionPaneMock.close();
        }
        
        // Crear una lógica con palabra difícil
        HangmanLogic customLogic = new HangmanLogic();
        customLogic.setSecret("xyz");
        
        try (MockedStatic<JOptionPane> localMock = mockStatic(JOptionPane.class)) {
            localMock.when(() -> JOptionPane.showInputDialog(any(), anyString()))
                    .thenReturn("xyz");
            localMock.when(() -> JOptionPane.showMessageDialog(any(), anyString()))
                    .thenAnswer(invocation -> null);
            
            HangmanUI customUI = GuiActionRunner.execute(() -> new HangmanUI(customLogic));
            FrameFixture customWindow = new FrameFixture(customUI.getFrame());
            customWindow.show();
            
            try {
                // Encontrar componentes
                JTextComponentFixture inputField = customWindow.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
                    @Override
                    protected boolean isMatching(JTextField textField) {
                        return textField.getColumns() == 10;
                    }
                });
                
                JButtonFixture submitButton = customWindow.button(new GenericTypeMatcher<JButton>(JButton.class) {
                    @Override
                    protected boolean isMatching(JButton button) {
                        return button.getText().equals("Submit");
                    }
                });
                
                // Hacer 8 intentos incorrectos
                String[] wrongLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};
                for (String letter : wrongLetters) {
                    if (submitButton.target().isEnabled()) {
                        inputField.enterText(letter);
                        submitButton.click();
                    }
                }
                
                // Verificar que el botón se deshabilitó
                submitButton.requireDisabled();
            } finally {
                customWindow.cleanUp();
            }
        }
    }

    @Test
    public void shouldHandleEnterKeyPress() {
        // Encontrar el campo de texto
        JTextComponentFixture inputField = window.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            @Override
            protected boolean isMatching(JTextField textField) {
                return textField.getColumns() == 10;
            }
        });
        
        // Escribir y presionar Enter
        inputField.enterText("e");
        inputField.pressAndReleaseKeys(java.awt.event.KeyEvent.VK_ENTER);
        
        // Verificar que el campo se limpia (indicando que se procesó)
        inputField.requireEmpty();
    }
}
