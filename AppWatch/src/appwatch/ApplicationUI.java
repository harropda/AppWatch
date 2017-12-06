package appwatch;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.net.URL;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;

/**
 * This class displays all vulnerabilities found for a specific application in
 * a table, allowing the user to select one if they wish to see more information
 * The values are retrieved directly from the report file
 * @author David Harrop
 */
public final class ApplicationUI extends javax.swing.JDialog {
    private final String product;
    private final String version;
    private final String repDate;
    private final File xml;
    private final String appID;
    VulnerabilityUI vulUI;
    
    /**
     * Creates new form applicationUI
     * @param parent calling Frame to be in front of
     * @param modal flag that tells Java to display in front
     * @param xml the report file from which the application and vulnerabilities are read
     * @param appID the unique identifier for the application in the report file
     * @param product the application name
     * @param version the application version number
     * @param repDate the last modified date of the report
     */
    public ApplicationUI(java.awt.Frame parent, boolean modal, File xml, String appID, String product, String version, String repDate) {
        super(parent, modal);
        initComponents();
        this.product = product;
        this.version = version;
        this.repDate = repDate;
        this.xml = xml;
        this.appID = appID;
                
        //update the UI with our generic report values
        appField.setText(product);
        verField.setText(version);
        dateField.setText(repDate);
        
        appField.setText(product);
        verField.setText(version);
        dateField.setText(repDate);
        
        // read the data from the report and write it to the table
        populateTable();
    }

    /**
     * Given a report file, traverse all entries to identify the application in
     * question (defined by appID).  Pull all vulnerabilities for that
     * application, retrieving the exploit details.  Push the exploit details to 
     * the table, row by row
     */       
    public void populateTable() {
        String cveID;
        String desc;
        String source;
        String url;
        URL cveURL;
        
        DefaultTableModel model = (DefaultTableModel) vulTable.getModel();
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
                
        model.setRowCount(0);
        //traverse the xml file to retrieve the application information
        //and output it to the table
        try {

            DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder dB = dBF.newDocumentBuilder();
            Document doc = dB.parse(xml);

            NodeList nodeList = doc.getElementsByTagName("Exploit");

            for (int i = 0; i < nodeList.getLength(); i++) {
                cveID = "";
                desc = "";
                source = "";
                url = "";
                
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element)node;
                    if (e.hasAttribute("ID")) {
                        System.out.println(e.getNodeName() + " has attribute ID");
                        Attr expAttr = e.getAttributeNode("ID");
                        String expID = expAttr.getTextContent();
                        if (expID.startsWith(appID)) {
                            NodeList childNodeList = e.getChildNodes();
                            if (childNodeList.getLength() > 0) {
                                for (int j = 0 ; j < childNodeList.getLength() ; j++) {
                                   Node childNode = childNodeList.item(j);
                                   if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element eExp = (Element)childNode;
                                        String expName = eExp.getNodeName();
                                        switch (expName) {
                                            case "CVE_ID":
                                                cveID = eExp.getTextContent();
                                                break;
                                            case "Description":
                                                desc = eExp.getTextContent();
                                                break;
                                            case "Source":
                                                source = eExp.getTextContent();
                                                break;
                                            case "CVE_URL":
                                                url = eExp.getTextContent();
                                                break;
                                        }    
                                    }
                                }
                            }
                        }
                    }
                }
                //the CVE ID is returned from Shodan surrounded with "[]"
                //we don't want to display those
                cveURL = new URL(url);
                cveID = cveID.replace("[","");
                cveID = cveID.replace("]","");
                cveID = cveID.replace("\"","");
                Object[] row = {source, cveID, desc, cveURL};
                model.addRow(row);
            }                 
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ApplicationUI.class.getName()).log(Level.SEVERE, null, ex);
        }                 
        //Sort by application name
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(vulTable.getModel());
        vulTable.setRowSorter(sorter);

        //Set the column widths
        TableColumn column;
        for (int i = 0; i < 4; i++) {
            column = vulTable.getColumnModel().getColumn(i);
            if (i <= 1) {
                column.setMinWidth(100);
            } else if (i == 2) {
                column.setMinWidth(400); 
            } else if (i < 4) {
                column.setMinWidth(200); 
                column.setCellRenderer(tcr);
                tcr.setForeground(Color.blue);
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

        appLabel = new javax.swing.JLabel();
        appField = new javax.swing.JLabel();
        verLabel = new javax.swing.JLabel();
        verField = new javax.swing.JLabel();
        dateTitle = new javax.swing.JLabel();
        dateField = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        vulTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        appLabel.setText("Application:");
        appLabel.setFocusable(false);
        appLabel.setRequestFocusEnabled(false);
        appLabel.setVerifyInputWhenFocusTarget(false);

        appField.setText("product_name");

        verLabel.setText("Version:");

        verField.setText("version_number");

        dateTitle.setText("Scan Date:");

        dateField.setText("dd/mm/yyyy");

        vulTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Source", "CVE ID", "Description", "Link"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        vulTable.setShowVerticalLines(false);
        vulTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vulTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(vulTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dateTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dateField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(appLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(appField)
                        .addGap(18, 18, 18)
                        .addComponent(verLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(verField))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 936, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appLabel)
                    .addComponent(appField)
                    .addComponent(verLabel)
                    .addComponent(verField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateTitle)
                    .addComponent(dateField))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The user can click on the table to interact with the contents.  Where and 
     * how many clicks defines the response by the application. 
     * i. One-click on CVE URL column - opens the CVE page in browser
     * ii. Double-click any other column - opens the Vulnerability Details
     * page for the selected vulnerability.
     * @param evt 
     */
    private void vulTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vulTableMouseClicked
        Integer column = vulTable.getSelectedColumn();
        if (column == 3) { //the CVE URL column
             if (evt.getClickCount() == 1) {
                Object link = vulTable.getValueAt(vulTable.getSelectedRow(), 3);
                 try {                     
                    if (Desktop.isDesktopSupported()) {
                        //assuming that the column value is a correctly 
                        //constructed URL, try to open it in the default browser
                        Desktop.getDesktop().browse(new URI(link.toString()));
                    }                     
                 } catch (MalformedURLException ex) {
                     Logger.getLogger(ApplicationUI.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException | URISyntaxException ex) {
                     Logger.getLogger(ApplicationUI.class.getName()).log(Level.SEVERE, null, ex);
                 }
            }
        } else { //any other column            
            if (evt.getClickCount() == 2) {
                //double clicked, so open the Vulnerability Details page using
                //information from the selected row
                Object source = vulTable.getValueAt(vulTable.getSelectedRow(), 0);
                Object cveID = vulTable.getValueAt(vulTable.getSelectedRow(), 1);
                Object desc = vulTable.getValueAt(vulTable.getSelectedRow(), 2);
                Object cveURL = vulTable.getValueAt(vulTable.getSelectedRow(), 3);
                try {
                    vulUI = new VulnerabilityUI(new javax.swing.JFrame(), true, source.toString(), cveID.toString(), desc.toString(), cveURL.toString());
                } catch (BadLocationException | IOException ex) {
                    Logger.getLogger(ApplicationUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                vulUI.setVisible(true);
            }
        }
       
    }//GEN-LAST:event_vulTableMouseClicked

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
            Logger.getLogger(ApplicationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ApplicationUI dialog = new ApplicationUI(new javax.swing.JFrame(), true, null, "", "", "", "");
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
    private javax.swing.JLabel appField;
    private javax.swing.JLabel appLabel;
    private javax.swing.JLabel dateField;
    private javax.swing.JLabel dateTitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel verField;
    private javax.swing.JLabel verLabel;
    private javax.swing.JTable vulTable;
    // End of variables declaration//GEN-END:variables
}
