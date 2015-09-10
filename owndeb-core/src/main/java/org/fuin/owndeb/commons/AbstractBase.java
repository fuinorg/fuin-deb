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

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;

import org.fuin.objects4j.common.Nullable;

/**
 * Provides default configuration for sub classes.
 */
public abstract class AbstractBase {

    @XmlAttribute(name = "prefix")
    private String prefix;

    @XmlAttribute(name = "maintainer")
    private String maintainer;

    @XmlAttribute(name = "arch")
    private String arch;

    @XmlAttribute(name = "installation-path")
    private String installationPath;

    /**
     * Default constructor.
     */
    public AbstractBase() {
        super();
    }

    /**
     * Constructor with all data.
     * 
     * @param prefix
     *            Prefix used to build the package like "fuin-".
     * @param maintainer
     *            Maintainer of the package.
     * @param arch
     *            Architecture identifier like "amd64".
     * @param installationPath
     *            Installation path like "/opt".
     */
    public AbstractBase(@Nullable final String prefix,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath) {
        super();
        this.prefix = prefix;
        this.maintainer = maintainer;
        this.arch = arch;
        this.installationPath = installationPath;
    }

    /**
     * Returns the prefix used to build the package.
     * 
     * @return Prefix like "fuin-".
     */
    @Nullable
    public final String getPrefix() {
        return prefix;
    }

    /**
     * Returns the maintainer of the package.
     * 
     * @return Maintainer.
     */
    @Nullable
    public final String getMaintainer() {
        return maintainer;
    }

    /**
     * Returns the architecture identifier.
     * 
     * @return Architecture like "amd64".
     */
    @Nullable
    public final String getArch() {
        return arch;
    }

    /**
     * Returns the installation path.
     * 
     * @return Installation path like "/opt".
     */
    @Nullable
    public final String getInstallationPath() {
        return installationPath;
    }

    /**
     * Copy all attributes from the given object if the field is
     * <code>null</code>.
     * 
     * @param other
     *            Object to copy values from.
     */
    public final void copyBaseIfNotAlreadySet(final AbstractBase other) {

        if (prefix == null) {
            this.prefix = other.prefix;
        }

        if (maintainer == null) {
            this.maintainer = other.maintainer;
        }

        if (arch == null) {
            this.arch = other.arch;
        }

        if (installationPath == null) {
            this.installationPath = other.installationPath;
        }

    }

    /**
     * Converts a string into an URL and wraps a possible malformed URL
     * exception into a runtime exception.
     * 
     * @param url
     *            String to convert into an URL.
     * 
     * @return String as URL.
     */
    protected static URL url(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
