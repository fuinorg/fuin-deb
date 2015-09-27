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
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.fuin.owndeb.commons.DebUtils;
import org.junit.Test;

/**
 * Tests the {@link EclipseIniModifier} class.
 */
// CHECKSTYLE:OFF for tests
public class EclipseIniModifierTest {

    @Test
    public void testUnmodified() throws IOException {

        // PREPARE
        final File file = copyResourceToTempFile("/eclipse/eclipse-unmodified.ini");
        final File expectedFile = copyResourceToTempFile("/eclipse/eclipse.ini.expected");
        final EclipseIniModifier testee = new EclipseIniModifier(file);

        // TEST
        testee.setVm("/opt/Oracle_Java/jdk1.8.0_60/bin/java");
        testee.setVmargs("-Dosgi.requiredJavaVersion=1.6 -XX:MaxPermSize=256m -Xms128m -Xmx1024m");
        testee.save();

        // VERIFY
        assertThat(file).hasSameContentAs(expectedFile);

    }

    @Test
    public void testModified() throws IOException {

        // PREPARE
        final File file = copyResourceToTempFile("/eclipse/eclipse-modified.ini");
        final File expectedFile = copyResourceToTempFile("/eclipse/eclipse.ini.expected");
        final EclipseIniModifier testee = new EclipseIniModifier(file);

        // TEST
        testee.setVm("/opt/Oracle_Java/jdk1.8.0_60/bin/java");
        testee.setVmargs("-Dosgi.requiredJavaVersion=1.6 -XX:MaxPermSize=256m -Xms128m -Xmx1024m");
        testee.save();

        // VERIFY
        assertThat(file).hasSameContentAs(expectedFile);

    }

    private File copyResourceToTempFile(final String resource)
            throws IOException {
        final String name = FilenameUtils.getName(resource);
        final File file = File.createTempFile("test-" + name + "-", null);
        file.deleteOnExit();
        DebUtils.copyResourceToFile(this.getClass(), resource, file);
        return file;
    }

}
// CHECKSTYLE:ON
