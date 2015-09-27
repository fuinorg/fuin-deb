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
import org.junit.Test;

/**
 * Tests the {@link ExampleModule} class.
 */
// CHECKSTYLE:OFF for tests
public final class ExampleModuleTest {

    @Test
    public final void testCreate() {

        // PREPARE
        final String version = "1.0.0";
        final String description = "Example Module 1";
        final String maintainer = "michael@fuin.org";
        final String arch = "amd64";
        final String installationPath = "/opt";
        final String section = "devel";
        final String priority = "low";
        final DebPackage pkg = new DebPackage("example1");
        final ExampleModule testee = new ExampleModule(version, description,
                maintainer, arch, installationPath, section, priority, pkg);
        testee.init(null);

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
