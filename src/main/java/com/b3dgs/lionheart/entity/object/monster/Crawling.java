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
package com.b3dgs.lionheart.entity.object.monster;

import java.io.IOException;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionheart.ThemeSwamp;
import com.b3dgs.lionheart.entity.EntityCollisionTile;
import com.b3dgs.lionheart.entity.EntityMonster;
import com.b3dgs.lionheart.entity.EntityState;
import com.b3dgs.lionheart.entity.Jumpable;
import com.b3dgs.lionheart.entity.SetupEntity;
import com.b3dgs.lionheart.entity.patrol.Patrol;
import com.b3dgs.lionheart.map.Map;
import com.b3dgs.lionheart.map.Tile;
import com.b3dgs.lionheart.map.TileCollision;

/**
 * Crawling monster implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Crawling
        extends EntityMonster
        implements Jumpable, ThemeSwamp
{
    /** Time before jump. */
    private static final int TIME_BEFORE_JUMP = 500;
    /** Jump timer. */
    private final Timing timerJump;
    /** Prepare jump flag. */
    private boolean prepareJump;
    /** Jumping flag. */
    private boolean jumping;
    /** Jumpable flag. */
    private boolean jumpable;
    /** Jump force. */
    private int jumpForceSpeed;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Crawling(SetupEntity setup)
    {
        super(setup);
        timerJump = new Timing();
        setFrameOffsets(0, -4);
        enableMovement(Patrol.HORIZONTAL);
    }

    @Override
    public void setJumpable(boolean jumpable)
    {
        this.jumpable = jumpable;
    }

    @Override
    public void setJumpForceSpeed(int jumpForceSpeed)
    {
        this.jumpForceSpeed = jumpForceSpeed;
    }

    @Override
    public int getJumpForceSpeed()
    {
        return jumpForceSpeed;
    }

    @Override
    public boolean isJumpable()
    {
        return jumpable;
    }

    /*
     * EntityMonster
     */

    @Override
    public void save(FileWriting file) throws IOException
    {
        super.save(file);
        file.writeBoolean(jumpable);
        file.writeInteger(jumpForceSpeed);
    }

    @Override
    public void load(FileReading file) throws IOException
    {
        super.load(file);
        jumpable = file.readBoolean();
        jumpForceSpeed = file.readInteger();
    }

    @Override
    public boolean canJump()
    {
        return jumpable && super.canJump();
    }

    @Override
    protected void updateActions()
    {
        super.updateActions();
        // Jump when slope
        final int side = -getSide();
        final Tile tile = map.getTile(this, side * Map.TILE_WIDTH, 0);
        final boolean jumpTile = tile == null || tile.getCollision() == TileCollision.NONE
                || tile.getCollision() == TileCollision.GROUND_SPIKE;
        if (canJump() && !jumping && jumpTile && getLocationX() < getPositionMax() && getLocationX() > getPositionMin())
        {
            jumping = true;
            timerJump.start();
        }
        if (jumping)
        {
            if (timerJump.elapsed(Crawling.TIME_BEFORE_JUMP))
            {
                prepareJump = false;
                jumpForce.setForce(0.0, 4.75);
                setMovementForce(jumpForceSpeed / 8.0 * side, 0.0);
                if (status.collisionChangedFromTo(EntityCollisionTile.NONE, EntityCollisionTile.GROUND))
                {
                    jumping = false;
                    jumpForce.setForce(Force.ZERO);
                    timerJump.stop();
                    movement.setForceToReach(getMovementSpeedMax() * side, 0.0);
                    setMovementForce(getMovementSpeedMax() * side, 0.0);
                }
            }
            else
            {
                prepareJump = true;
                movement.reset();
            }
        }
    }

    @Override
    protected void updateStates()
    {
        if (isFalling())
        {
            status.setState(EntityState.FALL);
        }
        else if (prepareJump)
        {
            status.setState(EntityState.PREPARE_JUMP);
        }
        else if (isJumping())
        {
            status.setState(EntityState.JUMP);
        }
        else
        {
            super.updateStates();
        }
    }
}
