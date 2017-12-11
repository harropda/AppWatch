/*
 * Copyright 2017 David Harrop.
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
public class HashListClassIT {
    
    public HashListClassIT() {
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
     * Test of main method, of class HashListClass.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        HashListClass.main(args);
    }

	/**
     * Test of checkHashFileExists method, of class HashListClass.
     */
    @Test
    public void testCheckHashFileExists() {
        System.out.println("checkHashFileExists");
        String dir = "C:\\Users\\IBM_ADMIN\\AppWatch";
        String filename = "hashlist.xml.enc";
        HashListClass instance = new HashListClass();
        Boolean expResult = true;
        Boolean result = instance.checkHashFileExists(dir, filename);
        assertEquals(expResult, result);
    }

    /**
     * Test of createHashFile method, of class HashListClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateHashFile() throws Exception {
        System.out.println("createHashFile");
        HashListClass instance = new HashListClass();
        instance.createHashFile();
    }

    /**
     * Test of encryptHashFile method, of class HashListClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testEncryptHashFile() throws Exception {
        System.out.println("encryptHashFile");
        String filename = "C:\\Users\\IBM_ADMIN\\AppWatch\\hashlist.xml";
        String key = "P@ssw0rd";
        HashListClass instance = new HashListClass();
        instance.encryptHashFile(filename, key);
    }

    /**
     * Test of decryptHashFile method, of class HashListClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testDecryptHashFile() throws Exception {
        System.out.println("decryptHashFile");
        String filename = "C:\\Users\\IBM_ADMIN\\AppWatch\\hashlist.xml.enc";
        String key = "P@ssw0rd";
        HashListClass instance = new HashListClass();
        instance.decryptHashFile(filename, key);
    }

    /**
     * Test of retrieveHash method, of class HashListClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testRetrieveHash() throws Exception {
        System.out.println("retrieveHash");
        String rID = "";
        HashListClass instance = new HashListClass();
        String expResult = "";
        String result = instance.retrieveHash(rID);
        assertEquals(expResult, result);
    }

    /**
     * Test of setHash method, of class HashListClass.
     * @throws java.lang.Exception
     */
    @Test
    public void testSetHash() throws Exception {
        System.out.println("setHash");
        String rID = "";
        String md5 = "";
        HashListClass instance = new HashListClass();
        instance.setHash(rID, md5);
    }
    
}