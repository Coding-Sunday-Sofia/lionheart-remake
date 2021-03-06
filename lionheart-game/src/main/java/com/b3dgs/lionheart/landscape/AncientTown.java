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
package com.b3dgs.lionheart.landscape;

import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.lionheart.Constant;
import com.b3dgs.lionheart.constant.Folder;

/**
 * Ancient Town full background implementation.
 */
final class AncientTown extends BackgroundAbstract
{
    private final Backdrop backdrop;
    private final boolean flickering;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param theme The theme name.
     * @param flickering The flickering flag.
     */
    AncientTown(SourceResolutionProvider source, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, 512);

        this.flickering = flickering;
        final String path = UtilFolder.getPath(Folder.BACKGROUNDS, "ancient_town", theme);
        final int width = source.getWidth();
        backdrop = new Backdrop(path, this.flickering, width);
        add(backdrop);
        totalHeight = 120;
        setScreenSize(source.getWidth(), source.getHeight());
    }

    /**
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public void setScreenSize(int width, int height)
    {
        setOffsetY(height - Constant.NATIVE_RESOLUTION.getHeight() + 72);
        backdrop.setScreenWidth(width);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop implements BackgroundComponent
    {
        private final BackgroundElement backcolorA;
        private final BackgroundElement backcolorB;
        private final boolean flickering;
        private int screenWidth;
        private int flickerCount;
        private boolean flickerType;

        /**
         * Constructor.
         * 
         * @param path The backdrop path.
         * @param flickering The flickering flag effect.
         * @param screenWidth The screen width.
         */
        Backdrop(String path, boolean flickering, int screenWidth)
        {
            super();

            this.flickering = flickering;
            if (flickering)
            {
                backcolorA = createElement(path, "backcolor_a.png", 0, 0);
                backcolorB = createElement(path, "backcolor_b.png", 0, 0);
                flickerCount = 0;
            }
            else
            {
                backcolorA = createElement(path, "backcolor.png", 0, 0);
                backcolorB = null;
            }
            setScreenWidth(screenWidth);
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        private void setScreenWidth(int width)
        {
            screenWidth = width;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);

            if (flickering)
            {
                flickerCount = (flickerCount + 1) % 2;
                if (flickerCount == 0)
                {
                    flickerType = !flickerType;
                }
            }
        }

        @Override
        public void render(Graphic g)
        {
            final Sprite sprite;
            if (flickerType || !flickering)
            {
                sprite = (Sprite) backcolorA.getRenderable();
            }
            else
            {
                sprite = (Sprite) backcolorB.getRenderable();
            }
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                final int x = backcolorA.getMainX() + i * sprite.getWidth();
                final double y = backcolorA.getOffsetY() + backcolorA.getMainY();
                sprite.setLocation(x, y);
                sprite.render(g);
            }
        }
    }
}
