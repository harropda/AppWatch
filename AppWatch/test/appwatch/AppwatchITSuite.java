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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author David Harrop
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({HashListClassIT.class, EncryptionManagerIT.class, ReportClassIT.class, HomePageIT.class, ApplicationUIIT.class, VulnerabilityUIIT.class, PowerShellClassIT.class, ReportUIIT.class, PasswordUIIT.class})
public class AppwatchITSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
