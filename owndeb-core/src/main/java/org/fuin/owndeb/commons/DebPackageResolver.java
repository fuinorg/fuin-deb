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

import org.fuin.objects4j.common.Nullable;

/**
 * Locates a package by it's name.
 */
public interface DebPackageResolver {

    /**
     * Returns a package by it's unique name.
     * 
     * @param packageName
     *            Unique package name.
     * 
     * @return Package or <code>null</code> if no package with the given name
     *         exists.
     */
    @Nullable
    public DebPackage resolve(String packageName);

    /**
     * Resolver that always returns <code>null</code>.
     */
    public static final DebPackageResolver NONE = new DebPackageResolver() {
        @Override
        public final DebPackage resolve(final String packageName) {
            return null;
        }
    };

}
