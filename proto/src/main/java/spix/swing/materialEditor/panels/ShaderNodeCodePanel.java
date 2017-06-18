package spix.swing.materialEditor.panels;

import com.jme3.shader.Shader;
import com.jme3.shader.ShaderNode;
import org.fife.ui.rtextarea.RTextScrollPane;
import spix.app.FileIoService;
import spix.core.RequestCallback;
import spix.swing.SwingGui;
import spix.swing.materialEditor.icons.Icons;
import spix.swing.materialEditor.utils.NoneSelectedButtonGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Created by Nehon on 03/06/2016.
 */
public class ShaderNodeCodePanel extends DockPanel {

    private ShaderCodeEditor editor;
    private SwingGui gui;
    private Map<Shader.ShaderType, JToggleButton> buttons = new HashMap<>();
    private Map<String, Shader> shaders;

    private JToolBar toolbar;

    private java.util.List<String> fileNames = new ArrayList<>();
    private Map<String, String> fileContents = new HashMap<>();
    private java.util.List<JToggleButton> tbButtons = new ArrayList<>();
    private int lastButtonIndex = 0;
    private ButtonGroup group = new ButtonGroup();

    public ShaderNodeCodePanel(Container container, SwingGui gui) {
        super(Slot.West, container);
        this.gui = gui;

        editor = new ShaderCodeEditor();

        JPanel panel = new JPanel(new BorderLayout());
        RTextScrollPane scrollPane = new RTextScrollPane(editor);
        panel.add(scrollPane, BorderLayout.CENTER);

        setComponent(panel);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                editor.fitContent();
            }
        });

        setTitle("Shader Node");
        setIcon(Icons.node);
        button.setIcon(Icons.shaderNode);
        button.setRolloverIcon(Icons.shaderNodeHover);

        toolbar = new JToolBar("Generated shader code");
        toolbar.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        toolbar.setFloatable(false);

        panel.add(toolbar, BorderLayout.NORTH);

    }

    public void setSelectedNode(Object item) {
        if (!(item instanceof ShaderNode)) {
            return;
        }
        fileNames.clear();
        fileContents.clear();
        toolbar.removeAll();
        lastButtonIndex = 0;
        ShaderNode node = (ShaderNode) item;
        System.err.println(node.getDefinition().getPath());
        fileNames.add(node.getDefinition().getPath());
        fileNames.addAll(node.getDefinition().getShadersPath());

        for (String fileName : fileNames) {

            gui.getSpix().getService(FileIoService.class).loadFileAsText(fileName, new RequestCallback<String>() {
                @Override
                public void done(String result) {
                    if (result == null) {
                        result = "";
                    }
                    fileContents.put(fileName, result);
                }
            });

            JToggleButton b = null;
            if (lastButtonIndex < tbButtons.size()) {
                b = tbButtons.get(lastButtonIndex);
            }
            if (b == null) {
                b = new JToggleButton();
                tbButtons.add(b);
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String name = ((JToggleButton) e.getSource()).getActionCommand();
                        editor.setText(fileContents.get(name));
                    }
                });
                group.add(b);
            }
            toolbar.add(b);
            lastButtonIndex++;
            b.setText(fileName.substring(fileName.lastIndexOf('/') + 1));
            b.setToolTipText(fileName);
            b.setActionCommand(fileName);
        }
        tbButtons.get(0).setSelected(true);
        editor.setText(fileContents.get(fileNames.get(0)));
    }


    private void updateText() {
        //Shader shader = shaders.get(currentShaderVersion);
//        if (shader == null) {
//            return;
//        }
//        for (Shader.ShaderSource shaderSource : shader.getSources()) {
//
//                editor.setText(shaderSource.getSource());
//
//        }
    }

}