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
package org.fuin.owndeb.modules.eclipse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.validation.constraints.NotNull;

import org.fuin.objects4j.common.Contract;
import org.fuin.objects4j.common.NotEmpty;

/**
 * Modifies an existing original (unmodified) 'eclipse.ini' file.
 */
public final class EclipseIniModifier {

    private final File file;

    private final List<String> lines;

    /**
     * Constructor with file to modify.
     * 
     * @param file
     *            Eclipse ini file to modify.
     */
    public EclipseIniModifier(@NotNull final File file) {
        super();
        Contract.requireArgNotNull("file", file);
        this.file = file;
        this.lines = readFile(file);
    }

    /**
     * Sets the "-vm" option. An existing option will be replaced.
     * 
     * @param pathToBinJava
     *            Path to "bin/java".
     */
    public final void setVm(@NotEmpty final String pathToBinJava) {
        Contract.requireArgNotEmpty("pathToBinJava", pathToBinJava);
        final int idxVm = lines.indexOf("-vm");
        if (idxVm < 0) {
            // No VM defined
            final int idxVmargs = lines.indexOf("-vmargs");
            if (idxVmargs < 0) {
                // Append at the end
                lines.add("-vm");
                lines.add(pathToBinJava);
            } else {
                lines.add(idxVmargs, "-vm");
                lines.add(idxVmargs + 1, pathToBinJava);
            }

        } else {
            // Replace existing VM definition
            lines.set(idxVm + 1, pathToBinJava);
        }
    }

    /**
     * Replaces the existing 'vmargs' settings.
     * 
     * @param options
     *            New '-vmargs' options.
     */
    public final void setVmargs(@NotEmpty final String options) {
        Contract.requireArgNotEmpty("options", options);
        final List<String> optionList = new ArrayList<>();
        final StringTokenizer tok = new StringTokenizer(options, " ");
        while (tok.hasMoreTokens()) {
            optionList.add(tok.nextToken());
        }
        setVmargs(optionList);
    }

    /**
     * Replaces the existing 'vmargs' settings.
     * 
     * @param options
     *            New '-vmargs' options.
     */
    public final void setVmargs(@NotNull final String... options) {
        Contract.requireArgNotNull("options", options);
        setVmargs(Arrays.asList(options));
    }

    /**
     * Replaces the existing 'vmargs' settings.
     * 
     * @param options
     *            New '-vmargs' options.
     */
    public final void setVmargs(@NotNull final List<String> options) {
        Contract.requireArgNotNull("options", options);
        final int idxVm = lines.indexOf("-vmargs");
        if (idxVm < 0) {
            // No vmargs defined
            lines.add("-vmargs");
            lines.addAll(options);
        } else {
            // Replace existing vmargs
            // Assuming that all lines until the end are arguments
            for (int idx = lines.size() - 1; idx > idxVm; idx--) {
                lines.remove(idx);
            }
            lines.addAll(options);
        }
    }

    /**
     * Saves the changes back to disk. This overrides the original file.
     */
    public final void save() {
        try {
            final FileWriter fw = new FileWriter(file);
            try {
                for (final String line : lines) {
                    fw.write(line);
                    fw.write(System.getProperty("line.separator"));
                }
            } finally {
                fw.close();
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Error writing file: " + file, ex);
        }
    }

    private static List<String> readFile(final File file) {
        try {
            final List<String> lines = new ArrayList<>();
            final LineNumberReader reader = new LineNumberReader(
                    new FileReader(file));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } finally {
                reader.close();
            }
            return lines;
        } catch (final IOException ex) {
            throw new RuntimeException("Error reading file: " + file, ex);
        }
    }

}
