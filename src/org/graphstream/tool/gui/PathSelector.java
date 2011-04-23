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

import static org.graphstream.tool.gui.IconButton.createIconButton;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PathSelector extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7164150016018758398L;

	static Color border = new Color(0, 0, 0);
	static Color background = new Color(74, 90, 123);
	static Color text = new Color(240, 240, 240);
	static Color labelback = new Color(30, 30, 30);
	static Color filled = new Color(52, 75, 121);

	JTextField path;
	IconButton find;
	JComponent label;
	int available;
	int loaded;

	public PathSelector(String label, int size) {
		JLabel theLabel = new JLabel(label, JLabel.RIGHT);
		theLabel.setForeground(text);
		theLabel.setSize(Math.max(size, theLabel.getWidth()), 20);
		theLabel.setPreferredSize(theLabel.getSize());

		init(theLabel);
	}

	public PathSelector(JComponent label) {
		init(label);
	}

	public void initLoadProgress(int available) {
		this.available = available;
		this.loaded = 100;
	}

	protected void init(JComponent l) {
		setLayout(new GridBagLayout());

		available = 0;
		loaded = 0;

		GridBagConstraints c = new GridBagConstraints();

		label = l;

		path = new JTextField(30);
		find = createIconButton(IconButton.Type.FIND, 28, null,
				new ChoosePathAction(this));

		path.setForeground(text);
		path.setBorder(null);
		path.setOpaque(false);
		path.setFont(Resources.getRegularFont(14.0f));
		path.setCaretColor(text);

		if (l != null) {
			int labelSize = Math.max(60, l.getPreferredSize().width);

			c.weightx = 0.0;
			c.gridwidth = 1;
			c.insets = new Insets(2, 5, 2, labelSize
					- l.getPreferredSize().width + 2);

			add(l, c);
		}

		c.insets = new Insets(2, 7, 2, 2);
		c.weightx = 1.0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;

		add(path, c);

		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;

		add(find, c);
	}

	protected void paintComponent(Graphics g) {
		int s = Math.max(60, label.getPreferredSize().width) + 10;
		int width = getWidth() - 8 - s;

		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}

		g.setColor(labelback);
		g.fillRoundRect(0, 0, 14, getHeight() - 1, 7, 7);
		g.fillRect(7, 0, s - 7, getHeight() - 1);

		if (loaded > 0) {
			float ratio = loaded / (float) available;
			ratio = Math.max(0, Math.min(1, ratio));

			g.setColor(filled);
			g.fillRect(s, 0, (int) (ratio * width), getHeight() - 1);

			if (ratio < 1) {
				g.setColor(background);
				g.fillRect(s + (int) (ratio * width), 0,
						(int) ((1 - ratio) * width), getHeight() - 1);
			}

			g.fillRoundRect(getWidth() - 15, 0, 14, getHeight() - 1, 7, 7);
		} else {
			g.setColor(background);
			g.fillRect(s, 0, width, getHeight() - 1);
			g.fillRoundRect(getWidth() - 15, 0, 14, getHeight() - 1, 7, 7);
		}

		g.setColor(border);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
	}

	class ChoosePathAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2039752502707273849L;

		protected PathSelector selector;

		public ChoosePathAction(PathSelector selector) {
			this.selector = selector;
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			int r = chooser.showOpenDialog(selector);

			if (r == JFileChooser.APPROVE_OPTION) {
				selector.path.setText(chooser.getSelectedFile()
						.getAbsolutePath());
				selector.repaint();
			}
		}
	}
}