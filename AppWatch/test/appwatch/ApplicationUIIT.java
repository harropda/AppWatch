/*
 * Copyright 2017 David Harrop.
 */
package appwatch;

import java.io.File;
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
public class ApplicationUIIT {
    
    public ApplicationUIIT() {
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
     * Test of main method, of class ApplicationUI.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        ApplicationUI.main(args);
    }

    /**
     * Test of populateTable method, of class ApplicationUI.
     */
    @Test
    public void testPopulateTable() {
        System.out.println("populateTable");
        ApplicationUI instance = new ApplicationUI(new java.awt.Frame(), true, new File("C:\\Users\\IBM_ADMIN\\AppWatch\\Da1512994371549.xml"), "1", "7-Zip 16.04 (x64)","16.04","11-12-2017");
        
        instance.populateTable();
    }

    /**
     * Test of onSingleClick method, of class ApplicationUI.
     */
    @Test
    public void testOnSingleClick() {
        System.out.println("onSingleClick");
        ApplicationUI instance = new ApplicationUI(new java.awt.Frame(), true, new File("C:\\Users\\IBM_ADMIN\\AppWatch\\Da1512994371549.xml"), "1", "7-Zip 16.04 (x64)","16.04","11-12-2017");
        instance.onSingleClick();
    }

    /**
     * Test of onDoubleClick method, of class ApplicationUI.
     */
    @Test
    public void testOnDoubleClick() {
        System.out.println("onDoubleClick");
        ApplicationUI instance = new ApplicationUI(new java.awt.Frame(), true, new File("C:\\Users\\IBM_ADMIN\\AppWatch\\Da1512994371549.xml"), "1", "7-Zip 16.04 (x64)","16.04","11-12-2017");
        instance.onDoubleClick();
    }
    
}
