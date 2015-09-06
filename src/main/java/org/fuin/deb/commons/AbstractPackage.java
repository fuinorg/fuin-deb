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
package org.fuin.deb.commons;

import javax.xml.bind.annotation.XmlAttribute;

import org.fuin.objects4j.common.Nullable;

/**
 * Provides default configuration for sub classes.
 */
public abstract class AbstractPackage extends AbstractBase {

    @XmlAttribute(name = "version")
    private String version;

    @XmlAttribute(name = "description")
    private String description;

    /**
     * Default constructor.
     */
    public AbstractPackage() {
        super();
    }

    /**
     * Constructor with all data.
     * 
     * @param version
     *            Package version.
     * @param description
     *            Package description.
     * @param prefix
     *            Prefix used to build the package like "fuin-".
     * @param maintainer
     *            Maintainer of the package.
     * @param arch
     *            Architecture identifier like "amd64".
     * @param installationPath
     *            Installation path like "/opt".
     */
    public AbstractPackage(@Nullable final String version,
            @Nullable final String description, @Nullable final String prefix,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath) {
        super(prefix, maintainer, arch, installationPath);
        this.version = version;
        this.description = description;
    }

    /**
     * Returns the package version.
     * 
     * @return Version.
     */
    @Nullable
    public final String getVersion() {
        return version;
    }

    /**
     * Returns the package description.
     * 
     * @return Description.
     */
    @Nullable
    public final String getDescription() {
        return description;
    }


    /**
     * Copy all attributes from the given object if the field is
     * <code>null</code>.
     * 
     * @param other
     *            Object to copy values from.
     */
    public final void copyPackageIfNotAlreadySet(final AbstractPackage other) {
        copyBaseIfNotAlreadySet(other);
        if (version == null) {
            this.version = other.version;
        }
        if (description == null) {
            this.description = other.description;
        }
    }
    
}
