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

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.fuin.owndeb.modules.example.ExampleModule;
import org.junit.Test;

/**
 * Test for {@link OwnDebMojo}.
 */
public class OwnDebMojoTest {

    // CHECKSTYLE:OFF Test

    @Test
    public void testExecute() throws MojoExecutionException {

        // PREPARE
        final File buildDir = new File("./target");
        File file = new File(this.getClass().getResource("/test-config.xml")
                .getFile());
        final OwnDebMojo testee = new OwnDebMojo();
        testee.setConfigFile(file);
        testee.setTargetDir(buildDir);
        testee.setModuleClasses(new String[] { ExampleModule.class.getName() });

        // TEST
        testee.execute();

        // VERIFY

        final File changesFile1 = new File(buildDir,
                "abc-p1_1.2.3_amd64.changes");
        final File debFile1 = new File(buildDir, "abc-p1_1.2.3_amd64.deb");
        assertThat(changesFile1).exists();
        assertThat(debFile1).exists();

        final File changesFile2 = new File(buildDir,
                "abc-p2_1.2.3_amd64.changes");
        final File debFile2 = new File(buildDir, "abc-p2_1.2.3_amd64.deb");
        assertThat(changesFile1).exists();
        assertThat(debFile1).exists();

    }

    // CHECKSTYLE:ON

}
