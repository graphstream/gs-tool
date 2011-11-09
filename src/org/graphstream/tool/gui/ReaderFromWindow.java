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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Reader;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

public class ReaderFromWindow extends Reader {

	JDialog frame;
	JTextArea text;
	JTextPane pane;
	StringBuffer buffer;
	boolean opened = false;

	public ReaderFromWindow(int width, int height) {
		frame = new JDialog();
		buffer = new StringBuffer();

		text = new JTextArea(3, 30);
		text.setFont(Resources.getMonospaceFont(14.0f));
		text.getKeymap().addActionForKeyStroke(
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK),
				new FlushAction());

		pane = new JTextPane();
		pane.setSize(width, height);
		pane.setEditable(false);
		pane.setBackground(Resources
				.getColor(Resources.ColorType.BACKGROUND_RIGHT));
		pane.setFont(Resources.getMonospaceFont(14.0f));

		JScrollPane scroller1 = new JScrollPane(pane);
		JScrollPane scroller2 = new JScrollPane(text);
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroller1,
				scroller2);

		scroller2.setPreferredSize(new Dimension(text.getPreferredSize().width,
				text.getPreferredSize().height + 5));
		scroller1.setPreferredSize(new Dimension(width, height
				- scroller2.getPreferredSize().height));

		split.resetToPreferredSizes();

		frame.add(split);
		frame.pack();
		frame.addWindowListener(new CloseHandler());

		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		if (!frame.isVisible() && opened)
			return -1;

		if (!opened) {
			frame.setVisible(true);
			opened = true;
		}

		try {
			synchronized (buffer) {
				if (buffer.length() == 0) {
					buffer.wait();
				}
			}
		} catch (InterruptedException e) {
			if (buffer.length() == 0)
				return 0;
		}

		int r = Math.min(len, buffer.length());

		for (int i = 0; i < r; i++)
			cbuf[off + i] = buffer.charAt(i);

		if (r > 0)
			buffer.delete(0, r);

		return r;
	}

	public void close() throws IOException {
		frame.setVisible(false);
	}

	private class FlushAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2505426155430691582L;

		public void actionPerformed(ActionEvent e) {
			String str = text.getText();
			int offset = pane.getDocument().getLength();

			if (!str.endsWith("\n"))
				str += "\n";

			text.setText("");
			buffer.append(str);

			synchronized (buffer) {
				buffer.notifyAll();
			}

			try {
				pane.getDocument().insertString(offset, str, null);
			} catch (BadLocationException e1) {
			}
		}
	}

	private class CloseHandler extends WindowAdapter {
		public void windowClosed(WindowEvent e) {
			synchronized (buffer) {
				buffer.notifyAll();
			}
		}
	}

	public static void main(String... args) throws IOException {
		ReaderFromWindow rfw = new ReaderFromWindow(400, 300);
		rfw.frame.setVisible(true);
		char[] buf = new char[128];

		int r = rfw.read(buf, 0, 128);
		while (r > 0) {
			System.out.printf("> %s", new String(buf, 0, r));
			r = rfw.read(buf, 0, 128);
		}
	}
}
