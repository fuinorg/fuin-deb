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
package org.fuin.owndeb.pkg.eclipse;

import static org.fuin.owndeb.commons.DebUtils.writeReplacedResource;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.NotEmpty;
import org.fuin.objects4j.common.Nullable;
import org.fuin.owndeb.commons.DebDependency;
import org.fuin.owndeb.commons.DebPackages;
import org.fuin.owndeb.commons.DebUtils;
import org.fuin.owndeb.pkg.base.AbstractDownloadTarGzPackage;

/**
 * Downloads Eclipse and creates a binary Debian package from it.
 */
@XmlRootElement(name = "eclipse-package")
public final class EclipsePackage extends AbstractDownloadTarGzPackage {

    private static final String VMARGS = "vmargs";

    private static final String VM = "vm";

    /** Name of the package. */
    public static final String NAME = "eclipse-package";

    @XmlAttribute(name = VM)
    private String vm;

    @XmlAttribute(name = VMARGS)
    private String vmArgs;

    /**
     * Default constructor for JAXB.
     */
    protected EclipsePackage() {
        super();
    }

    /**
     * Constructor with package array.
     * 
     * @param name
     *            Unique package name.
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
     * @param dependencies
     *            Array of dependencies.
     */
    public EclipsePackage(@NotEmpty final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String url,
            @Nullable final DebDependency... dependencies) {
        super(name, version, description, maintainer, arch, installationPath,
                section, priority, url, dependencies);
    }

    /**
     * Constructor with package list.
     * 
     * @param name
     *            Unique package name.
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
     * @param dependencies
     *            List of dependencies.
     */
    public EclipsePackage(@NotEmpty final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String url,
            @Nullable final List<DebDependency> dependencies) {
        super(name, version, description, maintainer, arch, installationPath,
                section, priority, url, dependencies);
    }

    @Override
    public final String getPackageName() {
        return NAME;
    }

    /**
     * Returns the VM settings.
     * 
     * @return VM or <code>null</code>.
     */
    @Nullable
    public final String getVm() {
        return variableValue(VM);
    }

    /**
     * Returns the VM arguments.
     * 
     * @return VM arguments or <code>null</code>.
     */
    @Nullable
    public final String getVmArgs() {
        return variableValue(VMARGS);
    }

    @Override
    protected final void applyModifications(final File packageDir) {

    }

    /**
     * Initializes the instance and it's childs.
     * 
     * @param parent
     *            Current parent.
     */
    public final void init(@Nullable final DebPackages parent) {
        addNonExistingVariables(parent);
        initDownloadTarGzPackage(parent);
        addOrReplaceVariable(VM, vm);
        addOrReplaceVariable(VMARGS, vmArgs);
        resolveVariables();
    }

    @Override
    protected final void copyControlFiles(final File controlDir) {
        final Map<String, String> vars = DebUtils.asMap(getVariables());
        writeReplacedResource(EclipsePackage.class, "/" + getPackageName()
                + "/control", controlDir, vars);
        writeReplacedResource(EclipsePackage.class, "/" + getPackageName()
                + "/postinst", controlDir, vars);
    }

    @Override
    public final String toString() {
        return getPackageName();
    }

}
