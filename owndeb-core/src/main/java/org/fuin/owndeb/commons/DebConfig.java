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
package org.fuin.owndeb.commons;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;

/**
 * Configuration for OwnDeb.
 */
@XmlRootElement(name = "owndeb-config")
public final class DebConfig {

    @XmlElement
    private DebModules modules;

    /**
     * Default constructor.
     */
    public DebConfig() {
        super();
    }

    /**
     * Constructor with modules.
     * 
     * @param modules
     *            Modules object.
     */
    public DebConfig(@Nullable final DebModules modules) {
        super();
        Contract.requireArgNotNull("modules", modules);
        this.modules = modules;
    }

    /**
     * Returns the list of modules to create.
     * 
     * @return Immutable modules list.
     */
    public final DebModules getModules() {
        return modules;
    }

    /**
     * Called after the configuration was unmarshalled using JAXB.
     */
    public final void afterUnmarshal(final Unmarshaller unmarshaller, final Object parent) {
        if (modules != null) {
            modules.applyDefaults();
        }
    }

}
