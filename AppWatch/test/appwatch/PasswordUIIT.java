/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appwatch;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David Harrop
 */
public class PasswordUIIT {
    
    public PasswordUIIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class PasswordUI.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        PasswordUI.main(args);
    }

    /**
     * Test of verifyPasswords method, of class PasswordUI.
     */
    @Test
    public void testVerifyPasswords() {
        System.out.println("verifyPasswords");
        PasswordUI instance = new PasswordUI(new java.awt.Frame(), true, "new");
        boolean expResult = false;
        boolean result = instance.verifyPasswords();
        assertEquals(expResult, result);
    }
    
}
