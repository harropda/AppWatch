/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appwatch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
public class EncryptionManagerIT {
    
    public EncryptionManagerIT() {
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
     * Test of encrypt method, of class EncryptionManager.
     * @throws java.lang.Exception
     */
    @Test
    public void testEncrypt() throws Exception {
        System.out.println("encrypt");
        int keyLength = 128;
        String pw = "P@ssw0rd";
        char[] password = pw.toCharArray();
        InputStream input = new FileInputStream("C:\\Users\\IBM_ADMIN\\AppWatch\\hashlist.xml");
        OutputStream output = new FileOutputStream("C:\\Users\\IBM_ADMIN\\AppWatch\\hashlist.xml.enc");
        EncryptionManager.encrypt(keyLength, password, input, output);
    }

    /**
     * Test of decrypt method, of class EncryptionManager.
     * @throws java.lang.Exception
     */
    @Test
    public void testDecrypt() throws Exception {
        System.out.println("decrypt");
        String pw = "P@ssw0rd";
        char[] password = pw.toCharArray();
        InputStream input = new FileInputStream("C:\\Users\\IBM_ADMIN\\AppWatch\\hashlist.xml.enc");
        OutputStream output = new FileOutputStream("C:\\Users\\IBM_ADMIN\\AppWatch\\hashlist.xml");
        int expResult = 128;
        int result = EncryptionManager.decrypt(password, input, output);
        assertEquals(expResult, result);
    }
    
}
