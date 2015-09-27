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
package org.fuin.owndeb.modules.example;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.fuin.owndeb.commons.DebPackage;
import org.fuin.owndeb.commons.DebPackageResolver;
import org.junit.Test;

/**
 * Tests the {@link ExamplePackage} class.
 */
// CHECKSTYLE:OFF for tests
public final class ExamplePackageTest {

    @Test
    public final void testCreate() {

        // PREPARE
        final String name = "example1";
        final String version = "1.0.0";
        final String description = "Example Package 1";
        final String maintainer = "michael@fuin.org";
        final String arch = "amd64";
        final String installationPath = "/opt";
        final String section = "devel";
        final String priority = "low";
        final ExamplePackage testee = new ExamplePackage(name, version, description,
                maintainer, arch, installationPath, section, priority);
        testee.init(null);
        testee.resolveDependencies(new DebPackageResolver() {
            @Override
            public DebPackage findDebPackage(final String packageName) {
                // Nothing to resolve in this test
                return null;
            }
        });

        final File buildDir = new File("./target");

        // TEST
        testee.create(buildDir);

        // VERIFY
        final File changesFile = new File(buildDir,
                "example1_1.0.0_amd64.changes");
        final File debFile = new File(buildDir, "example1_1.0.0_amd64.deb");
        assertThat(changesFile).exists();
        assertThat(debFile).exists();

    }

}
// CHECKSTYLE:ON
