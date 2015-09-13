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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.ContractViolationException;
import org.fuin.objects4j.common.Nullable;

/**
 * Provides default settings for the packages it contains.
 */
public abstract class DebModule extends AbstractPackage {

    @XmlElement(name = "package")
    private List<DebPackage> packages;

    private transient DebModules parent;

    /**
     * Default constructor.
     */
    public DebModule() {
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
     * @param packages
     *            Array of packages to create.
     * @param section
     *            Section like "devel".
     * @param priority
     *            Priority like "low".
     */
    public DebModule(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final DebPackage... packages) {
        this(version, description, maintainer, arch, installationPath, section,
                priority, Arrays.asList(packages));
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
     * @param packages
     *            List of packages to create.
     */
    public DebModule(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final List<DebPackage> packages) {
        super(version, description, maintainer, arch, installationPath,
                section, priority);
        Contract.requireArgNotNull("packages", packages);
        if (packages.isEmpty()) {
            throw new ContractViolationException(
                    "The list 'packages' cannot be ampty");
        }
        this.packages = packages;
    }

    /**
     * Returns the list of packages to create.
     * 
     * @return Immutable package list.
     */
    public final List<DebPackage> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    /**
     * Applies the default settings to all packages.
     * 
     * @param debPkg
     *            Default values to use.
     */
    public final void applyModuleDefaults(final AbstractPackage debPkg) {
        applyPackageDefaults(debPkg);
        if (packages != null) {
            for (final DebPackage pkg : packages) {
                pkg.applyPackageDefaults(this);
            }
        }
    }

    /**
     * Replaces variables in the base, package and module properties.
     * 
     * @param vars
     *            Variables to use.
     */
    public final void replaceModuleVariables(
            @Nullable final Map<String, String> vars) {
        replacePackageVariables(vars);
        if (packages != null) {
            for (final DebPackage pkg : packages) {
                pkg.replaceVariables(vars);
            }
        }
    }

    /**
     * Locates a package by it's name.
     * 
     * @param packageName
     *            Name of the package to find.
     * 
     * @return Package or <code>null</code> if no package with the given name
     *         was found.
     */
    public final DebPackage findPackageByName(final String packageName) {
        if (packages != null) {
            for (final DebPackage pkg : packages) {
                if (pkg.getName().equals(packageName)) {
                    return pkg;
                }
            }
        }
        return null;
    }

    /**
     * Updates the package references for all dependencies.
     * 
     * @param resolver
     *            Knows all packages.
     */
    public final void resolveDependencies(final DebPackageResolver resolver) {
        if (packages != null) {
            for (final DebPackage pkg : packages) {
                pkg.resolveDependencies(resolver);
            }
        }
    }

    /**
     * Returns the parent.
     * 
     * @return Current parent.
     */
    public final DebModules getParent() {
        return parent;
    }

    /**
     * Initializes the instance and it's childs.
     * 
     * @param parent
     *            Current parent.
     */
    public final void init(@Nullable final DebModules parent) {
        this.parent = parent;
        if (packages != null) {
            for (final DebPackage pkg : packages) {
                pkg.init(this);
            }
        }
    }

    /**
     * Returns the unique name of the module.
     * 
     * @return Module name.
     */
    public abstract String getModuleName();

    /**
     * Creates all packages of the module.
     * 
     * @param buildDirectory
     *            Directory to create the packages inside.
     */
    public abstract void create(@NotNull File buildDirectory);

    /**
     * Replaces variables in all properties.
     * 
     * @param vars
     *            Variables to use.
     */
    public abstract void replaceVariables(@Nullable Map<String, String> vars);

}
