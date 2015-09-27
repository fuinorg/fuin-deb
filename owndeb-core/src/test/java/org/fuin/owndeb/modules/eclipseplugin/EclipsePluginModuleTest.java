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
package org.fuin.owndeb.modules.eclipseplugin;

import static org.fest.assertions.Assertions.assertThat;
import static org.fuin.utils4j.JaxbUtils.unmarshal;

import java.io.File;
import java.net.URL;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.fuin.owndeb.commons.DebConfig;
import org.fuin.owndeb.modules.eclipse.EclipseModule;
import org.fuin.owndeb.modules.jdk.JdkModule;
import org.fuin.utils4j.Utils4J;
import org.junit.Test;

/**
 * Tests the {@link EclipsePluginModule} class.
 */
// CHECKSTYLE:OFF for tests
public final class EclipsePluginModuleTest {

    @Test
    public final void testCreate() {

        // PREPARE
        final URL url = Utils4J.url("classpath:owndeb-config.xml");
        final String xml = Utils4J.readAsString(url, "utf-8", 1024);
        final DebConfig config = unmarshal(xml, createXmlAdapter(),
                DebConfig.class, JdkModule.class, EclipseModule.class,
                EclipsePluginModule.class);
        final EclipsePluginModule testee = (EclipsePluginModule) config
                .getModules().getModules().get(3);

        final File buildDir = new File("./target");

        // TEST
        testee.create(buildDir);

        // VERIFY
        final File changesFileLuna = new File(buildDir,
                "fuin-eclipse-jee-luna-plugins-checkstyle_6.5.0_amd64.changes");
        final File debFileLuna = new File(buildDir,
                "fuin-eclipse-jee-luna-plugins-checkstyle_6.5.0_amd64.deb");
        assertThat(changesFileLuna).exists();
        assertThat(debFileLuna).exists();

    }

    private XmlAdapter<?, ?>[] createXmlAdapter() {
        // Not necessary now - Add XML adapter if needed later on...
        return new XmlAdapter[] {};
    }

}
// CHECKSTYLE:ON
