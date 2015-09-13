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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.fluent.Request;
import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.shaded.compress.io.IOUtils;

/**
 * Provides utility methods for creating company specific binary Debian
 * packages.
 */
public final class DebUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DebUtils.class);

    private DebUtils() {
        throw new UnsupportedOperationException(
                "Cannot create an instance of a utility class");
    }

    /**
     * Downloads content from a source URL to a target file. If the target file
     * already exists, nothing happens. The file is first downloaded to the
     * 'temp' directory. If it already exists in that directory, only a local
     * copy is done.
     * 
     * @param url
     *            URL to load.
     * @param dir
     *            Target directory
     * @param cookies
     *            Cookies for the request (Format: "name=value").
     * 
     * @return Downloaded file.
     */
    public static File cachedDownload(@NotNull final URL url,
            @NotNull final File dir, final String... cookies) {
        Contract.requireArgNotNull("url", url);
        Contract.requireArgNotNull("dir", dir);

        LOG.info("cachedDownload: {}", url);

        final File targetFile = new File(dir, FilenameUtils.getName(url
                .getFile()));
        try {
            if (targetFile.exists()) {
                LOG.info("File already exists in target directory: {}",
                        targetFile);
            } else {
                LOG.info("Downloading: {}", url);
                // Cache the file locally in the temporary directory
                final File tmpFile = new File(Utils4J.getTempDir(),
                        targetFile.getName());
                if (!tmpFile.exists()) {
                    download(url, Utils4J.getTempDir(), cookies);
                    LOG.info("Downloaded to: {}", tmpFile);
                }
                FileUtils.copyFile(tmpFile, targetFile);
                LOG.info("Copied from '{}' to: {}", tmpFile, targetFile);
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Error downloading: " + url, ex);
        }
        return targetFile;

    }

    /**
     * Downloads a file from an URL to a file and outputs progress in the log
     * (level 'info') every 1000 bytes.
     * 
     * @param url
     *            URL to download.
     * @param dir
     *            Target directory.
     * @param cookies
     *            Cookies for the request (Format: "name=value").
     */
    public static void download(@NotNull final URL url,
            @NotNull final File dir, final String... cookies) {
        Contract.requireArgNotNull("url", url);
        Contract.requireArgNotNull("dir", dir);

        LOG.info("Download: {}", url);

        try {
            final Request request = Request.Get(url.toURI());
            if (cookies != null) {
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < cookies.length; i++) {
                    if (i > 0) {
                        sb.append(";");
                    }
                    sb.append(cookies[i]);
                }
                request.addHeader("Cookie", sb.toString());
            }
            final File file = new File(dir,
                    FilenameUtils.getName(url.getFile()));
            request.execute().saveContent(file);
        } catch (final IOException | URISyntaxException ex) {
            throw new RuntimeException("Error downloading: " + url, ex);
        }
    }

    /**
     * Reads the first folder found from TAR.GZ file.
     * 
     * @param tarGzFile
     *            Archive file to peek the first folder from.
     * 
     * @return The first directory name or <code>null</code> if the archive only
     *         contains files (no folders).
     */
    @Nullable
    public static String peekFirstTarGzFolderName(@NotNull final File tarGzFile) {
        Contract.requireArgNotNull("tarGzFile", tarGzFile);

        try {
            final TarArchiveInputStream tarIn = new TarArchiveInputStream(
                    new GzipCompressorInputStream(new BufferedInputStream(
                            new FileInputStream(tarGzFile))));
            try {
                TarArchiveEntry entry;
                while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        return entry.getName();
                    }
                }
            } finally {
                tarIn.close();
            }
            return null;
        } catch (final IOException ex) {
            throw new RuntimeException("Error uncompressing archive: "
                    + tarGzFile, ex);
        }
    }

    /**
     * Untars a given 'tar.gz' file on a linux system using the 'tar' command in
     * the directory where it is placed.
     * 
     * @param tarFile
     *            File to unpack.
     */
    public static final void unTarGz(@NotNull final File tarFile) {
        Contract.requireArgNotNull("tarFile", tarFile);

        final String tarFilePath = Utils4J.getCanonicalPath(tarFile);
        LOG.info("unTarGz: {}", tarFilePath);

        final CommandLine cmdLine = new CommandLine("tar");
        cmdLine.addArgument("-zvxf");
        cmdLine.addArgument(tarFilePath);

        execute(cmdLine, tarFile.getParentFile(), "unTarGz: " + tarFilePath);

    }

    /**
     * Creates a tar.gz file on a linux system using the 'tar' command.
     * 
     * @param parentDir
     *            Working directory.
     * @param dirName
     *            Directory inside the working directory that will be archived.
     * 
     * @return Tar file.
     */
    public static final File tarGz(@NotNull final File parentDir,
            final String dirName) {

        Contract.requireArgNotNull("parentDir", parentDir);
        Contract.requireArgNotNull("dirName", dirName);

        final File tarFile = new File(parentDir, dirName + ".tar.gz");
        final String tarFileNameAndPath = Utils4J.getCanonicalPath(tarFile);
        LOG.info("tarGz '{}': {}", dirName, tarFileNameAndPath);

        final CommandLine cmdLine = new CommandLine("tar");
        cmdLine.addArgument("-zvcf");
        cmdLine.addArgument(tarFileNameAndPath);
        cmdLine.addArgument(dirName);

        execute(cmdLine, parentDir, "tar file: " + tarFileNameAndPath);

        return tarFile;
    }

    /**
     * Reads a resource and writes the replaced string into a file with the same
     * name.
     * 
     * @param clasz
     *            Class to use for reading teh resource.
     * @param resource
     *            Full path to the resource.
     * @param outDir
     *            Output directory to create a file inside.
     * @param vars
     *            Variables to replace.
     */
    public static void writeReplacedResource(final Class<?> clasz,
            final String resource, final File outDir,
            final Map<String, String> vars) {

        LOG.info("Write replaced resource '{}' to directory: {}", resource,
                outDir);

        final File outFile = new File(outDir, FilenameUtils.getName(resource));
        try {
            final InputStream inStream = clasz.getResourceAsStream(resource);
            if (inStream == null) {
                throw new IllegalArgumentException(
                        "Unable to locate resource: " + resource);
            }
            try {
                final String inStr = IOUtils.toString(inStream);
                final String outStr = Utils4J.replaceVars(inStr, vars);
                LOG.debug("Write resource {}\n{}", outFile, outStr);
                FileUtils.writeStringToFile(outFile, outStr);
            } finally {
                inStream.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(
                    "Error replacing and writing resource file '" + resource
                            + "' to: " + outFile, ex);
        }
    }

    /**
     * Copies a resource to a file.
     * 
     * @param clasz
     *            Class to use for getting the resource.
     * @param resource
     *            Path and name of the resource.
     * @param targetFile
     *            Target file to create.
     */
    public static void copyResourceToFile(final Class<?> clasz,
            final String resource, final File targetFile) {
        LOG.info("Copy resource '{}' to file: {}", resource, targetFile);
        try {
            final InputStream in = clasz.getResourceAsStream(resource);
            try {
                FileUtils.copyInputStreamToFile(in, targetFile);
            } finally {
                in.close();
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Error while copying resource '"
                    + resource + "' to: " + targetFile, ex);
        }
    }

    /**
     * Returns the string as list.
     * 
     * @param str
     *            String to split into lines.
     * 
     * @return List of lines.
     */
    public static final List<String> asList(final String str) {
        try {
            final List<String> lines = new ArrayList<String>();
            final LineNumberReader reader = new LineNumberReader(
                    new StringReader(str));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (final IOException ex) {
            throw new RuntimeException("Error creating string list", ex);
        }
    }

    private static void logError(final List<String> messages) {
        if (LOG.isErrorEnabled()) {
            for (final String message : messages) {
                LOG.error(message);
            }
        }
    }

    private static void execute(final CommandLine cmdLine,
            final File workingDir, final String errorMsg) {

        LOG.debug("Execute: " + cmdLine.toString());

        final Executor executor = new DefaultExecutor();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final PumpStreamHandler psh = new PumpStreamHandler(bos);
        executor.setStreamHandler(psh);
        executor.setWorkingDirectory(workingDir);
        try {
            final int result = executor.execute(cmdLine);
            if (result != 0) {
                logError(asList(bos.toString()));
                throw new RuntimeException("Error # " + result + " / "
                        + errorMsg);
            }
        } catch (final IOException ex) {
            logError(asList(bos.toString()));
            throw new RuntimeException(errorMsg, ex);
        }
    }

    /**
     * Creates a directory (and all of it's parent directories) if it does not
     * exist. The method logs the process and throws a runtime exception if the
     * standard Java 'mkdirs' method returns false.
     * 
     * @param dir
     *            Directory to create.
     */
    public static void mkdirs(final File dir) {
        if (!dir.exists()) {
            final boolean ok = dir.mkdirs();
            if (!ok) {
                throw new RuntimeException("Couldn't create directory: " + dir);
            }
            LOG.debug("Created directory: {}", dir);
        } else {
            LOG.debug("Directory already exist: {}", dir);
        }
    }

    /**
     * Returns a list of variables as map.
     * 
     * @param vars
     *            Variables.
     * 
     * @return Same content as map.
     */
    public static Map<String, String> asMap(final List<Variable> vars) {
        if (vars == null) {
            return null;
        }
        final Map<String, String> map = new HashMap<>();
        for (final Variable var : vars) {
            map.put(var.getName(), var.getValue());
        }
        return map;
    }

}
