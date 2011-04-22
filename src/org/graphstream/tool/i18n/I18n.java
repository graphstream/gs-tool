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
package org.graphstream.tool.i18n;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Internationalization tool for GraphStream. Internationalization (i18n) is a
 * main step to provide to users the best ergonomics using a software. i18n
 * leads to a better understanding of what is happening in a human to machine
 * interface.
 * 
 * We use here xml file to make a mapping key/values. Key are an unique
 * identifiant to a string in the program. The associated value is an human
 * understandable string in the language of the current locale.
 * 
 * @author Guilhelm Savin
 * 
 */
public class I18n {

	/**
	 * Get the resource bundle for an object supporting i18n. Default locale is
	 * used to determine language.
	 * 
	 * @param i18nObject
	 * @return
	 */
	public static ResourceBundle load(I18nSupport i18nObject) {
		return load(i18nObject.getDomain(), i18nObject.getLocale());
	}

	/**
	 * Get the resource bundle for a domain. Default locale is used to determine
	 * language.
	 * 
	 * @param domain
	 *            base name of the bundle
	 * @return a bundle
	 */
	public static ResourceBundle load(String domain) {
		return load(domain, Locale.getDefault());
	}

	/**
	 * Get the resource bundle for a domain. Default locale is used to determine
	 * language.
	 * 
	 * @param domain
	 *            base name of the bundle
	 * @param locale
	 *            the locale to use
	 * @return a bundle
	 */
	public static ResourceBundle load(String domain, Locale locale) {
		return ResourceBundle.getBundle(domain, locale, new Controler());
	}

	/**
	 * Get and format a string for a given key. Objects are inserted in the
	 * {index+1} location. For example, if we have the association "mykey" to
	 * "this example is really {1}", calling
	 * <code>_(bundle, "mykey", "fun")</code> will produce
	 * "this test is really fun".
	 * 
	 * @param i18nBundle
	 *            the bundle
	 * @param key
	 *            the key of the string
	 * @param objects
	 *            objects to insert in the string if any
	 * @return a formatted string modeling key
	 */
	public static String _(ResourceBundle i18nBundle, String key,
			String... objects) {
		String string = i18nBundle.getString(key);

		if (objects != null) {
			for (int i = 0; i < objects.length; i++)
				string = string.replace(String.format("{%d}", i + 1),
						objects[i]);
		}

		return string.replace("\n", "").replace("\\n", "\n")
				.replaceAll("\\s{2,}", " ");
	}

	private static class Controler extends ResourceBundle.Control {
		public List<String> getFormats(String baseName) {
			if (baseName == null)
				throw new NullPointerException();
			return Arrays.asList("xml");
		}

		public ResourceBundle newBundle(String baseName, Locale locale,
				String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException,
				IOException {
			if (baseName == null || locale == null || format == null
					|| loader == null)
				throw new NullPointerException();

			ResourceBundle bundle = null;

			if (format.equals("xml")) {
				String bundleName = toBundleName(baseName, locale);
				String resourceName = toResourceName(bundleName, format);

				InputStream stream = null;

				if (reload) {
					URL url = loader.getResource(resourceName);
					if (url != null) {
						URLConnection connection = url.openConnection();
						if (connection != null) {
							// Disable caches to get fresh data for
							// reloading.
							connection.setUseCaches(false);
							stream = connection.getInputStream();
						}
					}
				} else {
					stream = loader.getResourceAsStream(resourceName);
				}

				if (stream != null) {
					BufferedInputStream bis = new BufferedInputStream(stream);
					bundle = new XMLResourceBundle(bis);
					bis.close();
				}
			}
			return bundle;
		}
	}

	private static class XMLResourceBundle extends ResourceBundle {
		private Properties props;

		XMLResourceBundle(InputStream stream) throws IOException {
			props = new Properties();
			props.loadFromXML(stream);

			if (props.containsKey("__parent__"))
				setParent(ResourceBundle.getBundle(
						props.getProperty("__parent__"), new Controler()));
		}

		protected Object handleGetObject(String key) {
			return props.getProperty(key);
		}

		@SuppressWarnings("unchecked")
		public Enumeration<String> getKeys() {
			return (Enumeration<String>) props.propertyNames();
		}
	}
}
