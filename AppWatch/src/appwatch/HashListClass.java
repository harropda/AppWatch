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
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
 *
 * @author David Harrop
 */
public class hashListClass {
    powerShellClass ps;
    private final String dir;
    private final String hashList; 
    public static int blockSize = 16;
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    public hashListClass() {
        ps = new powerShellClass();
        this.dir = ps.getAppDir();
        this.hashList = dir + "hashlist.xml";
    }
    
     /**
     * Test the existence of the Hash List
     * @param dir AppWatch home directory
     * @param filename
     * @return exists Boolean indicating the existence of the Hash List
     */
    public Boolean checkHashFileExists(String dir, String filename) {
        Boolean exists = false;
        File folder;
        folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        System.out.println("hlC: Looking for " + filename);
        for (File file : listOfFiles) {
            System.out.println("hlC: Checking " + file.getName());
            if (file.isFile() && file.getName().equalsIgnoreCase(filename)) {
                exists = true;
            }
        }        
        return exists;
    }
        
     /**
     * source: https://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/
     * @throws java.security.InvalidAlgorithmParameterException
     * @throws java.io.IOException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.InvalidKeyException
     * @throws java.security.spec.InvalidKeySpecException
     * @throws java.security.spec.InvalidParameterSpecException
     * @throws javax.crypto.NoSuchPaddingException
     */
    public void createHashFile() throws InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException {
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
            Logger.getLogger(hashListClass.class.getName()).log(Level.SEVERE, null, ex); 
        }
    }
    
    /**
     * Credit: (Joshua & Gray) https://stackoverflow.com/questions/12645489/cipherinputstream-only-read-16-bytes-aes-java
     * @param filename
     * @param key
     * @throws java.lang.Exception
     */    
    public void encryptHashFile(String filename, String key) throws Exception {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        CipherOutputStream cos = null;        
        byte[] byteKey = key.getBytes("UTF-8");
        System.out.println("Encrypting haslist");
        Cipher cipher = getCipher(byteKey, "encrypt");
        
        try {
            fis = new FileInputStream(filename);
            fos = new FileOutputStream(filename + ".enc");
            cos = new CipherOutputStream(fos, cipher);
            byte[] buffer = new byte[1024];
            int read = fis.read(buffer);
            while (read != -1) {
                cos.write(buffer, 0, read);
                read = fis.read(buffer);
            }
            cos.flush();
        } catch (IOException ex) {
            Logger.getLogger(hashListClass.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (cos != null) {
                cos.close();
            }
            if (fos != null) {
               fos.close(); 
            }
            if (fis != null) {
               fis.close(); 
            }
        }
    }
    
    /**
     *
     * @param filename
     * @param key
     * @throws javax.crypto.BadPaddingException
     * @throws java.io.UnsupportedEncodingException
     */
    public void decryptHashFile(String filename, String key) throws BadPaddingException, UnsupportedEncodingException, Exception {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        CipherInputStream cis = null;   
        String outFile = filename.replace(".enc", "");
        System.out.println("decrypt out file = " + outFile);
        byte[] byteKey = key.getBytes("UTF-8");
        
        Cipher cipher = getCipher(byteKey, "decrypt");
        
        try {
            fis = new FileInputStream(filename);
            fos = new FileOutputStream(outFile);
            cis = new CipherInputStream(fis, cipher);
            byte[] buffer = new byte[1024];
            int read = cis.read(buffer);
            while (read != -1) {
                fos.write(buffer, 0, read);
                read = cis.read(buffer);
            }
        } catch (IOException  ex) {
            Logger.getLogger(hashListClass.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (cis != null) {
               cis.close(); 
            }
            if (fis != null) {
               fis.close(); 
            }
        }
    }
    
    public Cipher getCipher(byte[] key, String cryptFlag) throws Exception {
        byte[] keyBytes = getKeyBytes(key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
        if (cryptFlag.equals("encrypt")) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        }
        
        return cipher;
    }
    
    private byte[] getKeyBytes(final byte[] key) throws Exception {
        byte[] keyBytes = new byte[16];
        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, keyBytes.length));
        return keyBytes;
    }
    
    /**
     * @param rID
     * @return 
     * @throws java.io.IOException 
     */
    public String retrieveHash(String rID) throws IOException {
        String storedHash = "";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
        
            docBuilder = docFactory.newDocumentBuilder();
            
            // root elements
            Document doc = docBuilder.parse(hashList);
            
            NodeList nodeList = doc.getElementsByTagName("file");
            System.out.println("Found " + nodeList.getLength() + " 'file' tags");
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    System.out.print("Node(" + i + ") " + nodeList.item(i).getNodeName());
                    System.out.println(" - " + nodeList.item(i).getNodeValue());
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element)node;
                        System.out.println("rID = " + rID);
                        System.out.println(e.getAttribute("id"));
                        if (e.getAttribute("id").equals(rID)) {
                            System.out.println("Found rID match");
                            //storedHash = nodeList.item(i).getFirstChild().getNodeValue();
                            System.out.println("nodeList.item(i).getFirstChild().getNodeValue() =" + nodeList.item(i).getFirstChild().getNodeValue());
                            System.out.println("e.getNodeName() =" + e.getNodeName());
                            System.out.println("e.getNodeValue() =" + e.getNodeValue());
                            System.out.println("e.getTextContent() =" + e.getTextContent());
                            System.out.println("e.toString()() =" + e.toString());
                            
                            storedHash = e.getTextContent();
                            //this always equals null
                        }
                    }                       
                }                    
            }
        } catch (ParserConfigurationException | SAXException ex) {
            Logger.getLogger(hashListClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return storedHash;
    }
    
    /**
     *
     * @param rID
     * @param md5
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public void setHash(String rID, String md5) throws SAXException, IOException {
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.parse(hashList);
            Element rootElement = doc.getDocumentElement();
            //doc.appendChild(rootElement);
            Element rep = doc.createElement("file");
            rootElement.appendChild(rep);

            // set attribute to file element
            Attr attr = doc.createAttribute("id");
            attr.setValue(rID);
            rep.setAttributeNode(attr);

            // Hash value element
            Element hashval = doc.createElement("hashvalue");
            hashval.appendChild(doc.createTextNode(md5));
            rep.appendChild(hashval);
                
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(hashList));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
            
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException ex) {
            Logger.getLogger(hashListClass.class.getName()).log(Level.SEVERE, null, ex); 
        }
    }
}
