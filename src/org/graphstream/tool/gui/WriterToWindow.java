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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class WriterToWindow extends Writer {

	protected JTextPane textPane;
	protected JDialog frame;
	protected StringBuffer buffer;
	protected boolean autoShow;
	protected int bufferSize;

	public WriterToWindow(boolean autoShow, int width, int height) {
		this.buffer = new StringBuffer();
		this.textPane = new JTextPane();
		this.frame = new JDialog();
		this.autoShow = autoShow;
		this.bufferSize = 65536;

		textPane.setSize(width, height);
		textPane.setEditable(false);
		textPane.setBackground(Resources.getColor(Resources.ColorType.BACKGROUND_RIGHT));
		textPane.setFont(Resources.getMonospaceFont(14.0f));
		
		JScrollPane scroller = new JScrollPane(textPane);
		scroller.setPreferredSize(new Dimension(width, height));
		
		frame.setLayout(new BorderLayout());
		frame.add(scroller, BorderLayout.CENTER);
		frame.pack();
	}

	public void write(char[] cbuf, int off, int len) throws IOException {
		buffer.append(cbuf, off, len);

		if (buffer.length() > 128)
			flush();
	}

	public void flush() throws IOException {
		if (buffer.length() > 0) {
			Document doc = textPane.getDocument();
			
			if (doc.getLength() > bufferSize) {
				try {
					doc.remove(0, Math.min(bufferSize, buffer.length()));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}

			int offset = doc.getLength();

			try {
				doc.insertString(offset, buffer.toString(),
						null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			buffer.delete(0, buffer.length());
			textPane.repaint();
			
			if (autoShow && !frame.isVisible())
				frame.setVisible(true);
		}
	}

	public void close() throws IOException {
		flush();
	}
}
