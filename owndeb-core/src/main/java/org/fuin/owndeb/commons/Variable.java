/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.owndeb.commons;

import static org.fuin.utils4j.Utils4J.readAsString;
import static org.fuin.utils4j.Utils4J.url;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.Nullable;
import org.fuin.utils4j.Utils4J;

/**
 * Represents a variable with name and value. Equals and hash code are based on
 * the name.
 */
@XmlRootElement(name = "variable")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Variable implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "value")
    private String value;

    @XmlAttribute(name = "url")
    private String urlStr;

    @XmlAttribute(name = "encoding")
    private String encoding;

    private transient URL url;

    /**
     * Default constructor for JAXB.
     */
    protected Variable() {
        super();
    }

    /**
     * Constructor with value.
     * 
     * @param name
     *            Unique name - May not be <code>null</code> or empty.
     * @param value
     *            Variable value - May not be <code>null</code>.
     */
    public Variable(final String name, final String value) {
        super();
        Utils4J.checkNotNull("name", name);
        Utils4J.checkNotEmpty("name", name);
        Utils4J.checkNotNull("value", value);
        this.name = name;
        this.value = value;
    }

    /**
     * Constructor with URL.
     * 
     * @param name
     *            Unique name - May not be <code>null</code> or empty.
     * @param url
     *            URL that references a text resource - May not be
     *            <code>null</code>.
     */
    public Variable(final String name, final URL url) {
        this(name, url, null);
    }

    /**
     * Constructor with URL.
     * 
     * @param name
     *            Unique name - May not be <code>null</code> or empty.
     * @param url
     *            URL that references a text resource - May not be
     *            <code>null</code>.
     * @param encoding
     *            Encoding of the text resource the URL points to - May be
     *            <code>null</code> but not empty.
     */
    public Variable(final String name, final URL url, final String encoding) {
        super();
        Utils4J.checkNotNull("name", name);
        Utils4J.checkNotEmpty("name", name);
        Utils4J.checkNotNull("url", url);
        if (encoding != null) {
            Utils4J.checkNotEmpty("encoding", encoding);
        }
        this.name = name;
        this.urlStr = url.toString();
        this.encoding = encoding;
    }

    /**
     * Returns the name of the variable.
     * 
     * @return Name - Never <code>null</code>.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the value. If no value but an URL is defined, the value will be
     * loaded once from the URL. Later calls will only return the cached value.
     * 
     * @return Value or <code>null</code>.
     */
    public final String getValue() {
        if ((value == null) && (urlStr != null)) {
            value = readAsString(getURL(), getEncodingOrDefault(), 1024);
            if (value == null) {
                throw new IllegalStateException(
                        "Reading the URL returned null: " + urlStr);
            }
        }
        return value;
    }

    /**
     * Returns the URL.
     * 
     * @return URL or <code>null</code>.
     */
    public final URL getURL() {
        if (url == null) {
            try {
                url = url(urlStr);
            } catch (final IllegalArgumentException ex) {
                throw new RuntimeException("Variable '" + name
                        + "' has a wrong URL", ex);
            }
        }
        return url;
    }

    /**
     * Returns the encoding to use for reading the value from the URL.
     * 
     * @return Encoding or <code>null</code>.
     */
    public final String getEncoding() {
        return encoding;
    }

    /**
     * Returns the encoding to use for reading the value from the URL. If no
     * encoding is defined this method returns 'utf-8' as default.
     * 
     * @return Encoding - Never <code>null</code>.
     */
    public final String getEncodingOrDefault() {
        if (encoding == null) {
            return "utf-8";
        }
        return encoding;
    }

    /**
     * Replaces variables (if defined) in the value.
     * 
     * @param vars
     *            Variables to use.
     */
    public final void init(@Nullable final Map<String, String> vars) {
        value = Utils4J.replaceVars(value, vars);
    }

    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Variable other = (Variable) obj;
        return name.equals(other.name);
    }

    @Override
    public final String toString() {
        return name + "=" + value;
    }

}
