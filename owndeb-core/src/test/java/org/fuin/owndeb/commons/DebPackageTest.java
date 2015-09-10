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
import static org.junit.Assert.fail;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.fuin.objects4j.common.ContractViolationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//CHECKSTYLE:OFF for tests
public class DebPackageTest {

    private static final String NAME = "jdk8";

    private DebPackage testee;

    @Before
    public void setup() {
        testee = new DebPackage(NAME);
    }

    @After
    public void teardown() {
        testee = null;
    }

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(DebPackage.class)
                .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void testNullName() {
        try {
            new DebPackage((String) null);
            fail();
        } catch (final ContractViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo(
                    "The argument 'name' cannot be null");
        }
    }

    @Test
    public void testEmptyName() {
        try {
            new DebPackage("");
            fail();
        } catch (final ContractViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo(
                    "The argument 'name' cannot be empty");
        }
    }

    @Test
    public final void testMarshalUnmarshalXML() throws Exception {

        // PREPARE
        final String name = "abc";
        final String version = "1.2.3";
        final String description = "Aa Bb Cc";
        final String prefix = "fuin-";
        final String maintainer = "michael@fuin.org";
        final String arch = "amd64";
        final String installationPath = "/opt";
        final String section = "devel";
        final String priority = "low";
        final DebDependency depDef = new DebDependency("def");
        final DebDependency depGhi = new DebDependency("ghi");
        final DebPackage original = new DebPackage(name, version, description,
                prefix, maintainer, arch, installationPath, section, priority,
                depDef, depGhi);

        // TEST
        final String xml = marshal(original, createXmlAdapter(),
                DebPackage.class);

        // VERIFY
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert
                .assertXMLEqual(
                        XML_PREFIX
                                + "<package name=\"abc\" version=\"1.2.3\" description=\"Aa Bb Cc\" "
                                + "prefix=\"fuin-\" maintainer=\"michael@fuin.org\" arch=\"amd64\" "
                                + "section=\"devel\" "
                                + "priority=\"low\" "
                                + "installation-path=\"/opt\">"
                                + "    <dependency name=\"def\"/>"
                                + "    <dependency name=\"ghi\"/>"
                                + "</package>", xml);
        final DebPackage copy = unmarshal(xml, createXmlAdapter(),
                DebPackage.class);
        assertThat(copy.getName()).isEqualTo("abc");
        assertThat(copy.getVersion()).isEqualTo("1.2.3");
        assertThat(copy.getDescription()).isEqualTo("Aa Bb Cc");
        assertThat(copy.getPrefix()).isEqualTo("fuin-");
        assertThat(copy.getMaintainer()).isEqualTo("michael@fuin.org");
        assertThat(copy.getArch()).isEqualTo("amd64");
        assertThat(copy.getInstallationPath()).isEqualTo("/opt");
        assertThat(copy.getSection()).isEqualTo("devel");
        assertThat(copy.getPriority()).isEqualTo("low");
        assertThat(copy.getDependencies()).containsOnly(depDef, depGhi);
        assertThat(copy.getDebFilename()).isEqualTo("fuin-abc_1.2.3_amd64.deb");
        assertThat(copy.getPrefixedName()).isEqualTo("fuin-abc");
    }

    @Test
    public final void testMarshalUnmarshalEquals() throws Exception {

        // PREPARE
        final DebPackage original = testee;

        // TEST
        final String xml = marshal(original, createXmlAdapter(),
                DebPackage.class);

        final DebPackage copy = unmarshal(xml, createXmlAdapter(),
                DebPackage.class);

        // VERIFY
        assertThat(copy).isEqualTo(original);
    }

    private XmlAdapter<?, ?>[] createXmlAdapter() {
        // Not necessary now - Add XML adapter if needed later on...
        return new XmlAdapter[] {};
    }

}
// CHECKSTYLE:ON
