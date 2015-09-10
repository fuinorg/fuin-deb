/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * <http://www.fuin.org/>
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
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fuin.owndeb.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Reads an XML configuration file and creates a number of binary Debian
 * packages using it.
 */
@Mojo(name = "create", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, requiresProject = false)
public final class OwnDebMojo extends AbstractMojo {

    private static final Logger LOG = LoggerFactory.getLogger(OwnDebMojo.class);

    /** Location of the XML configuration file. */
    @Parameter(name = "config-file")
    private File configFile;

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

    @Override
    public final void execute() throws MojoExecutionException {
        StaticLoggerBinder.getSingleton().setMavenLog(getLog());
        LOG.info("config-file={}", configFile);

    }

}
