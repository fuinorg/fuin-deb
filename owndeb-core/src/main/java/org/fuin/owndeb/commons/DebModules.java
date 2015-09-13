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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.ContractViolationException;
import org.fuin.objects4j.common.Nullable;

/**
 * Provides default settings for the modules it contains.
 */
@XmlRootElement(name = "modules")
public final class DebModules extends AbstractPackage implements
        DebPackageResolver {

    @XmlAnyElement(lax = true)
    private List<DebModule> modules;

    /**
     * Default constructor.
     */
    public DebModules() {
        super();
    }

    /**
     * Constructor with package array.
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
     * @param modules
     *            Array of modules to create.
     */
    public DebModules(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final DebModule... modules) {
        this(version, description, maintainer, arch, installationPath, section,
                priority, Arrays.asList(modules));
    }

    /**
     * Constructor with package list.
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
     * @param modules
     *            List of modules to create.
     */
    public DebModules(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final List<DebModule> modules) {
        super(version, description, maintainer, arch, installationPath,
                section, priority);
        Contract.requireArgNotNull("modules", modules);
        if (modules.isEmpty()) {
            throw new ContractViolationException(
                    "The list 'modules' cannot be empty");
        }
        this.modules = modules;
    }

    /**
     * Returns the list of modules to create.
     * 
     * @return Immutable modules list.
     */
    public final List<DebModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    /**
     * Applies the default settings to all modules.
     */
    public final void applyModuleDefaults() {
        if (modules != null) {
            for (final DebModule module : modules) {
                module.applyPackageDefaults(this);
            }
        }
    }

    /**
     * Replaces variables in the base and package properties.
     * 
     * @param vars
     *            Variables to use.
     */
    public final void replaceModuleVariables(
            @Nullable final Map<String, String> vars) {
        replacePackageVariables(vars);
        if (modules != null) {
            for (final DebModule module : modules) {
                module.replaceVariables(vars);
            }
        }
    }

    @Override
    public final DebPackage findDebPackage(final String packageName) {
        if (modules != null) {
            for (final DebModule module : modules) {
                final DebPackage pkg = module.findPackageByName(packageName);
                if (pkg != null) {
                    return pkg;
                }
            }
        }
        return null;
    }

    /**
     * Updates the package references for all dependencies.
     */
    public final void resolveDependencies() {
        if (modules != null) {
            for (final DebModule module : modules) {
                module.resolveDependencies(this);
            }
        }
    }

}
