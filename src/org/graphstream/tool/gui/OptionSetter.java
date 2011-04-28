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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.graphstream.tool.ToolGUI;
import org.graphstream.tool.ToolOption;
import org.graphstream.tool.ToolOption.ToolEnumOption;
import org.graphstream.tool.gui.Resources.ColorType;

public class OptionSetter extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5595243256883567683L;

	public static OptionSetter create(ToolOption option, int maxWidth,
			Action removeAction) {
		String value = null;

		switch (option.type) {
		case FLAG:
			value = "";
			break;
		case INT:
			value = JOptionPane.showInputDialog(null, option.description,
					String.format("Value of \"%s\" ?", option.key),
					JOptionPane.QUESTION_MESSAGE);
			try {
				if (value != null)
					Integer.parseInt(value);
			} catch (NumberFormatException e) {
				ToolGUI.error("Invalid integer format",
						"You should enter an integer for this option.");
				value = null;
			}

			break;
		case REAL:
			value = JOptionPane.showInputDialog(null, option.description,
					String.format("Value of \"%s\" ?", option.key),
					JOptionPane.QUESTION_MESSAGE);
			try {
				if (value != null)
					Double.parseDouble(value);
			} catch (NumberFormatException e) {
				ToolGUI.error("Invalid real format",
						"You should enter a real for this option.");
				value = null;
			}

			break;
		case STRING:
			value = JOptionPane.showInputDialog(null, option.description,
					String.format("Value of \"%s\" ?", option.key),
					JOptionPane.QUESTION_MESSAGE);
			break;
		case OPTIONS:
			value = JOptionPane.showInputDialog(null, String.format(
					"%s\n\nUse a list of \"key=value\" separate by \";\".",
					option.description), String.format("Value of \"%s\" ?",
					option.key), JOptionPane.QUESTION_MESSAGE);
			break;
		case ENUM:
			value = "enum";
			ToolEnumOption<?> enumOption = (ToolEnumOption<?>) option;
			Enum<?> r = (Enum<?>) JOptionPane.showInputDialog(null,
					option.description,
					String.format("Value of \"%s\" ?", option.key),
					JOptionPane.QUESTION_MESSAGE, null,
					enumOption.choices.getEnumConstants(),
					enumOption.choices.getEnumConstants()[0]);
			value = r == null ? null : r.name();
			break;
		}

		if (value == null)
			return null;

		return new OptionSetter(option, maxWidth, value, removeAction);
	}

	int labelWidth;
	ToolOption option;
	JComponent valueLabel;
	String value;

	public OptionSetter(ToolOption option, int w, String value,
			Action removeAction) {
		this.option = option;
		this.value = value;

		GridBagConstraints c = new GridBagConstraints();
		JLabel label = new JLabel(option.key, JLabel.RIGHT);

		labelWidth = Math.max(w, label.getPreferredSize().width);

		label.setToolTipText(option.description);
		label.setForeground(Resources.getColor(ColorType.COMPONENT_TEXT));
		label.setSize(labelWidth, 20);
		label.setPreferredSize(label.getSize());
		label.setFont(Resources.getRegularFont(14.0f));

		setLayout(new GridBagLayout());

		c.weightx = 0.0;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 5, 0, 8);

		add(label, c);

		JLabel valueLabel = new JLabel(value);
		valueLabel.setForeground(Resources
				.getColor(ColorType.COMPONENT_LABEL_TEXT));
		valueLabel.setBackground(Resources
				.getColor(ColorType.COMPONENT_LABEL_BACKGROUND));
		valueLabel.setFont(Resources.getRegularFont(12.0f));

		c.weightx = 1.0;
		c.ipady = 2;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;
		c.insets = new Insets(0, 5, 0, 2);

		add(valueLabel, c);

		IconButton close = IconButton.createIconButton(IconButton.Type.CLOSE,
				28, null, removeAction);

		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 2);

		add(close, c);
	}

	protected void paintComponent(Graphics g) {
		int s = labelWidth + 10;// Math.max(60, label.getPreferredSize().width)
								// + 10;
		int width = getWidth() - 1;

		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		g.setColor(Resources.getColor(ColorType.COMPONENT_LABEL_BACKGROUND));
		g.fillRoundRect(0, 0, 14, getHeight() - 1, 7, 7);
		g.fillRect(7, 0, s - 7, getHeight() - 1);

		g.setColor(Resources.getColor(ColorType.COMPONENT_BACKGROUND));
		g.fillRect(s, 0, width - 7 - s, getHeight() - 1);
		g.fillRoundRect(width - 14, 0, 14, getHeight() - 1, 7, 7);

		g.setColor(Resources.getColor(ColorType.COMPONENT_BORDER));
		g.drawRoundRect(0, 0, width, getHeight() - 1, 7, 7);
	}
}
