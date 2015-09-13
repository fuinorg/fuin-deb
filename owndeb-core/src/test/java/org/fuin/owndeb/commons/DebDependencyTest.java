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
public class DebDependencyTest {

    private static final String NAME = "jdk8";

    private DebDependency testee;

    @Before
    public void setup() {
        testee = new DebDependency(NAME);
    }

    @After
    public void teardown() {
        testee = null;
    }

    @Test
    public void testEqualsHashCode() {
        EqualsVerifier.forClass(DebDependency.class)
                .suppress(Warning.NONFINAL_FIELDS, Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void testNullName() {
        try {
            new DebDependency(null);
            fail();
        } catch (final ContractViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo(
                    "The argument 'name' cannot be null");
        }
    }

    @Test
    public void testEmptyName() {
        try {
            new DebDependency("");
            fail();
        } catch (final ContractViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo(
                    "The argument 'name' cannot be empty");
        }
    }

    @Test
    public final void testMarshalUnmarshalXML() throws Exception {

        // PREPARE
        final DebDependency original = testee;

        // TEST
        String xml = marshal(original, createXmlAdapter(), DebDependency.class);

        // VERIFY
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(XML_PREFIX + "<dependency name=\"jdk8\"/>",
                xml);

    }

    @Test
    public final void testMarshalUnmarshalEquals() throws Exception {

        // PREPARE
        final DebDependency original = testee;

        // TEST
        final String xml = marshal(original, createXmlAdapter(),
                DebDependency.class);

        final DebDependency copy = unmarshal(xml, createXmlAdapter(),
                DebDependency.class);

        // VERIFY
        assertThat(copy).isEqualTo(original);
    }

    @Test
    public final void testResolveTrue() {

        // PREPARE
        final DebPackage pkg = new DebPackage(NAME);
        final DebPackageResolver resolver = new DebPackageResolver() {
            @Override
            public DebPackage resolve(final String packageName) {
                return pkg;
            }
        };

        // TEST
        final boolean ok = testee.resolve(resolver);

        // VERIFY
        assertThat(ok).isTrue();
        assertThat(testee.getResolvedDependency()).isSameAs(pkg);

    }

    @Test
    public final void testResolveFalse() {

        // PREPARE
        final DebPackageResolver resolver = new DebPackageResolver() {
            @Override
            public DebPackage resolve(final String packageName) {
                return null;
            }
        };

        // TEST
        final boolean ok = testee.resolve(resolver);

        // VERIFY
        assertThat(ok).isFalse();
        assertThat(testee.getResolvedDependency()).isNull();

    }

    private XmlAdapter<?, ?>[] createXmlAdapter() {
        // Not necessary now - Add XML adapter if needed later on...
        return new XmlAdapter[] {};
    }

}
// CHECKSTYLE:ON
