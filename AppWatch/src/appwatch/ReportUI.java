package appwatch;

/*
 * Copyright (c) 2017 David Harrop
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import static java.lang.Thread.sleep;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * The Report Display page - used to interact with the report in the UI (as
 opposed to ReportClass which interacts with report in the back-end
 Displays report content, triggers vulnerability scans and opens the 
 Application Details page.
 * 
 * @author David Harrop
 */
public class ReportUI extends javax.swing.JDialog {
    ReportClass rep;
    PowerShellClass ps;
    HashListClass hash;
    private final String filename;
    private final File xml;
    private final String date;
    public final String reportID;
    String baseURL;
    String keyURL;
    String charset;
    String platform;
    String searchType;
    Integer totalVuls = 0;
    DefaultTableModel model;
    ApplicationUI appUI;

    /**
     * This is the author's free API key for Shodan, that will allow us to perform
     * GET requests to multiple Exploit databases.
     * This value is in plain text in code as it is a free API key - anyone can
     * get one.  If this utility was to be commercialised this API key would
     * need to be secured via encryption.
     * 
     */
    public static final String apiKey = "bfjsR7ng6kijWAl4Z0S5V0j5HEDswx1O";
    
    /**
     * Creates new form reportUI
     * @param parent the calling Dialog to be displayed in front of
     * @param modal the flag that determines 'in front of'
     * @param rID the report UNID
     * @throws org.xml.sax.SAXException
     *  Catches errors in parsing the XML file
     * @throws java.io.IOException
     *  Catches any errors in interacting with the file system
     * @throws javax.xml.parsers.ParserConfigurationException
     *  Catches any errors in reading the parsed XML file
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ReportUI(java.awt.Frame parent, boolean modal, String rID) throws SAXException, IOException, ParserConfigurationException {
        super(parent, modal);
        initComponents();  
        setLocationRelativeTo(null); //central position
        //instantiate our classes and set variables
        Long lastMod;
        Date rDt; 
        boolean scanDone = false;
        ps = new PowerShellClass();
        rep = new ReportClass();
        this.reportID = rID;
        rIDValue.setText(rID);
        this.filename = ps.homeDir() + File.separator + "AppWatch" + File.separator + reportID + ".xml";
        this.xml = new File(filename);
        
        //We have the report id, now retrieve the date and number of applications from it
        //display those values in UI
        numAppsValue.setText(rep.readReport(xml, "app_Count"));
        numVulValue.setText(rep.readReport(xml, "vul_Count"));
        if (rep.readReport(xml, "report_Stage").equals("Vulnerability Scan")) {
            //scan already done, just permit read
            scanDone = true;
            jMenuItem1.setEnabled(false);
        }
        lastMod = xml.lastModified();
        rDt = new Date(lastMod);
        this.date = rDt.toString();
        rDtValue.setText(date);
        populateTable();
    }

    /**
     * Called upon class instantiation.  Retrieves values from the selected report
     * file and displays them in the UI.
     */
    public void populateTable() {
        String appID;
        String appName;
        String appVersion;
        String appPublisher;      
        String appVulCount;
        Integer count = 0;
        this.model = (DefaultTableModel) appTable.getModel();
        model.setRowCount(0);
        //traverse the xml file to retrieve the application information 
        //and output it to the table
        try {
            DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder dB = dBF.newDocumentBuilder();
            Document doc = dB.parse(xml);

            NodeList nodeList = doc.getElementsByTagName("Object");

            for (int i = 0; i < nodeList.getLength(); i++) {
                appID = "-";
                appName = "-";
                appVersion = "-";
                appPublisher = "-";
                appVulCount = "0";
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element)node;
                    NodeList childNodeList = e.getChildNodes();
                    if (childNodeList.getLength() > 0) {
                        for (int j = 0 ; j < childNodeList.getLength() ; j++) {
                           Node childNode = childNodeList.item(j);
                           if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element eApp = (Element)childNode;
                                if (eApp.hasAttribute("Name")) {
                                    Attr eAttr = eApp.getAttributeNode("Name"); 
                                    String attrLabel = eAttr.getTextContent();
                                    switch (attrLabel) {
                                        case "AppID":
                                            appID = eApp.getTextContent();
                                            break;
                                        case "DisplayName":
                                            appName = eApp.getTextContent();
                                            break;
                                        case "DisplayVersion":
                                            appVersion = eApp.getTextContent();
                                            break;
                                        case "Publisher":
                                            appPublisher = eApp.getTextContent();
                                            break;
                                        case "AppVulCount":
                                            appVulCount = eApp.getTextContent();
                                            break;
                                        default:
                                            break;
                                    } 
                                }                                 
                            }
                        }
                    }
                    count++;                          
                    //if the report contains a node with a blank application name
                    //we aren't going to display it. This can happen with PowerShell
                    if (!"".equals(appName)) {
                        Object[] row = {appID, appName, appVersion, appPublisher, appVulCount};
                       model.addRow(row); 
                    } 
                    model.fireTableDataChanged();                    
                }
            }            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
        }          
        //update the UI with aggregate data from report
        try {
            numVulValue.setText(rep.readReport(xml, "vul_Count"));
        } catch (SAXException | IOException ex) {
            Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Sort by application name
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(appTable.getModel());
        appTable.setRowSorter(sorter);
        //Set column widths to suit content
        TableColumn column;
        for (int i = 0; i < 4; i++) {
            column = appTable.getColumnModel().getColumn(i);
            switch (i) {
                case 1:
                    column.setMinWidth(150);
                    break;
                case 2:
                    column.setMaxWidth(75);
                    column.setMinWidth(75);
                    break;
                case 3:
                    column.setMinWidth(125);
                    break;
                case 4:
                    column.setMaxWidth(90);
                    column.setMinWidth(90);
                    break;
                default:
                    column.setMaxWidth(35);
                    column.setMinWidth(35);
                    break;
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        appTable = new javax.swing.JTable();
        repIDLabel = new javax.swing.JLabel();
        repDateLabel = new javax.swing.JLabel();
        repNumAppsLabel = new javax.swing.JLabel();
        vulFoundLabel = new javax.swing.JLabel();
        status = new javax.swing.JLabel();
        useVersionNum = new javax.swing.JCheckBox();
        rIDValue = new javax.swing.JLabel();
        rDtValue = new javax.swing.JLabel();
        numAppsValue = new javax.swing.JLabel();
        numVulValue = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));

        appTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Application", "Version", "Publisher", "Vulnerabilities"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        appTable.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        appTable.setRowSelectionAllowed(false);
        appTable.setShowVerticalLines(false);
        appTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                appTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(appTable);

        repIDLabel.setText("Report ID: ");

        repDateLabel.setText("Report Date: ");

        repNumAppsLabel.setText("Number of Applications: ");

        vulFoundLabel.setText("Number of Vulnerabilities found: ");

        status.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N

        useVersionNum.setText("Ignore Version Number when scanning");
        useVersionNum.setToolTipText("Enabling this setting ignores the version number for each application.  The Vulnerability Scan is performed on the application name only");
        useVersionNum.setBorder(null);

        rIDValue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rIDValue.setText("...");
        rIDValue.setMaximumSize(new java.awt.Dimension(999, 14));

        rDtValue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rDtValue.setText("...");
        rDtValue.setMaximumSize(new java.awt.Dimension(999, 14));

        numAppsValue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        numAppsValue.setText("...");

        numVulValue.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        numVulValue.setText("...");

        jMenu1.setText("Action");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem1.setText("Perform Vulnerability Scan");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(useVersionNum)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(status))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 735, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(repDateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rDtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(repIDLabel)
                                .addGap(18, 18, 18)
                                .addComponent(rIDValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(209, 209, 209)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vulFoundLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numVulValue))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(repNumAppsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(numAppsValue)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(repNumAppsLabel)
                        .addComponent(numAppsValue))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(repIDLabel)
                        .addComponent(rIDValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(repDateLabel)
                        .addComponent(rDtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(vulFoundLabel)
                        .addComponent(numVulValue)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(status)
                    .addComponent(useVersionNum))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Double-clicking any row opens the Application Details page relevant to 
     * the application at the selected row
     * @param evt 
     */
    private void appTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appTableMouseClicked
               
        if (evt.getClickCount() == 2) {
            onDoubleClick();
        }        
    }//GEN-LAST:event_appTableMouseClicked

    /**
     * Action to be performed when a row in the table is double-clicked
     * Open the Application Details page for the selected row
     */
    public void onDoubleClick() {
        String appID;
        String appName;
        String appVersion;
        Object aID = appTable.getValueAt(appTable.getSelectedRow(), 0);
        appID = aID.toString();
        Object product = appTable.getValueAt(appTable.getSelectedRow(), 1);
        appName = product.toString();
        Object version = appTable.getValueAt(appTable.getSelectedRow(), 2);
        appVersion = version.toString();
        if (reportID.equals("")) {
            // a row without a Report ID value has been clicked, can't do anything
            JOptionPane.showMessageDialog(null, "Invalid selection", "WARNING!", JOptionPane.WARNING_MESSAGE);
        } else {
                appUI = new ApplicationUI(new javax.swing.JFrame(), true, xml, appID, appName, appVersion, date);
                appUI.setVisible(true);
        }
    }
    
    /**
     * Performs a Vulnerability scan for each application listed in the table.
     * Application Name & Version are parsed into a query URL, which is fired
     * as a HTTPS GET request to Shodan.  Return is in JSON format, which must 
     * be parsed into the report file.
     * @param evt 
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        //Set variables and instantiate support classes
        this.baseURL = "https://exploits.shodan.io/api/search?query=";
        this.keyURL = "&key=" + apiKey;
        this.charset = "UTF-8";
        this.platform = "platform:\"windows\""; //Not interested in exploits for other OS at this time.
        this.searchType = "";
        String rID;
        String md5Hash;
        boolean scanOK;       
        
        rep = new ReportClass();
        hash = new HashListClass();  
         
        this.model = (DefaultTableModel) appTable.getModel();
        Integer lastrow = model.getRowCount();
        //open the XML file for read/edit
        /*DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
        DocumentBuilder dB;
        try {
            dB = dBF.newDocumentBuilder();
            Document doc = dB.parse(new File(filename));
            NodeList nodeList = doc.getElementsByTagName("Object");
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
        }  */          
        
        status.setText("searching...");
        for (int r = 0 ; r < lastrow ; r++) {
        //for (int r = 0 ; r < 1 ; r++) {
            scanOK = performScanForApp(r);
            if (scanOK == false){
                break;
            }
            
        }
        //update the report file with new status and the aggregate data from our query
        //finally update the stored hash value for the report.
        try {
            rep.insertXML(totalVuls.toString(), "vul_Count", filename);
            rep.insertXML("Vulnerability Scan", "report_Stage", filename);
            rep.insertXML(searchType, "scan_Type", filename);
            md5Hash = rep.getHash(new File(filename));
            rID = rep.getUNIDFromFile(filename);
            hash.setHash(rID, md5Hash);
        } catch (IOException | NoSuchAlgorithmException | SAXException ex) {
            Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
        }  
        status.setText("Scan complete");
        numVulValue.setText(totalVuls.toString());
        populateTable();
        JOptionPane.showMessageDialog(null, "Finished searching for vulnerabilities for " + lastrow + " applications."
                                    , "Scan status", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * When triggered, a loop that cycles through each table row (representing an
     * application) is processed through this method.  It forms the GET query, 
     * triggers the HTTP connection and processes the returned JSON stream into
     * the XML report by calling the Report class addVResult method.
     * Also identifies a problem with the host internet connection.
     * @param r Row number from table identifying application to scan for
     * @return true or false, indicating success of scanning for 
     * vulnerabilities for the app in question
     */
    public boolean performScanForApp(Integer r) {
        String product;
        String version;
        String appID;
        String query;
        String source;
        String cve;
        String cveURL;
        String description;
        
        HttpURLConnection connection;
        URL url;
        InputStream response;
        
        model.fireTableDataChanged();
        appTable.updateUI();
        appID = model.getValueAt(r, 0).toString();
        product = model.getValueAt(r, 1).toString();
        version = model.getValueAt(r, 2).toString();
        status.setText("searching.");
                                   
        try {   
            //Seems like the free Shoden API is throttled - without a sleep
            //the second GET request returns 503 error.
            sleep(1000);
            product = product.replace("-", "");
            version = version.replace("-", "");
            //product = "FTP Explorer"; //for testing only - delete
            product = product.replace("[", "");
            product = product.replace("]", "");
            //version = ""; //for testing only - delete
            System.out.println("Searching vulnerabilities for " + product + " " + version);
            //create the query string
            if (useVersionNum.isSelected()) {
                query = URLEncoder.encode(product, charset) + "+"  
                    + URLEncoder.encode(platform, charset);
                searchType = "Application Name Only";
            } else {
                query = URLEncoder.encode(product, charset) + "+" 
                    + URLEncoder.encode(version, charset) + "+" 
                    //+ URLEncoder.encode(querytype, charset) + "+" 
                    + URLEncoder.encode(platform, charset);
                searchType = "Application Name & Version";
            }

            url = new URL(baseURL + query + keyURL);      
            //open the connection
            connection = (HttpURLConnection)url.openConnection();
            System.out.println("HTTP Response Code: " + connection.getResponseCode());
            //read the response to GET request
            response = connection.getInputStream();

            //JSON parsing
            BufferedReader buffer = new BufferedReader(new InputStreamReader(response));
            // get the data
            JSONObject jsonObject = (JSONObject) JSONValue.parse(buffer);
            System.out.println("jsonObject.toString()= " + jsonObject.toString());
            if (!jsonObject.toString().contains("error")) {

                // get the first array of values
                JSONArray arraySet = (JSONArray) jsonObject.get("matches");
                System.out.println("Found: " + arraySet.size());
                totalVuls = totalVuls + arraySet.size();
                int i;
                status.setText("searching..");
                //we don't want every value from each array, just some key ones.
                //traverse the each array in the set, retreiving only the desired
                // values
                for (i = 0 ; i < arraySet.size() ; i++) {
                    JSONObject array = (JSONObject) arraySet.get(i);
                    if (array.containsKey("source")) {
                        source = array.get("source").toString();
                    } else {
                        source = "";
                    }
                    if (array.containsKey("cve")) {
                        cve = array.get("cve").toString();
                    } else {
                        cve = "";
                    }
                    if (array.containsKey("description")) {
                        description = array.get("description").toString();
                    } else {
                        description = "";
                    }
                    rep.addVResult(filename, i, appID, cve, description, source, arraySet.size());
                    populateTable();
                }
            } else {
                rep.addVResult(filename, 0, appID, "Error", "query error", "Error", 0);
                populateTable();
            }
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(null, "Seems like you don't have an internet connection or Shodan is down"
                                , "Connection Error", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException | InterruptedException ex) {
            Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ReportUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            @SuppressWarnings("null")
            public void run() {
                ReportUI dialog = null;
                try {
                    dialog = new ReportUI(new javax.swing.JFrame(), true, "");
                } catch (SAXException | IOException | ParserConfigurationException ex) {
                    Logger.getLogger(ReportUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable appTable;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel numAppsValue;
    private javax.swing.JLabel numVulValue;
    private javax.swing.JLabel rDtValue;
    private javax.swing.JLabel rIDValue;
    private javax.swing.JLabel repDateLabel;
    public javax.swing.JLabel repIDLabel;
    private javax.swing.JLabel repNumAppsLabel;
    private javax.swing.JLabel status;
    private javax.swing.JCheckBox useVersionNum;
    private javax.swing.JLabel vulFoundLabel;
    // End of variables declaration//GEN-END:variables

}
