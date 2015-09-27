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

import java.io.File;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

//CHECKSTYLE:OFF for tests
public class DebPackagesTest {

    @Test
    public final void testMarshalUnmarshalXML() throws Exception {

        // PREPARE
        final String version = "1.2.3";
        final String description = "Aa Bb Cc";
        final String maintainer = "michael@fuin.org";
        final String arch = "amd64";
        final String installationPath = "/opt";
        final String section = "devel";
        final String priority = "low";
        final DebPackage package1 = new DebPackage1();
        final DebPackage package2 = new DebPackage2();
        final DebPackages original = new DebPackages(version, description,
                maintainer, arch, installationPath, section, priority, package1,
                package2);

        // TEST
        final String xml = marshal(original, createXmlAdapter(),
                DebPackages.class, DebPackage1.class, DebPackage2.class);

        // VERIFY
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert
                .assertXMLEqual(
                        XML_PREFIX
                                + "<packages version=\"1.2.3\" description=\"Aa Bb Cc\" "
                                + "maintainer=\"michael@fuin.org\" arch=\"amd64\" "
                                + "section=\"devel\" " + "priority=\"low\" "
                                + "installation-path=\"/opt\">"
                                + "<package1/> <package2/>" + "</packages>", xml);
        final DebPackages copy = unmarshal(xml, createXmlAdapter(),
                DebPackages.class, DebPackage1.class, DebPackage2.class);
        copy.init(null);
        assertThat(copy.getVersion()).isEqualTo("1.2.3");
        assertThat(copy.getDescription()).isEqualTo("Aa Bb Cc");
        assertThat(copy.getMaintainer()).isEqualTo("michael@fuin.org");
        assertThat(copy.getArch()).isEqualTo("amd64");
        assertThat(copy.getInstallationPath()).isEqualTo("/opt");
        assertThat(copy.getSection()).isEqualTo("devel");
        assertThat(copy.getPriority()).isEqualTo("low");
        assertThat(copy.getPackages()).hasSize(2);
        assertThat(copy.getPackages().get(0).getClass()).isEqualTo(
                DebPackage1.class);
        assertThat(copy.getPackages().get(1).getClass()).isEqualTo(
                DebPackage2.class);

    }

    private XmlAdapter<?, ?>[] createXmlAdapter() {
        // Not necessary now - Add XML adapter if needed later on...
        return new XmlAdapter[] {};
    }

    /**
     * Test package 1.
     */
    @XmlRootElement(name = "package1")
    public static class DebPackage1 extends DebPackage {

        @Override
        public final String getPackageName() {
            return "package1";
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

    /**
     * Test package 2.
     */
    @XmlRootElement(name = "package2")
    public static class DebPackage2 extends DebPackage {

        @Override
        public final String getPackageName() {
            return "package2";
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
