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
package com.b3dgs.lionheart.object;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Stats configuration.
 */
public class StatsConfig
{
    /** Stats node name. */
    private static final String NODE_STATS = "stats";
    /** Health attribute name. */
    private static final String ATT_HEALTH = "health";
    /** Life attribute name. */
    private static final String ATT_LIFE = "life";

    /**
     * Imports the stats config from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The stats data.
     * @throws LionEngineException If unable to read node.
     */
    public static StatsConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        final int health = configurer.getIntegerDefault(0, ATT_HEALTH, NODE_STATS);
        final int life = configurer.getIntegerDefault(0, ATT_LIFE, NODE_STATS);

        return new StatsConfig(health, life);
    }

    /** Health modifier. */
    private final int health;
    /** Life modifier. */
    private final int life;

    /**
     * Create stats config.
     * 
     * @param health The health modifier.
     * @param life The life modifier.
     */
    private StatsConfig(int health, int life)
    {
        super();

        this.health = health < 0 ? Integer.MAX_VALUE : health;
        this.life = life;
    }

    /**
     * Get the health modifier.
     * 
     * @return The health modifier.
     */
    public int getHealth()
    {
        return health;
    }

    /**
     * Get the life modifier.
     * 
     * @return The life modifier.
     */
    public int getLife()
    {
        return life;
    }
}
