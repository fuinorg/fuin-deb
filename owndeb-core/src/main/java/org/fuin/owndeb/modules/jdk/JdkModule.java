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

import static org.fuin.owndeb.commons.DebUtils.cachedDownload;
import static org.fuin.owndeb.commons.DebUtils.peekFirstTarGzFolderName;
import static org.fuin.owndeb.commons.DebUtils.tarGz;
import static org.fuin.owndeb.commons.DebUtils.unTarGz;
import static org.fuin.owndeb.commons.DebUtils.writeReplacedResource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;
import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;
import org.fuin.owndeb.commons.DebModule;
import org.fuin.owndeb.commons.DebPackage;
import org.fuin.owndeb.commons.DebUtils;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.ant.Data;
import org.vafer.jdeb.ant.DebAntTask;
import org.vafer.jdeb.ant.Mapper;

/**
 * Downloads and Oracle JDK and creates a binary Debian package from it.
 */
@XmlRootElement(name = "jdk-module")
public final class JdkModule extends DebModule {

    private static final Logger LOG = LoggerFactory.getLogger(JdkModule.class);

    @XmlAttribute(name = "url")
    private String urlStr;

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
                section, priority, packages);
        Contract.requireArgNotNull("jdkUrl", jdkUrl);
        this.urlStr = jdkUrl;
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
                section, priority, packages);
        Contract.requireArgNotNull("jdkUrl", jdkUrl);
        this.urlStr = jdkUrl;
    }

    @Override
    public final String getModuleName() {
        return "jdk-module";
    }

    @Override
    public final void create(final File buildDirectory) {

        Contract.requireArgNotNull("buildDirectory", buildDirectory);
        LOG.info("Creating module in: {}", buildDirectory);

        final File jdkArchiveFile = cachedDownload(url(urlStr), buildDirectory,
                "oraclelicense=accept-securebackup-cookie");

        final List<DebPackage> debPackages = getPackages();
        for (final DebPackage pkg : debPackages) {

            final DebPackage debPackage = new DebPackage(pkg);
            debPackage.applyPackageDefaults(this);

            LOG.info("Creating package: {}", debPackage.getName());

            final File packageDir = new File(buildDirectory,
                    debPackage.getName());
            final File controlDir = new File(buildDirectory,
                    debPackage.getName() + "-control");

            LOG.debug("packageDir: {}", packageDir);
            LOG.debug("controlDir: {}", controlDir);

            final File srcDir = rootJdkFolder(buildDirectory, jdkArchiveFile);
            if (srcDir.exists()) {
                LOG.info("JDK directory already exists: " + srcDir);
            } else {
                unTarGz(jdkArchiveFile);
                renameJdkToPackageDir(srcDir, packageDir);
            }
            final File tarFile = tarGz(buildDirectory, debPackage.getName());
            copyControlFiles(debPackage, getModuleName(), controlDir);
            createDebianPackage(debPackage, buildDirectory, packageDir,
                    controlDir, tarFile);

        }

    }

    @Override
    public final void replaceVariables(final Map<String, String> vars) {
        replaceModuleVariables(vars);
        urlStr = Utils4J.replaceVars(urlStr, vars);        
    }

    private static void renameJdkToPackageDir(final File srcDir,
            final File packageDir) {
        if (packageDir.exists()) {
            LOG.info("Delete existing package directory: {}", packageDir);
            FileUtils.deleteQuietly(packageDir);
        }
        LOG.info("Rename JDK root folder '{}' to: {}", srcDir, packageDir);
        try {
            FileUtils.moveDirectory(srcDir, packageDir);
        } catch (final IOException ex) {
            throw new RuntimeException("Error moving " + srcDir + " to: "
                    + packageDir, ex);
        }
    }

    private static File rootJdkFolder(final File buildDirectory,
            final File jdkArchiveFile) {
        final String folderName = peekFirstTarGzFolderName(jdkArchiveFile);
        if (folderName == null) {
            throw new IllegalArgumentException(
                    "Couldn't find directory in JDK archive: " + jdkArchiveFile);
        }
        return new File(buildDirectory, folderName);
    }

    private static void copyControlFiles(final DebPackage debPackage,
            final String moduleName, final File controlDir) {

        DebUtils.mkdirs(controlDir);
        final Map<String, String> vars = debPackage.getVariables();
        writeReplacedResource(JdkModule.class, "/" + moduleName + "/control",
                controlDir, vars);
        writeReplacedResource(JdkModule.class, "/" + moduleName + "/postinst",
                controlDir, vars);

    }

    private static void createDebianPackage(final DebPackage debPackage,
            final File buildDirectory, final File packageDir,
            final File controlDir, final File tarFile) {

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
        data.setSrc(tarFile);
        data.setType("archive");
        final Mapper mapper = new Mapper();
        mapper.setType("perm");
        mapper.setPrefix(debPackage.getInstallationPath());
        mapper.setUser("root");
        mapper.setGroup("developer");
        data.addMapper(mapper);

        task.addData(data);

        task.execute();

        LOG.info("Finished creating Debian package");

    }

}
