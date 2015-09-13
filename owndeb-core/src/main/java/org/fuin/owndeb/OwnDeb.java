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
package org.fuin.owndeb;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.fuin.objects4j.common.Contract;
import org.fuin.owndeb.commons.DebConfig;
import org.fuin.owndeb.commons.DebModule;
import org.fuin.owndeb.commons.DebModules;
import org.fuin.owndeb.modules.jdk.JdkModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates binary Debian packages by reading an XML configuration and executing
 * it.
 */
public final class OwnDeb {

    private static final Logger LOG = LoggerFactory.getLogger(OwnDeb.class);

    private final DebConfig config;

    private final File targetDir;

    /**
     * Constructor with configuration.
     * 
     * @param config
     *            Configuration.
     * @param targetDir
     *            Directory to create the packages inside.
     */
    public OwnDeb(@NotNull final DebConfig config, @NotNull final File targetDir) {
        super();
        Contract.requireArgNotNull("config", config);
        Contract.requireArgNotNull("targetDir", targetDir);
        this.config = config;
        this.targetDir = targetDir;
    }

    /**
     * Constructor with configuration file.
     * 
     * @param configFile
     *            XML file.
     * @param targetDir
     *            Directory to create the packages inside.
     * @param jaxbClasses
     *            Classes to bind to the JAXB context.
     */
    public OwnDeb(@NotNull final File configFile,
            @NotNull final File targetDir, final Class<?>...jaxbClasses) {
        this(unmarshal(configFile, jaxbClasses), targetDir);
    }

    /**
     * Creates the packages defined in the configuration.
     */
    public final void execute() {
        LOG.info("Started creating packages");
        final List<DebModule> modules = config.getModules().getModules();
        for (final DebModule module : modules) {
            LOG.info("Creating module: {}", module.getModuleName());
            module.create(targetDir);
        }
        LOG.info("Finished creating packages");
    }

    /**
     * Unmarshals the given file.
     * 
     * @param configFile
     *            XML file to read.
     * @param jaxbClasses
     *            Classes to add to the context.
     * 
     * @return Data.
     * 
     * @param <T>
     *            Type of the expected data.
     */
    @SuppressWarnings("unchecked")
    private static <T> T unmarshal(final File configFile,
            final Class<?>... jaxbClasses) {
        Contract.requireArgNotNull("configFile", configFile);
        try {
            final JAXBContext ctx = JAXBContext.newInstance(jaxbClasses);
            final Unmarshaller unmarshaller = ctx.createUnmarshaller();
            unmarshaller.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(final ValidationEvent event) {
                    if (event.getSeverity() > 0) {
                        if (event.getLinkedException() == null) {
                            throw new RuntimeException(
                                    "Error unmarshalling the data: "
                                            + event.getMessage());
                        }
                        throw new RuntimeException(
                                "Error unmarshalling the data", event
                                        .getLinkedException());
                    }
                    return true;
                }
            });
            return (T) unmarshaller.unmarshal(configFile);
        } catch (final JAXBException ex) {
            throw new RuntimeException("Error unmarshalling test data", ex);
        }
    }

}
