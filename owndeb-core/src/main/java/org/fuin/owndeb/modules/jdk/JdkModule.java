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
package org.fuin.owndeb.modules.jdk;

import static org.fuin.owndeb.commons.DebUtils.writeReplacedResource;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.Nullable;
import org.fuin.owndeb.commons.DebModules;
import org.fuin.owndeb.commons.DebPackage;
import org.fuin.owndeb.commons.DebUtils;
import org.fuin.owndeb.commons.Variable;
import org.fuin.owndeb.modules.base.AbstractDownloadTarGzModule;
import org.fuin.utils4j.Utils4J;
import org.fuin.utils4j.VariableResolver;

/**
 * Downloads and Oracle JDK and creates a binary Debian package from it.
 */
@XmlRootElement(name = "jdk-module")
public final class JdkModule extends AbstractDownloadTarGzModule {

    /** Name of the module. */
    public static final String NAME = "jdk-module";

    /**
     * Default constructor for JAXB.
     */
    protected JdkModule() {
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
     * @param jdkUrl
     *            URL with "tar.gz" JDK file.
     * @param packages
     *            Array of packages to create.
     */
    public JdkModule(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String jdkUrl, @NotNull final DebPackage... packages) {
        super(version, description, maintainer, arch, installationPath,
                section, priority, jdkUrl, packages);
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
     * @param jdkUrl
     *            URL with "tar.gz" JDK file.
     * @param packages
     *            List of packages to create.
     */
    public JdkModule(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String jdkUrl,
            @NotNull final List<DebPackage> packages) {
        super(version, description, maintainer, arch, installationPath,
                section, priority, jdkUrl, packages);
    }

    @Override
    public final String getModuleName() {
        return NAME;
    }

    @Override
    public final void init(@Nullable final DebModules parent) {
        initDownloadTarGzModule(parent);
        if (parent != null) {
            addNonExistingVariables(parent.getVariables());
        }
    }
    
    @Override
    protected final void applyModifications(final DebPackage debPackage,
            final File packageDir) {
        // No modifications
    }

    @Override
    protected final void copyControlFiles(final DebPackage debPackage,
            final File controlDir) {
        final Map<String, String> vars = DebUtils.asMap(getParent().getVariables());
        writeReplacedResource(JdkModule.class, "/" + getModuleName()
                + "/control", controlDir, vars);
        writeReplacedResource(JdkModule.class, "/" + getModuleName()
                + "/postinst", controlDir, vars);
    }

    @Override
    public final String toString() {
        return getModuleName();
    }

}
