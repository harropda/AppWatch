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
 *
 * @author David Harrop
 */
public class reportClass {
    hashListClass hash;
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
    
    public String getReportID() {
        return this.rID;
    }
    
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
     *
     * @param xml the report file from which we wish to read something
     * @param tag the XML tag value we wish to read from the report
     * @return tagVal the report id value from the report file
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
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
            //e = doc.getDocumentElement();
            nl = doc.getElementsByTagName(tag);
            if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
                tagVal = nl.item(0).getFirstChild().getNodeValue();
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(reportClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tagVal;
    }  
    
    /**
     * After a new XML report of installed apps is created, we wish to add the 
     * report UNID to the file.
     * @param val the value of the XML tag to be added to the report
     * @param tag the XML Tag to be updated with val
     * @param xml the report file
     * source: https://stackoverflow.com/questions/7373567/how-to-read-and-write-xml-files
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
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
                //rootEle.appendChild(e);
                rootEle.appendChild(e);
            } 
            
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, cat + ".dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // send DOM to file
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(xml)));

            
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(reportClass.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }  
    
    /**
     *
     * @param rID
     * @param hashVal
     * @return 
     * @throws java.io.IOException 
     */
    public boolean validateHash(String rID, String hashVal) throws IOException {
        boolean returnCode;
        hash = new hashListClass();
        String storedHash = hash.retrieveHash(rID);
        System.out.println("storedhash = " + storedHash);
        System.out.println("hashVal = " + hashVal);
        returnCode = storedHash.equals(hashVal);
        return returnCode;
    }    
    
    /**
     * For a given file, retrieve and return the MD5 Hash value (MD5 method 
     * specified by the MessageDigest parameter)
     *
     * Credit: https://howtodoinjava.com/core-java/io/how-to-generate-sha-or-md5-file-checksum-hash-in-java/
     * @param file
     * @return 
     * @throws java.io.IOException
     * @throws java.security.NoSuchAlgorithmException
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
    
    /*
    * Once the user has selected a report from the presented list of reports on the home page
    * we must then open that report.  This requires:
    * The report UNID
    * Validating the report file Hash value against the Hash List
    */
    @SuppressWarnings("null")
    public void openReport (String rID) throws ParserConfigurationException{
       reportUI repUI = null;
        try {
            repUI = new reportUI(new javax.swing.JFrame(), true, rID);
        } catch (SAXException | IOException ex) {
            Logger.getLogger(reportClass.class.getName()).log(Level.SEVERE, null, ex);
        }
       repUI.setVisible(true);
    }
    
    public Integer countApps(String xml) {
        Integer count = 0;
        
        try {
		DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
		DocumentBuilder dB = dBF.newDocumentBuilder();
		Document doc = dB.parse(xml);

		NodeList list = doc.getElementsByTagName("Object");

		count = list.getLength();

	} catch (ParserConfigurationException | IOException | SAXException ex) {
		Logger.getLogger(reportClass.class.getName()).log(Level.SEVERE, null, ex);
	}
        
        return count;
    } 
    
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
                if (node.getNodeType() == Node.ELEMENT_NODE) {
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
            //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, cat + ".dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // send DOM to file
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(xml)));
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(reportClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void addVResult(String filename, Integer sequence, String appID, String cve, String description, String source, Integer appVulCount) {
        String cveURL = generateCVEURL(cve);
        try {
            //open the XML file for edit
            DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder dB = dBF.newDocumentBuilder();
            Document doc = dB.parse(new File(filename));
            
            NodeList nodeList = doc.getElementsByTagName("Object");
            
            for (int i = 0; i < nodeList.getLength(); i++) {
               Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element)node;
                    NodeList childNodeList = e.getChildNodes();
                    if (childNodeList.getLength() > 0) {
                        Node childNode = childNodeList.item(1);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eApp = (Element)childNode;
                            Attr eAttr = eApp.getAttributeNode("Name");                                
                            String attrLabel = eAttr.getTextContent();
                            if (attrLabel.equals("AppID")) {
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

            // send DOM to file
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(filename)));
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(reportClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        
    private String generateCVEURL(String cve) {
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
        return "http://www.cvedetails.com/cve/CVE-" + cve + "/";
    }
}
