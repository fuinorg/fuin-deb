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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.Nullable;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdeb.shaded.compress.io.IOUtils;

/**
 * Provides utility methods for creating company specific binary Debian packages.
 */
public final class FuinDebUtils {

    private static final Logger LOG = LoggerFactory
            .getLogger(FuinDebUtils.class);

    private FuinDebUtils() {
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
     * @param targetFile
     *            Position where to store the result of the download.
     */
    public static void cachedDownload(@NotNull final URL url,
            @NotNull final File targetFile) {
        Contract.requireArgNotNull("url", url);
        Contract.requireArgNotNull("targetFile", targetFile);

        try {
            if (targetFile.exists()) {
                LOG.info("File already exists in target directory: {}",
                        targetFile);
            } else {
                LOG.info("Dowloading: {}", url);
                // Cache the file locally in the temporary directory
                final File tmpFile = new File(Utils4J.getTempDir(),
                        targetFile.getName());
                if (!tmpFile.exists()) {
                    download(url, tmpFile);
                    LOG.info("Downloaded to: {}", tmpFile);
                }
                FileUtils.copyFile(tmpFile, targetFile);
                LOG.info("Copied from '{}' to: {}", tmpFile, targetFile);
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Error downloading: " + url, ex);
        }
    }

    /**
     * Downloads a file from an URL to a file and outputs progress in the log
     * (level 'info') every 1000 bytes.
     * 
     * @param url
     *            URL to download.
     * @param file
     *            Target file to write.
     */
    public static void download(@NotNull final URL url, @NotNull final File file) {
        Contract.requireArgNotNull("url", url);
        Contract.requireArgNotNull("file", file);
        
        // wget --no-check-certificate 
        //      --no-cookies 
        //      --header "Cookie: oraclelicense=accept-securebackup-cookie" 
        //      http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jdk-8u60-linux-x64.tar.gz
        
        try {
            final InputStream in = new CountingInputStream(url.openStream()) {

                private int called = 0;

                @Override
                protected final void afterRead(final int n) {
                    super.afterRead(n);
                    called++;
                    if ((called % 1000) == 0) {
                        LOG.info("{} - {} bytes", file.getName(), getCount());
                    }
                }
            };
            try {
                FileUtils.copyInputStreamToFile(in, file);
            } finally {
                in.close();
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Error downloading from " + url
                    + " to: " + file, ex);
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
     * Untars a given file on a linux system using the 'tar' command in the
     * directory where it is placed.
     * 
     * @param tarFile
     *            File to unpack.
     */
    public static final void linuxUntar(@NotNull final File tarFile) {
        Contract.requireArgNotNull("tarFile", tarFile);

        final String tarFilePath = Utils4J.getCanonicalPath(tarFile);

        final CommandLine cmdLine = new CommandLine("tar");
        cmdLine.addArgument("-zvxf");
        cmdLine.addArgument(tarFilePath);
        final Executor executor = new DefaultExecutor();
        executor.setWorkingDirectory(tarFile.getParentFile());
        try {
            final int result = executor.execute(cmdLine);
            if (result != 0) {
                throw new RuntimeException("Error # " + result
                        + " while trying to untar file: " + tarFilePath);
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Error while trying untar file: "
                    + tarFilePath, ex);
        }
    }

    /**
     * Creates a tar file on a linux system using the 'tar' command.
     * 
     * @param parentDir
     *            Working directory.
     * @param dirName
     *            Directory inside the working directory that will be archived.
     * 
     * @return Tar file.
     */
    public static final File linuxTar(@NotNull final File parentDir,
            final String dirName) {

        Contract.requireArgNotNull("parentDir", parentDir);
        Contract.requireArgNotNull("dirName", dirName);

        final File tarFile = new File(parentDir, dirName + ".tar.gz");
        final String tarFilePath = Utils4J.getCanonicalPath(tarFile);

        final CommandLine cmdLine = new CommandLine("tar");
        cmdLine.addArgument("-cvjf");
        cmdLine.addArgument(tarFilePath);
        cmdLine.addArgument(dirName);

        final Executor executor = new DefaultExecutor();
        executor.setWorkingDirectory(parentDir);
        try {
            final int result = executor.execute(cmdLine);
            if (result != 0) {
                throw new RuntimeException("Error # " + result
                        + " while trying to tar file: " + tarFilePath);
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Error while trying tar file: "
                    + tarFilePath, ex);
        }

        return tarFile;
    }

    /**
     * Reads a resource and writes the replaced string into a file with the same
     * name.
     * 
     * @param clasz
     *            Class to use for reading teh resource.
     * @param resourceName
     *            Full path to the resource.
     * @param outDir
     *            Output directory to create a file inside.
     * @param vars
     *            Variables to replace.
     */
    public static void writeReplacedResource(final Class<?> clasz,
            final String resourceName, final File outDir,
            final Map<String, String> vars) {

        final File outFile = new File(outDir,
                FilenameUtils.getName(resourceName));
        try {
            final InputStream inStream = clasz
                    .getResourceAsStream(resourceName);
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
                    "Error replacing and writing resource file '"
                            + resourceName + "' to: " + outFile, ex);
        }
    }
}
