/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionheart.object.state;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionheart.object.EntityModel;
import com.b3dgs.lionheart.object.State;

/**
 * Liana soar state implementation.
 */
final class StateLianaSoar extends State
{
    private final int FRAME_6 = 6;
    private final int FRAME_9 = 9;

    private final int OFFSET_1 = 3;
    private final int OFFSET_6 = -37;
    private final int OFFSET_9 = -62;

    private static final double SOAR_SPEED = 0.85;

    /** Handle frame vertical specific offset for rendering. */
    private final AnimatorFrameListener listener;

    /** Initial horizontal location. */
    private double y;
    /** Progressive offset during soar. */
    private double offset;
    /** Specific frame offset computed by listener. */
    private int frameOffset;
    /** Soar side (1 rising, -1 descending). */
    private int side = 1;

    /**
     * Create the state.
     * 
     * @param model The model reference.
     * @param animation The animation reference.
     */
    StateLianaSoar(EntityModel model, Animation animation)
    {
        super(model, animation);

        listener = (AnimatorFrameListener) frame ->
        {
            if (frame - animation.getFirst() < FRAME_6)
            {
                frameOffset = OFFSET_1;
            }
            if (frame - animation.getFirst() >= FRAME_6 && frame - animation.getFirst() < FRAME_9)
            {
                frameOffset = OFFSET_6;
            }
            else if (frame - animation.getFirst() >= FRAME_9)
            {
                frameOffset = OFFSET_9;
            }
            rasterable.setFrameOffsets(0, (int) Math.floor(offset) + frameOffset);
        };

        addTransition(StateBorder.class, () -> side > 0 && is(AnimState.REVERSING));
        addTransition(StateFall.class, () -> is(AnimState.FINISHED));
    }

    @Override
    public void enter()
    {
        super.enter();

        movement.setDestination(0.0, 0.0);
        movement.setDirection(DirectionNone.INSTANCE);
        animatable.addListener(listener);

        if (isGoDown())
        {
            side = -1;
            offset = -OFFSET_9;
            frameOffset = OFFSET_9;
            animatable.setFrame(animation.getLast());
        }
        else
        {
            side = 1;
            offset = 0;
            frameOffset = OFFSET_1;
            animatable.setFrame(animation.getFirst());
        }
        y = transformable.getY();
    }

    @Override
    public void update(double extrp)
    {
        offset += SOAR_SPEED * side;
        if (side > 0)
        {
            transformable.teleportY(y + offset);
        }
        else
        {
            transformable.teleportY(y + offset + OFFSET_9);
        }
        rasterable.setFrameOffsets(0, (int) Math.floor(offset) + frameOffset);
    }

    @Override
    public void exit()
    {
        super.exit();

        offset = 0.0;
        frameOffset = 0;
        rasterable.setFrameOffsets(0, 0);
        animatable.removeListener(listener);
        transformable.moveLocationY(1.0, 1.0);
    }
}
