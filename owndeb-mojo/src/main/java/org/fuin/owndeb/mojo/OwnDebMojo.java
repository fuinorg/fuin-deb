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
package org.fuin.owndeb.mojo;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.fuin.owndeb.OwnDeb;
import org.fuin.owndeb.commons.DebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Reads an XML configuration file and creates a number of binary Debian
 * packages using it.
 */
@Mojo(name = "create", requiresProject = false)
public final class OwnDebMojo extends AbstractMojo {

    private static final Logger LOG = LoggerFactory.getLogger(OwnDebMojo.class);

    /** Location of the XML configuration file. */
    @Parameter(name = "config-file")
    private File configFile;

    /** The target build directory. */
    @Parameter(name = "target-dir", property = "project.build.directory")
    private File targetDir = new File("./target");

    /** A list of package classes to be bound to the JAXB context. */
    @Parameter(name = "package-classes")
    private String[] packageClasses;

    /**
     * Returns the list of package classes to be bound to the JAXB context.
     * 
     * @return Full qualified package class names.
     */
    public final String[] getPackageClasses() {
        return packageClasses;
    }

    /**
     * Sets the list of package classes to be bound to the JAXB context.
     * 
     * @param moduleClasses
     *            Full qualified package class names.
     */
    public final void setPackageClasses(final String[] moduleClasses) {
        this.packageClasses = moduleClasses;
    }

    /**
     * Checks if a variable is not <code>null</code> and throws an
     * <code>IllegalNullArgumentException</code> if this rule is violated.
     * 
     * @param name
     *            Name of the variable to be displayed in an error message.
     * @param value
     *            Value to check for <code>null</code>.
     * 
     * @throws MojoExecutionException
     *             Checked value was NULL.
     */
    protected final void checkNotNull(final String name, final Object value)
            throws MojoExecutionException {
        if (value == null) {
            throw new MojoExecutionException(name + " cannot be null!");
        }
    }

    /**
     * Returns the configuration file.
     * 
     * @return XML config to load.
     */
    public final File getConfigFile() {
        return configFile;
    }

    /**
     * Sets the configuration file.
     * 
     * @param configFile
     *            XML config file to load.
     */
    public final void setConfigFile(final File configFile) {
        this.configFile = configFile;
    }

    /**
     * Returns the target directory.
     * 
     * @return Target directory.
     */
    public final File getTargetDir() {
        return targetDir;
    }

    /**
     * Sets the target directory.
     * 
     * @param targetDir
     *            Target directory to set.
     */
    public final void setTargetDir(final File targetDir) {
        this.targetDir = targetDir;
    }

    @Override
    public final void execute() throws MojoExecutionException {
        StaticLoggerBinder.getSingleton().setMavenLog(getLog());

        LOG.info("config-file={}", configFile);
        LOG.debug("target-dir={}", targetDir);

        checkNotNull("config-file", configFile);
        checkNotNull("target-dir", targetDir);

        new OwnDeb(configFile, targetDir, getJaxbContextClasses(this.getClass()
                .getClassLoader())).execute();
    }

    private Class<?>[] getJaxbContextClasses(final ClassLoader classLoader)
            throws MojoExecutionException {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(DebConfig.class);
        if ((packageClasses != null) && (packageClasses.length > 0)) {
            for (final String name : packageClasses) {
                LOG.info("Load package class: " + name);
                try {
                    final Class<?> clasz = classLoader.loadClass(name);
                    classes.add(clasz);
                } catch (final ClassNotFoundException ex) {
                    throw new MojoExecutionException(
                            "Class to add to JAXB context not found: " + name,
                            ex);
                }
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }

}
