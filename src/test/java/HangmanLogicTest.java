import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class HangmanLogicTest {
	
	HangmanLogic hangmanlogic;
	
	@Test
	void testHangmanConstructor() {
		//given	//when
		var hangmanlogic = new HangmanLogic();
		//then
		assertNotNull(hangmanlogic);
	}
	
	
	@Test
	void testGetSecret() {
		//given
		String EXPECTED_SECRET = "AZUL";
		hangmanlogic.setSecret(EXPECTED_SECRET);
		//when
		String secret = hangmanlogic.getSecret();
		//then
		assertEquals(EXPECTED_SECRET, secret);
	}
	
	
	@Test
	void testGetSecretIsNull() {
		assertNull(hangmanlogic.getSecret());
	}
	
	@Test
	void testSetSecret() {
		
		String word = "hola";
		
		hangmanlogic.setSecret(word);
		
		assertEquals("_ _ _ _ " , hangmanlogic.getCurrentWordState() );
	}
	
	
	@Test
	void testSetSecretWitha_NULLLetterWord() {
		String word = null;
		
		
		Executable executable = new Executable() {

			@Override
			public void execute() throws Throwable {
				hangmanlogic.setSecret(word);
			}
		};
			
		assertThrows(NullPointerException.class, executable);
		assertThrows(NullPointerException.class, () -> hangmanlogic.setSecret(word));

	}
	
	
	@Test
	void testSetSecretWithAzeroLetterWord() {
		String word = "";

		hangmanlogic.setSecret(word);
		
		assertEquals("" , hangmanlogic.getCurrentWordState() );

	}
	
	@Test
	void testSetSecretWithAoneLetterWord() {
		String word = "h";

		hangmanlogic.setSecret(word);
		
		assertEquals("_ " , hangmanlogic.getCurrentWordState() );

	}
	
	@Test
	void testSetSecretWithAfourLetterWord() {
		//given
		String word = "hola";
		hangmanlogic.setSecret(word);
		//when
		String currentWordState = hangmanlogic.getCurrentWordState();
		//then
		assertNotNull(currentWordState);
		assertEquals("_ _ _ _ " , currentWordState );

	}



	@Test
	void testGuessLetter() {
		//given
		String word = "hola";
		hangmanlogic.setSecret(word);
		//when 
		boolean guessLetter = hangmanlogic.guessLetter(word);
		//then
		assertTrue(guessLetter);

	}
	
	@Test
	void testGuessLetterfail() {
		//given
		hangmanlogic.setSecret("secret");
		//when 
		boolean guessLetter = hangmanlogic.guessLetter("fail");
		//then
		assertFalse(guessLetter);

	}
	
	@BeforeEach
	void setUp() throws Exception {
		hangmanlogic = new HangmanLogic();
	}



}
