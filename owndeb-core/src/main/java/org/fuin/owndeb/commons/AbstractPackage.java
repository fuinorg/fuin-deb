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

import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;

import org.fuin.objects4j.common.Nullable;
import org.fuin.utils4j.Utils4J;
import org.fuin.utils4j.VariableResolver;

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
     * Copy constructor.
     * 
     * @param other
     *            Package to copy.
     */
    public AbstractPackage(@NotNull final AbstractPackage other) {
        super(other);
        this.version = other.version;
        this.description = other.description;
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
    private void applyDefaults(final VariablesContainer parent) {
        if (parent != null) {
            if (version == null) {
                this.version = parent.variableValue("version");
            }
            if (description == null) {
                this.description = parent.variableValue("description");
            }
        }
    }

    /**
     * Replaces variables in the properties.
     * 
     * @param vars
     *            Variables to use.
     */
    private void replaceVariables() {
        final Map<String, String> vars = new VariableResolver(
                DebUtils.asMap(getVariables())).getResolved();
        version = Utils4J.replaceVars(version, vars);
        description = Utils4J.replaceVars(description, vars);
    }

    /**
     * Adds the properties defined in this class as variables. If any of them
     * already exist, an {@link IllegalStateException} will be thrown.
     */
    private final void addVariables() {
        if (version != null) {
            addVariable(new Variable("version", version));
        }
        if (description != null) {
            addVariable(new Variable("description", description));
        }
    }

    /**
     * Initialize base stuff.
     * 
     * @param parent
     *            Parent to set.
     */
    public final void initPackage(final VariablesContainer parent) {
        initBase(parent);
        applyDefaults(parent);
        addVariables();
        replaceVariables();
    }

}
