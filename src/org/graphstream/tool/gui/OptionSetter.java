/*
 * Copyright 2006 - 2011 
 *     Julien Baudry	<julien.baudry@graphstream-project.org>
 *     Antoine Dutot	<antoine.dutot@graphstream-project.org>
 *     Yoann Pigné		<yoann.pigne@graphstream-project.org>
 *     Guilhelm Savin	<guilhelm.savin@graphstream-project.org>
 * 
 * This file is part of GraphStream <http://graphstream-project.org>.
 * 
 * GraphStream is a library whose purpose is to handle static or dynamic
 * graph, create them from scratch, file or any source and display them.
 * 
 * This program is free software distributed under the terms of two licenses, the
 * CeCILL-C license that fits European law, and the GNU Lesser General Public
 * License. You can  use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
 * URL <http://www.cecill.info> or under the terms of the GNU LGPL as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C and LGPL licenses and that you accept their terms.
 */
package org.graphstream.tool.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import org.graphstream.tool.ToolOption;
import org.graphstream.tool.ToolOption.ToolEnumOption;

public abstract class OptionSetter extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5595243256883567683L;

	static Color border = new Color(0, 0, 0);
	static Color background = new Color(74, 90, 123);
	static Color text = new Color(240, 240, 240);
	static Color labelback = new Color(30, 30, 30);

	public static OptionSetter create(ToolOption option, int maxWidth) {
		OptionSetter setter = null;

		switch (option.type) {
		case ENUM:
			setter = new EnumOptionSetter(option, maxWidth);
			break;
		default:
			setter = new StringOptionSetter(option, maxWidth);
			break;
		}

		return setter;
	}

	int labelWidth;
	ToolOption option;
	JComponent value;

	public OptionSetter(ToolOption option, int w) {
		this.option = option;
		
		GridBagConstraints c = new GridBagConstraints();
		JLabel label = new JLabel(option.key, JLabel.RIGHT);

		labelWidth = Math.max(w, label.getPreferredSize().width);

		label.setToolTipText(option.description);
		label.setForeground(text);
		label.setSize(labelWidth, 20);
		label.setPreferredSize(label.getSize());
		label.setFont(Resources.getRegularFont(14.0f));

		setLayout(new GridBagLayout());

		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 5, 0, 10);

		add(label, c);

		value = getValueComponent();// new JTextField(20);
		value.setForeground(text);
		value.setBorder(null);
		value.setOpaque(false);
		value.setFont(Resources.getRegularFont(14.0f));
		// value.setCaretColor(text);
		// value.setHorizontalAlignment(JTextField.RIGHT);
		value.setEnabled(option.type != ToolOption.Type.FLAG);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;

		add(value, c);

		IconButton close = IconButton.createIconButton(IconButton.Type.CLOSE,
				28, null, null);

		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(0, 5, 0, 2);

		add(close, c);
	}

	protected void paintComponent(Graphics g) {
		int s = labelWidth + 10;// Math.max(60, label.getPreferredSize().width)
								// + 10;
		int width = getWidth() - 8 - s;

		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		g.setColor(labelback);
		g.fillRoundRect(0, 0, 14, getHeight() - 1, 7, 7);
		g.fillRect(7, 0, s - 7, getHeight() - 1);

		g.setColor(background);
		g.fillRect(s, 0, width, getHeight() - 1);
		g.fillRoundRect(getWidth() - 15, 0, 14, getHeight() - 1, 7, 7);

		g.setColor(border);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
	}

	public abstract String getOptionValue();

	public abstract JComponent getValueComponent();

	static class StringOptionSetter extends OptionSetter {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7435967932756156539L;

		public StringOptionSetter(ToolOption option, int w) {
			super(option, w);
		}

		public String getOptionValue() {
			return ((JTextField) value).getText();
		}

		public JComponent getValueComponent() {
			JTextField f = new JTextField(20);
			f.setCaretColor(text);
			f.setHorizontalAlignment(JTextField.RIGHT);

			return f;
		}
	}

	static class EnumOptionSetter extends OptionSetter {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4624286114488086653L;

		public EnumOptionSetter(ToolOption option, int w) {
			super(option, w);
		}

		public String getOptionValue() {
			return ((JComboBox) value).getSelectedItem().toString();
		}

		public JComponent getValueComponent() {
			JComboBox box = new JComboBox(
					((ToolEnumOption<?>) option).choices.getEnumConstants());
			box.setRenderer(new InternalComboBoxRenderer());
			return box;
		}
	}
	
	static class InternalComboBoxRenderer implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel l = new JLabel(value.toString());
			l.setForeground(text);
			l.setBorder(null);
			l.setOpaque(false);
			l.setFont(Resources.getRegularFont(14.0f));
			
			return l;
		}
	}
}
