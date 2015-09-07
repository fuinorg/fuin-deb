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
package org.fuin.deb.commons;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.fuin.utils4j.Utils4J;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

//CHECKSTYLE:OFF for tests
@RunWith(MockitoJUnitRunner.class)
public class FuinDebUtilsTest {

    @Mock
    private Appender mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Before
    public void setup() {
        final Logger logger = (Logger) LoggerFactory
                .getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(mockAppender);
    }

    @After
    public void teardown() {
        final Logger logger = (Logger) LoggerFactory
                .getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(mockAppender);
    }

    @Test
    public void testCachedWgetAlreadyExistsInTarget() throws IOException {

        // PREPARE
        final File targetDir = new File("./target").getCanonicalFile();
        final File targetFile = new File(targetDir, "smiley.gif");
        FuinDebUtils.copyResourceToFile(getClass(), "/smiley.gif", targetFile);

        // TEST
        FuinDebUtils.cachedWget(
                new URL("http://www.fuin.org/images/smiley.gif"), targetDir);

        // VERIFY
        verify(mockAppender).doAppend(captorLoggingEvent.capture());
        final LoggingEvent loggingEvent = captorLoggingEvent.getValue();
        assertThat(loggingEvent.getLevel(), is(Level.INFO));
        assertThat(loggingEvent.getFormattedMessage(),
                is("File already exists in target directory: " + targetFile));

    }

    @Test
    public void testCachedWgetAlreadyExistsInTemp() throws IOException {

        // PREPARE
        final File targetDir = new File("./target").getCanonicalFile();
        final File targetFile = new File(targetDir, "smiley.gif");
        final File tempFile = new File(Utils4J.getTempDir(), "smiley.gif");
        FuinDebUtils.copyResourceToFile(getClass(), "/smiley.gif", tempFile);
        FileUtils.deleteQuietly(targetFile);

        // TEST
        final File resultFile = FuinDebUtils.cachedWget(new URL(
                "http://www.fuin.org/images/smiley.gif"), targetDir);

        // VERIFY
        verify(mockAppender, times(2)).doAppend(captorLoggingEvent.capture());
        final List<LoggingEvent> events = captorLoggingEvent.getAllValues();

        final LoggingEvent loggingEvent1 = events.get(0);
        assertThat(loggingEvent1.getLevel(), is(Level.INFO));
        assertThat(loggingEvent1.getFormattedMessage(),
                is("Downloading: http://www.fuin.org/images/smiley.gif"));

        final LoggingEvent loggingEvent2 = events.get(1);
        assertThat(loggingEvent2.getLevel(), is(Level.INFO));
        assertThat(loggingEvent2.getFormattedMessage(),
                is("Copied from '/tmp/smiley.gif' to: " + targetFile));

    }

    @Test
    public void testCachedWgetDoesNotExist() throws IOException {

        // PREPARE
        final File targetDir = new File("./target").getCanonicalFile();
        final File targetFile = new File(targetDir, "smiley.gif");
        final File tempFile = new File(Utils4J.getTempDir(), "smiley.gif");
        FileUtils.deleteQuietly(targetFile);
        FileUtils.deleteQuietly(tempFile);

        // TEST
        final File resultFile = FuinDebUtils.cachedWget(new URL(
                "http://www.fuin.org/images/smiley.gif"), targetDir);

        // VERIFY
        verify(mockAppender, times(3)).doAppend(captorLoggingEvent.capture());
        final List<LoggingEvent> events = captorLoggingEvent.getAllValues();

        final LoggingEvent loggingEvent1 = events.get(0);
        assertThat(loggingEvent1.getLevel(), is(Level.INFO));
        assertThat(loggingEvent1.getFormattedMessage(),
                is("Downloading: http://www.fuin.org/images/smiley.gif"));

        final LoggingEvent loggingEvent2 = events.get(1);
        assertThat(loggingEvent2.getLevel(), is(Level.INFO));
        assertThat(loggingEvent2.getFormattedMessage(),
                is("Downloaded to: /tmp/smiley.gif"));

        final LoggingEvent loggingEvent3 = events.get(2);
        assertThat(loggingEvent3.getLevel(), is(Level.INFO));
        assertThat(loggingEvent3.getFormattedMessage(),
                is("Copied from '/tmp/smiley.gif' to: " + targetFile));

    }

    @Test
    public void testWget() throws MalformedURLException {

        // PREPARE
        final File expectedFile = new File(Utils4J.getTempDir(),
                "test-smiley.gif");
        FuinDebUtils
                .copyResourceToFile(getClass(), "/smiley.gif", expectedFile);
        final File file = new File(Utils4J.getTempDir(), "smiley.gif");
        FileUtils.deleteQuietly(file);

        // TEST
        FuinDebUtils.wget(new URL("http://www.fuin.org/images/smiley.gif"),
                Utils4J.getTempDir());

        // VERIFY
        assertThat(file).hasSameContentAs(expectedFile);

    }

    @Test
    public void testPeekFirstTarGzFolderName() {

        // PREPARE
        final File targetFile = new File(Utils4J.getTempDir(),
                "test-dir.tar.gz");
        FuinDebUtils.copyResourceToFile(getClass(), "/test-dir.tar.gz",
                targetFile);

        // TEST
        final String folderName = FuinDebUtils
                .peekFirstTarGzFolderName(targetFile);

        // VERIFY
        assertThat(folderName).isEqualTo("test-dir/");

    }

    @Test
    public void testUntarTar() throws IOException {

        // PREPARE
        final File tarGzFile = new File(Utils4J.getTempDir(), "test-dir.tar.gz");
        FileUtils.deleteDirectory(new File(Utils4J.getTempDir(), "test-dir"));
        FileUtils.deleteQuietly(tarGzFile);
        FuinDebUtils.copyResourceToFile(getClass(), "/test-dir.tar.gz",
                tarGzFile);

        // TEST
        FuinDebUtils.unTarGz(tarGzFile);
        final File copy = FuinDebUtils.tarGz(Utils4J.getTempDir(), "test-dir");

        // VERIFY
        assertThat(tarGzFile).hasSameContentAs(copy);

    }

    @Test
    public void testWriteReplacedResource() {

        // PREPARE
        final String filename = "write-replaced-resource.txt";
        final File file = new File(Utils4J.getTempDir(), filename);
        FileUtils.deleteQuietly(file);
        FuinDebUtils.copyResourceToFile(getClass(), "/" + filename, file);

        final String expectedFilename = "write-replaced-resource-expected.txt";
        final File expectedFile = new File(Utils4J.getTempDir(),
                expectedFilename);
        FileUtils.deleteQuietly(expectedFile);
        FuinDebUtils.copyResourceToFile(getClass(), "/" + expectedFilename,
                expectedFile);

        final Map<String, String> vars = new HashMap<>();
        vars.put("a", "Abc");
        vars.put("b", "Def");

        // TEST
        FuinDebUtils.writeReplacedResource(getClass(),
                "/write-replaced-resource.txt", Utils4J.getTempDir(), vars);

        // VERIFY
        assertThat(file).hasSameContentAs(expectedFile);

    }

    @Test
    public void testAsList() {

        // PREPARE
        final String str = "abc\ndef\n\nghi";

        // TEST
        final List<String> list = FuinDebUtils.asList(str);

        // VERIFY
        assertThat(list).containsExactly("abc", "def", "", "ghi");

    }

}
// CHECKSTYLE:ON
