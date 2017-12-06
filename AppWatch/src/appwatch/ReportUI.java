package appwatch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author David Harrop
 */
public class reportUI extends javax.swing.JDialog {
    reportClass rep;
    powerShellClass ps;
    hashListClass hash;
    private final String filename;
    private final File xml;
    private final String date;
    public final String reportID;
    applicationUI appUI;

    /**
     * This is the author's free API key for Shodan, that will allow us to perform
     * GET requests to multiple Exploit databases.
     */
    public static final String apiKey = "bfjsR7ng6kijWAl4Z0S5V0j5HEDswx1O";
    
    /**
     * Creates new form reportUI
     * @param parent
     * @param modal
     * @param rID
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public reportUI(java.awt.Frame parent, boolean modal, String rID) throws SAXException, IOException, ParserConfigurationException {
        super(parent, modal);
        initComponents();  
        setLocationRelativeTo(null);
        Long lastMod;
        Date rDt; 
        boolean scanDone = false;
        ps = new powerShellClass();
        rep = new reportClass();
        this.reportID = rID;
        repID.setText(rID);
        this.filename = ps.homeDir() + File.separator + "AppWatch" + File.separator + reportID + ".xml";
        this.xml = new File(filename);
        
        //We have the report id, now retrieve the date and number of applications from it
        repNumApps.setText(rep.readReport(xml, "app_Count"));
        vulFound.setText(rep.readReport(xml, "vul_Count"));
        if (rep.readReport(xml, "report_Stage").equals("Vulnerability Scan")) {
            //scan done, just permit read
            scanDone = true;
            jMenuItem1.setEnabled(false);
        }
        lastMod = xml.lastModified();
        rDt = new Date(lastMod);
        this.date = rDt.toString();
        repDate.setText(date);
        populateTable();
    }

    private void populateTable() {
        String appID;
        String appName;
        String appVersion;
        String appPublisher;      
        String appVulCount;
        Integer count = 0;
        DefaultTableModel model = (DefaultTableModel) appTable.getModel();
        model.setRowCount(0);
        //traverse the xml file to retrieve the application informationand output it to the table
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
                        //Node childNode = childNodeList.item(1);
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
                    
                    if (!"".equals(appName)) {
                        Object[] row = {appID, appName, appVersion, appPublisher, appVulCount};
                       model.addRow(row); 
                    } 
                    model.fireTableDataChanged();
                    vulFound.setText(rep.readReport(xml, "vul_Count"));
                }
            }            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(reportUI.class.getName()).log(Level.SEVERE, null, ex);
        }                 
        //Sort by application name
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(appTable.getModel());
        appTable.setRowSorter(sorter);

        /*
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        */

        //Set the Vulnerabilities column to be widest
        TableColumn column;
        for (int i = 0; i < 4; i++) {
            column = appTable.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setMaxWidth(35);
                column.setMinWidth(35);
            } else if (i == 2) {
                column.setMinWidth(150); 
            } else if (i < 4) {
                column.setMinWidth(200); 
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
        repID = new javax.swing.JTextField();
        repDate = new javax.swing.JTextField();
        repNumApps = new javax.swing.JTextField();
        vulFound = new javax.swing.JTextField();
        status = new javax.swing.JLabel();
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

        repID.setEditable(false);
        repID.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        repID.setText("...");
        repID.setBorder(null);
        repID.setOpaque(false);
        repID.setSelectedTextColor(new java.awt.Color(240, 240, 240));

        repDate.setEditable(false);
        repDate.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        repDate.setText("...");
        repDate.setBorder(null);
        repDate.setOpaque(false);

        repNumApps.setEditable(false);
        repNumApps.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        repNumApps.setText("...");
        repNumApps.setBorder(null);
        repNumApps.setOpaque(false);

        vulFound.setEditable(false);
        vulFound.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        vulFound.setText("...");
        vulFound.setBorder(null);
        vulFound.setOpaque(false);

        status.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1084, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(status)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(repDateLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(repDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(repIDLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(repID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(200, 200, 200)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(repNumAppsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(repNumApps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(vulFoundLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vulFound, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(repIDLabel)
                            .addComponent(repID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(repDateLabel)
                            .addComponent(repDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(repNumAppsLabel)
                            .addComponent(repNumApps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vulFoundLabel)
                            .addComponent(vulFound, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5)
                .addComponent(status)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void appTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appTableMouseClicked
        String appID;
        String appName;
        String appVersion;
        
        if (evt.getClickCount() == 2) {
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
                    appUI = new applicationUI(new javax.swing.JFrame(), true, xml, appID, appName, appVersion, date);
                    appUI.setVisible(true);
            }
        }
        
    }//GEN-LAST:event_appTableMouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        /* Traverse the table, retrieving the Application Name and Version
        * For each row, fire a HTTPS GET request to Shodan and interpret the 
        * results into the XML file, then refresh the table.
        */
        String baseURL = "https://exploits.shodan.io/api/search?query=";
        String keyURL = "&key=" + apiKey;
        String charset = "UTF-8";
        String product;
        String version;
        String appID;
        //String querytype = "type:\"exploit\"";
        String platform = "platform:\"windows\"";
        String query;
        String source;
        String cve;
        String cveURL;
        String description;
        String rID;
        String md5Hash;
        HttpURLConnection connection;
        URL url;
        Integer totalVuls = 0;
        InputStream response;
        rep = new reportClass();
        hash = new hashListClass();  
         
        DefaultTableModel model = (DefaultTableModel) appTable.getModel();
        Integer lastrow = model.getRowCount();
        //open the XML file for read/edit
        DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
        DocumentBuilder dB;
        try {
            dB = dBF.newDocumentBuilder();
            Document doc = dB.parse(new File(filename));
            NodeList nodeList = doc.getElementsByTagName("Object");
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(reportUI.class.getName()).log(Level.SEVERE, null, ex);
        }            
        
        //for (int r = 0 ; r < lastrow ; r++) {
        for (int r = 0 ; r < 1 ; r++) {
            model.setValueAt("searching...", r, 4);
            model.fireTableDataChanged();
            appTable.updateUI();
            appID = model.getValueAt(r, 0).toString();
            product = model.getValueAt(r, 1).toString();
            version = model.getValueAt(r, 2).toString();
            status.setText("processing " + product + " " + version);
            
                       
            try {   
                sleep(1000);
                //product = product.replace("-", "");
                //version = version.replace("-", "");
                product = "Microsoft SQL Server";
                version = "2000";
                System.out.println("Searching vulnerabiiities for " + product + " " + version);
                query = URLEncoder.encode(product, charset) + "+" 
                        + URLEncoder.encode(version, charset) + "+" 
                        //+ URLEncoder.encode(querytype, charset) + "+" 
                        + URLEncoder.encode(platform, charset);
                url = new URL(baseURL + query + keyURL);                
                connection = (HttpURLConnection)url.openConnection();
                System.out.println("HTTP Response Code: " + connection.getResponseCode());
                response = connection.getInputStream();
                
                //JSON parsing
                BufferedReader buffer = new BufferedReader(new InputStreamReader(response));
                JSONObject jsonObject = (JSONObject) JSONValue.parse(buffer);                
		// get the data
		JSONArray genreArray = (JSONArray) jsonObject.get("matches");
		// get the first genre
                System.out.println("genreArray.size();" + genreArray.size());
                totalVuls = totalVuls + genreArray.size();
                int i;
                for (i = 0 ; i < genreArray.size() ; i++) {
                    JSONObject firstGenre = (JSONObject) genreArray.get(i);
                    if (firstGenre.containsKey("source")) {
                        source = firstGenre.get("source").toString();
                    } else {
                        source = "";
                    }
                    if (firstGenre.containsKey("cve")) {
                        cve = firstGenre.get("cve").toString();
                    } else {
                        cve = "";
                    }
                    if (firstGenre.containsKey("description")) {
                        description = firstGenre.get("description").toString();
                    } else {
                        description = "";
                    }
                    rep.addVResult(filename, i, appID, cve, description, source, genreArray.size());
                    populateTable();
                }
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(reportUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException | InterruptedException ex) {
                Logger.getLogger(reportUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(reportUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            rep.insertXML(totalVuls.toString(), "vul_Count", filename);
            rep.insertXML("Vulnerability Scan", "report_Stage", filename);
            md5Hash = rep.getHash(new File(filename));
            rID = rep.getUNIDFromFile(filename);
            hash.setHash(rID, md5Hash);
        } catch (IOException | NoSuchAlgorithmException | SAXException ex) {
            Logger.getLogger(reportUI.class.getName()).log(Level.SEVERE, null, ex);
        }  
        
        JOptionPane.showMessageDialog(null, "Finished searching for vulnerabilities for " + lastrow + " applications."
                                    , "Scan status", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

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
            Logger.getLogger(reportUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            @SuppressWarnings("null")
            public void run() {
                reportUI dialog = null;
                try {
                    dialog = new reportUI(new javax.swing.JFrame(), true, "");
                } catch (SAXException | IOException | ParserConfigurationException ex) {
                    Logger.getLogger(reportUI.class.getName()).log(Level.SEVERE, null, ex);
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
    private javax.swing.JTextField repDate;
    private javax.swing.JLabel repDateLabel;
    private javax.swing.JTextField repID;
    public javax.swing.JLabel repIDLabel;
    private javax.swing.JTextField repNumApps;
    private javax.swing.JLabel repNumAppsLabel;
    private javax.swing.JLabel status;
    private javax.swing.JTextField vulFound;
    private javax.swing.JLabel vulFoundLabel;
    // End of variables declaration//GEN-END:variables

}
