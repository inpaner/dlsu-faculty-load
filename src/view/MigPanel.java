package view;

import net.miginfocom.layout.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

/*
 * License (BSD):
 * ==============
 *
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (miglayout (at) miginfocom (dot) com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * Neither the name of the MiG InfoCom AB nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @version 1.0
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 2006-sep-08
 */
@SuppressWarnings("serial")
public class MigPanel extends JPanel {
    public static final int INITIAL_INDEX = 0;
    private static final boolean OPAQUE = false;

    private static boolean buttonOpaque = true;
    private static boolean contentAreaFilled = true;
    
    private static int benchRuns = 0;
    private static long startupMillis = 0;
    private static long timeToShowMillis = 0;
    
    public static void main(String[] args) {
        MainFrame mf = new MainFrame();
        MigPanel mp = new MigPanel();
        mf.setPanel(mp);
    }

    public MigPanel() {
        setLayout(new MigLayout());
        
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_B && (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) > 0) {
                    startupMillis = System.currentTimeMillis();
                    timeToShowMillis = System.currentTimeMillis() - startupMillis;
                    benchRuns = 1;
                    return true;
                }
                return false;
            }   
        } );
    }



    // **********************************************************
    // * Helper Methods
    // **********************************************************

    private final ToolTipListener toolTipListener = new ToolTipListener();
    private final ConstraintListener constraintListener = new ConstraintListener();

    protected JLabel createLabel(String text)
    {
        return createLabel(text, SwingConstants.LEADING);
    }

    private JLabel createLabel(String text, int align)
    {
        final JLabel b = new JLabel(text, align);
        configureActiveComponent(b);
        return b;
    }

    public JComboBox createCombo(String[] items)
    {
        JComboBox combo = new JComboBox(items);

        if (PlatformDefaults.getCurrentPlatform() == PlatformDefaults.MAC_OSX)
            combo.setOpaque(false);

        return combo;
    }

    protected JTextField createTextField(int cols)
    {
        return createTextField("", cols);
    }

    private JTextField createTextField(String text)
    {
        return createTextField(text, 0);
    }

    private JTextField createTextField(String text, int cols)
    {
        final JTextField b = new JTextField(text, cols);

        configureActiveComponent(b);

        return b;
    }

    private static final Font BUTT_FONT = new Font("monospaced", Font.PLAIN, 12);
    protected JButton createButton()
    {
        return createButton("");
    }

    private JButton createButton(String text)
    {
        return createButton(text, false);
    }

    private JButton createButton(String text, boolean bold)
    {
        JButton b = new JButton(text) {
            public void addNotify()
            {
                super.addNotify();
                if (benchRuns == 0) {   // Since this does not exist in the SWT version
                    if (getText().length() == 0) {
                        String lText = (String) ((MigLayout) getParent().getLayout()).getComponentConstraints(this);
                        setText(lText != null && lText.length() > 0 ? lText : "<Empty>");
                    }
                } else {
                    setText("Benchmark Version");
                }
            }
        };

        if (bold)
            b.setFont(b.getFont().deriveFont(Font.BOLD));

        configureActiveComponent(b);

        b.setOpaque(buttonOpaque); // Or window's buttons will have strange border
        b.setContentAreaFilled(contentAreaFilled);

        return b;
    }

    private JToggleButton createToggleButton(String text)
    {
        JToggleButton b = new JToggleButton(text);
//      configureActiveComponet(b);
        b.setOpaque(buttonOpaque); // Or window's buttons will have strange border
        return b;
    }

    private JCheckBox createCheck(String text)
    {
        JCheckBox b = new JCheckBox(text);

        configureActiveComponent(b);

        b.setOpaque(OPAQUE); // Or window's checkboxes will have strange border
        return b;
    }

    private JPanel createTabPanel(LayoutManager lm)
    {
        JPanel panel = new JPanel(lm);
        configureActiveComponent(panel);
        panel.setOpaque(OPAQUE);
        return panel;
    }

    private JComponent createPanel()
    {
        return createPanel("");
    }

    private JComponent createPanel(String s)
    {
        JLabel panel = new JLabel(s, SwingConstants.CENTER) {
            public void addNotify()
            {
                super.addNotify();
                if (benchRuns == 0) {   // Since this does not exist in the SWT version
                    if (getText().length() == 0) {
                        String lText = (String) ((MigLayout) getParent().getLayout()).getComponentConstraints(this);
                        setText(lText != null && lText.length() > 0 ? lText : "<Empty>");
                    }
                }
            }
        };
        panel.setBorder(new EtchedBorder());
        panel.setOpaque(true);
        configureActiveComponent(panel);

        return panel;
    }

    private JTextArea createTextArea(String text, int rows, int cols)
    {
        JTextArea ta = new JTextArea(text, rows, cols);
        ta.setBorder(UIManager.getBorder("TextField.border"));
        ta.setFont(UIManager.getFont("TextField.font"));
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);

        configureActiveComponent(ta);

        return ta;
    }

    private JScrollPane createTextAreaScroll(String text, int rows, int cols, boolean hasVerScroll)
    {
        JTextArea ta = new JTextArea(text, rows, cols);
        ta.setFont(UIManager.getFont("TextField.font"));
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(
                ta,
                hasVerScroll ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private JComponent configureActiveComponent(JComponent c)
    {
        if (benchRuns == 0) {
            c.addMouseMotionListener(toolTipListener);
            c.addMouseListener(constraintListener);
        }
        return c;
    }

    static final Color LABEL_COLOR = new Color(0, 70, 213);
    protected void addSeparator(JPanel panel, String text)
    {
        JLabel l = createLabel(text);
        l.setForeground(LABEL_COLOR);

        panel.add(l, "gapbottom 1, span, split 2, aligny center");
        panel.add(configureActiveComponent(new JSeparator()), "gapleft rel, growx");
    }

    private class ConstraintListener extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            if (e.isPopupTrigger())
                react(e);
        }

        public void mouseReleased(MouseEvent e)
        {
            if (e.isPopupTrigger())
                react(e);
        }

        public void react(MouseEvent e)
        {
            
        }
    }

    private static class ToolTipListener extends MouseMotionAdapter
    {
        public void mouseMoved(MouseEvent e)
        {
            
        }
    }

}
