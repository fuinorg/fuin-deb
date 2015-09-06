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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
public final class DebModules extends AbstractPackage {

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
     * @param prefix
     *            Prefix used to build the package like "fuin-".
     * @param maintainer
     *            Maintainer of the package.
     * @param arch
     *            Architecture identifier like "amd64".
     * @param installationPath
     *            Installation path like "/opt".
     * @param modules
     *            Array of modules to create.
     */
    public DebModules(@Nullable final String version,
            @Nullable final String description, @Nullable final String prefix,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @NotNull final DebModule... modules) {
        this(version, description, prefix, maintainer, arch, installationPath,
                Arrays.asList(modules));
    }

    /**
     * Constructor with package list.
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
     * @param modules
     *            List of modules to create.
     */
    public DebModules(@Nullable final String version,
            @Nullable final String description, @Nullable final String prefix,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @NotNull final List<DebModule> modules) {
        super(version, description, prefix, maintainer, arch, installationPath);
        Contract.requireArgNotNull("modules", modules);
        if (modules.isEmpty()) {
            throw new ContractViolationException(
                    "The list 'modules' cannot be ampty");
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

}
