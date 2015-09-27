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
package org.fuin.owndeb.pkg.eclipseplugin;

import static org.fuin.owndeb.commons.DebUtils.writeReplacedResource;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.tools.ant.Project;
import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.NotEmpty;
import org.fuin.objects4j.common.Nullable;
import org.fuin.owndeb.commons.DebDependency;
import org.fuin.owndeb.commons.DebPackage;
import org.fuin.owndeb.commons.DebPackages;
import org.fuin.owndeb.commons.DebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.ant.DebAntTask;

/**
 * Creates a binary Debian package for an Eclipse plugin.
 */
@XmlRootElement(name = "eclipse-plugin-package")
public class EclipsePluginPackage extends DebPackage {

    private static final String REPOSITORY = "repository";

    private static final String INSTALLIUS = "installIUs";

    /** Name of the package. */
    public static final String NAME = "eclipse-plugin-package";

    private static final Logger LOG = LoggerFactory
            .getLogger(EclipsePluginPackage.class);

    @XmlAttribute(name = REPOSITORY)
    private String repository;

    @XmlAttribute(name = INSTALLIUS)
    private String installIUs;

    /**
     * Default constructor for JAXB.
     */
    protected EclipsePluginPackage() {
        super();
    }

    /**
     * Constructor with dependencies array.
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
     * @param repository
     *            Eclipse P2 repsoitory URL.
     * @param installIUs
     *            List of units to install.
     * @param dependencies
     *            Array of dependencies.
     */
    public EclipsePluginPackage(@NotEmpty final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String repository, @NotNull final String installIUs,
            @Nullable final DebDependency... dependencies) {
        super(name, version, description, maintainer, arch, installationPath,
                section, priority, dependencies);
        Contract.requireArgNotNull(REPOSITORY, repository);
        Contract.requireArgNotNull(INSTALLIUS, installIUs);
        this.repository = repository;
        this.installIUs = installIUs;
    }

    /**
     * Constructor with dependencies list.
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
     * @param repository
     *            Eclipse P2 repsoitory URL.
     * @param installIUs
     *            List of units to install.
     * @param dependencies
     *            List of dependencies.
     */
    public EclipsePluginPackage(@NotEmpty final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String repository, @NotNull final String installIUs,
            @Nullable final List<DebDependency> dependencies) {
        super(name, version, description, maintainer, arch, installationPath,
                section, priority, dependencies);
        Contract.requireArgNotNull(REPOSITORY, repository);
        Contract.requireArgNotNull(INSTALLIUS, installIUs);
        this.repository = repository;
        this.installIUs = installIUs;
    }

    /**
     * Returns the Eclipse repository.
     * 
     * @return Repository.
     */
    @NotNull
    public final String getRepository() {
        return variableValue(REPOSITORY);
    }

    /**
     * Returns the Eclipse feature groups.
     * 
     * @return Features to install.
     */
    @NotNull
    public final String getInstallIUs() {
        return variableValue(INSTALLIUS);
    }

    @Override
    public final String getPackageName() {
        return NAME;
    }

    @Override
    public final void create(final File buildDirectory) {

        Contract.requireArgNotNull("buildDirectory", buildDirectory);
        LOG.info("Creating package in: {}", buildDirectory);

        final File controlDir = new File(buildDirectory, getName() + "-control");

        LOG.debug("controlDir: {}", controlDir);

        copyControlFiles(this, getPackageName(), controlDir);
        createDebianPackage(this, buildDirectory, controlDir);

    }

    @Override
    public final void init(@Nullable final DebPackages parent) {
        addNonExistingVariables(parent);
        initPackage(parent);
        addOrReplaceVariable(REPOSITORY, repository);
        addOrReplaceVariable(INSTALLIUS, installIUs);
        resolveVariables();
    }

    private static void copyControlFiles(final DebPackage debPackage,
            final String packageName, final File controlDir) {

        DebUtils.mkdirs(controlDir);
        final Map<String, String> vars = DebUtils.asMap(debPackage
                .getVariables());
        writeReplacedResource(EclipsePluginPackage.class, "/" + packageName
                + "/control", controlDir, vars);
        writeReplacedResource(EclipsePluginPackage.class, "/" + packageName
                + "/postinst", controlDir, vars);
        writeReplacedResource(EclipsePluginPackage.class, "/" + packageName
                + "/postrm", controlDir, vars);

    }

    private static void createDebianPackage(final DebPackage debPackage,
            final File buildDirectory, final File controlDir) {

        LOG.info("Start creating package " + debPackage.getName());

        final File debName = new File(buildDirectory,
                debPackage.getDebFilename());

        LOG.debug("controlDir: {}", controlDir);

        final Project project = new Project();

        final DebAntTask task = new DebAntTask();
        task.setProject(project);
        task.setDestfile(debName);
        task.setControl(controlDir);

        task.execute();

        LOG.info("Finished creating package " + debPackage.getName());

    }

    @Override
    public final String toString() {
        return getPackageName();
    }

}
