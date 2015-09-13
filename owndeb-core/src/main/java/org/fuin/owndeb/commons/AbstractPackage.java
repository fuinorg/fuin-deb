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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;

import org.fuin.objects4j.common.Nullable;
import org.fuin.utils4j.Utils4J;

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
     * @param maintainer
     *            Maintainer of the package.
     * @param arch
     *            Architecture identifier like "amd64".
     * @param installationPath
     *            Installation path like "/opt".
     * @param section
     *            Section like "devel".
     * @param priority
     *            Priority like "low".
     */
    public AbstractPackage(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority) {
        super(maintainer, arch, installationPath, section, priority);
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
    public final void applyPackageDefaults(final AbstractPackage other) {
        applyBaseDefaults(other);
        if (version == null) {
            this.version = other.version;
        }
        if (description == null) {
            this.description = other.description;
        }
    }

    /**
     * Returns control file relevant properties (including properties from
     * {@link AbstractBase}).
     * 
     * @return Variables for the control files.
     */
    public final Map<String, String> getPackageVariables() {
        final Map<String, String> vars = new HashMap<>();
        vars.putAll(getBaseVariables());
        vars.put("version", version);
        vars.put("description", description);
        return vars;
    }

    /**
     * Replaces variables in the base and package properties.
     * 
     * @param vars
     *            Variables to use.
     */
    public final void replacePackageVariables(
            @Nullable final Map<String, String> vars) {
        replaceBaseVariables(vars);
        version = Utils4J.replaceVars(version, vars);
        description = Utils4J.replaceVars(description, vars);
    }

}
