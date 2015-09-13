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

import java.util.List;

import javax.validation.constraints.NotNull;

import org.fuin.objects4j.common.Nullable;

/**
 * Contains a list of variables.
 */
public interface VariablesContainer {

    /**
     * Returns the list of variables.
     * 
     * @return Immutable list.
     */
    @Nullable
    public List<Variable> getVariables();

    /**
     * Returns the value of a variable.
     * 
     * @param name
     *            Name of the variable to find.
     * 
     * @return Value or <code>null</code> if the variable was not found.
     */
    @Nullable
    public String variableValue(@NotNull String name);

}
