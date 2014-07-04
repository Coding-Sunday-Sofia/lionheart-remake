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

import java.util.Set;

import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.CollisionTileCategory;
import com.b3dgs.lionheart.map.TileCollision;

/**
 * List of entity collision categories on tile.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum EntityCollisionTileCategory implements CollisionTileCategory
{
    /** Default ground center collision. */
    GROUND_CENTER(TileCollision.COLLISION_VERTICAL);

    /** The collisions list. */
    private final Set<CollisionTile> collisions;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     */
    private EntityCollisionTileCategory(Set<CollisionTile> collisions)
    {
        this.collisions = collisions;
    }

    @Override
    public Set<CollisionTile> getCollisions()
    {
        return collisions;
    }
}
