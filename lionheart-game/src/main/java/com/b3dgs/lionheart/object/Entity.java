/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionheart.object;

import java.util.Locale;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableConfig;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableModel;

/**
 * Entity base representation.
 */
public final class Entity extends FeaturableModel
{
    private static final int PREFIX = State.class.getSimpleName().length();

    /**
     * Get animation name from state class.
     * 
     * @param state The state class.
     * @return The animation name.
     */
    private static String getAnimationName(Class<? extends State> state)
    {
        return state.getSimpleName().substring(PREFIX).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Create an entity.
     * 
     * @param services The services reference.
     * @param setup The setup used.
     * @throws LionEngineException If error.
     */
    public Entity(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        addFeature(new MirrorableModel());
        addFeature(new TransformableModel(setup));
        addFeature(new AnimatableModel(services, setup));
        if (setup.hasNode(LayerableConfig.NODE_LAYERABLE))
        {
            addFeature(new LayerableModel(services, setup));
        }
        else
        {
            addFeature(new LayerableModel(2, 1));
        }
        final Collidable collidable = addFeatureAndGet(new CollidableModel(services, setup));
        collidable.setOrigin(Origin.CENTER_BOTTOM);
        collidable.addAccept(1);

        final Rasterable rasterable = addFeatureAndGet(new RasterableModel(services, setup));
        rasterable.setOrigin(Origin.CENTER_BOTTOM);

        addFeature(new EntityRenderer());

        final Body body = addFeatureAndGet(new BodyModel());
        final TileCollidable tileCollidable = addFeatureAndGet(new TileCollidableModel(services, setup));
        tileCollidable.addListener((tile, category) ->
        {
            if (Axis.Y == category.getAxis())
            {
                body.resetGravity();
            }
        });

        final EntityModel model = addFeatureAndGet(new EntityModel(services, setup));
        final Force movement = model.getMovement();
        movement.setVelocity(10);
        movement.setSensibility(0.1);

        final StateHandler stateHandler = addFeatureAndGet(new StateHandler(setup, Entity::getAnimationName));
        stateHandler.changeState(StateIdle.class);
    }

    @Override
    public void addAfter(Services services, Setup setup)
    {
        if (!hasFeature(Routine.class))
        {
            addFeature(new RoutineVoid());
        }
        final EntityModel model = getFeature(EntityModel.class);
        addFeature(new EntityUpdater(services, model));
    }
}
