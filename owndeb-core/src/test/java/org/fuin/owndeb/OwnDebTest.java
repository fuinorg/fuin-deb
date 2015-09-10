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
package org.fuin.owndeb;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.fuin.objects4j.common.ContractViolationException;
import org.fuin.owndeb.commons.DebConfig;
import org.fuin.owndeb.commons.DebModule;
import org.fuin.owndeb.commons.DebModules;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

//CHECKSTYLE:OFF for tests
@RunWith(MockitoJUnitRunner.class)
public class OwnDebTest {

    private static final File TARGET_DIR = new File("./target");

    @Mock
    private DebModule module1;

    @Captor
    private ArgumentCaptor<File> arg1;

    @Mock
    private DebModule module2;

    @Captor
    private ArgumentCaptor<File> arg2;

    private OwnDeb testee;

    @Before
    public void setup() {
        final DebModules modules = new DebModules("1.2.3", "Whatever", "abc-",
                "your-name@mydomain.tld", "amd64", "/opt", "devel", "low",
                module1, module2);
        final DebConfig config = new DebConfig(modules);
        testee = new OwnDeb(config, TARGET_DIR);
    }

    @After
    public void teardown() {
        testee = null;
    }

    @Test
    public void testExecute() {

        // PREPARE

        // TEST
        testee.execute();

        // VERIFY
        verify(module1).create(arg1.capture());
        assertThat(arg1.getValue()).isEqualTo(TARGET_DIR);
        verify(module2).create(arg2.capture());
        assertThat(arg2.getValue()).isEqualTo(TARGET_DIR);

    }

    @Test
    public void testNullConfig() {
        try {
            new OwnDeb((DebConfig) null, TARGET_DIR);
            fail();
        } catch (final ContractViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo(
                    "The argument 'config' cannot be null");
        }
    }

    @Test
    public void testNullTargetDir() {
        try {
            new OwnDeb(new DebConfig(), null);
            fail();
        } catch (final ContractViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo(
                    "The argument 'targetDir' cannot be null");
        }
    }

    @Test
    public void testNullConfigFile() {
        try {
            new OwnDeb((File) null, TARGET_DIR, DebConfig.class);
            fail();
        } catch (final ContractViolationException ex) {
            assertThat(ex.getMessage()).isEqualTo(
                    "The argument 'configFile' cannot be null");
        }
    }

}
// CHECKSTYLE:ON
