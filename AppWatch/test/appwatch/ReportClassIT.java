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
public class ReportClassIT {
    
    public ReportClassIT() {
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
     * Test of main method, of class ReportClass.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        ReportClass.main(args);
    }

    /**
     * Test of setReportID method, of class ReportClass.
     */
    @Test
    public void testSetReportID() {
        System.out.println("setReportID");
        ReportClass instance = new ReportClass();
        instance.setReportID();
    }

    /**
     * Test of getReportID method, of class ReportClass.
     */
    @Test
    public void testGetReportID() {
        System.out.println("getReportID");
        ReportClass instance = new ReportClass();
        String expResult = null;
        testSetReportID();
        String result = instance.getReportID();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUNIDFromFile method, of class ReportClass.
     */
    @Test
    public void testGetUNIDFromFile() {
        System.out.println("getUNIDFromFile");
        String filename = "C:\\Users\\IBM_ADMIN\\AppWatch\\Da1513009865691.xml";
        ReportClass instance = new ReportClass();
        String expResult = "1513009865691";
        String result = instance.getUNIDFromFile(filename);
        assertEquals(expResult, result);
    }

    /**
     * Test of readReport method, of class ReportClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadReport() throws Exception {
        System.out.println("readReport");
        File xml = new File("C:\\Users\\IBM_ADMIN\\AppWatch\\Da1513009865691.xml");
        String tag = "report_ID";
        ReportClass instance = new ReportClass();
        String expResult = "Da1513009865691";
        String result = instance.readReport(xml, tag);
        assertEquals(expResult, result);
    }

    /**
     * Test of validateHash method, of class ReportClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testValidateHash() throws Exception {
        System.out.println("validateHash");
        String rID = "Da1513009865691";
        String hashVal = "9d590e5a9a63a78e73179766c7afbd71";
        ReportClass instance = new ReportClass();
        boolean expResult = false;
        boolean result = instance.validateHash(rID, hashVal);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getHash method, of class ReportClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetHash() throws Exception {
        System.out.println("getHash");
        File file = new File("C:\\Users\\IBM_ADMIN\\AppWatch\\Da1513009865691.xml");
        ReportClass instance = new ReportClass();
        String expResult = "9d590e5a9a63a78e73179766c7afbd71";
        String result = instance.getHash(file);
        assertEquals(expResult, result);
    }

    /**
     * Test of openReport method, of class ReportClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testOpenReport() throws Exception {
        System.out.println("openReport");
        String rID = "Da1513009865691";
        ReportClass instance = new ReportClass();
        instance.openReport(rID);
    }

    /**
     * Test of countApps method, of class ReportClass.
     */
    @Test
    public void testCountApps() {
        System.out.println("countApps");
        String xml = "C:\\Users\\IBM_ADMIN\\AppWatch\\Da1513009865691.xml";
        ReportClass instance = new ReportClass();
        Integer expResult = 82;
        Integer result = instance.countApps(xml);
        assertEquals(expResult, result);
    }

    /**
     * Test of insertXML method, of class ReportClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testInsertXML() throws Exception {
        System.out.println("insertXML");
        String val = "success";
        String tag = "test";
        String xml = "C:\\Users\\IBM_ADMIN\\AppWatch\\Da1513009865691.xml";
        ReportClass instance = new ReportClass();
        instance.insertXML(val, tag, xml);
    }
    
    /**
     * Test of addAppUNIDS method, of class ReportClass.
     */
    @Test
    public void testAddAppUNIDS() {
        System.out.println("addAppUNIDS");
        String xml = "C:\\Users\\IBM_ADMIN\\AppWatch\\Da1513009865691.xml";
        ReportClass instance = new ReportClass();
        instance.addAppUNIDS(xml);
    }

    /**
     * Test of addVResult method, of class ReportClass.
     */
    @Test
    public void testAddVResult() {
        System.out.println("addVResult");
        String filename = "C:\\Users\\IBM_ADMIN\\AppWatch\\Da1513009865691.xml";
        Integer sequence = 1;
        String appID = "1";
        String cve = "100";
        String description = "test success";
        String source = "SUCCESS";
        Integer appVulCount = 1;
        ReportClass instance = new ReportClass();
        instance.addVResult(filename, sequence, appID, cve, description, source, appVulCount);
    }

    /**
     * Test of deleteBlankNodes method, of class ReportClass.
     */
    @Test
    public void testDeleteBlankNodes() {
        System.out.println("deleteBlankNodes");
        String xml = "C:\\Users\\IBM_ADMIN\\AppWatch\\Da1513009865691.xml";
        ReportClass instance = new ReportClass();
        instance.deleteBlankNodes(xml);
    }

    /**
     * Test of generateCVEURL method, of class ReportClass.
     */
    @Test
    public void testGenerateCVEURL() {
        System.out.println("generateCVEURL");
        String cve = "[CVE-2001-0001],[2001-0002]";
        ReportClass instance = new ReportClass();
        String expResult = "https://www.cvedetails.com/cve/CVE-2001-0001/";
        String result = instance.generateCVEURL(cve);
        assertEquals(expResult, result);
    }
    
}