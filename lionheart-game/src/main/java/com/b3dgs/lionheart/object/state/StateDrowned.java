/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object.state;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionheart.Sfx;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;
import com.b3dgs.lionheart.object.feature.Drownable;
import com.b3dgs.lionheart.object.feature.Stats;

/**
 * Drowned state implementation.
 */
public final class StateDrowned extends State
{
    /** Drown limit drown vertical position. */
    private static final int DROWN_END_Y = -60;
    /** Drown fall speed. */
    private static final double DEATH_FALL_SPEED = -0.7;

    private final Stats stats = model.getFeature(Stats.class);
    private final Drownable drownable = model.getFeature(Drownable.class);

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    public StateDrowned(EntityModel model, Animation animation)
    {
        super(model, animation);

        addTransition(StateIdle.class, () -> transformable.getY() < DROWN_END_Y);
    }

    @Override
    public void enter()
    {
        super.enter();

        stats.applyDamages(stats.getHealth());
        movement.setDirection(DirectionNone.INSTANCE);
        movement.setDestination(0.0, 0.0);
        Sfx.VALDYN_DIE.play();
    }

    @Override
    public void exit()
    {
        super.exit();

        transformable.teleport(216, 64);
        model.getCamera().resetInterval(transformable);
        stats.fillHealth();
        stats.decreaseLife();
        drownable.recycle();
    }

    @Override
    public void update(double extrp)
    {
        body.resetGravity();
        model.getMovement().setDestination(0.0, DEATH_FALL_SPEED);
    }
}
