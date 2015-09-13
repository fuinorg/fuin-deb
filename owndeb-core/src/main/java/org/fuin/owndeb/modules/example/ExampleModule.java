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
package org.fuin.owndeb.modules.example;

import static org.fuin.owndeb.commons.DebUtils.writeReplacedResource;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.tools.ant.Project;
import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;
import org.fuin.owndeb.commons.DebModule;
import org.fuin.owndeb.commons.DebPackage;
import org.fuin.owndeb.commons.DebPackageResolver;
import org.fuin.owndeb.commons.DebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.ant.Data;
import org.vafer.jdeb.ant.DebAntTask;
import org.vafer.jdeb.ant.Mapper;

/**
 * Example module that only installs a "hello.txt" file into the '/opt'
 * directory.
 */
@XmlRootElement(name = "example-module")
public class ExampleModule extends DebModule {

    private static final Logger LOG = LoggerFactory
            .getLogger(ExampleModule.class);

    /**
     * Default constructor for JAXB.
     */
    protected ExampleModule() {
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
     * @param packages
     *            Array of packages to create.
     */
    public ExampleModule(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final DebPackage... packages) {
        super(version, description, maintainer, arch, installationPath,
                section, priority, packages);
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
    public ExampleModule(@Nullable final String version,
            @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final List<DebPackage> packages) {
        super(version, description, maintainer, arch, installationPath,
                section, priority, packages);
    }

    @Override
    public final String getModuleName() {
        return "example-module";
    }

    @Override
    public final void create(final DebPackageResolver resolver, final File buildDirectory) {

        Contract.requireArgNotNull("buildDirectory", buildDirectory);
        LOG.info("Creating module in: {}", buildDirectory);

        final List<DebPackage> debPackages = getPackages();
        for (final DebPackage pkg : debPackages) {

            final DebPackage debPackage = new DebPackage(pkg);
            debPackage.applyPackageDefaults(this);

            LOG.info("Creating package: {}", debPackage.getName());

            final File packageDir = new File(buildDirectory,
                    debPackage.getName());
            final File controlDir = new File(buildDirectory,
                    debPackage.getName() + "-control");
            final File helloFile = new File(packageDir, "hello.txt");
            DebUtils.copyResourceToFile(this.getClass(), "/" + getModuleName()
                    + "/hello.txt", helloFile);

            LOG.debug("packageDir: {}", packageDir);
            LOG.debug("controlDir: {}", controlDir);

            copyControlFiles(debPackage, getModuleName(), controlDir);
            createDebianPackage(debPackage, buildDirectory, controlDir,
                    packageDir);

        }

    }

    @Override
    public final void replaceVariables(final Map<String, String> vars) {
        replaceModuleVariables(vars);
    }

    private static void copyControlFiles(final DebPackage debPackage,
            final String moduleName, final File controlDir) {

        DebUtils.mkdirs(controlDir);
        final Map<String, String> vars = debPackage.getVariables();
        writeReplacedResource(ExampleModule.class, "/" + moduleName
                + "/control", controlDir, vars);

    }

    private static void createDebianPackage(final DebPackage debPackage,
            final File buildDirectory, final File controlDir,
            final File packageDir) {

        LOG.info("Start creating Debian package");

        final File debName = new File(buildDirectory,
                debPackage.getDebFilename());

        LOG.debug("packageDir: {}", packageDir);
        LOG.debug("controlDir: {}", controlDir);
        LOG.debug("debName: {}", debName);

        final Project project = new Project();

        final DebAntTask task = new DebAntTask();
        task.setProject(project);
        task.setDestfile(debName);
        task.setControl(controlDir);

        final Data data = new Data();
        data.setSrc(packageDir);
        data.setType("directory");
        final Mapper mapper = new Mapper();
        mapper.setType("perm");
        mapper.setPrefix(debPackage.getInstallationPath() + "/"
                + packageDir.getName());
        mapper.setUser("root");
        mapper.setGroup("developer");
        data.addMapper(mapper);

        task.addData(data);

        task.execute();

        LOG.info("Finished creating Debian package");

    }

}
