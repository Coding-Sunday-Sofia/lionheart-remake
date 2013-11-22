/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionheart;

/**
 * List of game levels.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum LevelType
{
    /** Stage 1. */
    SWAMP_1("level1");

    /** Total number of levels. */
    public static final int LEVELS_NUMBER = LevelType.values().length;
    /** Level filename. */
    private final String filename;

    /**
     * Constructor.
     * 
     * @param filename The level filename.
     */
    private LevelType(String filename)
    {
        this.filename = filename + "." + Level.FILE_FORMAT;
    }

    /**
     * Get the level filename.
     * 
     * @return The level filename.
     */
    public String getFilename()
    {
        return filename;
    }
}
