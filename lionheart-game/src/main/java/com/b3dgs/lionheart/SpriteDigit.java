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
package com.b3dgs.lionheart;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Sprite with digits representation.
 */
public class SpriteDigit implements Sprite
{
    /** Sprite reference. */
    private final SpriteTiled sprite;
    /** Digits reference. */
    private final SpriteTiled[] digits;
    /** Number of digits. */
    private final int digitNumber;

    /**
     * Create sprite digit.
     * 
     * @param media The sprite media (must not be <code>null</code>).
     * @param tileWidth The tile width (must be strictly positive).
     * @param tileHeight The tile height (must be strictly positive).
     * @param digitNumber The number of digits.
     */
    public SpriteDigit(Media media, int tileWidth, int tileHeight, int digitNumber)
    {
        super();

        this.digitNumber = digitNumber;
        sprite = Drawable.loadSpriteTiled(media, tileWidth, tileHeight);
        digits = new SpriteTiled[digitNumber];
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i] = Drawable.loadSpriteTiled(sprite.getSurface(), tileWidth, tileHeight);
        }
    }

    /**
     * Create sprite digit.
     * 
     * @param surface The surface reference (must not be <code>null</code>).
     * @param tileWidth The tile width (must be strictly positive).
     * @param tileHeight The tile height (must be strictly positive).
     * @param digitNumber The number of digits.
     */
    public SpriteDigit(ImageBuffer surface, int tileWidth, int tileHeight, int digitNumber)
    {
        super();

        this.digitNumber = digitNumber;
        sprite = Drawable.loadSpriteTiled(surface, tileWidth, tileHeight);
        digits = new SpriteTiled[digitNumber];
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i] = Drawable.loadSpriteTiled(sprite.getSurface(), tileWidth, tileHeight);
        }
    }

    /**
     * Set the digit value.
     * 
     * @param value The digit value.
     */
    public void setValue(int value)
    {
        for (int i = 0; i < digitNumber; i++)
        {
            final int index = digitNumber - i - 1;
            if (i > 0)
            {
                digits[index].setTile(1 + value / (com.b3dgs.lionengine.Constant.DECADE * i));
            }
            else
            {
                digits[index].setTile(1 + value % com.b3dgs.lionengine.Constant.DECADE);
            }
        }
    }

    @Override
    public void load()
    {
        sprite.load();
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].load();
        }
    }

    @Override
    public void prepare()
    {
        sprite.prepare();
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].prepare();
        }
    }

    @Override
    public void dispose()
    {
        sprite.dispose();
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].dispose();
        }
    }

    @Override
    public void stretch(double percentWidth, double percentHeight)
    {
        sprite.stretch(percentWidth, percentHeight);
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].stretch(percentWidth, percentHeight);
        }
    }

    @Override
    public void rotate(int angle)
    {
        sprite.rotate(angle);
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].rotate(angle);
        }
    }

    @Override
    public void filter(Filter filter)
    {
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].filter(filter);
        }
    }

    @Override
    public void render(Graphic g)
    {
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].render(g);
        }
    }

    @Override
    public void setOrigin(Origin origin)
    {
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].setOrigin(origin);
        }
    }

    @Override
    public void setLocation(double x, double y)
    {
        sprite.setLocation(x, y);
        int offsetX = 0;
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].setLocation(x + offsetX, y);
            offsetX += sprite.getTileWidth();
        }
    }

    @Override
    public void setTransparency(ColorRgba mask)
    {
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].setTransparency(mask);
        }
    }

    @Override
    public void setLocation(Viewer viewer, Localizable localizable)
    {
        sprite.setLocation(viewer, localizable);
    }

    @Override
    public void setAlpha(int alpha)
    {
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].setAlpha(alpha);
        }
    }

    @Override
    public void setFade(int alpha, int fade)
    {
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].setFade(alpha, fade);
        }
    }

    @Override
    public void setMirror(Mirror mirror)
    {
        sprite.setMirror(mirror);
        for (int i = 0; i < digitNumber; i++)
        {
            digits[i].setMirror(mirror);
        }
    }

    @Override
    public boolean isLoaded()
    {
        for (int i = 0; i < digitNumber; i++)
        {
            if (!digits[i].isLoaded())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ImageBuffer getSurface()
    {
        return sprite.getSurface();
    }

    @Override
    public double getX()
    {
        return sprite.getX();
    }

    @Override
    public double getY()
    {
        return sprite.getY();
    }

    @Override
    public int getWidth()
    {
        return sprite.getWidth();
    }

    @Override
    public int getHeight()
    {
        return sprite.getHeight();
    }

    @Override
    public Mirror getMirror()
    {
        return sprite.getMirror();
    }
}
