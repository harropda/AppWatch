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
 *
 * @author David Harrop
 */
public class powerShellClass {
    reportClass report;
    String currentUsersHomeDir;
    String appDir;
       
    public static void main(String[] args) {
        // TODO code application logic here
        
    }   
    
    public final String setAppDir() {
        return this.currentUsersHomeDir + File.separator + "AppWatch"+ File.separator;
    }

    public powerShellClass() {
        this.currentUsersHomeDir = homeDir();
        this.appDir = setAppDir();
    }

    
    /**
     *
     * @return
     */
    public String getAppDir() {
        setAppDir();
        return this.appDir;
    }
    
    /**
     * Compile the Windows Powershell command that retrieves the list of installed
     * applications and parses it to a local file.
     * The local file must be uniquely named, so get the UNID of the report
     * from the reportClass
     * Execute the command and retrieve the results
     * @return Status message
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     */
    public String appSearch() throws IOException, SAXException {
        
        report = new reportClass();
        report.setReportID();
        String unid = report.getReportID();
        Integer appCount;
        String quotes = "'";
        String target = getAppDir() + unid + ".xml";        
        String targetQ = quotes + target + quotes;
        String command = "powershell.exe  (Get-ItemProperty HKLM:\\Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName, DisplayVersion, Publisher | ConvertTo-XML –NoTypeInformation).Save("+ targetQ + ")";
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
        report.insertXML(unid, "report_ID", target);
        report.insertXML("Application Scan", "report_Stage", target);
        appCount = report.countApps(target);
        report.insertXML(appCount.toString(), "app_Count", target);
        return target;
    }
    
    /**
     * Source: https://examples.javacodegeeks.com/core-java/io/file/check-if-directory-exists/
     * @return 
     */
    public boolean dirSearch() {
        File dir = new File(this.appDir);
        boolean dirExists = dir.exists();
        return dirExists;
    }
    
    /**
     * Code sourced from https://examples.javacodegeeks.com/core-java/io/file/construct-a-file-path-in-java-example/
     * @param homeDir the AppWatch directory
     * @return 
     * @throws java.io.IOException
     */
    public static boolean dirMake(String homeDir) throws IOException {
        boolean dirMade = false;
        try {
            File dir = new File(homeDir + File.separator + "AppWatch" + File.separator);
            dir.getParentFile().mkdirs();
            dirMade = dir.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(powerShellClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dirMade;
    }

    public final String homeDir() {
        return System.getProperty("user.home");
    }
}
