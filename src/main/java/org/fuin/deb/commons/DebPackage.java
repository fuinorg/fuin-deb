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
package org.fuin.deb.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;

/**
 * A binary Debian package to create. Equals and hash code are based on the
 * name.
 */
public final class DebPackage extends AbstractPackage {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "dependency")
    private List<DebDependency> dependencies;

    /**
     * Default constructor for JAXB.
     */
    protected DebPackage() {
        super();
    }

    /**
     * Constructor with mandatory data.
     * 
     * @param name
     *            Unique package name.
     */
    public DebPackage(@NotNull final String name) {
        super();
        Contract.requireArgNotNull("name", name);
        this.name = name;
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
     * @param prefix
     *            Prefix used to build the package like "fuin-".
     * @param maintainer
     *            Maintainer of the package.
     * @param arch
     *            Architecture identifier like "amd64".
     * @param installationPath
     *            Installation path like "/opt".
     * @param dependencies
     *            Array of dependencies.
     */
    public DebPackage(@NotNull final String name,
            @Nullable final String version, @Nullable final String description,
            @Nullable final String prefix, @Nullable final String maintainer,
            @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final DebDependency... dependencies) {
        super(version, description, prefix, maintainer, arch, installationPath);
        Contract.requireArgNotNull("name", name);
        this.name = name;
        if (dependencies == null) {
            this.dependencies = null;
        } else {
            this.dependencies = Arrays.asList(dependencies);
        }
    }

    /**
     * Constructor with package list.
     * 
     * @param name
     *            Unique package name.
     * @param prefix
     *            Prefix used to build the package like "fuin-".
     * @param maintainer
     *            Maintainer of the package.
     * @param arch
     *            Architecture identifier like "amd64".
     * @param installationPath
     *            Installation path like "/opt".
     * @param version
     *            Package version.
     * @param description
     *            Package description.
     * @param dependencies
     *            List of dependencies.
     */
    public DebPackage(@NotNull final String name,
            @Nullable final String prefix, @Nullable final String maintainer,
            @Nullable final String arch,
            @Nullable final String installationPath,
            @Nullable final String version, @Nullable final String description,
            @Nullable final List<DebDependency> dependencies) {
        super(prefix, maintainer, arch, installationPath, version, description);
        Contract.requireArgNotNull("name", name);
        this.name = name;
        this.dependencies = dependencies;
    }

    /**
     * Copy constructor.
     * 
     * @param other
     *            Package to copy.
     */
    public DebPackage(@NotNull final DebPackage other) {
        super();
        Contract.requireArgNotNull("other", other);
        copyPackageIfNotAlreadySet(other);
        this.name = other.name;
        if (other.dependencies == null) {
            this.dependencies = null;
        } else {
            this.dependencies = new ArrayList<>(other.dependencies);
        }
    }

    /**
     * Returns the package name.
     * 
     * @return Unique package name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the prefixed package name.
     * 
     * @return Unique prefixed package name.
     */
    public final String getPrefixedName() {
        return getPrefix() + name;
    }

    /**
     * Returns the Debian filename.
     * 
     * @return Filename of the package.
     */
    public final String getDebFilename() {
        return getPrefixedName() + "_" + getVersion() + "_" + getArch()
                + ".deb";
    }

    /**
     * Returns the list of dependencies.
     * 
     * @return Immutable dependency list.
     */
    @Nullable
    public final List<DebDependency> getDependencies() {
        if (dependencies == null) {
            return null;
        }
        return Collections.unmodifiableList(dependencies);
    }

    /**
     * Returns the list of dependencies as comma separated string.
     * 
     * @return All dependency names.
     */
    @NotNull
    public final String getDependenciesAsControlString() {
        final StringBuilder sb = new StringBuilder();
        if (dependencies != null) {
            for (int i = 0; i < dependencies.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                final DebDependency dependency = dependencies.get(i);
                final DebPackage debPackage = dependency
                        .getResolvedDependency();
                if (debPackage == null) {
                    throw new IllegalStateException("Unresolved dependency: "
                            + dependency.getName());
                }
                sb.append(debPackage.getPrefixedName());
            }
        }
        return sb.toString();
    }

    @Override
    public final int hashCode() {
        return name.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DebPackage other = (DebPackage) obj;
        return name.equals(other.name);
    }

    @Override
    public final String toString() {
        return "DebPackage [name=" + name + "]";
    }

}
