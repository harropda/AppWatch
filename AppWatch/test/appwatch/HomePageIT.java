/*
 * The MIT License
 *
 * Copyright 2017 David Harrop.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
public class HomePageIT {
    
    public HomePageIT() {
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
     * Test of onOpenDo method, of class HomePage.
     */
    @Test
    public void testOnOpenDo() {
        System.out.println("onOpenDo");
        HomePage instance = new HomePage(new javax.swing.JFrame(), true);
        instance.onOpenDo();
    }

    /**
     * Test of existingHashFile method, of class HomePage.
     */
    @Test
    public void testExistingHashFile() {
        System.out.println("existingHashFile");
        String file = "C:\\Users\\IBM_ADMIN\\AppWatch\\hashlist.xml.enc";
        HomePage instance = new HomePage(new javax.swing.JFrame(), true);
        Integer expResult = 2;
        Integer result = instance.existingHashFile(file);
        assertEquals(expResult, result);
    }

    /**
     * Test of deleteAllFiles method, of class HomePage.
     */
    @Test
    public void testDeleteAllFiles() {
        System.out.println("deleteAllFiles");
        HomePage instance = new HomePage(new javax.swing.JFrame(), true);
        instance.deleteAllFiles();
    }

    /**
     * Test of main method, of class HomePage.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        HomePage.main(args);
    }
    
}
