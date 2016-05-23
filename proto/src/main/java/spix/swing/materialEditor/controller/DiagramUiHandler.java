package spix.swing.materialEditor.controller;

import com.jme3.material.TechniqueDef;
import com.jme3.shader.*;
import spix.swing.materialEditor.*;
import spix.swing.materialEditor.nodes.*;
import spix.swing.materialEditor.utils.MaterialDefUtils;
import spix.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by Nehon on 21/05/2016.
 */
public class DiagramUiHandler {

    private Diagram diagram;
    private String currentTechniqueName;
    private Map<String, NodePanel> nodes = new HashMap<>();
    //A convenience map to easy access to the output nodes;
    private Map<Shader.ShaderType, Map<String, List<OutPanel>>> outPanels = new HashMap<>();
    protected List<Connection> connections = new ArrayList<>();

    public DiagramUiHandler(MatDefEditorController controller) {
        diagram = new Diagram(controller);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(diagram);
        controller.getEditor().getContentPane().add(scrollPane);
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                diagram.fitContent();
            }
        });
    }

    void clear() {
        nodes.clear();
        outPanels.clear();
        connections.clear();
        diagram.removeAll();
    }

    void refreshDiagram() {
        diagram.revalidate();
        diagram.repaint();
    }


    private void attachNodePanel(NodePanel node) {
        if(node == null){
            return;
        }

        diagram.add(node);
        diagram.setComponentZOrder(node, 0);
    }

    public void dispatchEventToDiagram(MouseEvent e, Component source) {
        MouseEvent me = SwingUtils.convertEvent(source, e, diagram);
        diagram.dispatchEvent(me);
    }

    void setCurrentTechniqueName(String currentTechniqueName) {
        clear();
        this.currentTechniqueName = currentTechniqueName;
    }

    void makeConnection(MatDefEditorController controller, VariableMapping mapping, TechniqueDef technique, String nodeName) {
        NodePanel forNode = nodes.get(MaterialDefUtils.makeShaderNodeKey(technique.getName(), nodeName));

        Dot rightDot = findConnectPointForInput(controller, mapping, forNode);
        Dot leftDot = findConnectPointForOutput(controller, mapping, forNode);
        connect(controller, leftDot, rightDot);
    }

    Connection connect(MatDefEditorController controller, Dot start, Dot end) {
        String key = MaterialDefUtils.makeConnectionKey(start.getNode().getName(), start.getText(), end.getNode().getName(), end.getText(), currentTechniqueName);
        Connection conn = new Connection(controller, key, start, end);
        start.connect(conn);
        end.connect(conn);
        connections.add(conn);
        diagram.add(conn);
        refreshDiagram();
        return conn;
    }

    private Dot findConnectPointForInput(MatDefEditorController controller, VariableMapping mapping, NodePanel forNode) {
        String nameSpace = mapping.getLeftVariable().getNameSpace();
        String name = mapping.getLeftVariable().getName();
        return getNodePanelForConnection(controller, forNode, nameSpace, name, true).getInputConnectPoint(name);
    }

    private Dot findConnectPointForOutput(MatDefEditorController controller, VariableMapping mapping, NodePanel forNode) {
        String nameSpace = mapping.getRightVariable().getNameSpace();
        String name = mapping.getRightVariable().getName();
        return getNodePanelForConnection(controller, forNode, nameSpace, name, false).getOutputConnectPoint(name);
    }

    private NodePanel getNodePanelForConnection(MatDefEditorController controller, NodePanel forNode, String nameSpace, String name, boolean forInput) {
        NodePanel np = null;
        if (isShaderInput(nameSpace)) {
            np = nodes.get(MaterialDefUtils.makeInputKey(currentTechniqueName, nameSpace, name));
        } else if (isGlobal(nameSpace)) {
            np = getOutPanel(controller, forNode.getShaderType(), new ShaderNodeVariable("vec4", "Global", name), forNode, forInput);
        } else {
            np = nodes.get(MaterialDefUtils.makeShaderNodeKey(currentTechniqueName, nameSpace));
        }
        return np;
    }

    private boolean isShaderInput(String nameSpace) {
        return nameSpace.equals("MatParam")
                || nameSpace.equals("WorldParam")
                || nameSpace.equals("Attr");
    }

    private boolean isGlobal(String nameSpace) {
        return nameSpace.equals("Global");
    }

    private NodePanel getOutPanel(MatDefEditorController controller, Shader.ShaderType type, ShaderNodeVariable var, NodePanel node, boolean forInput) {

        List<OutPanel> panelList = getOutPanelList(type, var);


        for (OutPanel outPanel : panelList) {
            if (forInput) {
                if (outPanel.isOutputAvailable() && !outPanel.getInputConnectPoint(var.getName()).isConnectedToNode(node)) {
                    return outPanel;
                }
            } else {
                if (outPanel.isInputAvailable() && !outPanel.getOutputConnectPoint(var.getName()).isConnectedToNode(node)) {
                    return outPanel;
                }
            }
        }

        return controller.addOutPanel(type, var);

    }

    private List<OutPanel> getOutPanelList(Shader.ShaderType type, ShaderNodeVariable var) {
        Map<String, List<OutPanel>> map = outPanels.get(type);
        if (map == null) {
            map = new HashMap<>();
            outPanels.put(type, map);
        }
        List<OutPanel> panelList = map.get(var.getName());
        if (panelList == null) {
            panelList = new ArrayList<>();
            map.put(var.getName(), panelList);
        }
        return panelList;
    }

    void removeNode(MatDefEditorController controller, String key) {
        NodePanel n = nodes.remove(key);
        if (n == null){
            //todo Somtimes, for some unknown reason the node is null.
            //It smells like a race condition between several threads, but I should always be on the swing thread...
            //in case it happens again I display all I can here and crash the app.
            System.err.println("Is event dispatch thread: " + SwingUtilities.isEventDispatchThread());
            for (String k : nodes.keySet()) {
                System.err.println("Key: "+ k + " => " + nodes.get(k));
            }
            throw new IllegalArgumentException("Cannot delete node for key: " + key);
        }

        if (n instanceof OutPanel) {
            OutPanel p = (OutPanel)n;
            outPanels.get(p.getShaderType()).get(p.getVarName()).remove(p);
        }
        n.cleanup();

        for (Iterator<Connection> it = connections.iterator(); it.hasNext(); ) {
            Connection conn = it.next();
            if (conn.getStart().getNode() == n || conn.getEnd().getNode() == n) {
                it.remove();
                //it's important to call this from the controller so the connections are not just removed from the UI
                controller.removeConnection(conn);
            }
        }

        diagram.remove(n);
        refreshDiagram();
    }

    public void removeConnection(Connection conn) {
        connections.remove(conn);
        conn.getEnd().disconnect(conn);
        conn.getStart().disconnect(conn);
        diagram.remove(conn);
        refreshDiagram();
    }

    NodePanel makeOutPanel(MatDefEditorController controller, Shader.ShaderType type, ShaderNodeVariable var) {
        List<OutPanel> panelList = getOutPanelList(type, var);
        String key = MaterialDefUtils.makeGlobalOutKey(currentTechniqueName, var.getName(), UUID.randomUUID().toString());
        OutPanel node = OutPanel.create(controller, key, type, var);
        panelList.add(node);
        nodes.put(key, node);
        attachNodePanel(node);
        return node;
    }

    NodePanel addShaderNodePanel(MatDefEditorController controller, ShaderNode sn) {
        NodePanel node = ShaderNodePanel.create(controller, MaterialDefUtils.makeShaderNodeKey(currentTechniqueName, sn.getName()), sn);
        nodes.put(node.getKey(), node);
        attachNodePanel(node);
        return node;
    }

    NodePanel addInputPanel(MatDefEditorController controller, ShaderNodeVariable shaderNodeVariable) {
        String key = MaterialDefUtils.makeInputKey(currentTechniqueName, shaderNodeVariable.getNameSpace(), shaderNodeVariable.getName());
        NodePanel node = nodes.get(key);
        if (node == null) {

            switch (shaderNodeVariable.getNameSpace()) {
                case "MatParam":
                    node = InputPanel.create(controller, key, InputPanel.ShaderInputType.MatParam, shaderNodeVariable);
                    break;
                case "Attr":
                    node = InputPanel.create(controller, key, InputPanel.ShaderInputType.Attribute, shaderNodeVariable);
                    break;
                case "WorldParam":
                    node = InputPanel.create(controller, key, InputPanel.ShaderInputType.WorldParam, shaderNodeVariable);
                    break;
            }
            nodes.put(node.getKey(), node);
            attachNodePanel(node);
        }
        return node;
    }

    String fixNodeName(String name, int count) {
        for (NodePanel nodePanel : nodes.values()) {
            if ((name + (count == 0 ? "" : count)).equals(nodePanel.getNodeName())) {
                return fixNodeName(name, count + 1);
            }
        }
        return name + (count == 0 ? "" : count);
    }

    Connection pickForConnection(MouseEvent e) {
        for (Connection connection : connections) {
            MouseEvent me = SwingUtilities.convertMouseEvent(diagram, e, connection);
            if (connection.pick(me)) {
                return connection;
            }
        }
        return null;
    }

    public void autoLayout(){
        diagram.autoLayout();
        refreshDiagram();
    }

    public void fitContent(){
        diagram.fitContent();
    }
}