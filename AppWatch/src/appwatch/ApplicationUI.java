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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author David Harrop
 */
public class applicationUI extends javax.swing.JDialog {
    private final String product;
    private final String version;
    private final String repDate;
    private final File xml;
    private final String appID;
    /**
     * Creates new form applicationUI
     * @param parent
     * @param modal
     * @param xml
     * @param appID
     * @param product
     * @param version
     * @param repDate
     */
    public applicationUI(java.awt.Frame parent, boolean modal, File xml, String appID, String product, String version, String repDate) {
        super(parent, modal);
        initComponents();
        this.product = product;
        this.version = version;
        this.repDate = repDate;
        this.xml = xml;
        this.appID = appID;
                
        appField.setText(product);
        verField.setText(version);
        dateField.setText(repDate);
        
        appField.setText(product);
        verField.setText(version);
        dateField.setText(repDate);
        
        populateTable();
    }

    private void populateTable() {
        String cveID;
        String desc;
        String source;
        String url;
        URL cveURL;
        
        DefaultTableModel model = (DefaultTableModel) vulTable.getModel();
        JTableHeader th = vulTable.getTableHeader();  
        TableColumnModel tcm = th.getColumnModel();
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
                
        model.setRowCount(0);
        //traverse the xml file to retrieve the application informationand output it to the table
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
                                //Node childNode = childNodeList.item(1);
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
                cveURL = new URL(url);
                Object[] row = {source, cveID, desc, cveURL};
                model.addRow(row);
                
                       
            }                 
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(applicationUI.class.getName()).log(Level.SEVERE, null, ex);
        }                 
        //Sort by application name
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(vulTable.getModel());
        vulTable.setRowSorter(sorter);

        /*
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        */

        //Set the Vulnerabilities column to be widest
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
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        vulTable = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();

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

        jButton1.setText("Print");

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
        vulTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vulTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(vulTable);

        jButton2.setText("Close");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(358, 358, 358)
                        .addComponent(jButton2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dateTitle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dateField)
                                .addGap(215, 215, 215)
                                .addComponent(jButton1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(appLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(appField)
                                .addGap(18, 18, 18)
                                .addComponent(verLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(verField))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 936, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                    .addComponent(dateField)
                    .addComponent(jButton1))
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void vulTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vulTableMouseClicked
        Integer column = vulTable.getSelectedColumn();
        if (column == 3) {
             if (evt.getClickCount() == 2) {
                Object link = vulTable.getValueAt(vulTable.getSelectedRow(), 3);
                 try {                     
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(new URI(link.toString()));
                    }                     
                 } catch (MalformedURLException ex) {
                     Logger.getLogger(applicationUI.class.getName()).log(Level.SEVERE, null, ex);
                 } catch (IOException | URISyntaxException ex) {
                     Logger.getLogger(applicationUI.class.getName()).log(Level.SEVERE, null, ex);
                 }
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
            Logger.getLogger(applicationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                applicationUI dialog = new applicationUI(new javax.swing.JFrame(), true, null, "", "", "", "");
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel verField;
    private javax.swing.JLabel verLabel;
    private javax.swing.JTable vulTable;
    // End of variables declaration//GEN-END:variables
}
