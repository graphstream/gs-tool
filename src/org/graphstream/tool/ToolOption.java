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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class ToolOption implements ToolsCommon {
	public final String key;
	public final String description;
	public final boolean optional;
	public final OptionType type;

	public ToolOption(String key, String description, boolean optional,
			OptionType type) {
		this.key = key;
		this.description = description;
		this.optional = optional;
		this.type = type;
	}

	public static class ToolEnumOption<T extends Enum<T>> extends ToolOption {
		public final Class<T> choices;

		public ToolEnumOption(String key, String description, boolean optional,
				Class<T> choices) {
			super(key, description, optional, OptionType.ENUM);
			this.choices = choices;
		}
	}

	public static class ParsedOptions implements Iterable<String> {
		HashMap<String, String> keyValues;
		LinkedList<String> notOptions;

		public ParsedOptions() {
			keyValues = new HashMap<String, String>();
			notOptions = new LinkedList<String>();
		}

		public Iterator<String> iterator() {
			return keyValues.keySet().iterator();
		}

		public void registerOption(String key, String value) {
			keyValues.put(key, value);
		}

		public void registerNotOption(String arg) {
			notOptions.add(arg);
		}

		public CheckResult check(ToolOption opt) {
			if (!opt.optional && !contains(opt.key))
				return CheckResult.MISSING;

			if (!contains(opt.key))
				return CheckResult.VALID;

			String v = get(opt.key);

			if (v == null) {
				if (opt.type != OptionType.FLAG)
					return CheckResult.INVALID;
				else
					return CheckResult.VALID;
			}

			if (opt instanceof ToolEnumOption)
				return check((ToolEnumOption<?>) opt);

			boolean match = false;

			switch (opt.type) {
			case BOOL:
				match = v.matches(BOOL_MATCHER);
				break;
			case INT:
				match = v.matches(INT_MATCHER);
				break;
			case REAL:
				match = v.matches(REAL_MATCHER);
				break;
			case STRING:
				match = true;
				break;
			case OPTIONS:
				match = v.matches(OPTIONS_MATCHER);
				break;
			}

			if (match)
				return CheckResult.VALID;

			return CheckResult.INVALID;
		}

		public CheckResult check(ToolEnumOption<?> teo) {
			if (contains(teo.key)) {
				String v = get(teo.key);

				if (v == null)
					return CheckResult.INVALID;

				if (v.equals("?"))
					return CheckResult.HELP;

				for (Enum<?> t : teo.choices.getEnumConstants()) {
					if (t.name().equals(v))
						return CheckResult.VALID;
				}

				return CheckResult.INVALID;
			} else if (!teo.optional)
				return CheckResult.MISSING;

			return CheckResult.VALID;
		}

		public boolean checkNotOptions(int s) {
			return notOptions.size() == s;
		}

		public boolean isHelpNeeded() {
			return keyValues.containsKey(ToolsCommon.HELP_KEY);
		}

		public boolean contains(String key) {
			return keyValues.containsKey(key);
		}

		public int getInt(String key) {
			return Integer.parseInt(keyValues.get(key));
		}

		public long getLong(String key) {
			return Long.parseLong(keyValues.get(key));
		}

		public double getDouble(String key) {
			return Double.parseDouble(keyValues.get(key));
		}

		public boolean getBoolean(String key) {
			return Boolean.parseBoolean(keyValues.get(key));
		}

		public String[][] getOptions(String key) {
			return Tools.getKeyValue(keyValues.get(key));
		}

		public String get(String key) {
			return keyValues.get(key);
		}

		public <T extends Enum<T>> T getEnum(String key, Class<T> type) {
			String value = keyValues.get(key);

			for (T t : type.getEnumConstants())
				if (t.name().equals(value))
					return t;

			return null;
		}

		public int getNotOptionsCount() {
			return notOptions.size();
		}

		public String getNotOption(int i) {
			return notOptions.get(i);
		}
	}
}
