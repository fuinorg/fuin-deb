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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;

/**
 * Configuration for OwnDeb.
 */
@XmlRootElement(name = "owndeb-config")
public final class DebConfig implements VariablesContainer {

    @XmlElementWrapper(name = "variables")
    @XmlElement(name = "variable")
    private List<Variable> variables;

    @XmlElement
    private DebPackages packages;

    /**
     * Default constructor for JAXB.
     */
    protected DebConfig() {
        super();
    }

    /**
     * Constructor with packages.
     * 
     * @param packages
     *            Packages object.
     */
    public DebConfig(@NotNull final DebPackages packages) {
        this(packages, (List<Variable>) null);
    }

    /**
     * Constructor packages and variable array.
     * 
     * @param packages
     *            Packages object.
     * @param variables
     *            Variable array.
     */
    public DebConfig(@NotNull final DebPackages packages,
            @Nullable final Variable... variables) {
        // CHECKSTYLE:OFF Inline conditional is here the only way to go
        this(packages, variables == null ? null : Arrays.asList(variables));
        // CHECKSTYLE:ON
    }

    /**
     * Constructor packages and variable list.
     * 
     * @param packages
     *            Packages object.
     * @param variables
     *            Variable list.
     */
    public DebConfig(@NotNull final DebPackages packages,
            @Nullable final List<Variable> variables) {
        super();
        Contract.requireArgNotNull("packages", packages);
        this.packages = packages;
        if (variables == null) {
            this.variables = null;
        } else {
            this.variables = new ArrayList<>(variables);
        }
    }

    /**
     * Returns the list of packages to create.
     * 
     * @return Immutable packages list.
     */
    @NotNull
    public final DebPackages getPackages() {
        return packages;
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
     * Called after the configuration was unmarshalled using JAXB.
     * 
     * @param unmarshaller
     *            Unmarshaller.
     * @param parent
     *            Parent.
     */
    public final void afterUnmarshal(final Unmarshaller unmarshaller,
            final Object parent) {
        if (packages != null) {
            packages.init(this);
            packages.resolveDependencies();
        }
    }

}
