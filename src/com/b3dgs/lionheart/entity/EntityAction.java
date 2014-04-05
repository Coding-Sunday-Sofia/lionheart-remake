/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.entity;

import com.b3dgs.lionengine.core.Keyboard;

/**
 * List of entity actions.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityAction
{
    /** Move left. */
    MOVE_LEFT(Keyboard.LEFT),
    /** Move right. */
    MOVE_RIGHT(Keyboard.RIGHT),
    /** Move down. */
    MOVE_DOWN(Keyboard.DOWN),
    /** Jump. */
    JUMP(Keyboard.UP),
    /** Attack. */
    ATTACK(Keyboard.CONTROL);

    /** Values. */
    public static final EntityAction[] VALUES = EntityAction.values();
    /** The key binding (used in case of control with keyboard). */
    private final Integer key;

    /**
     * Constructor.
     * 
     * @param key The key binding.
     */
    private EntityAction(Integer key)
    {
        this.key = key;
    }

    /**
     * Get the key binding.
     * 
     * @return The key binding.
     */
    public Integer getKey()
    {
        return key;
    }
}
