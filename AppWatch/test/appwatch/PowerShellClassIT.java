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
public class PowerShellClassIT {
    
    public PowerShellClassIT() {
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
     * Test of main method, of class PowerShellClass.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        PowerShellClass.main(args);
    }

    /**
     * Test of setAppDir method, of class PowerShellClass.
     */
    @Test
    public void testSetAppDir() {
        System.out.println("setAppDir");
        PowerShellClass instance = new PowerShellClass();
        String expResult = "C:\\Users\\IBM_ADMIN\\AppWatch";
        String result = instance.setAppDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAppDir method, of class PowerShellClass.
     */
    @Test
    public void testGetAppDir() {
        System.out.println("getAppDir");
        PowerShellClass instance = new PowerShellClass();
        String expResult = "C:\\Users\\IBM_ADMIN\\AppWatch";
        String result = instance.getAppDir();
        assertEquals(expResult, result);
    }

    /**
     * Test of appSearch method, of class PowerShellClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testAppSearch() throws Exception {
        System.out.println("appSearch");
        PowerShellClass instance = new PowerShellClass();
        String expResult = "<file name created @ runtime>";
        String result = instance.appSearch();
        assertEquals(expResult, result);
    }

    /**
     * Test of dirSearch method, of class PowerShellClass.
     */
    @Test
    public void testDirSearch() {
        System.out.println("dirSearch");
        PowerShellClass instance = new PowerShellClass();
        boolean expResult = true;
        boolean result = instance.dirSearch();
        assertEquals(expResult, result);
    }

    /**
     * Test of dirMake method, of class PowerShellClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testDirMake() throws Exception {
        System.out.println("dirMake");
        String homeDir = "C:\\Users\\IBM_ADMIN\\AppWatch";
        boolean expResult = true;
        boolean result = PowerShellClass.dirMake(homeDir);
        assertEquals(expResult, result);
    }

    /**
     * Test of homeDir method, of class PowerShellClass.
     */
    @Test
    public void testHomeDir() {
        System.out.println("homeDir");
        PowerShellClass instance = new PowerShellClass();
        String expResult = "C:\\Users\\IBM_ADMIN\\";
        String result = instance.homeDir();
        assertEquals(expResult, result);
    }    
}
