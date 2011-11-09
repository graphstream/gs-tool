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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class TitleLabel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4096602903174491162L;

	Image background;

	public TitleLabel(String label) {
		GridBagConstraints c = new GridBagConstraints();
		JLabel l = new JLabel(label, JLabel.LEFT);

		setLayout(new GridBagLayout());

		l.setFont(Resources.getBoldFont(25.0f));
		l.setOpaque(false);

		c.insets = new Insets(5, 10, 5, 10);
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;

		add(l, c);

		background = Resources.getImage(Resources.LINES, 0, 0, true);
	}

	protected void paintComponent(Graphics g) {
		if (background != null) {
			if (g instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D) g;

				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			}

			int w = background.getWidth(this);

			for (int i = 0; i < getWidth(); i += w) {
				g.drawImage(background, i, 0, null);
			}
		}
	}
}