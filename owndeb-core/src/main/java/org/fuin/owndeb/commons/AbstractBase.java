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
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;
import org.fuin.utils4j.Utils4J;
import org.fuin.utils4j.VariableResolver;

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
     * Copy constructor.
     * 
     * @param other
     *            Object to copy.
     */
    public AbstractBase(@NotNull final AbstractBase other) {
        super();
        Contract.requireArgNotNull("other", other);
        this.maintainer = other.maintainer;
        this.arch = other.arch;
        this.installationPath = other.installationPath;
        this.section = other.section;
        this.priority = other.priority;
        if (other.variables == null) {
            this.variables = null;
        } else {
            this.variables = new ArrayList<>(other.variables);
        }
        this.parent = other.parent;
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
     * Returns the section.
     * 
     * @return Section like "devel".
     */
    @Nullable
    public final String getSection() {
        return section;
    }

    /**
     * Returns the priority.
     * 
     * @return Priority like "low".
     */
    @Nullable
    public final String getPriority() {
        return priority;
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
     * Adds a variable and fails with an {@link IllegalStateException} if it
     * already exists.
     * 
     * @param varToAdd
     *            Variable to add.
     */
    protected final void addVariable(@NotNull final Variable varToAdd) {
        Contract.requireArgNotNull("varToAdd", varToAdd);
        if (variables == null) {
            variables = new ArrayList<>();
        } else {
            if (variables.contains(varToAdd)) {
                throw new IllegalStateException("Variable '"
                        + varToAdd.getName() + "' already exists: " + this);
            }
        }
        variables.add(varToAdd);
    }

    /**
     * Add some variables and ignores duplicates.
     * 
     * @param varsToAdd
     *            Variables that will only be added if they are not in the list
     *            yet.
     */
    protected final void addNonExistingVariables(@Nullable final List<Variable> varsToAdd) {
        if (varsToAdd != null) {
            if (variables == null) {
                variables = new ArrayList<>();
            }
            for (final Variable var : varsToAdd) {
                if (!variables.contains(var)) {
                    variables.add(var);
                }
            }
        }
    }

    /**
     * Adds the properties defined in this class as variables. If any of them
     * already exist, an {@link IllegalStateException} is thrown.
     */
    private void addVariables() {
        if (maintainer != null) {
            addVariable(new Variable("maintainer", maintainer));
        }
        if (arch != null) {
            addVariable(new Variable("arch", arch));
        }
        if (installationPath != null) {
            addVariable(new Variable("installation-path", installationPath));
        }
        if (section != null) {
            addVariable(new Variable("section", section));
        }
        if (priority != null) {
            addVariable(new Variable("priority", priority));
        }
    }

    /**
     * Replaces variables in the base properties.
     */
    private void replaceVariables() {
        final Map<String, String> vars = new VariableResolver(
                DebUtils.asMap(variables)).getResolved();
        installationPath = Utils4J.replaceVars(installationPath, vars);
        arch = Utils4J.replaceVars(arch, vars);
        maintainer = Utils4J.replaceVars(maintainer, vars);
        section = Utils4J.replaceVars(section, vars);
        priority = Utils4J.replaceVars(priority, vars);
    }

    /**
     * Copy all attributes from the given object if the field is
     * <code>null</code>.
     * 
     * @param other
     *            Object to copy values from.
     */
    private void applyDefaults(final VariablesContainer other) {
        if (parent != null) {
            if (maintainer == null) {
                this.maintainer = other.variableValue("maintainer");
            }
            if (arch == null) {
                this.arch = other.variableValue("arch");
            }
            if (installationPath == null) {
                this.installationPath = other.variableValue("installationPath");
            }
            if (section == null) {
                this.section = other.variableValue("section");
            }
            if (priority == null) {
                this.priority = other.variableValue("priority");
            }
        }
    }

    /**
     * Initialize base stuff.
     * 
     * @param parent
     *            Parent to set.
     */
    public final void initBase(@Nullable final VariablesContainer parent) {
        this.parent = parent;
        applyDefaults(parent);
        addVariables();
        replaceVariables();
    }

}
