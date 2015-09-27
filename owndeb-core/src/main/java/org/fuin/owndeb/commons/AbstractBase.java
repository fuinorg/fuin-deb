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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;

/**
 * Provides default configuration for sub classes.
 */
public abstract class AbstractBase implements VariablesContainer {

    @XmlAttribute(name = "maintainer")
    private String maintainer;

    @XmlAttribute(name = "arch")
    private String arch;

    @XmlAttribute(name = "installation-path")
    private String installationPath;

    @XmlAttribute(name = "section")
    private String section;

    @XmlAttribute(name = "priority")
    private String priority;

    @XmlElement(name = "variable")
    private List<Variable> variables;

    private transient VariablesContainer parent;

    /**
     * Default constructor.
     */
    public AbstractBase() {
        super();
    }

    /**
     * Constructor with all data.
     * 
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
    public AbstractBase(@Nullable final String maintainer,
            @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority) {
        super();
        this.maintainer = maintainer;
        this.arch = arch;
        this.installationPath = installationPath;
        this.section = section;
        this.priority = priority;
    }

    /**
     * Returns the maintainer of the package.
     * 
     * @return Maintainer.
     */
    @Nullable
    public final String getMaintainer() {
        return variableValue("maintainer");
    }

    /**
     * Returns the architecture identifier.
     * 
     * @return Architecture like "amd64".
     */
    @Nullable
    public final String getArch() {
        return variableValue("arch");
    }

    /**
     * Returns the installation path.
     * 
     * @return Installation path like "/opt".
     */
    @Nullable
    public final String getInstallationPath() {
        return variableValue("installation-path");
    }

    /**
     * Returns the section.
     * 
     * @return Section like "devel".
     */
    @Nullable
    public final String getSection() {
        return variableValue("section");
    }

    /**
     * Returns the priority.
     * 
     * @return Priority like "low".
     */
    @Nullable
    public final String getPriority() {
        return variableValue("priority");
    }

    @Override
    public final List<Variable> getVariables() {
        if (variables == null) {
            return null;
        }
        return Collections.unmodifiableList(variables);
    }

    @Override
    public final String variableValue(final String name) {
        if (variables == null) {
            return null;
        }
        final int idx = variables.indexOf(new Variable(name, ""));
        if (idx < 0) {
            return null;
        }
        return variables.get(idx).getValue();
    }

    /**
     * Returns the parent.
     * 
     * @return Current parent.
     */
    @Nullable
    public final VariablesContainer getParent() {
        return parent;
    }

    /**
     * Adds a variable if the given value is not <code>null</code>. Any existing
     * variable with the same name will be replaced.
     * 
     * @param name
     *            Name of the variable to add.
     * @param value
     *            Value of the variable to add.
     */
    protected final void addOrReplaceVariable(@NotNull final String name,
            @Nullable final String value) {
        Contract.requireArgNotNull("name", name);
        if (value != null) {
            if (variables == null) {
                variables = new ArrayList<>();
            }
            final Variable var = new Variable(name, value);
            final int idx = variables.indexOf(var);
            if (idx < 0) {
                variables.add(var);
            } else {
                variables.set(idx, var);
            }
        }
    }

    /**
     * Add some variables and ignores duplicates.
     * 
     * @param container
     *            Container with variables that will only be added if they are
     *            not in the list yet.
     */
    protected final void addNonExistingVariables(
            @Nullable final VariablesContainer container) {
        if (container == null) {
            return;
        }
        final List<Variable> varsToAdd = container.getVariables();
        if (varsToAdd == null) {
            return;
        }
        if (variables == null) {
            variables = new ArrayList<>();
        }
        for (final Variable var : varsToAdd) {
            if (!variables.contains(var)) {
                variables.add(var);
            }
        }
    }

    /**
     * Resolves all variable references in the variable values.
     */
    protected final void resolveVariables() {
        variables = DebUtils.resolve(variables);
    }

    /**
     * Initialize base stuff.
     * 
     * @param parent
     *            Parent to set.
     */
    public final void initBase(@Nullable final VariablesContainer parent) {
        this.parent = parent;
        addOrReplaceVariable("maintainer", maintainer);
        addOrReplaceVariable("arch", arch);
        addOrReplaceVariable("installation-path", installationPath);
        addOrReplaceVariable("section", section);
        addOrReplaceVariable("priority", priority);
    }

}
