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
package com.b3dgs.lionheart.entity.monster;

import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.Patrol;

/**
 * Bee monster base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityMonsterBee
        extends EntityMonster
{
    /**
     * @see Entity#Entity(SetupSurfaceRasteredGame)
     */
    protected EntityMonsterBee(SetupSurfaceRasteredGame setup)
    {
        super(setup);
        enableMovement(Patrol.HORIZONTAL);
        enableMovement(Patrol.VERTICAL);
    }

    /*
     * EntityMonster
     */

    @Override
    protected void updateStates()
    {
        super.updateStates();
        mirror(false);
        if (status.getState() == EntityState.IDLE)
        {
            status.setState(EntityState.WALK);
        }
    }

    @Override
    protected void updateCollisions()
    {
        // Nothing to do
    }
}
