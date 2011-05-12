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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.graphstream.tool.ToolGUI;
import org.graphstream.tool.ToolOption;
import org.graphstream.tool.ToolsCommon;
import org.graphstream.tool.i18n.I18n;

public class ConfigurationPanel extends JDialog implements ToolsCommon {
	/**
	 * 
	 */
	private static final long serialVersionUID = 141408222742689371L;

	DefaultComboBoxModel model;
	int maxWidth;
	GridBagConstraints constraints;
	HashMap<String, ToolOption> options;
	JPanel optionContainer;
	HashMap<String, OptionSetter> setters;

	public ConfigurationPanel(Frame frame, boolean modal, String dialogTitle,
			String panelTitle, ResourceBundle strings, ToolOption[] options,
			int maxWidth) {
		super(frame, dialogTitle, modal);

		this.options = new HashMap<String, ToolOption>();
		this.setters = new HashMap<String, OptionSetter>();
		this.maxWidth = maxWidth;

		setContentPane(new MainTitledPanel(panelTitle, 100));

		model = new DefaultComboBoxModel();

		if (options != null) {
			for (int i = 0; i < options.length; i++) {
				this.options.put(options[i].key, options[i]);
				model.addElement(options[i].key);
			}
		}

		JComponent keySelection = null;
		IconButton add = null;

		keySelection = new JComboBox(model);
		add = createIconButton(IconButton.Type.ADD, ToolGUI.ICON_SIZE, null,
				new AddOptionAction());

		GridBagConstraints c = new GridBagConstraints();

		JLabel l = new JLabel(String.format("%s:",
				I18n._(strings, "configuration:available_options")));

		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 5, 0, 5);

		add(l, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(2, 5, 2, 5);

		add(keySelection, c);

		c.gridx = 3;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.insets = new Insets(1, 1, 1, 1);

		add(add, c);

		optionContainer = new JPanel();
		optionContainer.setOpaque(false);
		optionContainer.setLayout(new GridBagLayout());

		JScrollPane scroller = new JScrollPane(optionContainer);

		scroller.setPreferredSize(new Dimension(300, 200));
		scroller.setBorder(null);
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);

		c.gridwidth = 3;
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(1, 5, 1, 1);

		add(scroller, c);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(2, 2, 2, 2);

		constraints = (GridBagConstraints) c.clone();

		pack();
	}

	public void display() {
		setVisible(true);
	}

	public String[] getOptionsAsArguments() {
		return getOptionsAsList().toArray(new String[0]);
	}

	public List<String> getOptionsAsList() {
		LinkedList<String> args = new LinkedList<String>();

		for (OptionSetter setter : setters.values())
			args.add(String.format("--%s%s%s", setter.option.key,
					setter.option.type == OptionType.FLAG ? "" : "=",
					setter.value));

		return args;
	}

	class AddOptionAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 172686762442438415L;

		public void actionPerformed(ActionEvent e) {
			if (model.getSelectedItem() != null) {
				String key = model.getSelectedItem().toString();
				ToolOption option = options.get(key);
				OptionSetter setter = OptionSetter.create(option, maxWidth,
						new RemoveOptionAction(key));

				if (setter != null) {
					model.removeElement(key);
					ConfigurationPanel.this.repaint();

					setters.put(key, setter);
					optionContainer.add(setter, constraints);
				}
			}
		}
	}

	class RemoveOptionAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2972511811223993562L;

		String key;

		RemoveOptionAction(String key) {
			this.key = key;
		}

		public void actionPerformed(ActionEvent e) {
			OptionSetter setter = setters.get(key);

			if (setter != null) {
				setters.remove(key);
				optionContainer.remove(setter);
				model.addElement(key);
				ConfigurationPanel.this.repaint();
			}
		}
	}
}