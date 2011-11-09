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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
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
		setBorder(BorderFactory.createLineBorder(Color.RED));
		
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