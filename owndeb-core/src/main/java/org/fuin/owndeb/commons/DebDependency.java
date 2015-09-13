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

import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.NotEmpty;
import org.fuin.objects4j.common.Nullable;
import org.fuin.utils4j.Utils4J;

/**
 * Dependency to a package. Redefines equals and hash code based on the name.
 */
@XmlRootElement(name = "dependency")
public final class DebDependency {

    @XmlAttribute(name = "name")
    private String name;

    private transient DebPackage resolvedDependency;

    /**
     * Default constructor for JAXB.
     */
    protected DebDependency() {
        super();
    }

    /**
     * Constructor with mandatory data.
     * 
     * @param name
     *            Unique name of the dependency.
     */
    public DebDependency(@NotEmpty final String name) {
        super();
        Contract.requireArgNotEmpty("name", name);
        this.name = name;
    }

    /**
     * Returns the name of the referenced package.
     * 
     * @return Referenced package name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the package that corresponds to the name if it already was
     * resolved.
     * 
     * @return Referenced package.
     */
    public final DebPackage getResolvedDependency() {
        return resolvedDependency;
    }

    /**
     * Tries to resolve the package the is referenced by the name. After this
     * method was called the {@link #resolvedDependency} is set if a module with
     * that name was found.
     * 
     * @param resolver
     *            Contains all known packages.
     * 
     * @return TRUE if the package could be resolved.
     */
    public final boolean resolve(@NotNull final DebPackageResolver resolver) {
        Contract.requireArgNotNull("resolver", resolver);
        resolvedDependency = resolver.findDebPackage(name);
        return resolvedDependency != null;
    }

    /**
     * Replaces variables in the properties.
     * 
     * @param vars
     *            Variables to use.
     */
    public final void replaceVariables(@Nullable final Map<String, String> vars) {
        name = Utils4J.replaceVars(name, vars);
    }
    
    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    @Override
    public final boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DebDependency other = (DebDependency) obj;
        return name.equals(other.name);
    }

    @Override
    public final String toString() {
        return "DebDependency [name=" + name + "]";
    }

}
