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
package org.fuin.owndeb.pkg.base;

import static org.fuin.owndeb.commons.DebUtils.cachedDownload;
import static org.fuin.owndeb.commons.DebUtils.peekFirstTarGzFolderName;
import static org.fuin.owndeb.commons.DebUtils.tarGz;
import static org.fuin.owndeb.commons.DebUtils.unTarGz;
import static org.fuin.utils4j.Utils4J.url;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.io.FileUtils;
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
 * Downloads an archive and creates a binary Debian package from it.
 */
public abstract class AbstractDownloadTarGzPackage extends DebPackage {

    private static final String URL = "url";

    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractDownloadTarGzPackage.class);

    @XmlAttribute(name = URL)
    private String urlStr;

    /**
     * Default constructor for JAXB.
     */
    protected AbstractDownloadTarGzPackage() {
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
    public AbstractDownloadTarGzPackage(@NotEmpty final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String url,
            @Nullable final List<DebDependency> dependencies) {
        super(name, version, description, maintainer, arch, installationPath,
                section, priority, dependencies);
        Contract.requireArgNotNull(URL, url);
        this.urlStr = url;
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
     *            Array of dependencies.
     */
    public AbstractDownloadTarGzPackage(@NotEmpty final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String maintainer, @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String section, @Nullable final String priority,
            @NotNull final String url,
            @Nullable final DebDependency... dependencies) {
        super(name, version, description, maintainer, arch, installationPath,
                section, priority, dependencies);
        Contract.requireArgNotNull(URL, url);
        this.urlStr = url;
    }

    @Override
    public final void create(final File buildDirectory) {

        Contract.requireArgNotNull("buildDirectory", buildDirectory);
        LOG.info("Creating package '{}' in: {}", getPackageName(),
                buildDirectory);

        final File archiveFile = cachedDownload(url(urlStr), buildDirectory);

        final File packageDir = new File(buildDirectory, getName());
        final File controlDir = new File(buildDirectory, getName() + "-control");

        LOG.debug("packageDir: {}", packageDir);
        LOG.debug("controlDir: {}", controlDir);

        final File srcDir = rootArchiveFolder(buildDirectory, archiveFile);
        if (srcDir.exists()) {
            LOG.debug("Directory already exists: " + srcDir);
            FileUtils.deleteQuietly(srcDir);
        }
        unTarGz(archiveFile);
        renameOriginalToPackageDir(srcDir, packageDir);
        applyModifications(packageDir);

        final File tarFile = tarGz(buildDirectory, getName());
        DebUtils.mkdirs(controlDir);
        copyControlFiles(controlDir);
        createDebianPackage(this, buildDirectory, packageDir, controlDir,
                tarFile);

    }

    /**
     * Initialize base stuff.
     * 
     * @param parent
     *            Parent to set.
     */
    protected final void initDownloadTarGzPackage(final DebPackages parent) {
        initPackage(parent);
        addOrReplaceVariable(URL, urlStr);
    }

    /**
     * Returns the URL.
     * 
     * @return URL.
     */
    public final String getUrlStr() {
        return variableValue(URL);
    }

    /**
     * Returns the URL.
     * 
     * @return URL.
     */
    public final URL getUrl() {
        return url(getUrlStr());
    }

    private static void renameOriginalToPackageDir(final File srcDir,
            final File packageDir) {
        if (packageDir.exists()) {
            LOG.info("Delete existing package directory: {}", packageDir);
            FileUtils.deleteQuietly(packageDir);
        }
        LOG.info("Rename original directory '{}' to: {}", srcDir, packageDir);
        try {
            FileUtils.moveDirectory(srcDir, packageDir);
        } catch (final IOException ex) {
            throw new RuntimeException("Error moving " + srcDir + " to: "
                    + packageDir, ex);
        }
    }

    private static File rootArchiveFolder(final File buildDirectory,
            final File archiveFile) {
        final String folderName = peekFirstTarGzFolderName(archiveFile);
        if (folderName == null) {
            throw new IllegalArgumentException(
                    "Couldn't find directory in archive: " + archiveFile);
        }
        return new File(buildDirectory, folderName);
    }

    private static void createDebianPackage(final DebPackage debPackage,
            final File buildDirectory, final File packageDir,
            final File controlDir, final File tarFile) {

        LOG.info("Start creating package " + debPackage.getName());

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

        LOG.info("Finished creating package " + debPackage.getName());

    }

    /**
     * Modifies the original package content.
     * 
     * @param packageDir
     *            Directory that contains the package content.
     */
    protected abstract void applyModifications(@NotNull File packageDir);

    /**
     * Copies the control files for the given package into the control
     * directory.
     * 
     * @param controlDir
     *            Target control directory.
     */
    protected abstract void copyControlFiles(@NotNull File controlDir);

}
