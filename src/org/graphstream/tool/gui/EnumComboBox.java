/*
 * Copyright 2006 - 2011 
 *     Stefan Balev 	<stefan.balev@graphstream-project.org>
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public class EnumComboBox extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2648376232791766130L;

	static Color border = new Color(0, 0, 0);
	static Color background = new Color(50, 50, 50);
	static Color text = new Color(240, 240, 240);
	static Color labelback = new Color(30, 30, 30);

	JLabel selected;
	Class<? extends Enum<?>> choices;

	public EnumComboBox(Class<? extends Enum<?>> choices) {
		this.choices = choices;

		GridBagConstraints c = new GridBagConstraints();

		selected = new JLabel(choices.getEnumConstants()[0].name());
		selected.setForeground(text);
		selected.setFont(Resources.getRegularFont(12.0f));

		IconButton openAlternative = IconButton.createIconButton(
				IconButton.Type.DOWN, selected.getPreferredSize().height + 2,
				null, new OpenChoicesAction());

		setLayout(new GridBagLayout());

		c.gridwidth = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(1, 7, 1, 1);

		add(selected, c);

		c.weightx = 0.0;
		c.insets = new Insets(1, 1, 1, 2);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;

		add(openAlternative, c);
	}

	protected void paintComponent(Graphics g) {
		g.setColor(background);
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

		g.setColor(border);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
	}

	class Choices extends JPanel implements FocusListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3605605810816910542L;

		Choices() {
			JScrollPane scroll = new JScrollPane();
			scroll.setPreferredSize(new Dimension(EnumComboBox.this.getWidth(),
					200));
			scroll.setOpaque(false);
			scroll.getViewport().setOpaque(false);
			scroll.setBorder(null);
			setBackground(background);
			
			scroll.addFocusListener(this);
			add(scroll);
		}

		public void focusGained(FocusEvent e) {
			System.err.printf("gain\n");
		}

		public void focusLost(FocusEvent e) {
			System.err.printf("lost\n");
		}
	}

	class OpenChoicesAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1759297585641231141L;

		public void actionPerformed(ActionEvent e) {
			Popup popup = PopupFactory
					.getSharedInstance()
					.getPopup(
							EnumComboBox.this,
							new Choices(),
							EnumComboBox.this.getLocationOnScreen().x,
							EnumComboBox.this.getLocationOnScreen().y
									+ EnumComboBox.this.getHeight());
			popup.show();
		}
	}
}
