package org.graphstream.tool.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class LabelOptions<T extends Enum<T>> extends JPanel
		implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8918485474465683366L;

	JLabel label;
	JComboBox optionsBox;

	public LabelOptions(JComponent icon, Class<T> options) {
		setOpaque(false);
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		optionsBox = new JComboBox(options.getEnumConstants());
		optionsBox.setBorder(null);
		optionsBox.setFont(Resources.getRegularFont(11.0f));
		optionsBox.setPreferredSize(new Dimension(75, 20));
		// optionsBox.setSize(optionsBox.getPreferredSize());

		c.gridwidth = 1;

		add(icon, c);

		c.gridwidth = 1;
		c.insets = new Insets(0, 5, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;

		add(optionsBox, c);

		optionsBox.setVisible(false);

		icon.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		optionsBox.setVisible(!optionsBox.isVisible());
		getParent().repaint();
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}