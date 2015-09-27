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
import org.fuin.owndeb.modules.eclipse.EclipsePackage;
import org.fuin.owndeb.modules.eclipseplugin.EclipsePluginPackage;
import org.fuin.owndeb.modules.jdk.JdkPackage;
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
        final DebPackages packages = new DebPackages(version, description,
                maintainer, arch, installationPath, section, priority,
                new DebPackageA());
        testee = new DebConfig(packages);
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
                    "The argument 'packages' cannot be null");
        }
    }

    @Test
    public final void testMarshalUnmarshalXML() throws Exception {

        // PREPARE
        final DebConfig original = testee;

        // TEST
        String xml = marshal(original, createXmlAdapter(), DebConfig.class,
                DebPackageA.class);

        // VERIFY
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(XML_PREFIX + "<owndeb-config>"
                + "<packages version=\"1.2.3\" description=\"Aa Bb Cc\" "
                + "maintainer=\"michael@fuin.org\" arch=\"amd64\" "
                + "section=\"devel\" " + "priority=\"low\" "
                + "installation-path=\"/opt\">" + "<packageA/>" + "</packages>"
                + "</owndeb-config>", xml);

    }

    @Test
    public final void testMarshalUnmarshalEquals() throws Exception {

        // PREPARE
        final DebConfig original = testee;

        // TEST
        final String xml = marshal(original, createXmlAdapter(),
                DebConfig.class, DebPackageA.class);

        final DebConfig copy = unmarshal(xml, createXmlAdapter(),
                DebConfig.class, DebPackageA.class);

        // VERIFY
        final DebPackages packages = copy.getPackages();
        assertThat(packages.getVersion()).isEqualTo("1.2.3");
        assertThat(packages.getDescription()).isEqualTo("Aa Bb Cc");
        assertThat(packages.getMaintainer()).isEqualTo("michael@fuin.org");
        assertThat(packages.getArch()).isEqualTo("amd64");
        assertThat(packages.getInstallationPath()).isEqualTo("/opt");
        assertThat(packages.getSection()).isEqualTo("devel");
        assertThat(packages.getPriority()).isEqualTo("low");
        assertThat(packages.getPackages()).hasSize(1);
        assertThat(packages.getPackages().get(0).getClass()).isEqualTo(
                DebPackageA.class);
    }

    @Test
    public final void testUnmarshalFile() {

        // PREPARE
        final URL url = Utils4J.url("classpath:owndeb-config.xml");
        final String xml = Utils4J.readAsString(url, "utf-8", 1024);

        // TEST
        final DebConfig config = unmarshal(xml, createXmlAdapter(),
                DebConfig.class, JdkPackage.class, EclipsePackage.class,
                EclipsePluginPackage.class);

        // VERIFY
        assertThat(config).isNotNull();
        assertThat(config.getPackages()).isNotNull();
        assertThat(config.getPackages().getPackages()).isNotNull();
        assertThat(config.getPackages().getPackages()).hasSize(5);

        final JdkPackage jdkPackage = (JdkPackage) config.getPackages()
                .getPackages().get(0);
        assertThat(jdkPackage.getName()).isEqualTo("fuin-jdk8");
        assertThat(jdkPackage.getVersion()).isEqualTo("1.8.0.60");
        assertThat(jdkPackage.getDescription()).isEqualTo(
                "Java SE Development Kit 8");
        assertThat(jdkPackage.getMaintainer()).isEqualTo("michael@fuin.org");
        assertThat(jdkPackage.getArch()).isEqualTo("amd64");
        assertThat(jdkPackage.getInstallationPath()).isEqualTo("/opt");
        assertThat(jdkPackage.getSection()).isEqualTo("devel");
        assertThat(jdkPackage.getPriority()).isEqualTo("low");
        assertThat(jdkPackage.getUrlStr())
                .isEqualTo(
                        "http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jdk-8u60-linux-x64.tar.gz");

        final EclipsePackage lunaPackage = (EclipsePackage) config.getPackages()
                .getPackages().get(1);
        assertThat(lunaPackage.getName()).isEqualTo("fuin-eclipse-jee-luna");
        assertThat(lunaPackage.getVersion()).isEqualTo("4.4");
        assertThat(lunaPackage.getDescription()).isEqualTo(
                "Eclipse Luna IDE for Java EE Developers");
        assertThat(lunaPackage.getVm()).isEqualTo("/opt/fuin-jdk8/bin/java");
        assertThat(lunaPackage.getVmArgs())
                .isEqualTo(
                        "-Dosgi.requiredJavaVersion=1.6 -XX:MaxPermSize=256m -Xms128m -Xmx1024m");

        final EclipsePackage marsPackage = (EclipsePackage) config.getPackages()
                .getPackages().get(2);
        assertThat(marsPackage.getName()).isEqualTo("fuin-eclipse-jee-mars");
        assertThat(marsPackage.getVersion()).isEqualTo("4.5");
        assertThat(marsPackage.getDescription()).isEqualTo(
                "Eclipse Mars IDE for Java EE Developers");

    }

    private XmlAdapter<?, ?>[] createXmlAdapter() {
        // Not necessary now - Add XML adapter if needed later on...
        return new XmlAdapter[] {};
    }

    /**
     * Test package A.
     */
    @XmlRootElement(name = "packageA")
    public static class DebPackageA extends DebPackage {

        @Override
        public final String getPackageName() {
            return "packageA";
        }

        @Override
        public final void create(final File buildDirectory) {
            // Do nothing
        }

        @Override
        public final void init(final DebPackages parent) {
            // Do nothing
        }

    }

}
// CHECKSTYLE:ON
