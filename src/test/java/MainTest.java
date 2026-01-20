import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;

import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import javax.swing.*;
import static org.mockito.Mockito.*;

class MainTest {
	void testmain_runsUIWithoutExceptions() {
	    try (MockedStatic<SwingUtilities> swingMock = mockStatic(SwingUtilities.class)) {
	        swingMock.when(() -> SwingUtilities.invokeLater(any(Runnable.class)))
	                 .thenAnswer(invocation -> {
	                     Runnable runnable = invocation.getArgument(0);
	                     
	                     try (MockedStatic<JOptionPane> paneMock = mockStatic(JOptionPane.class)) {
	                         paneMock.when(() -> JOptionPane.showInputDialog(any(), anyString()))
	                                 .thenReturn("testword");
	                         paneMock.when(() -> JOptionPane.showMessageDialog(any(), anyString()))
	                                 .thenAnswer(i -> null);
	                         
	                         runnable.run();
	                     }
	                     return null;
	                 });

	        Main.main(new String[]{});

	        swingMock.verify(() -> SwingUtilities.invokeLater(any(Runnable.class)));
	    }
	}
	
	   @BeforeAll
	    static void setUpHeadless() {
	        System.setProperty("java.awt.headless", "true");
	    }
}