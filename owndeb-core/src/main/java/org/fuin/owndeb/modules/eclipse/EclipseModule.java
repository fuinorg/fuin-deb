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
package org.fuin.owndeb.modules.eclipse;

import static org.fuin.owndeb.commons.DebUtils.writeReplacedResource;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.Nullable;
import org.fuin.owndeb.commons.DebPackage;
import org.fuin.owndeb.modules.base.AbstractDownloadTarGzModule;

/**
 * Downloads Eclipse and creates a binary Debian package from it.
 */
@XmlRootElement(name = "eclipse-module")
public final class EclipseModule extends AbstractDownloadTarGzModule {

    /**
     * Default constructor for JAXB.
     */
    protected EclipseModule() {
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
     * @param url
     *            URL with "tar.gz" file.
     * @param packages
     *            Array of packages to create.
     */
    public EclipseModule(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String url, @NotNull final DebPackage... packages) {
        super(version, description, maintainer, arch, installationPath,
                section, priority, url, packages);
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
     * @param url
     *            URL with "tar.gz" file.
     * @param packages
     *            List of packages to create.
     */
    public EclipseModule(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String url,
            @NotNull final List<DebPackage> packages) {
        super(version, description, maintainer, arch, installationPath,
                section, priority, url, packages);
    }

    @Override
    public final String getModuleName() {
        return "eclipse-module";
    }

    @Override
    public final void replaceVariables(final Map<String, String> vars) {
        replaceDownloadTarGzModuleVariables(vars);
    }

    @Override
    protected final void applyModifications(final File packageDir) {
        // TODO Auto-generated method stub
    }
    
    @Override
    protected final void copyControlFiles(final DebPackage debPackage,
            final File controlDir) {
        final Map<String, String> vars = debPackage.getVariables();
        writeReplacedResource(EclipseModule.class, "/" + getModuleName()
                + "/control", controlDir, vars);
        writeReplacedResource(EclipseModule.class, "/" + getModuleName()
                + "/postinst", controlDir, vars);
    }

}
