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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

// -Dsun.java2d.opengl=true -Dawt.useSystemAAFontSettings=on

public class ToolGUI extends JPanel implements ToolsCommon {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8646940433915429831L;

	protected static final String GS_LOGO = "org/graphstream/tool/resources/logo.png";

	protected static final String LINES = "org/graphstream/tool/resources/lines.png";

	protected static final String ABOUT_OFF = "org/graphstream/tool/resources/about.png";
	protected static final String ABOUT_ON = "org/graphstream/tool/resources/about_on.png";
	protected static final String ABOUT_CLICK = "org/graphstream/tool/resources/about_click.png";

	protected static final String CONFIG_OFF = "org/graphstream/tool/resources/config.png";
	protected static final String CONFIG_ON = "org/graphstream/tool/resources/config_on.png";
	protected static final String CONFIG_CLICK = "org/graphstream/tool/resources/config_click.png";

	protected static final String FIND_OFF = "org/graphstream/tool/resources/find.png";
	protected static final String FIND_ON = "org/graphstream/tool/resources/find_on.png";
	protected static final String FIND_CLICK = "org/graphstream/tool/resources/find_click.png";

	protected static final String FONT_BOLD = "org/graphstream/tool/resources/Ubuntu-B.ttf";
	protected static final String FONT_REGULAR = "org/graphstream/tool/resources/Ubuntu-R.ttf";

	protected static final String SOURCE_OFF = "org/graphstream/tool/resources/source.png";
	protected static final String SOURCE_ON = "org/graphstream/tool/resources/source_on.png";
	protected static final String SOURCE_CLICK = "org/graphstream/tool/resources/source_click.png";

	protected static final String SINK_OFF = "org/graphstream/tool/resources/sink.png";
	protected static final String SINK_ON = "org/graphstream/tool/resources/sink_on.png";
	protected static final String SINK_CLICK = "org/graphstream/tool/resources/sink_click.png";

	protected static final int ICON_SIZE = 28;

	protected static Font fontBold = null;
	protected static Font fontRegular = null;

	static {
		try {
			InputStream boldInput = ToolGUI.class.getClassLoader()
					.getResourceAsStream(FONT_BOLD);
			InputStream regularInput = ToolGUI.class.getClassLoader()
					.getResourceAsStream(FONT_REGULAR);

			if (boldInput == null) {
				System.err.printf("Can not find font resource.\n");
				System.exit(1);
			}

			if (regularInput == null) {
				System.err.printf("Can not find font resource.\n");
				System.exit(1);
			}

			fontBold = Font.createFont(Font.TRUETYPE_FONT, boldInput);
			fontRegular = Font.createFont(Font.TRUETYPE_FONT, regularInput);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected Tool tool;
	protected JFrame frame;
	protected PathSelector inputSelector, outputSelector;
	protected ConfigurationPanel configuration;

	public ToolGUI(Tool tool) {
		this.tool = tool;
		build();
	}

	protected void build() {
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		ImageIcon logo = new ImageIcon(getImage(GS_LOGO, 200, 200, true));

		c.gridwidth = 1;
		c.gridheight = 8;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTH;

		add(new JLabel(logo), c);

		TitleLabel title = new TitleLabel(tool.getName());

		c.weightx = 1.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 0, 15, 0);

		add(title, c);

		ImageIcon about1 = new ImageIcon(getImage(ABOUT_OFF, 28, 28, false));
		ImageIcon about2 = new ImageIcon(getImage(ABOUT_ON, 28, 28, false));
		ImageIcon about3 = new ImageIcon(getImage(ABOUT_CLICK, 28, 28, false));
		IconButton about = new IconButton(new ShowAboutAction(), about2,
				about1, about3);

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

		ImageIcon source1 = new ImageIcon(getImage(SOURCE_OFF, 30, 30, true));
		ImageIcon source2 = new ImageIcon(getImage(SOURCE_ON, 30, 30, true));
		ImageIcon source3 = new ImageIcon(getImage(SOURCE_CLICK, 30, 30, true));
		IconButton source = new IconButton(null, source2, source1, source3);

		ImageIcon sink1 = new ImageIcon(getImage(SINK_OFF, 30, 30, true));
		ImageIcon sink2 = new ImageIcon(getImage(SINK_ON, 30, 30, true));
		ImageIcon sink3 = new ImageIcon(getImage(SINK_CLICK, 30, 30, true));
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

		ImageIcon config1 = new ImageIcon(getImage(CONFIG_OFF, 24, 24, false));
		ImageIcon config2 = new ImageIcon(getImage(CONFIG_ON, 24, 24, false));
		ImageIcon config3 = new ImageIcon(getImage(CONFIG_CLICK, 24, 24, false));
		IconButton config = new IconButton(new ShowConfigurationPanelAction(),
				config2, config1, config3, "Configuration");

		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;

		add(config, c);

		configuration = new ConfigurationPanel(tool);

		c.gridwidth = 2;
		c.gridy++;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		add(configuration, c);
		configuration.setVisible(false);
	}

	protected static Image getImage(String url, int width, int height,
			boolean keepAspect) {
		InputStream in = ToolGUI.class.getClassLoader()
				.getResourceAsStream(url);
		BufferedImage image;

		try {
			image = ImageIO.read(in);
		} catch (IOException e) {
			image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
		}

		boolean validWidth = width == image.getWidth();
		boolean validHeight = height == image.getHeight();

		if ((validWidth && !validHeight && !keepAspect)
				|| (validHeight && !validWidth && !keepAspect)
				|| (!validWidth && !validHeight)) {
			if (keepAspect) {
				double ratio = image.getWidth() / (double) image.getHeight();

				width = validWidth ? width : (int) (ratio * height);
				height = validWidth ? (int) (width / ratio) : height;
			}

			return image.getScaledInstance(width, height,
					BufferedImage.SCALE_SMOOTH);
		}

		return image;
	}

	public JFrame display() {
		if (frame == null) {
			frame = new JFrame(
					String.format("GraphStream : %s", tool.getName()));
			frame.setContentPane(this);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			packMe();
		}

		frame.setVisible(true);

		return frame;
	}

	protected void packMe() {
		if (frame != null) {
			frame.pack();
			frame.setMinimumSize(frame.getPreferredSize());
		}
	}

	public static class ConfigurationPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 141408222742689371L;

		public ConfigurationPanel(Tool tool) {
			setPreferredSize(new Dimension(200, 100));
			setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		}
	}

	public static class IconButton extends JLabel implements MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4729909769134257616L;

		protected Icon onIcon;
		protected Icon offIcon;
		protected Icon clickIcon;

		protected Action action;
		protected String command;

		protected int eventId;

		public IconButton(Action action, Icon on, Icon off, Icon click,
				String label) {
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

	public static class TitleLabel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4096602903174491162L;

		Image background;

		public TitleLabel(String label) {
			GridBagConstraints c = new GridBagConstraints();
			JLabel l = new JLabel(label, JLabel.LEFT);

			setLayout(new GridBagLayout());

			l.setFont(fontBold.deriveFont(25.0f));
			l.setOpaque(false);

			c.insets = new Insets(5, 10, 5, 10);
			c.weightx = 1.0;
			c.fill = GridBagConstraints.HORIZONTAL;

			add(l, c);

			try {
				background = ImageIO.read(getClass().getClassLoader()
						.getResourceAsStream(LINES));
			} catch (Exception e) {
				System.err.printf("Can not find resource.\n");
				background = null;
			}
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

	public static class PathSelector extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7164150016018758398L;

		static ImageIcon findOff = new ImageIcon(getImage(FIND_OFF, ICON_SIZE,
				ICON_SIZE, false));
		static ImageIcon findOn = new ImageIcon(getImage(FIND_ON, ICON_SIZE,
				ICON_SIZE, false));
		static ImageIcon findClick = new ImageIcon(getImage(FIND_CLICK,
				ICON_SIZE, ICON_SIZE, false));

		static Color border = new Color(0, 0, 0);
		static Color background = new Color(100, 100, 100);
		static Color text = new Color(240, 240, 240);
		static Color labelback = new Color(30, 30, 30);

		JTextField path;
		IconButton find;
		JComponent label;

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

		protected void init(JComponent l) {
			setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			label = l;

			path = new JTextField(30);
			find = new IconButton(new ChoosePathAction(this), findOn, findOff,
					findClick);

			path.setForeground(text);
			path.setBorder(null);
			path.setOpaque(false);
			path.setFont(fontRegular.deriveFont(14.0f));

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

			if (g instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D) g;

				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			}

			g.setColor(labelback);
			g.fillRoundRect(0, 0, 14, getHeight() - 1, 7, 7);
			g.fillRect(7, 0, s - 7, getHeight() - 1);
			g.setColor(background);
			g.fillRect(s, 0, getWidth() - 8 - s, getHeight() - 1);
			g.fillRoundRect(getWidth() - 15, 0, 14, getHeight() - 1, 7, 7);
			g.setColor(border);
			g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
		}

	}

	public static class LabelOptions<T extends Enum<T>> extends JPanel
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

			GridBagConstraints c = new GridBagConstraints();

			optionsBox = new JComboBox(options.getEnumConstants());
			optionsBox.setBorder(null);
			optionsBox.setFont(fontRegular.deriveFont(11.0f));
			optionsBox.setPreferredSize(new Dimension(75, 20));
			optionsBox.setSize(optionsBox.getPreferredSize());

			c.gridwidth = 1;

			add(icon, c);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(0, 5, 0, 0);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;

			add(optionsBox, c);

			optionsBox.setVisible(false);

			icon.addMouseListener(this);
		}

		public void mouseClicked(MouseEvent e) {
			optionsBox.setVisible(!optionsBox.isVisible());
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

	public static class ChoosePathAction extends AbstractAction {
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

	class ShowConfigurationPanelAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1772989484551007839L;

		public void actionPerformed(ActionEvent e) {
			configuration.setVisible(!configuration.isVisible());
			ToolGUI.this.packMe();
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

	public static class AboutDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4468824723350853466L;

		public AboutDialog(Tool tool) {
			setTitle("About");
			setResizable(false);
			setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			ImageIcon logo = new ImageIcon(getImage(GS_LOGO, 100, 100, true));

			c.gridwidth = 1;
			c.gridheight = 2;
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.NORTH;
			c.insets = new Insets(5, 5, 5, 10);

			add(new JLabel(logo), c);

			TitleLabel title = new TitleLabel(tool.getName());

			c.weightx = 1.0;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.gridx = 1;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(5, 0, 15, 0);

			add(title, c);

			JTextPane description = new JTextPane();
			SimpleAttributeSet sa = new SimpleAttributeSet();
			StyleConstants.setAlignment(sa, StyleConstants.ALIGN_JUSTIFIED);
			description.setText(tool.getDescription());
			description.setOpaque(false);
			description.setEditable(false);
			description.setPreferredSize(new Dimension(300, -1));
			description.setFont(fontRegular.deriveFont(14.0f));
			description.getStyledDocument().setParagraphAttributes(0,
					description.getStyledDocument().getLength(), sa, false);
			JScrollPane scroller = new JScrollPane(description);
			scroller.setPreferredSize(new Dimension(350, 200));

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
