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
package com.b3dgs.lionheart.entity.launcher;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionheart.AppLionheart;
import com.b3dgs.lionheart.FolderType;
import com.b3dgs.lionheart.entity.projectile.FactoryProjectile;
import com.b3dgs.lionheart.entity.projectile.HandlerProjectile;

/**
 * Factory launcher base.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryLauncher
        extends FactoryObjectGame<SetupLauncher, Launcher>
{
    /** Factory projectile. */
    private final FactoryProjectile factory;
    /** Projectile handler. */
    private final HandlerProjectile handler;

    /**
     * Constructor.
     * 
     * @param factory The factory projectile reference.
     * @param handler The projectile handler reference.
     */
    public FactoryLauncher(FactoryProjectile factory, HandlerProjectile handler)
    {
        super(AppLionheart.LAUNCHERS_DIR);
        this.factory = factory;
        this.handler = handler;
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupLauncher createSetup(Class<? extends Launcher> type, Media config)
    {
        final FolderType theme = FolderType.getType(type);
        final Media media = Core.MEDIA.create(folder, theme.getPath(), type.getSimpleName() + ".xml");

        return new SetupLauncher(media, factory, handler);
    }
}
