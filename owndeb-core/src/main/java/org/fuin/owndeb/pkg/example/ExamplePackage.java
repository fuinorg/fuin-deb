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
package org.fuin.owndeb.pkg.example;

import static org.fuin.owndeb.commons.DebUtils.writeReplacedResource;

import java.io.File;
import java.util.List;
import java.util.Map;

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
import org.vafer.jdeb.ant.Data;
import org.vafer.jdeb.ant.DebAntTask;
import org.vafer.jdeb.ant.Mapper;

/**
 * Example package that only installs a "hello.txt" file into the '/opt'
 * directory.
 */
@XmlRootElement(name = "example-package")
public class ExamplePackage extends DebPackage {

    private static final Logger LOG = LoggerFactory
            .getLogger(ExamplePackage.class);

    /**
     * Default constructor for JAXB.
     */
    protected ExamplePackage() {
        super();
    }

    /**
     * Constructor with dependency array.
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
     * @param dependencies
     *            Array of dependencies.
     */
    public ExamplePackage(@NotEmpty final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @Nullable final DebDependency... dependencies) {
        super(name, version, description, maintainer, arch, installationPath,
                section, priority, dependencies);
    }

    /**
     * Constructor with dependency list.
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
     * @param dependencies
     *            List of dependencies.
     */
    public ExamplePackage(@NotEmpty final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @Nullable final List<DebDependency> dependencies) {
        super(name, version, description, maintainer, arch, installationPath,
                section, priority, dependencies);
    }

    @Override
    public final String getPackageName() {
        return "example-package";
    }

    @Override
    public final void create(final File buildDirectory) {

        Contract.requireArgNotNull("buildDirectory", buildDirectory);
        LOG.info("Creating package in: {}", buildDirectory);

        final File packageDir = new File(buildDirectory, getName());
        final File controlDir = new File(buildDirectory, getName() + "-control");
        final File helloFile = new File(packageDir, "hello.txt");
        DebUtils.copyResourceToFile(this.getClass(), "/" + getPackageName()
                + "/hello.txt", helloFile);

        LOG.debug("packageDir: {}", packageDir);
        LOG.debug("controlDir: {}", controlDir);

        copyControlFiles(this, getPackageName(), controlDir);
        createDebianPackage(this, buildDirectory, controlDir, packageDir);

    }

    @Override
    public final void init(@Nullable final DebPackages parent) {
        addNonExistingVariables(parent);
        initPackage(parent);
        resolveVariables();
    }

    private static void copyControlFiles(final DebPackage debPackage,
            final String packageName, final File controlDir) {

        DebUtils.mkdirs(controlDir);
        final Map<String, String> vars = DebUtils.asMap(debPackage
                .getVariables());
        writeReplacedResource(ExamplePackage.class, "/" + packageName
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

    @Override
    public final String toString() {
        return getPackageName();
    }

}
