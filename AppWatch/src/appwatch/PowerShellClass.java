package appwatch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
/**
 * Class used to interact with the Windows OS in order to retrieve the
 * list of installed applications, create/get/set the default application 
 * directory.
 * 
 * @author David Harrop
 */
public class PowerShellClass {
    ReportClass report;
    String currentUsersHomeDir;
    String appDir;
       
    public static void main(String[] args) {
        // TODO code application logic here
        
    }   
    /**
     * setter method for directory
     * @return the Application Directory
     */
    public final String setAppDir() {
        return this.currentUsersHomeDir + File.separator + "AppWatch"+ File.separator;
    }

    public PowerShellClass() {
        this.currentUsersHomeDir = homeDir();
        this.appDir = setAppDir();
    }

    
    /**
     * Identify the application directory
     * @return the full directory path
     */
    public String getAppDir() {
        setAppDir();
        return this.appDir;
    }
    
    /**
     * Credit: https://stackoverflow.com/questions/29545611/executing-powershell-commands-in-java-program
     * Compile the Windows Powershell command that retrieves the list of installed
     * applications and parses it to a local file.
     * The local file must be uniquely named, so get the UNID of the report
 from the ReportClass
 Execute the command and retrieve the results
     * @return Status message
     * @throws java.io.IOException
     *  Catches errors writing to report file
     * @throws org.xml.sax.SAXException
     *  Catches errors parsing XML file
     */
    public String appSearch() throws IOException, SAXException {
        
        //we are creating a new report, so we need a UNID for it
        report = new ReportClass();
        report.setReportID();
        String unid = report.getReportID();
        Integer appCount;
        String quotes = "'";
        //set the report file as target
        String target = getAppDir() + unid + ".xml";        
        String targetQ = quotes + target + quotes;
        String command = "powershell.exe  (Get-ItemProperty HKLM:\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName, DisplayVersion, Publisher | ConvertTo-XML –NoTypeInformation).Save("+ targetQ + ")";
        String line;
        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);

        // Getting the results
        powerShellProcess.getOutputStream().close();
        
        System.out.println("Standard Output:");
        try (BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()))) {
            while ((line = stdout.readLine()) != null) {
                System.out.println(line);
            }
        }
        System.out.println("Standard Error:");
        try (BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()))) {
            while ((line = stderr.readLine()) != null) {
                System.out.println(line);
                return "Error detected during scan";
            }
        }
        //update the new report with identifying data so we can re-use the report
        //later
        report.insertXML(unid, "report_ID", target);
        report.insertXML("Application Scan", "report_Stage", target);
        appCount = report.countApps(target);
        report.insertXML(appCount.toString(), "app_Count", target);
        return target;
    }
    
    /**
     * Credit: https://examples.javacodegeeks.com/core-java/io/file/check-if-directory-exists/
     * Confirm the existence of lack thereof of the application directory
     * @return true or false
     */
    public boolean dirSearch() {
        File dir = new File(this.appDir);
        boolean dirExists = dir.exists();
        return dirExists;
    }
    
    /**
     * Credit: https://examples.javacodegeeks.com/core-java/io/file/construct-a-file-path-in-java-example/
     * Create the application directory, as it does not exist.
     * @param homeDir the AppWatch directory
     * @return true or false, was the directory created successfully
     * @throws java.io.IOException
     *  Catches errors writing to the file system
     */
    public static boolean dirMake(String homeDir) throws IOException {
        boolean dirMade = false;
        try {
            File dir = new File(homeDir + File.separator + "AppWatch" + File.separator);
            dir.getParentFile().mkdirs();
            dirMade = dir.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(PowerShellClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dirMade;
    }

    /**
     * get the hoe directory associated with the users OS login
     * @return  the User Directory in the file system
     */
    public final String homeDir() {
        return System.getProperty("user.home");
    }
}
