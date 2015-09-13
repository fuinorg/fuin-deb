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
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;
import org.fuin.utils4j.VariableResolver;

/**
 * Configuration for OwnDeb.
 */
@XmlRootElement(name = "owndeb-config")
public final class DebConfig {

    @XmlElementWrapper(name = "variables")
    @XmlElement(name = "variable")
    private List<Variable> variables;

    @XmlElement
    private DebModules modules;

    /**
     * Default constructor for JAXB.
     */
    protected DebConfig() {
        super();
    }

    /**
     * Constructor with modules.
     * 
     * @param modules
     *            Modules object.
     */
    public DebConfig(@NotNull final DebModules modules) {
        this(modules, (List<Variable>) null);
    }

    /**
     * Constructor modules and variable array.
     * 
     * @param modules
     *            Modules object.
     * @param variables
     *            Variable array.
     */
    public DebConfig(@NotNull final DebModules modules,
            @Nullable final Variable... variables) {
        // CHECKSTYLE:OFF Inline conditional is here the only way to go
        this(modules, variables == null ? null : Arrays.asList(variables));
        // CHECKSTYLE:ON
    }

    /**
     * Constructor modules and variable list.
     * 
     * @param modules
     *            Modules object.
     * @param variables
     *            Variable list.
     */
    public DebConfig(@NotNull final DebModules modules,
            @Nullable final List<Variable> variables) {
        super();
        Contract.requireArgNotNull("modules", modules);
        this.modules = modules;
        if (variables == null) {
            this.variables = null;
        } else {
            this.variables = new ArrayList<>(variables);
        }
    }

    /**
     * Returns the list of modules to create.
     * 
     * @return Immutable modules list.
     */
    @NotNull
    public final DebModules getModules() {
        return modules;
    }

    /**
     * Returns the list of variables.
     * 
     * @return Unmodifiable list.
     */
    @Nullable
    public final List<Variable> getVariables() {
        return Collections.unmodifiableList(variables);
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
        final Map<String, String> resolved = new VariableResolver(
                DebUtils.asMap(variables)).getResolved();
        if (variables != null) {
            for (final Variable variable : variables) {
                variable.init(resolved);
            }
        }
        if (modules != null) {
            modules.replaceModuleVariables(resolved);
            modules.applyModuleDefaults();          
        }
    }

}
