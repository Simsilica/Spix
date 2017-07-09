/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spix.swing.materialEditor.dialog;

import com.jme3.asset.*;
import com.jme3.shader.*;
import spix.app.FileIoService;
import spix.core.RequestCallback;
import spix.swing.SwingGui;
import spix.swing.materialEditor.controller.MatDefEditorController;
import spix.swing.materialEditor.icons.Icons;
import spix.swing.materialEditor.utils.*;
import spix.ui.FileRequester;
import spix.ui.MessageRequester;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 *
 * @author Nehon
 */
public class AddNodeDialog extends javax.swing.JDialog {

    private List<ShaderNodeDefinition> defList = new ArrayList<ShaderNodeDefinition>();
    private MatDefEditorController controller;
    private Point clickPosition;
    private String path;
    private JToolBar toolbar = new JToolBar();
    private SwingGui gui;
    /**
     * Creates new form NewJDialog
     */
    public AddNodeDialog(java.awt.Frame parent, boolean modal, MatDefEditorController controller, SwingGui gui, Point clickPosition) {
        super(parent, modal);
        this.controller = controller;
        initComponents();
        fillList(gui);
        this.clickPosition = clickPosition;
        this.gui = gui;

        setLocationRelativeTo(parent);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        getContentPane().setLayout(new BorderLayout());
        toolbar.setFloatable(false);
        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(new LoadAction());
        toolbar.add(loadButton);
        JButton newButton = new JButton("New");
        newButton.addActionListener(new NewAction());
        toolbar.add(newButton);
        getContentPane().add(toolbar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new JScrollPane();
        jTree1 = new javax.swing.JTree();
        shaderNodesList = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add a Shader Node");
        setModal(true);

        jButton1.setText("ok");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setMnemonic(KeyEvent.VK_ESCAPE);
        jButton2.setText("cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1)
                .addComponent(jButton2))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader node definitions"));

        jScrollPane3.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        shaderNodesList.setBorder(javax.swing.BorderFactory.createTitledBorder("Shader node definition"));

        javax.swing.GroupLayout shaderNodesListLayout = new javax.swing.GroupLayout(shaderNodesList);
        shaderNodesList.setLayout(shaderNodesListLayout);
        shaderNodesListLayout.setHorizontalGroup(
            shaderNodesListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );
        shaderNodesListLayout.setVerticalGroup(
            shaderNodesListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(shaderNodesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(centerPanel);
        centerPanel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setVisible(false);
        controller.addNodesFromDefs(defList, clickPosition);
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTree jTree1;
    private javax.swing.JPanel shaderNodesList;
    // End of variables declaration//GEN-END:variables

    private void createDoc(ShaderNodeDefinition def) {
        JTextPane doc = new JTextPane();
        doc.setEditable(false);
        //doc.setBackground(new java.awt.Color(240, 240, 240));
        doc.setMaximumSize(new java.awt.Dimension(300, 300));
        doc.setMinimumSize(new java.awt.Dimension(300, 300));
        doc.setPreferredSize(new java.awt.Dimension(300, 300));
        JScrollPane defPanel = new JScrollPane();
        defPanel.setViewportView(doc);

        jTabbedPane1.addTab(def.getName(), def.getType() == Shader.ShaderType.Vertex ? Icons.vert : Icons.frag, defPanel);
        doc.setText("");
        DocFormatter.addDoc(def, doc.getStyledDocument());
        doc.setCaretPosition(0);
    }

    private void fillList(SwingGui gui) {
        List<String> l = new ArrayList<String>();
        FileIoService fileIoService = gui.getSpix().getService(FileIoService.class);
        l.addAll(fileIoService.findFilesWithExtension("j3sn"));
        try {
            l.addAll(ResourcesUtils.getShaderNodeDefinitionsFromClassPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] leaves = l.toArray(new String[l.size()]);
        TreeUtil.createTree(jTree1, leaves);
        TreeUtil.expandTree(jTree1, (TreeNode) jTree1.getModel().getRoot(), 10);
        jTree1.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree1.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();

                if (node == null) {
                    return;
                }


                if (node.isLeaf()) {
                    jTabbedPane1.removeAll();
                    path = TreeUtil.getPath(node.getUserObjectPath());
                    path = path.substring(0, path.lastIndexOf("/"));
                    ShaderNodeDefinitionKey k = new ShaderNodeDefinitionKey(path);
                    k.setLoadDocumentation(true);
                    fileIoService.loadAsset(k, new RequestCallback<Object>() {
                        @Override
                        public void done(Object result) {
                            defList = (List<ShaderNodeDefinition>) result;
                            for (ShaderNodeDefinition def : defList) {
                                createDoc(def);
                            }
                        }
                    });


                }
            }
        });
    }

    private class LoadAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            gui.getService(FileIoService.class).requestJ3sn(new RequestCallback<List<ShaderNodeDefinition>>() {
                @Override
                public void done(List<ShaderNodeDefinition> result) {
                    gui.runOnSwing(new Runnable() {
                        @Override
                        public void run() {
                            setVisible(false);
                            controller.addNodesFromDefs(result, clickPosition);
                        }
                    });

                }
            });
        }
    }

    private class NewAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            gui.getService(FileIoService.class).newJ3sn(new RequestCallback<List<ShaderNodeDefinition>>() {
                @Override
                public void done(List<ShaderNodeDefinition> result) {
                    gui.runOnSwing(new Runnable() {
                        @Override
                        public void run() {
                            setVisible(false);
                            controller.addNodesFromDefs(result, clickPosition);
                        }
                    });
                }
            });
        }
    }
}
