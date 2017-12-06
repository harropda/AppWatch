package appwatch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is used to manage the Hash List, which is a Control File that
 * contains the md5 hash values of all of our reports.  We encrypt this file
 * when AppWatch is closed, only decrypting it when AppWatch is opened.
 * This class is also used to check for the existence of the Hash File and
 * create a new Hash File if needed.
 * @author David Harrop
 */
public class HashListClass {
    PowerShellClass ps;
    EncryptionManager enc;
    private final String dir;
    private final String hashList; 
    public static int blockSize = 16;
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    public HashListClass() {
        ps = new PowerShellClass();
        enc = new EncryptionManager();
        this.dir = ps.getAppDir();
        this.hashList = dir + "hashlist.xml";
    }
    
     /**
     * Test the existence of the Hash File
     * @param dir AppWatch home directory
     * @param filename Hash File
     * @return exists Boolean indicating the existence of the Hash List
     */
    public Boolean checkHashFileExists(String dir, String filename) {
        Boolean exists = false;
        File folder;
        folder = new File(dir);
        File[] listOfFiles = folder.listFiles();        
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().equalsIgnoreCase(filename)) {
                exists = true;
                System.out.println("Found Hash File: " + filename);
            }
        }        
        return exists;
    }
        
     /**
     * Credit: https://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
     * Create a blank Hash File containing one set of dummy values
     */
    public void createHashFile() {
        String filename = dir + File.separator + "hashlist.xml";
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("hashes");
            doc.appendChild(rootElement);
            Element rep = doc.createElement("file");
            rootElement.appendChild(rep);

            // set attribute to file element
            Attr attr = doc.createAttribute("id");
            attr.setValue("null");
            rep.setAttributeNode(attr);

            // Hash value element
            Element hashval = doc.createElement("hashvalue");
            hashval.appendChild(doc.createTextNode("blank"));
            rep.appendChild(hashval);
                
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filename));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
            
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(HashListClass.class.getName()).log(Level.SEVERE, null, ex); 
        }
    }
    
    /**
     * Encrypt the Hash File using AES 128-bit cipher and a user-supplied password
     * @param filename Hash File
     * @param key password
     * @throws java.lang.Exception
     *  catch any exception generated by method
     */    
    public void encryptHashFile(String filename, String key) throws Exception {
        FileInputStream fis;
        FileOutputStream fos;  
        char[] password = key.toCharArray();
        Integer keyLength = 128;
        fis = new FileInputStream(filename);
        fos = new FileOutputStream(filename + ".enc");
        EncryptionManager.encrypt(keyLength, password, fis, fos);
        fos.close();
        fis.close(); 
    }
    
    /**
     * Decrypt the Hash File using AES 128-bit cipher and a user-supplied password
     * @param filename Hash File to be decrypted
     * @param key Password used to decrypt Hash File
     * @throws appwatch.EncryptionManager.InvalidPasswordException
     *  catches incorrect password supplied by the user
     * @throws java.io.FileNotFoundException
     *  catch if input or output file don't exists in file system
     */
    public void decryptHashFile(String filename, String key) throws EncryptionManager.InvalidPasswordException, FileNotFoundException, IOException {
        FileInputStream fis;
        FileOutputStream fos; 
        String outFile = filename.replace(".enc", "");
        char[] password = key.toCharArray();
        fis = new FileInputStream(filename);
        fos = new FileOutputStream(outFile);
        try {
            EncryptionManager.decrypt(password, fis, fos);            
        } catch (EncryptionManager.StrongEncryptionNotAvailableException | EncryptionManager.InvalidAESStreamException ex) {
            Logger.getLogger(HashListClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EncryptionManager.InvalidPasswordException ex) {
            throw new EncryptionManager.InvalidPasswordException();
        }
        fos.close();
        fis.close(); 
    }
    
    /**
     * For a given report ID number, retrieve the stored md5 hash value, so that
     * it can be compared to the reports actual current md5 hash value.
     * @param rID the reports unique ID
     * @return the stored hash value
     * @throws java.io.IOException 
     *  Catch any issues parsing the Hash List
     */
    public String retrieveHash(String rID) throws IOException {
        String storedHash = "";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
        
            docBuilder = docFactory.newDocumentBuilder();
            
            // open the Hash File for read
            Document doc = docBuilder.parse(hashList);
            
            //traverse each element in the Hash File and, where we find a match
            //with the reports UNID, return the associated stored hash value.
            NodeList nodeList = doc.getElementsByTagName("file");
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) { //ignore white space nodes                        
                        Element e = (Element)node;
                        System.out.println("rID = " + rID);
                        System.out.println(e.getAttribute("id"));
                        if (e.getAttribute("id").equals(rID)) {
                            //found it - send it back to the calling method
                            storedHash = e.getTextContent();
                        }
                    }                       
                }                    
            }
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(HashListClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return storedHash;
    }
    
    /**
     * After a report is created or updated we need to update the stored hash
     * value in the Hash File
     * @param rID the reports UNID
     * @param md5 the new md5 hash value of the report to be stored.
     */
    public void setHash(String rID, String md5) {
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.parse(hashList);
            
            //first, check to see if we already have a record for this report
            //traverse each element in the Hash File and, where we find a match
            //with the reports UNID, return the associated stored hash value.
            NodeList nodeList = doc.getElementsByTagName("file");
            boolean foundRID = false;
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) { //ignore white space nodes                        
                        Element e = (Element)node;
                        if (e.getAttribute("id").equals(rID)) {
                            //found it - set the value to the new hash
                            NodeList childNodeList = e.getChildNodes();  
                            for (int j = 0 ; j < childNodeList.getLength() ; j++) {
                                //there should only be one child node, but we do
                                //this to avoid any white space nodes
                                Node childNode = childNodeList.item(j);
                                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element eC = (Element)childNode;
                                    eC.setTextContent(md5);
                                }
                            }
                            foundRID = true;
                        }
                    }                       
                }                    
            }
            
            //if we didn't find a record for the report, create a new one
            if (foundRID == false) {
                Element rootElement = doc.getDocumentElement();
                //parent node, containing report UNID
                Element rep = doc.createElement("file");
                rootElement.appendChild(rep);

                // set attribute to rID value
                Attr attr = doc.createAttribute("id");
                attr.setValue(rID);
                rep.setAttributeNode(attr);

                // Hash value element
                Element hashval = doc.createElement("hashvalue");
                hashval.appendChild(doc.createTextNode(md5));
                rep.appendChild(hashval);
            }            
                
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(hashList));

            transformer.transform(source, result);
            
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException ex) {
            Logger.getLogger(HashListClass.class.getName()).log(Level.SEVERE, null, ex); 
        }
    }
}
