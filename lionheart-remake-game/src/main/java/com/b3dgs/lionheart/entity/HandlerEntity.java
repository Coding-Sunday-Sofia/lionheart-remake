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

import java.io.IOException;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.entity.HandlerEntityPlatform;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.entity.player.Valdyn;
import com.b3dgs.lionheart.map.Map;

/**
 * Handle all entity on the map.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class HandlerEntity
        extends HandlerEntityPlatform<Entity>
{
    /** The camera reference. */
    private final CameraPlatform camera;
    /** The entity factory reference. */
    private final FactoryEntity factoryEntity;
    /** The player reference. */
    private Valdyn player;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param factoryEntity The entity factory reference.
     */
    public HandlerEntity(CameraPlatform camera, FactoryEntity factoryEntity)
    {
        super(camera);
        this.camera = camera;
        this.factoryEntity = factoryEntity;
    }

    /**
     * Save entities to an existing file.
     * 
     * @param file The level file.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeShort((short) size());
        for (final Entity entity : list())
        {
            file.writeString(entity.getConfig().getPath());
            entity.save(file);
        }
    }

    /**
     * Load entities from an existing file.
     * 
     * @param file The level file.
     * @throws IOException If error.
     */
    public void load(FileReading file) throws IOException
    {
        removeAll();
        final int entitiesNumber = file.readShort();
        for (int i = 0; i < entitiesNumber; i++)
        {
            final Entity entity = factoryEntity.createEntity(file);
            entity.load(file);
            add(entity);
        }
    }

    /**
     * Prepare entities.
     */
    public void prepare()
    {
        for (final Entity entity : list())
        {
            entity.prepare();
        }
    }

    /**
     * Set the player reference.
     * 
     * @param player The player reference.
     */
    public void setPlayer(Valdyn player)
    {
        this.player = player;
    }

    /*
     * HandlerEntityPlatform
     */

    @Override
    protected boolean canUpdateEntity(Entity entity)
    {
        return camera.isVisible(entity, Map.TILE_WIDTH * 2, Map.TILE_HEIGHT * 4);
    }

    @Override
    protected void updatingEntity(Entity entity, double extrp)
    {
        if (!player.isDead())
        {
            entity.checkCollision(player);
        }
        entity.onUpdated();
    }

    @Override
    protected void renderingEntity(Entity entity, Graphic g, CameraPlatform camera)
    {
        if (AppLionheart.SHOW_COLLISIONS)
        {
            entity.renderCollision(g, camera);
        }
    }
}
