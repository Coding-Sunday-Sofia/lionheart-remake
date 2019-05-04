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
package com.b3dgs.lionheart.object.feature;

import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Spawner;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionheart.Constant;

/**
 * Hurtable feature implementation.
 */
@FeatureInterface
public final class Hurtable extends FeatureModel implements CollidableListener, Recyclable
{
    private final CollidableListener hurt;

    private CollidableListener current;

    @FeatureGet private Identifiable identifiable;
    @FeatureGet private Transformable transformable;

    /**
     * Create hurtable.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Hurtable(Services services, SetupSurfaceRastered setup)
    {
        super();

        final Spawner spawner = services.get(Spawner.class);
        final HurtableConfig config = HurtableConfig.imports(setup);

        hurt = (collidable, with, by) ->
        {
            if (by.getName().startsWith(Constant.ANIM_PREFIX_ATTACK))
            {
                spawner.spawn(config.getEffect(), transformable);
                identifiable.destroy();
                current = CollidableListener.VOID;
            }
        };

        recycle();
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        current.notifyCollided(collidable, with, by);
    }

    @Override
    public void recycle()
    {
        current = hurt;
    }
}