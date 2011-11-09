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
package org.graphstream.tool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.graphstream.tool.gui.ConfigurationPanel;
import org.graphstream.tool.gui.IconButton;
import org.graphstream.tool.gui.MainTitledPanel;
import org.graphstream.tool.gui.PathSelector;
import org.graphstream.tool.gui.ReaderFromWindow;
import org.graphstream.tool.gui.Resources;
import org.graphstream.tool.gui.WriterToWindow;
import org.graphstream.tool.i18n.I18n;
import org.graphstream.tool.i18n.I18nSupport;

import static org.graphstream.tool.gui.IconButton.createIconButton;
import static org.graphstream.tool.gui.Resources.getImage;

// -Dsun.java2d.opengl=true -Dawt.useSystemAAFontSettings=on|lcd

public class ToolGUI extends MainTitledPanel implements ToolsCommon,
		ToolRunnerListener, I18nSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8646940433915429831L;

	public static final int ICON_SIZE = 28;

	protected static final Color COLOR_1 = new Color(74, 90, 123);
	protected static final Color COLOR_2 = new Color(52, 75, 121);

	public static void error(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title,
				JOptionPane.ERROR_MESSAGE);
	}

	protected Tool tool;
	protected JFrame frame;
	protected PathSelector inputSelector, outputSelector;
	protected ConfigurationPanel configuration;
	protected Dimension frameDimension;
	protected ComboBoxModel sourceFormat, sinkFormat;
	protected String sourceOptions, sinkOptions;
	protected ResourceBundle i18n;

	public ToolGUI(Tool tool) {
		super(tool.getName());

		this.tool = tool;
		this.i18n = I18n.load(this);

		build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.tool.i18n.I18nSupport#getDomain()
	 */
	public String getDomain() {
		return "org.graphstream.tool.i18n.gui";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.graphstream.tool.i18n.I18nSupport#setLocale(java.util.Locale)
	 * 
	 * @see java.awt.Component#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale l) {
		super.setLocale(l);
		i18n = I18n.load(this);
	}

	/**
	 * Print the i18n key inserting objects.
	 * 
	 * @param key
	 *            key of the i18n entry
	 * @param objects
	 *            objects to insert in the string
	 * @return the well formatted representation of the key
	 * 
	 * @see org.graphstream.tool.i18n.I18n#_(ResourceBundle, String, String...)
	 */
	protected String i18n(String key, String... objects) {
		return I18n._(i18n, key, objects);
	}

	protected void build() {
		/*
		 * Build the frame
		 */
		frame = new JFrame(String.format("GraphStream : %s", tool.getName()));

		GridBagConstraints c = new GridBagConstraints();
		IconButton about = createIconButton(IconButton.Type.ABOUT, 28, null,
				new ShowAboutAction());

		about.setToolTipText(i18n("tips:about"));

		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(10, 5, 15, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHEAST;

		add(about, c);

		IconButton source = createIconButton(IconButton.Type.SOURCE, 30, null,
				new SetSourceOptionsAction());
		IconButton sink = createIconButton(IconButton.Type.SINK, 30, null,
				new SetSinkOptionsAction());

		source.setToolTipText(i18n("tips:configure_source"));
		sink.setToolTipText(i18n("tips:configure_sink"));

		JComboBox sourceFormats = new JComboBox(SourceFormat.values());
		JComboBox sinkFormats = new JComboBox(SinkFormat.values());

		sourceFormats.setToolTipText(i18n("tips:choose_source_format"));
		sinkFormats.setToolTipText(i18n("tips:choose_sink_format"));

		this.sourceFormat = sourceFormats.getModel();
		this.sinkFormat = sinkFormats.getModel();

		int w = Math.max(sourceFormats.getPreferredSize().width,
				sinkFormats.getPreferredSize().width);

		int h = Math.max(sourceFormats.getPreferredSize().height,
				sinkFormats.getPreferredSize().height);

		sourceFormats.setPreferredSize(new Dimension(w, h));
		sinkFormats.setSize(new Dimension(w, h));

		inputSelector = new PathSelector(source, sourceFormats);
		outputSelector = new PathSelector(sink, sinkFormats);

		inputSelector.setFindToolTipText(i18n("tips:choose_source"));
		outputSelector.setFindToolTipText(i18n("tips:choose_sink"));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 1;
		c.gridy = 1;

		if (tool.hasInput) {
			add(inputSelector, c);
			c.gridy++;
		}

		if (tool.hasOutput)
			add(outputSelector, c);

		JPanel bottomButtons = new JPanel();

		IconButton run = createIconButton(IconButton.Type.RUN, 24,
				i18n("button:run:label"), new RunAction());
		IconButton config = createIconButton(IconButton.Type.CONFIG, 24,
				i18n("button:configuration:label"),
				new ShowConfigurationPanelAction());

		run.setToolTipText(i18n("tips:run"));
		config.setToolTipText(i18n("tips:configuration"));

		bottomButtons.add(run);
		bottomButtons.add(config);

		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy++;
		c.anchor = GridBagConstraints.SOUTHEAST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;

		add(bottomButtons, c);

		configuration = createConfigurationPanel();

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

	protected ConfigurationPanel createConfigurationPanel() {
		int maxWidth = 0;
		HashSet<String> filter = new HashSet<String>();
		LinkedList<ToolOption> options = new LinkedList<ToolOption>();

		/*
		 * Following options are set in an other way.
		 */
		filter.add("help");
		filter.add("source");
		filter.add("source-format");
		filter.add("source-options");
		filter.add("sink");
		filter.add("sink-format");
		filter.add("sink-options");

		for (ToolOption option : tool.getEachToolOption()) {
			if (!filter.contains(option.key)) {
				JLabel l = new JLabel(option.key);
				maxWidth = Math.max(maxWidth, l.getPreferredSize().width);
				options.add(option);
			}
		}

		ToolOption[] optionsArray = options.toArray(new ToolOption[0]);

		return new ConfigurationPanel(frame, false,
				i18n("frame:configuration:title"), i18n(
						"frame:configuration:paneltitle", tool.getName()),
				i18n, optionsArray, maxWidth);
	}

	public JFrame display() {
		frame.setVisible(true);
		return frame;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.tool.gui.MainTitledPanel#paintComponent(java.awt.Graphics
	 * )
	 */
	protected void paintComponent(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.setPaint(Resources.getBackgroundPaint());
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.tool.ToolRunnerListener#executionStart(org.graphstream
	 * .tool.Tool)
	 */
	public void executionStart(Tool t) {
		setToolGUIEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.tool.ToolRunnerListener#initializationFailed(org.graphstream
	 * .tool.Tool, org.graphstream.tool.ToolInitializationException)
	 */
	public void initializationFailed(Tool t, ToolInitializationException e) {
		error(i18n("error:initialization_failed:title"), e.getMessage());
		setToolGUIEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.tool.ToolRunnerListener#executionFailed(org.graphstream
	 * .tool.Tool, org.graphstream.tool.ToolExecutionException)
	 */
	public void executionFailed(Tool t, ToolExecutionException e) {
		error(i18n("error:execution_failed:title"), e.getMessage());
		setToolGUIEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.graphstream.tool.ToolRunnerListener#executionSuccess(org.graphstream
	 * .tool.Tool)
	 */
	public void executionSuccess(Tool t) {
		setToolGUIEnabled(true);
	}

	protected void setToolGUIEnabled(boolean on) {
		setEnabled(on);
		LinkedList<Component> components = new LinkedList<Component>();
		components.add(this);

		while (components.size() > 0) {
			Component c = components.poll();
			c.setEnabled(on);

			if (c instanceof Container) {
				Container cont = (Container) c;

				for (int i = 0; i < cont.getComponentCount(); i++)
					components.add(cont.getComponent(i));
			}
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
			super(ToolGUI.this.frame, i18n("frame:about:title"));

			setIconImage(getImage(Resources.GS_ICON, 32, 32, false));
			setResizable(false);
			setContentPane(new MainTitledPanel(i18n("frame:about:paneltitle",
					tool.getName()), 100));

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

	class RunAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6986878124049013848L;

		public void actionPerformed(ActionEvent e) {
			if (false && tool.hasInput && inputSelector.getPath().length() == 0) {
				error(i18n("error:noinput:title"),
						i18n("error:noinput:description"));
				return;
			}

			if (tool.hasInput && inputSelector.getPath().length() == 0)
				tool.setDefaultInput(new ReaderFromWindow(300, 200));

			if (tool.hasOutput && outputSelector.getPath().length() == 0)
				tool.setDefaultOutput(new WriterToWindow(true, 300, 200));

			LinkedList<String> args = new LinkedList<String>();

			if (tool.hasInput) {
				if (inputSelector.getPath().length() > 0)
					args.add(String.format("--source=%s",
							inputSelector.getPath()));

				args.add(String.format("--source-format=%s",
						sourceFormat.getSelectedItem()));

				if (sourceOptions != null)
					args.add(String
							.format("--source-options=%s", sourceOptions));
			}

			if (tool.hasOutput) {
				if (outputSelector.getPath().length() > 0)
					args.add(String.format("--sink=%s",
							outputSelector.getPath()));

				args.add(String.format("--sink-format=%s",
						sinkFormat.getSelectedItem()));

				if (sinkOptions != null)
					args.add(String.format("--sink-options=%s", sinkOptions));
			}

			args.addAll(configuration.getOptionsAsList());

			ToolRunner runner = new ToolRunner(tool,
					args.toArray(new String[0]));

			runner.addListener(ToolGUI.this);
			runner.start();
		}
	}

	class SetSourceOptionsAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5550179564623219227L;

		public void actionPerformed(ActionEvent e) {
			String v = JOptionPane.showInputDialog(null,
					String.format("%s\n%s", i18n("option:source_options"),
							i18n("usekeyval")),
					i18n("frame:source_options:title"),
					JOptionPane.QUESTION_MESSAGE);

			if (v != null)
				sourceOptions = v;
		}
	}

	class SetSinkOptionsAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5550179564623219227L;

		public void actionPerformed(ActionEvent e) {
			String v = JOptionPane.showInputDialog(null, String.format(
					"%s\n%s", i18n("option:sink_options"), i18n("usekeyval")),
					i18n("frame:sink_options:title"),
					JOptionPane.QUESTION_MESSAGE);

			if (v != null)
				sinkOptions = v;
		}
	}

	public static enum ToolType {
		GENERATE, CONVERT, PLAYER
	}

	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ToolType tt = (ToolType) JOptionPane.showInputDialog(null,
						"Choose tool type:", "GraphStream Tool",
						JOptionPane.QUESTION_MESSAGE, null, ToolType.values(),
						ToolType.GENERATE);

				if (tt != null) {
					Tool t = null;

					switch (tt) {
					case GENERATE:
						t = new Generate();
						break;
					case CONVERT:
						t = new Convert();
						break;
					case PLAYER:
						t = new Player();
						break;
					}
					ToolGUI gui = new ToolGUI(t);
					gui.display();
				}
			}
		});
	}
}
