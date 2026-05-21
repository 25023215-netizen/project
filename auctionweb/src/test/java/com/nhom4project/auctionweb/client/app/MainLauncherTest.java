package com.nhom4project.auctionweb.client.app;

import javafx.application.Application;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for MainLauncher using Equivalence Partitioning (EP) and Boundary Value Analysis (BVA).
 * Uses static mocking with Mockito to achieve full isolation from the native JavaFX toolkit runtime.
 */
public class MainLauncherTest {

    @Test
    public void testMainNominalArgs() {
        try (MockedStatic<Application> applicationMock = Mockito.mockStatic(Application.class)) {
            String[] args = {"arg1", "arg2"};
            
            // Expected: Method executes without exceptions
            assertDoesNotThrow(() -> MainLauncher.main(args));
            
            // Expected: Delegation called with exactly the expected parameters
            applicationMock.verify(() -> Application.launch(App.class, args));
        }
    }

    @Test
    public void testMainEmptyArgs() {
        try (MockedStatic<Application> applicationMock = Mockito.mockStatic(Application.class)) {
            String[] args = {};
            
            // Expected: Method executes without exceptions
            assertDoesNotThrow(() -> MainLauncher.main(args));
            
            // Expected: Delegation called with empty array
            applicationMock.verify(() -> Application.launch(App.class, args));
        }
    }

    @Test
    public void testMainNullArgs() {
        try (MockedStatic<Application> applicationMock = Mockito.mockStatic(Application.class)) {
            // Expected: Method executes without exceptions when passing null
            assertDoesNotThrow(() -> MainLauncher.main(null));
            
            // Expected: Delegation called with null args array
            applicationMock.verify(() -> Application.launch(App.class, (String[]) null));
        }
    }
}
