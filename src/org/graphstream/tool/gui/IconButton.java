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

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class IconButton extends JLabel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4729909769134257616L;

	public static enum Type {
		ABOUT(Resources.ABOUT_OFF, Resources.ABOUT_ON, Resources.ABOUT_CLICK), CONFIG(
				Resources.CONFIG_OFF, Resources.CONFIG_ON,
				Resources.CONFIG_CLICK), FIND(Resources.FIND_OFF,
				Resources.FIND_OFF, Resources.FIND_CLICK), RUN(
				Resources.RUN_OFF, Resources.RUN_ON, Resources.RUN_CLICK), SOURCE(
				Resources.SOURCE_OFF, Resources.SOURCE_ON,
				Resources.SOURCE_CLICK), SINK(Resources.SINK_OFF,
				Resources.SINK_ON, Resources.SINK_CLICK), CLOSE(
				Resources.CLOSE_OFF, Resources.CLOSE_ON, Resources.CLOSE_CLICK), DOWN(
				Resources.DOWN_OFF, Resources.DOWN_ON, Resources.DOWN_CLICK), ADD(
				Resources.ADD_OFF, Resources.ADD_ON, Resources.ADD_CLICK)

		;

		String off;
		String on;
		String click;

		Type(String off, String on, String click) {
			this.off = off;
			this.on = on;
			this.click = click;
		}
	}

	public static IconButton createIconButton(Type type, int size,
			String label, Action action) {
		ImageIcon off, on, click;

		off = new ImageIcon(Resources.getImage(type.off, size, size, false));
		on = new ImageIcon(Resources.getImage(type.on, size, size, false));
		click = new ImageIcon(Resources.getImage(type.click, size, size, false));

		return new IconButton(action, off, on, click, label);
	}

	protected Icon onIcon;
	protected Icon offIcon;
	protected Icon clickIcon;

	protected Action action;
	protected String command;

	protected int eventId;

	public IconButton(Action action, Icon off, Icon on, Icon click, String label) {
		this(action, on, off, click);

		setText(label);
		setHorizontalTextPosition(JLabel.LEFT);
	}

	public IconButton(Action action, Icon on, Icon off, Icon click) {
		super(off);

		setSize(off.getIconWidth(), off.getIconHeight());

		this.action = action;
		this.onIcon = on;
		this.offIcon = off;
		this.clickIcon = click;
		this.eventId = 0;
		this.command = null;

		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (action != null) {
			ActionEvent ae = new ActionEvent(this, eventId++, command);
			action.actionPerformed(ae);
		}

	}

	public void mousePressed(MouseEvent e) {
		if (isEnabled())
			setIcon(clickIcon);
	}

	public void mouseReleased(MouseEvent e) {
		setIcon(contains(e.getX(), e.getY()) ? onIcon : offIcon);
	}

	public void mouseEntered(MouseEvent e) {
		if (isEnabled())
			setIcon(onIcon);
	}

	public void mouseExited(MouseEvent e) {
		setIcon(offIcon);
	}
}