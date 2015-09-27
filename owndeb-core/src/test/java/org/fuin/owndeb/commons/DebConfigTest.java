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

import static org.fest.assertions.Assertions.assertThat;
import static org.fuin.utils4j.JaxbUtils.XML_PREFIX;
import static org.fuin.utils4j.JaxbUtils.marshal;
import static org.fuin.utils4j.JaxbUtils.unmarshal;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.fuin.objects4j.common.ContractViolationException;
import org.fuin.owndeb.modules.eclipse.EclipseModule;
import org.fuin.owndeb.modules.eclipseplugin.EclipsePluginModule;
import org.fuin.owndeb.modules.jdk.JdkModule;
import org.fuin.utils4j.Utils4J;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//CHECKSTYLE:OFF for tests
public class DebConfigTest {

    private DebConfig testee;

    @Before
    public void setup() {
        final String version = "1.2.3";
        final String description = "Aa Bb Cc";
        final String maintainer = "michael@fuin.org";
        final String arch = "amd64";
        final String installationPath = "/opt";
        final String section = "devel";
        final String priority = "low";
        final DebModules modules = new DebModules(version, description,
                maintainer, arch, installationPath, section, priority,
                new DebModuleA());
        testee = new DebConfig(modules);
    }

    @After
    public void teardown() {
        testee = null;
    }

    @Test
    public void testNullConfig() {
        try {
            new DebConfig(null);
            fail();
        } catch (final ContractViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo(
                    "The argument 'modules' cannot be null");
        }
    }

    @Test
    public final void testMarshalUnmarshalXML() throws Exception {

        // PREPARE
        final DebConfig original = testee;

        // TEST
        String xml = marshal(original, createXmlAdapter(), DebConfig.class,
                DebModuleA.class);

        // VERIFY
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(XML_PREFIX + "<owndeb-config>"
                + "<modules version=\"1.2.3\" description=\"Aa Bb Cc\" "
                + "maintainer=\"michael@fuin.org\" arch=\"amd64\" "
                + "section=\"devel\" " + "priority=\"low\" "
                + "installation-path=\"/opt\">" + "<moduleA/>" + "</modules>"
                + "</owndeb-config>", xml);

    }

    @Test
    public final void testMarshalUnmarshalEquals() throws Exception {

        // PREPARE
        final DebConfig original = testee;

        // TEST
        final String xml = marshal(original, createXmlAdapter(),
                DebConfig.class, DebModuleA.class);

        final DebConfig copy = unmarshal(xml, createXmlAdapter(),
                DebConfig.class, DebModuleA.class);

        // VERIFY
        final DebModules modules = copy.getModules();
        assertThat(modules.getVersion()).isEqualTo("1.2.3");
        assertThat(modules.getDescription()).isEqualTo("Aa Bb Cc");
        assertThat(modules.getMaintainer()).isEqualTo("michael@fuin.org");
        assertThat(modules.getArch()).isEqualTo("amd64");
        assertThat(modules.getInstallationPath()).isEqualTo("/opt");
        assertThat(modules.getSection()).isEqualTo("devel");
        assertThat(modules.getPriority()).isEqualTo("low");
        assertThat(modules.getModules()).hasSize(1);
        assertThat(modules.getModules().get(0).getClass()).isEqualTo(
                DebModuleA.class);
    }

    @Test
    public final void testUnmarshalFile() {

        // PREPARE
        final URL url = Utils4J.url("classpath:owndeb-config.xml");
        final String xml = Utils4J.readAsString(url, "utf-8", 1024);

        // TEST
        final DebConfig config = unmarshal(xml, createXmlAdapter(),
                DebConfig.class, JdkModule.class, EclipseModule.class,
                EclipsePluginModule.class);

        // VERIFY
        assertThat(config).isNotNull();
        assertThat(config.getModules()).isNotNull();
        assertThat(config.getModules().getModules()).isNotNull();
        assertThat(config.getModules().getModules()).hasSize(5);

        final JdkModule jdkModule = (JdkModule) config.getModules()
                .getModules().get(0);
        assertThat(jdkModule.getVersion()).isEqualTo("1.8.0.60");
        assertThat(jdkModule.getDescription()).isEqualTo(
                "Java SE Development Kit 8");
        assertThat(jdkModule.getMaintainer()).isEqualTo("michael@fuin.org");
        assertThat(jdkModule.getArch()).isEqualTo("amd64");
        assertThat(jdkModule.getInstallationPath()).isEqualTo("/opt");
        assertThat(jdkModule.getSection()).isEqualTo("devel");
        assertThat(jdkModule.getPriority()).isEqualTo("low");
        assertThat(jdkModule.getUrlStr())
                .isEqualTo(
                        "http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jdk-8u60-linux-x64.tar.gz");

        final EclipseModule lunaModule = (EclipseModule) config.getModules()
                .getModules().get(1);
        assertThat(lunaModule.getVersion()).isEqualTo("4.4");
        assertThat(lunaModule.getDescription()).isEqualTo(
                "Eclipse Luna IDE for Java EE Developers");
        assertThat(lunaModule.getVm()).isEqualTo("/opt/fuin-jdk8/bin/java");
        assertThat(lunaModule.getVmArgs())
                .isEqualTo(
                        "-Dosgi.requiredJavaVersion=1.6 -XX:MaxPermSize=256m -Xms128m -Xmx1024m");

        final EclipseModule marsModule = (EclipseModule) config.getModules()
                .getModules().get(2);
        assertThat(marsModule.getVersion()).isEqualTo("4.5");
        assertThat(marsModule.getDescription()).isEqualTo(
                "Eclipse Mars IDE for Java EE Developers");

    }

    private XmlAdapter<?, ?>[] createXmlAdapter() {
        // Not necessary now - Add XML adapter if needed later on...
        return new XmlAdapter[] {};
    }

    /**
     * Test module 1.
     */
    @XmlRootElement(name = "moduleA")
    public static class DebModuleA extends DebModule {

        @Override
        public final String getModuleName() {
            return "moduleA";
        }

        @Override
        public final void create(final File buildDirectory) {
            // Do nothing
        }

        @Override
        public final void init(final DebModules parent) {
            // Do nothing
        }

    }

}
// CHECKSTYLE:ON
