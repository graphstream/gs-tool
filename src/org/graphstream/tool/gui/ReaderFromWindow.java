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
