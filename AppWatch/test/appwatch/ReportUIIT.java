/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appwatch;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author David Harrop
 */
public class ReportUIIT {
    
    public ReportUIIT() {
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
     * Test of main method, of class ReportUI.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        ReportUI.main(args);
    }

    /**
     * Test of populateTable method, of class ReportUI.
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    @Test
    public void testPopulateTable() throws SAXException, IOException, ParserConfigurationException {
        System.out.println("populateTable");
        ReportUI instance = new ReportUI(new java.awt.Frame(), true, "Da1512994371549");
        instance.populateTable();
    }

    /**
     * Test of onDoubleClick method, of class ReportUI.
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    @Test
    public void testOnDoubleClick() throws SAXException, IOException, ParserConfigurationException {
        System.out.println("onDoubleClick");
        ReportUI instance = new ReportUI(new java.awt.Frame(), true, "Da1512994371549");
        instance.onDoubleClick();
    }

    /**
     * Test of performScanForApp method, of class ReportUI.
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    @Test
    public void testPerformScanForApp() throws SAXException, IOException, ParserConfigurationException {
        System.out.println("performScanForApp");
        Integer r = 0;
        ReportUI instance = new ReportUI(new java.awt.Frame(), true, "Da1512994371549");
        boolean expResult = true;
        boolean result = instance.performScanForApp(r);
        assertEquals(expResult, result);
    }
    
}
