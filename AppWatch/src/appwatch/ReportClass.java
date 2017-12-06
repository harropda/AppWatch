package appwatch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Class used to interact with a report file in the back-end and not in the UI
 * @author David Harrop
 */
public class ReportClass {
    HashListClass hash;
    String rID; 
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    /**
     * Using a set of variables (User Name and current Date/Time, combine them 
     * to form a unique identifier for the report file (reportID). 
     */
    public void setReportID() {
        String user = System.getProperty("user.name");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        long time = cal.getTimeInMillis();
        Long stamp = System.currentTimeMillis();
           this.rID = user.substring(0, 2) + String.valueOf(time);
    }
    
    /**
     * return the report UNID to the caller
     * @return report UNID
     */
    public String getReportID() {
        return this.rID;
    }
    
    /**
     * Given a report file, extract its UNID from it
     * @param filename the file from which to extract the UNID
     * @return the UNID of the report file
     */
    public String getUNIDFromFile(String filename) {
        char c;
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < filename.length() ; i++) {
           c = filename.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            } 
        }            
        return sb.toString();
    }
    
    /**
     * Given a report file and tag name, search for the tag and return the 
     * associated value.
     * @param xml the report file from which we wish to read something
     * @param tag the XML tag value we wish to read from the report
     * @return tagVal the value from the specified tag in the report file
     * @throws org.xml.sax.SAXException
     *  Catches parsing errors on XML file
     * @throws java.io.IOException
     *  Catches any errors during file system interaction
     */
    public String readReport(File xml, String tag) throws SAXException, IOException {
        String tagVal = "";
        Element e;
        NodeList nl;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            Document doc;
            doc = db.parse(xml);
            nl = doc.getElementsByTagName(tag);
            if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
                tagVal = nl.item(0).getFirstChild().getNodeValue();
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ReportClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tagVal;
    }  
    
    /**
     * Credit: https://stackoverflow.com/questions/7373567/how-to-read-and-write-xml-files
     * Given a report file, a tag name and a value, search the file for the tag.
     * if found, update the value, otherwise create a new tag and add the value 
     * to it.
     * @param val the value of the XML tag to be added to the report
     * @param tag the XML Tag to be updated with val
     * @param xml the report file
     * @throws java.io.IOException
     *  Catches any errors interacting with the file system
     * @throws org.xml.sax.SAXException
     *  Catches any parsing errors with the XML file
     * 
     */
    public void insertXML(String val, String tag, String xml) throws IOException, SAXException {
        Element e;
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            Document doc;
            doc = db.parse(xml);

            // create the root element
            Element rootEle = doc.getDocumentElement();

            /* If the node already exists, update it, otherwise
            *  create new node and place them under root
            */            
            NodeList nodeList = doc.getElementsByTagName(tag);
            System.out.println("tag = " + tag);
            System.out.println("nodeList.getLength() = " + nodeList.getLength());
            if (nodeList.getLength() > 0) {
                System.out.println("looking for tag");
                for (int i = 0 ; i < nodeList.getLength() ; i++) {
                    //should only be one, but loop just in case
                    Node node = nodeList.item(i);
                    node.setTextContent(val);
                }
            } else {
                System.out.println("adding tag");
                e = doc.createElement(tag);
                e.appendChild(doc.createTextNode(val));
                rootEle.appendChild(e);
            } 
            
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // save the updates to the file
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(xml)));
            
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(ReportClass.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }  
    
    /**
     * Given a report UNID and the current md5hash value of the report, locate
     * the stored hash value for the report in the Hash File and compare stored
     * vs current.
     * @param rID the UNID of the report
     * @param hashVal The expected md5 hash value of the report
     * @return true or false, do the stored and current hash values match
     * @throws java.io.IOException 
     *  Catches any errors interacting with the file system
     */
    public boolean validateHash(String rID, String hashVal) throws IOException {
        boolean returnCode;
        hash = new HashListClass();
        String storedHash = hash.retrieveHash(rID);
        System.out.println("storedhash = " + storedHash);
        System.out.println("hashVal = " + hashVal);
        returnCode = storedHash.equals(hashVal);
        return returnCode;
    }    
    
    /**
     * Credit: https://howtodoinjava.com/core-java/io/how-to-generate-sha-or-md5-file-checksum-hash-in-java/
     * For a given file, retrieve and return the MD5 Hash value (MD5 method 
     * specified by the MessageDigest parameter)
     * @param file the file we wish to derive the current md5 hash value for.
     * @return current md5 hash value of file
     * @throws java.io.IOException
     *  Catches any errors interacting with the file system
     * @throws java.security.NoSuchAlgorithmException
     *  Catches an error caused by reading the digest
     */
    public String getHash (File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5"); 
        //Create byte array to read data in chunks
        try ( //Get file input stream for reading the file content
                FileInputStream fis = new FileInputStream(file)) {
            //Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;
            //Read file data and update in message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            //close the stream; We don't need it now.
        }

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
       return sb.toString();
    }  
    
    /**
     * Once the user has selected a report from the presented list of reports on 
     * the home page, we must then open that report.  First, we validate the reports
     * current md5 hash value against the stored hash value.  
     * @param rID the report UNID
     * @throws ParserConfigurationException 
     *  Catches errors caused during report read
     */
    
    @SuppressWarnings("null")
    public void openReport (String rID) throws ParserConfigurationException{
       ReportUI repUI = null;
        try {
            repUI = new ReportUI(new javax.swing.JFrame(), true, rID);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ReportClass.class.getName()).log(Level.SEVERE, null, ex);
        }
       repUI.setVisible(true);
    }
    
    /**
     * Count how many applications were identified in the report - this also counts
     * blank values
     * @param xml the report file
     * @return the number of applications in the report file
     */
    public Integer countApps(String xml) {
        Integer count = 0;
        
        try {
		DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
		DocumentBuilder dB = dBF.newDocumentBuilder();
		Document doc = dB.parse(xml);
		NodeList list = doc.getElementsByTagName("Object");
		count = list.getLength();
	} catch (ParserConfigurationException | IOException | SAXException ex) {
		Logger.getLogger(ReportClass.class.getName()).log(Level.SEVERE, null, ex);
	}        
        return count;
    } 
    
    /**
     * In order to make future read/edit of the report simpler we add a UNID to each
     * application in the report.  The UNID is a simple index as it is only used
     * within the context of the report and not beyond.
     * @param xml the report file to be updated
     */
    public void addAppUNIDS(String xml) {
        
        try {
            //open the XML file for edit
            DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder dB = dBF.newDocumentBuilder();
            Document doc = dB.parse(new File(xml));
            
            NodeList nodeList = doc.getElementsByTagName("Object");
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Integer newIDint = i + 1;
                String newID = newIDint.toString();
                if (node.getNodeType() == Node.ELEMENT_NODE) {  //only update non-white space nodes
                    Element e = (Element)node;
                    NodeList childNodeList = e.getChildNodes();
                    if (childNodeList.getLength() > 0) {
                        Node childNode = childNodeList.item(0);
                        Element newElement = doc.createElement("Property");
                            newElement.setAttribute("Name", "AppID");
                            newElement.setTextContent(newID);
                            childNode.getParentNode().insertBefore(newElement, childNode.getNextSibling());
                    }
                }                
            }
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // save changes to the file
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(xml)));
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(ReportClass.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * When performing a Vulnerability scan we must update the report with the results.
     * A single application can have many vulnerabilities, so we need track the
     * vulnerability index number per application as well as the application
     * index number.
     * @param filename the report file and path
     * @param sequence the vulnerability index number
     * @param appID the application index number
     * @param cve the CVE ID of the vulnerability
     * @param description the full text description of the vulnerability
     * @param source the source of the vulnerability information
     * @param appVulCount the total number of vulnerabilities for the application
     */
    public void addVResult(String filename, Integer sequence, String appID, String cve, String description, String source, Integer appVulCount) {
        String cveURL = generateCVEURL(cve);
        try {
            //open the XML file for edit
            DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder dB = dBF.newDocumentBuilder();
            Document doc = dB.parse(new File(filename));
            
            NodeList nodeList = doc.getElementsByTagName("Object");
            
            for (int i = 0; i < nodeList.getLength(); i++) { //top level
               Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) { //ignore white space
                    Element e = (Element)node;
                    NodeList childNodeList = e.getChildNodes();
                    if (childNodeList.getLength() > 0) { //second level
                        Node childNode = childNodeList.item(1);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) { //ignore white space
                            Element eApp = (Element)childNode;
                            Attr eAttr = eApp.getAttributeNode("Name");                                
                            String attrLabel = eAttr.getTextContent();
                            if (attrLabel.equals("AppID")) { //match on the application index number
                                String xmlAppID = eApp.getTextContent();
                                if (xmlAppID.equals(appID)) {
                                    // if this is the first exploit for this application, insert a 
                                    // new node showing the total vulnerability count before any exploits
                                    if (Objects.equals(sequence, 0)) {
                                        Element vulCount = doc.createElement("Property"); 
                                        vulCount.setAttribute("Name", "AppVulCount");
                                        vulCount.setTextContent(appVulCount.toString());
                                        node.appendChild(vulCount); 
                                    }
                                    //add the vulnerability information to the new node
                                    Element newExploit = doc.createElement("Exploit");
                                    newExploit.setAttribute("ID", appID + "." + sequence.toString());
                                    node.appendChild(newExploit);
                                    Element newCVEID = doc.createElement("CVE_ID");
                                    newCVEID.setTextContent(cve);
                                    newExploit.appendChild(newCVEID);
                                    Element newDescription = doc.createElement("Description");
                                    newDescription.setTextContent(description);
                                    newExploit.appendChild(newDescription);
                                    Element newSource = doc.createElement("Source");
                                    newSource.setTextContent(source);
                                    newExploit.appendChild(newSource);
                                    Element newCVEURL = doc.createElement("CVE_URL");
                                    newCVEURL.setTextContent(cveURL);
                                    newExploit.appendChild(newCVEURL);
                                }
                            }
                        }
                    }
                } 
            }
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, cat + ".dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // save the changes to the file
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(filename)));
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(ReportClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        
    /**
     * Using the CVE ID, create a string representing the http URL for the 
     * vulnerability on CVE
     * @param cve the CVE ID
     * @return the URL for the CVE site
     */
    public String generateCVEURL(String cve) {
        //remove any unwanted characters
        cve = cve.replace("[", "");
        cve = cve.replace("]", "");
        cve = cve.replace("\"", "");
        cve = cve.replace("CVE-", "");
        CharSequence div = ",";
        if (cve.contains(div)) {
            //there is more than one CVE ID - we will just use the first one
            Integer split = cve.indexOf(",", 0);
            System.out.println(split);
            cve = cve.substring(0, split); 
        }
        System.out.println(cve);              
        return "https://www.cvedetails.com/cve/CVE-" + cve + "/";
    }
}
