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
package org.graphstream.tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.graphstream.tool.gui.IconButton;
import org.graphstream.tool.gui.LabelOptions;
import org.graphstream.tool.gui.MainTitledPanel;
import org.graphstream.tool.gui.OptionSetter;
import org.graphstream.tool.gui.PathSelector;
import org.graphstream.tool.gui.Resources;

import static org.graphstream.tool.gui.IconButton.createIconButton;
import static org.graphstream.tool.gui.Resources.getImage;

// -Dsun.java2d.opengl=true -Dawt.useSystemAAFontSettings=on|lcd

public class ToolGUI extends MainTitledPanel implements ToolsCommon {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8646940433915429831L;

	protected static final int ICON_SIZE = 28;

	protected static final Color COLOR_1 = new Color(74, 90, 123);
	protected static final Color COLOR_2 = new Color(52, 75, 121);

	protected Tool tool;
	protected JFrame frame;
	protected PathSelector inputSelector, outputSelector;
	protected ConfigurationPanel configuration;
	protected Dimension frameDimension;

	public ToolGUI(Tool tool) {
		super(tool.getName());

		this.tool = tool;
		build();
	}

	protected void build() {
		/*
		 * Build the frame
		 */
		frame = new JFrame(String.format("GraphStream : %s", tool.getName()));

		GridBagConstraints c = new GridBagConstraints();
		IconButton about = createIconButton(IconButton.Type.ABOUT, 28, null,
				new ShowAboutAction());

		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(10, 5, 15, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;

		add(about, c);

		ImageIcon source1 = new ImageIcon(Resources.getImage(
				Resources.SOURCE_OFF, 30, 30, true));
		ImageIcon source2 = new ImageIcon(Resources.getImage(
				Resources.SOURCE_ON, 30, 30, true));
		ImageIcon source3 = new ImageIcon(Resources.getImage(
				Resources.SOURCE_CLICK, 30, 30, true));
		IconButton source = new IconButton(null, source2, source1, source3);

		ImageIcon sink1 = new ImageIcon(Resources.getImage(Resources.SINK_OFF,
				30, 30, true));
		ImageIcon sink2 = new ImageIcon(Resources.getImage(Resources.SINK_ON,
				30, 30, true));
		ImageIcon sink3 = new ImageIcon(Resources.getImage(
				Resources.SINK_CLICK, 30, 30, true));
		IconButton sink = new IconButton(null, sink2, sink1, sink3);

		inputSelector = new PathSelector(new LabelOptions<SourceFormat>(source,
				SourceFormat.class));
		outputSelector = new PathSelector(new LabelOptions<SinkFormat>(sink,
				SinkFormat.class));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 1;
		c.gridy = 1;

		add(inputSelector, c);

		c.gridy++;

		add(outputSelector, c);

		JPanel bottomButtons = new JPanel();

		IconButton run = createIconButton(IconButton.Type.RUN, 24, "Run", null);
		IconButton config = createIconButton(IconButton.Type.CONFIG, 24,
				"Configuration", new ShowConfigurationPanelAction());

		bottomButtons.add(run);
		bottomButtons.add(config);

		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;

		add(bottomButtons, c);

		configuration = new ConfigurationPanel(tool);

		c.gridwidth = 2;
		c.gridy++;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		// add(configuration, c);
		// configuration.setVisible(false);

		frame.setContentPane(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frameDimension = frame.getPreferredSize();
		frame.setMinimumSize(frameDimension);
		frame.setIconImage(getImage(Resources.GS_ICON, 32, 32, false));
	}

	public JFrame display() {
		frame.setVisible(true);
		return frame;
	}

	protected void packMe() {
	}

	protected void paintComponent(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setPaint(Resources.getBackgroundPaint());
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	class ConfigurationPanel extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 141408222742689371L;

		public ConfigurationPanel(Tool tool) {
			super(frame, "Configuration", false);
			setContentPane(new MainTitledPanel("Options for " + tool.getName(),
					100));

			HashSet<String> filter = new HashSet<String>();
			filter.add("help");
			filter.add("source");
			filter.add("source-format");
			filter.add("sink");
			filter.add("sink-format");

			int maxWidth = 0;
			for (ToolOption option : tool.getEachToolOption()) {
				if (!filter.contains(option.key)) {
					JLabel l = new JLabel(option.key);
					maxWidth = Math.max(maxWidth, l.getPreferredSize().width);
				}
			}

			GridBagConstraints c = new GridBagConstraints();

			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = 2;
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(2, 5, 3, 5);

			for (ToolOption option : tool.getEachToolOption()) {
				if (!filter.contains(option.key)) {
					OptionSetter setter = OptionSetter.create(option, maxWidth);
					add(setter, c);

					c.gridy++;
				}
			}

			pack();
		}

		public void display() {
			setLocation(frame.getWidth() / 2, frame.getHeight() / 2);
			setVisible(true);
		}

		public String[] getOptionsAsArguments() {
			return null;
		}
	}

	class ShowConfigurationPanelAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1772989484551007839L;

		public void actionPerformed(ActionEvent e) {
			configuration.display();
		}
	}

	class ShowAboutAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 597282438356224169L;

		AboutDialog dialog;

		public ShowAboutAction() {
			dialog = new AboutDialog(ToolGUI.this.tool);
		}

		public void actionPerformed(ActionEvent e) {
			dialog.setVisible(true);
		}

	}

	class AboutDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4468824723350853466L;

		public AboutDialog(Tool tool) {
			super(ToolGUI.this.frame, "About");

			setIconImage(getImage(Resources.GS_ICON, 32, 32, false));
			setResizable(false);
			setContentPane(new MainTitledPanel(tool.getName(), 100));

			GridBagConstraints c = new GridBagConstraints();

			JTextPane description = new JTextPane();
			SimpleAttributeSet sa = new SimpleAttributeSet();
			StyleConstants.setAlignment(sa, StyleConstants.ALIGN_JUSTIFIED);
			description.setText(tool.getDescription());
			description.setOpaque(false);
			description.setEditable(false);
			description.setPreferredSize(new Dimension(300, -1));
			description.setFont(Resources.getRegularFont(14.0f));
			description.getStyledDocument().setParagraphAttributes(0,
					description.getStyledDocument().getLength(), sa, false);
			JScrollPane scroller = new JScrollPane(description);
			scroller.setPreferredSize(new Dimension(350, 200));
			scroller.setBorder(null);
			scroller.setOpaque(false);
			scroller.getViewport().setOpaque(false);

			c.gridy = 1;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(5, 0, 15, 5);

			add(scroller, c);

			pack();
		}
	}

	public static void main(String... args) {
		ToolGUI gui = new ToolGUI(new Generate());
		gui.display();
	}
}
