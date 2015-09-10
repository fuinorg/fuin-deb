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
import static org.fuin.owndeb.commons.DebUtils.XML_PREFIX;
import static org.fuin.units4j.Units4JUtils.marshal;
import static org.fuin.units4j.Units4JUtils.unmarshal;

import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

//CHECKSTYLE:OFF for tests
public class DebModulesTest {

    @Test
    public final void testMarshalUnmarshalXML() throws Exception {

        // PREPARE
        final String version = "1.2.3";
        final String description = "Aa Bb Cc";
        final String prefix = "fuin-";
        final String maintainer = "michael@fuin.org";
        final String arch = "amd64";
        final String installationPath = "/opt";
        final String section = "devel";
        final String priority = "low";
        final DebModule module1 = new DebModule1();
        final DebModule module2 = new DebModule2();
        final DebModules original = new DebModules(version, description,
                prefix, maintainer, arch, installationPath, section, priority,
                module1, module2);

        // TEST
        final String xml = marshal(original, createXmlAdapter(),
                DebModules.class, DebModule1.class, DebModule2.class);

        // VERIFY
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert
                .assertXMLEqual(
                        XML_PREFIX
                                + "<modules version=\"1.2.3\" description=\"Aa Bb Cc\" "
                                + "prefix=\"fuin-\" maintainer=\"michael@fuin.org\" arch=\"amd64\" "
                                + "section=\"devel\" " + "priority=\"low\" "
                                + "installation-path=\"/opt\">"
                                + "<module1/> <module2/>" + "</modules>", xml);
        final DebModules copy = unmarshal(xml, createXmlAdapter(),
                DebModules.class, DebModule1.class, DebModule2.class);
        assertThat(copy.getVersion()).isEqualTo("1.2.3");
        assertThat(copy.getDescription()).isEqualTo("Aa Bb Cc");
        assertThat(copy.getPrefix()).isEqualTo("fuin-");
        assertThat(copy.getMaintainer()).isEqualTo("michael@fuin.org");
        assertThat(copy.getArch()).isEqualTo("amd64");
        assertThat(copy.getInstallationPath()).isEqualTo("/opt");
        assertThat(copy.getSection()).isEqualTo("devel");
        assertThat(copy.getPriority()).isEqualTo("low");
        assertThat(copy.getModules()).hasSize(2);
        assertThat(copy.getModules().get(0).getClass()).isEqualTo(
                DebModule1.class);
        assertThat(copy.getModules().get(1).getClass()).isEqualTo(
                DebModule2.class);

    }

    private XmlAdapter<?, ?>[] createXmlAdapter() {
        // Not necessary now - Add XML adapter if needed later on...
        return new XmlAdapter[] {};
    }

    /**
     * Test module 1.
     */
    @XmlRootElement(name = "module1")
    public static class DebModule1 extends DebModule {

        @Override
        public final String getModuleName() {
            return "module1";
        }

        @Override
        public final void create(final File buildDirectory) {
            // Do nothing
        }

    }

    /**
     * Test module 2.
     */
    @XmlRootElement(name = "module2")
    public static class DebModule2 extends DebModule {

        @Override
        public final String getModuleName() {
            return "module2";
        }

        @Override
        public final void create(final File buildDirectory) {
            // Do nothing
        }

    }

}
// CHECKSTYLE:ON
