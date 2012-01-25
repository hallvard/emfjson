/*******************************************************************************
 * Copyright (c) 2011 Guillaume Hillairet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Guillaume Hillairet - initial API and implementation
 *******************************************************************************/
package org.eclipselabs.emfjson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.PlatformResourceURIHandlerImpl;
import org.eclipse.emf.ecore.resource.impl.URIHandlerImpl;
import org.eclipselabs.emfjson.internal.JsOutputStream;

/**
 *
 * @author ghillairet
 *
 */
@Deprecated
public class JsURIHandlerImpl extends URIHandlerImpl {

	public JsURIHandlerImpl() {}
	
	@Override
	public boolean canHandle(URI uri) {
		Map<String,?> result = getAttributes(uri, Collections.<Object,Object> emptyMap());
		System.out.println(result);
		return true;
	}

	@Override
	public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
//		return new JsInputStream(uri, options);
		return null;
	}

	@Override
	public OutputStream createOutputStream(final URI uri, final Map<?, ?> options) throws IOException {
		final Map<Object, Object> response = getResponse(options);

		OutputStream result = null;

		if (uri.isPlatform()) {
			result = new JsOutputStream(options) {
				@Override
				public void close() throws IOException {
					try {
						OutputStream out = new PlatformResourceURIHandlerImpl().createOutputStream(uri, options);
						this.writer.getDelegate().writeValue(out, currentRoot);
						out.close();
					}
					finally {
						response.put(
								URIConverter.RESPONSE_TIME_STAMP_PROPERTY, 
								getAttributes(uri, options).get(URIConverter.ATTRIBUTE_TIME_STAMP));
					}
				}
			};
		} else if (uri.isFile()) {
			result = new JsOutputStream(options) {
				@Override
				public void close() throws IOException {
					try {
						File file = new File(uri.toFileString());
						this.writer.getDelegate().writeValue(file, currentRoot);
						super.close();
					}
					finally {
						response.put(
								URIConverter.RESPONSE_TIME_STAMP_PROPERTY, 
								getAttributes(uri, options).get(URIConverter.ATTRIBUTE_TIME_STAMP));
					}
				}
			};
		} else {
			result = new JsOutputStream(options) {
				@Override
				public void close() throws IOException {
					try {
						if ("http".equalsIgnoreCase(uri.scheme())) {
							HttpURLConnection connection = (HttpURLConnection) new URL(uri.toString()).openConnection();
							
							connection.setDoOutput(true);
							connection.setRequestMethod("PUT");
							connection.setRequestProperty("Content-Type", "application/json");
							OutputStream output = null;

							try {
								output = connection.getOutputStream();
								writer.getDelegate().writeValue(output, this.currentRoot);
							} finally {
								if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
							}

							InputStream response = connection.getInputStream();
							System.out.println(response);
						}
						super.close();
					}
					finally {
						response.put(
								URIConverter.RESPONSE_TIME_STAMP_PROPERTY, 
								getAttributes(uri, options).get(URIConverter.ATTRIBUTE_TIME_STAMP));
					}
				}
			};
		}


		return result; 
	}

	@Override
	public void delete(URI uri, Map<?, ?> options) throws IOException {
		super.delete(uri, options);
	}

	@Override
	public boolean exists(URI uri, Map<?, ?> options) {
		return true;
	}

}