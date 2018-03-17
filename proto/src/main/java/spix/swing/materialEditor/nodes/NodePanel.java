/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spix.swing.materialEditor.nodes;

import com.jme3.shader.*;
import spix.swing.materialEditor.*;
import spix.swing.materialEditor.controller.MatDefEditorController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * @author Nehon
 */
public abstract class NodePanel extends DraggablePanel implements Selectable {

    protected List<JLabel> inputLabels = new ArrayList<>();
    protected List<JLabel> outputLabels = new ArrayList<>();
    protected Map<String, Dot> inputDots = new LinkedHashMap<>();
    protected Map<String, Dot> outputDots = new LinkedHashMap<>();
    private JPanel content;
    private JLabel header;
    protected JLabel previewLabel;
    protected Color color;
    private Icon icon;
    private String nodeName;
    private String key;
    private NodeToolBar toolBar;
    protected boolean selected = false;
    protected boolean displayPreview = false;
    protected Color backgroundColor = new Color(100, 100, 100, 200);


    public NodePanel(MatDefEditorController controller, String key, Color color, Icon icon) {
        super(controller);
        this.color = color;
        this.icon = icon;
        this.key = key;
        toolBar = new NodeToolBar(this);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    controller.removeSelected();
                }
            }
        });
    }

    public abstract Shader.ShaderType getShaderType();

    protected abstract void initHeader(JLabel header);

    protected void init(List<ShaderNodeVariable> inputs, List<ShaderNodeVariable> outputs) {

        for (ShaderNodeVariable input : inputs) {

            JLabel label = createLabel(input.getType(), input.getName(), Dot.ParamType.Input);
            Dot dot = createDot(input.getType(), Dot.ParamType.Input, input.getName(), this.getNodeName());
            inputLabels.add(label);
            inputDots.put(this.getNodeName() + "." + input.getName(), dot);
        }
        int index = 0;
        for (ShaderNodeVariable output : outputs) {
            JLabel label = createLabel(output.getType(), output.getName(), Dot.ParamType.Output);
            Dot dot = createDot(output.getType(), Dot.ParamType.Output, output.getName(), this.getNodeName());
            dot.setIndex(index++);
            outputLabels.add(label);
            outputDots.put(this.getNodeName() + "." +output.getName(), dot);
        }

        init();
    }

    protected void init() {
        if (displayPreview) {
            setBounds(0, 0, 150, 30 + inputLabels.size() * 20 + outputLabels.size() * 20 + 95);
        } else {
            setBounds(0, 0, 110, 30 + inputLabels.size() * 17 + outputLabels.size() * 17);
        }

        initComponents();

        initHeader(header);
        setOpaque(false);
    }

    public void setTitle(String s) {
        header.setText(s);
        header.setToolTipText(s);
    }

    public String getNodeName() {
        return nodeName;
    }

    protected void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Dot getInputConnectPoint(String nameSpace, String varName) {
        return inputDots.get(nameSpace + "." + varName);
    }

    public Dot getOutputConnectPoint(String nameSpace, String varName) {
        return outputDots.get(nameSpace + "." + varName);
    }

    public Map<String, Dot> getInputConnectPoints() {
        return Collections.unmodifiableMap(inputDots);
    }

    public Map<String, Dot> getOutputConnectPoints() {
        return Collections.unmodifiableMap(outputDots);
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        Color borderColor = Color.BLACK;
        if (selected) {
            borderColor = Color.WHITE;
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (selected) {
            Color[] colors = new Color[]{new Color(0.6f, 0.6f, 1.0f, 0.8f), new Color(0.6f, 0.6f, 1.0f, 0.5f)};
            float[] factors = {0f, 1f};
            g.setPaint(new RadialGradientPaint(getWidth() / 2, getHeight() / 2, getWidth() / 2, factors, colors));
            g.fillRoundRect(8, 3, getWidth() - 10, getHeight() - 6, 15, 15);
        } else {
            if (toolBar.isVisible()) {
                hideToolBar();
            }
        }

        g.setColor(backgroundColor);
        g.fillRoundRect(5, 1, getWidth() - 9, getHeight() - 6, 15, 15);
        g.setColor(borderColor);

        g.drawRoundRect(4, 0, getWidth() - 9, getHeight() - 6, 15, 15);
        g.setColor(backgroundColor);
        g.fillRect(4, 1, 10, 10);
        g.setColor(borderColor);
        g.drawLine(4, 0, 14, 0);
        g.drawLine(4, 0, 4, 10);
        g.setColor(Color.BLACK);
        g.drawLine(5, 15, getWidth() - 6, 15);
        g.setColor(new Color(190, 190, 190));
        g.drawLine(5, 16, getWidth() - 6, 16);

        Color c1 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 150);
        Color c2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
        g.setPaint(new GradientPaint(0, 15, c1, getWidth(), 15, c2));
        g.fillRect(5, 1, getWidth() - 10, 14);

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getName() {
        return nodeName;
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        super.onMousePressed(e);
        controller.select(this, e.isShiftDown() || e.isControlDown());
        showToolBar();
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        controller.multiStopDrag();
    }

    protected void showToolBar() {
        toolBar.display();
    }

    public void hideToolBar() {
        toolBar.setVisible(false);
    }

    /**
     * override to do edit this node content.
     */
    public void edit() {

    }

    public void cleanup() {
        toolBar.cleanup();
        if (toolBar.getParent() == null) {
            return;
        }
        toolBar.getParent().remove(toolBar);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        header = new JLabel(icon);
        header.setForeground(Color.white);
        header.addMouseListener(labelMouseMotionListener);
        header.addMouseMotionListener(labelMouseMotionListener);
        header.setHorizontalAlignment(SwingConstants.LEFT);
        header.setFont(new Font("Tahoma", Font.BOLD, 9));
        header.setIconTextGap(2);

        content = new JPanel();
        content.setOpaque(false);
        GroupLayout contentLayout = new GroupLayout(content);
        content.setLayout(contentLayout);

        int txtLength = 90;


        if (displayPreview) {
            previewLabel = new JLabel();
            previewLabel.setBackground(new java.awt.Color(100, 100, 100));
            previewLabel.setForeground(new java.awt.Color(100, 100, 100));
            previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            previewLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            previewLabel.setIconTextGap(0);
            previewLabel.setMaximumSize(new java.awt.Dimension(128, 128));
            previewLabel.setMinimumSize(new java.awt.Dimension(128, 128));
            previewLabel.setOpaque(true);
            previewLabel.setPreferredSize(new java.awt.Dimension(128, 128));
            add(previewLabel);
            previewLabel.setBounds(11, 24, 128, 128);
        }


        GroupLayout.ParallelGroup grpHoriz = contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

        int i = 0;
        for (String key : outputDots.keySet()) {
            grpHoriz.addGroup(GroupLayout.Alignment.TRAILING, contentLayout.createSequentialGroup()
                    .addGap(0, 0, displayPreview ? Short.MAX_VALUE : 8)
                    .addComponent(outputLabels.get(i), GroupLayout.PREFERRED_SIZE, txtLength, GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)
                    .addComponent(outputDots.get(key), GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE));
            i++;
        }

        i = 0;
        for (String key : inputDots.keySet()) {
            grpHoriz.addGroup(GroupLayout.Alignment.LEADING, contentLayout.createSequentialGroup()
                    .addComponent(inputDots.get(key), GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                    .addGap(2, 2, 2)
                    .addComponent(inputLabels.get(i), GroupLayout.PREFERRED_SIZE, txtLength, GroupLayout.PREFERRED_SIZE));
            i++;
        }

        contentLayout.setHorizontalGroup(grpHoriz);

        GroupLayout.ParallelGroup grpVert = contentLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

        GroupLayout.SequentialGroup grp = contentLayout.createSequentialGroup();

        if (displayPreview) {
            grp.addGroup(contentLayout.createSequentialGroup()
                    .addGap(55, 55, 55));
            grp.addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(inputDots.get(inputDots.keySet().iterator().next()), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(inputLabels.get(0))
                    .addComponent(outputDots.get(outputDots.keySet().iterator().next()), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputLabels.get(0)));

        } else {

            i = 0;
            for (String key : inputDots.keySet()) {
                grp.addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(inputDots.get(key), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(inputLabels.get(i))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
                i++;
            }

            i = 0;
            for (String key : outputDots.keySet()) {
                grp.addGroup(contentLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(outputDots.get(key), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(outputLabels.get(i))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
                i++;
            }
        }
        grpVert.addGroup(GroupLayout.Alignment.TRAILING, grp);

        contentLayout.setVerticalGroup(grpVert);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(header, 100, 100, 100))
                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(6, 6, 6))
                        .addComponent(content, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(header, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(content, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5));
    }

    public JLabel createLabel(String glslType, String txt, Dot.ParamType type) {
        JLabel label = new JLabel(txt);
        label.setForeground(Color.WHITE);
        label.setToolTipText(glslType + " " + txt);
        label.setOpaque(false);

        label.setHorizontalAlignment(type == Dot.ParamType.Output ? SwingConstants.RIGHT : SwingConstants.LEFT);
        label.setFont(new Font("Tahoma", 0, 9));
        label.addMouseListener(labelMouseMotionListener);
        label.addMouseMotionListener(labelMouseMotionListener);

        return label;
    }

    public Dot createDot(String type, Dot.ParamType paramType, String paramName, String nodeName) {
        Dot dot1 = new Dot(controller);
        dot1.setShaderType(getShaderType());
        dot1.setNode(this);
        dot1.setText(paramName);
        dot1.setVariableName(paramName);
        dot1.setNodeName(nodeName);
        dot1.setParamType(paramType);
        dot1.setType(type);
        return dot1;
    }


    public final void refresh(String name) {
        setNodeName(name);
        for (Dot dot : inputDots.values()) {
            dot.setNodeName(name);
        }
        for (Dot dot : outputDots.values()) {
            dot.setNodeName(name);
        }
        setTitle(name);
    }


    public void delete() {
        controller.removeSelected();
    }

    @Override
    public String toString() {
        return nodeName;
    }

    // used to pass press and drag events to the NodePanel when they occur on the label
    private LabelMouseMotionListener labelMouseMotionListener = new LabelMouseMotionListener();

    private class LabelMouseMotionListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            MouseEvent me = SwingUtilities.convertMouseEvent(e.getComponent(), e, NodePanel.this);
            NodePanel.this.dispatchEvent(me);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            MouseEvent me = SwingUtilities.convertMouseEvent(e.getComponent(), e, NodePanel.this);
            NodePanel.this.dispatchEvent(me);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            MouseEvent me = SwingUtilities.convertMouseEvent(e.getComponent(), e, NodePanel.this);
            NodePanel.this.dispatchEvent(me);
        }
    }
}
