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
package org.fuin.owndeb.modules.eclipse;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.fuin.owndeb.commons.DebDependency;
import org.fuin.owndeb.commons.DebModule;
import org.fuin.owndeb.commons.DebPackageResolver;
import org.fuin.owndeb.modules.jdk.JdkModule;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the {@link EclipseModule} class.
 */
// CHECKSTYLE:OFF for tests
public final class EclipseModuleTest {

    @Ignore("Download is too big - Think about better solution later")
    @Test
    public final void testCreate() {

        // PREPARE
        final String name = "eclipse-jee-luna";
        final String version = "4.4";
        final String description = "Eclipse Luna IDE for Java EE Developers";
        final String maintainer = "michael@fuin.org";
        final String arch = "amd64";
        final String installationPath = "/opt";
        final String section = "devel";
        final String priority = "low";
        final String url = "http://ftp.halifax.rwth-aachen.de/eclipse/technology/epp/downloads/release/luna/SR2/eclipse-jee-luna-SR2-linux-gtk-x86_64.tar.gz";
        final DebDependency dependency = createDependencyJdk8();
        final EclipseModule testee = new EclipseModule(name, version,
                description, maintainer, arch, installationPath, section,
                priority, url, dependency);
        testee.init(null);

        final File buildDir = new File("./target");

        // TEST
        testee.create(buildDir);

        // VERIFY
        final File changesFile = new File(buildDir,
                "eclipse-jee-luna_4.4_amd64.changes");
        final File debFile = new File(buildDir,
                "eclipse-jee-luna_4.4_amd64.deb");
        assertThat(changesFile).exists();
        assertThat(debFile).exists();

    }

    private DebDependency createDependencyJdk8() {
        final JdkModule jdkModule = new JdkModule("jdk8", "1.8.0.60",
                "Java SE Development Kit 8", "michael@fuin.org", "amd64",
                "/opt", "devel", "low", "");
        jdkModule.init(null);
        final DebDependency dependency = new DebDependency("jdk8");
        dependency.init(jdkModule);
        dependency.resolve(new DebPackageResolver() {
            @Override
            public DebModule findDebPackage(String packageName) {
                return jdkModule;
            }
        });
        return dependency;
    }

}
// CHECKSTYLE:ON
