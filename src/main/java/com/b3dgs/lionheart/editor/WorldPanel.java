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
package com.b3dgs.lionheart.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionheart.Editor;
import com.b3dgs.lionheart.Level;
import com.b3dgs.lionheart.WorldData;
import com.b3dgs.lionheart.entity.Entity;
import com.b3dgs.lionheart.entity.FactoryEntity;
import com.b3dgs.lionheart.entity.patrol.Patrol;
import com.b3dgs.lionheart.entity.patrol.Patrollable;
import com.b3dgs.lionheart.launcher.FactoryLauncher;
import com.b3dgs.lionheart.map.Map;
import com.b3dgs.lionheart.projectile.FactoryProjectile;
import com.b3dgs.lionheart.projectile.HandlerProjectile;

/**
 * Represents the world scene, containing the map and the entities.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class WorldPanel
        extends JPanel
        implements MouseListener, MouseMotionListener
{
    /** Uid. */
    private static final long serialVersionUID = -3609110757656654125L;
    /** Color of the selection area. */
    private static final ColorRgba COLOR_MOUSE_SELECTION = new ColorRgba(128, 128, 192, 192);
    /** Color of the box around the selected entity. */
    private static final ColorRgba COLOR_ENTITY_SELECTION = new ColorRgba(128, 240, 128, 192);
    /** Color of the entity patrol. */
    private static final ColorRgba COLOR_ENTITY_PATROL = new ColorRgba(240, 240, 128, 192);
    /** Color if the entity patrol area. */
    private static final ColorRgba COLOR_ENTITY_PATROL_AREA = new ColorRgba(128, 128, 128, 192);
    /** Default width. */
    private static final int DEFAULT_WIDTH = 640;
    /** Default height. */
    private static final int DEFAULT_HEIGHT = 480;

    /**
     * Draw the grid.
     * 
     * @param g The graphic output.
     * @param tw Horizontal grid spacing (width).
     * @param th Vertical grid spacing (height).
     * @param areaX Horizontal global grid size.
     * @param areaY Vertical global grid size.
     * @param color Grid color.
     */
    private static void drawGrid(Graphic g, int tw, int th, int areaX, int areaY, ColorRgba color)
    {
        g.setColor(color);
        for (int v = 0; v <= areaY; v += tw)
        {
            g.drawLine(0, v, areaX, v);
        }
        for (int h = 0; h <= areaX; h += th)
        {
            g.drawLine(h, 0, h, areaY);
        }
    }

    /** Level reference. */
    public final Level level;
    /** The map reference. */
    public final Map map;
    /** The camera reference. */
    public final CameraPlatform camera;
    /** World data. */
    public final WorldData worldData;
    /** The entity handler reference. */
    public final Handler handlerEntity;
    /** The factory entity reference. */
    public final FactoryEntity factoryEntity;
    /** The handler projectile reference. */
    public final HandlerProjectile handlerProjectile;
    /** The factory launcher reference. */
    public final FactoryLauncher factoryLauncher;
    /** The factory projectile reference. */
    public final FactoryProjectile factoryProjectile;
    /** The editor reference. */
    private final Editor editor;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Selecting flag. */
    private boolean selecting;
    /** Selected flag. */
    private boolean selected;
    /** Clicking flag. */
    private boolean clicking;
    /** Moving entity flag. */
    private boolean moving;
    /** Moving offset x. */
    private int movingOffsetX;
    /** Moving offset y. */
    private int movingOffsetY;
    /** Current player selection state. */
    private SelectionPlayerType playerSelection;
    /** Last selection for place mode. */
    private Class<? extends Entity> lastSelection;
    /** Last selection entity for place mode. */
    private Entity lastSelectionEntity;
    /** Selection starting horizontal location. */
    private int selectStartX;
    /** Selection starting vertical location. */
    private int selectStartY;
    /** Selection ending horizontal location. */
    private int selectEndX;
    /** Selection ending vertical location. */
    private int selectEndY;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public WorldPanel(Editor editor)
    {
        super();
        this.editor = editor;
        camera = new CameraPlatform(WorldPanel.DEFAULT_WIDTH, WorldPanel.DEFAULT_HEIGHT);
        factoryEntity = new FactoryEntity();
        handlerEntity = new Handler(factoryEntity);
        handlerProjectile = new HandlerProjectile(camera, handlerEntity);
        factoryProjectile = new FactoryProjectile();
        factoryLauncher = new FactoryLauncher(factoryProjectile, handlerProjectile);
        level = new Level(camera, factoryEntity, handlerEntity, factoryLauncher, factoryProjectile, 60);
        factoryEntity.setLevel(level);
        worldData = level.worldData;
        map = level.map;
        camera.setLimits(map);
        setPreferredSize(new Dimension(WorldPanel.DEFAULT_WIDTH, WorldPanel.DEFAULT_HEIGHT));
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Save a level to a file.
     * 
     * @param media The file to save level to.
     * @throws IOException If error.
     */
    public void save(Media media) throws IOException
    {
        try (final FileWriting file = File.createFileWriting(media);)
        {
            level.save(file);
        }
    }

    /**
     * Load a level from a file.
     * 
     * @param media The level file.
     * @throws IOException If error.
     */
    public void load(Media media) throws IOException
    {
        try (final FileReading file = File.createFileReading(media);)
        {
            level.load(file);
        }
        handlerEntity.update();
    }

    /**
     * Set the current player selection type.
     * 
     * @param selection The current selection.
     */
    public void setPlayerSelection(SelectionPlayerType selection)
    {
        playerSelection = selection;
    }

    /**
     * Check if mouse is clicking.
     * 
     * @return <code>true</code> if clicking, <code>false</code> else.
     */
    public boolean isClicking()
    {
        return clicking;
    }

    /**
     * Draw all entities.
     * 
     * @param g The graphic output.
     * @param hOff The horizontal offset.
     * @param vOff The vertical offset.
     * @param height The rendering height (render from bottom).
     */
    private void drawEntities(Graphic g, int hOff, int vOff, int height)
    {
        final int th = map.getTileHeight();
        for (final Entity entity : handlerEntity.list())
        {
            final int sx = entity.getLocationIntX();
            final int sy = entity.getLocationIntY();

            // Patrol
            if (entity instanceof Patrollable)
            {
                final Patrollable mover = (Patrollable) entity;
                drawEntityMovement(g, mover, hOff, vOff, height);
            }

            if (entity.isSelected() || entity.isOver())
            {
                g.setColor(WorldPanel.COLOR_ENTITY_SELECTION);
                g.drawRect(sx - hOff - entity.getWidth() / 2,
                        -sy + vOff - entity.getHeight() + UtilMath.getRounded(height, th), entity.getWidth(),
                        entity.getHeight(), true);
            }
            entity.render(g, camera);
        }
    }

    /**
     * Draw entity movement.
     * 
     * @param g The graphic output.
     * @param mover The entity reference.
     * @param hOff The horizontal offset.
     * @param vOff The vertical offset.
     * @param height The rendering height (render from bottom).
     */
    private void drawEntityMovement(Graphic g, Patrollable mover, int hOff, int vOff, int height)
    {
        if (mover.getPatrolType() != Patrol.NONE)
        {
            final int sx = mover.getLocationIntX();
            final int sy = mover.getLocationIntY();
            final int tw = map.getTileWidth();
            final int th = map.getTileHeight();
            final int left = tw * mover.getPatrolLeft();
            final int right = tw * (mover.getPatrolLeft() + mover.getPatrolRight());

            g.setColor(WorldPanel.COLOR_ENTITY_PATROL_AREA);
            if (mover.getPatrolType() == Patrol.HORIZONTAL)
            {
                g.drawRect(sx - hOff - left - mover.getWidth() / 2, -sy + vOff + UtilMath.getRounded(height, th)
                        - mover.getHeight(), mover.getWidth() + right, mover.getHeight(), true);
            }
            else if (mover.getPatrolType() == Patrol.VERTICAL)
            {
                g.drawRect(sx - hOff - mover.getWidth() / 2, -sy + vOff - left + UtilMath.getRounded(height, th)
                        - mover.getHeight(), mover.getWidth(), mover.getHeight() + right, true);
            }

            g.setColor(WorldPanel.COLOR_ENTITY_PATROL);
            if (mover.getPatrolType() == Patrol.HORIZONTAL)
            {
                g.drawRect(sx - hOff - left, -sy + vOff + UtilMath.getRounded(height, th), right, th, true);
            }
            else if (mover.getPatrolType() == Patrol.VERTICAL)
            {
                g.drawRect(sx - hOff + mover.getWidth() / 2, -sy + vOff + UtilMath.getRounded(height, th) - left
                        - mover.getHeight() / 2, tw, right, true);
            }
        }
    }

    /**
     * Draw the cursor.
     * 
     * @param g The graphic output.
     * @param tw The tile width.
     * @param th The tile height.
     * @param areaX Maximum width.
     * @param areaY Maximum height.
     */
    private void drawCursor(Graphic g, int tw, int th, int areaX, int areaY)
    {
        if (!selecting && !moving)
        {
            if (mouseX >= 0 && mouseY >= 0 && mouseX < areaX && mouseY < areaY)
            {
                final int mx = UtilMath.getRounded(mouseX, tw);
                final int my = UtilMath.getRounded(mouseY, th);

                g.setColor(WorldPanel.COLOR_MOUSE_SELECTION);
                g.drawRect(mx, my, tw, th, true);
            }
        }
    }

    /**
     * Draw the current selection.
     * 
     * @param g The graphic output.
     */
    private void drawSelection(Graphic g)
    {
        if (selecting)
        {
            final int sx = selectStartX;
            final int sy = selectStartY;
            final int w = selectEndX - sx;
            final int h = selectEndY - sy;
            g.setColor(WorldPanel.COLOR_MOUSE_SELECTION);
            g.drawRect(sx, sy, w, h, true);
        }
        if (editor.getSelectionState() == SelectionType.PLACE)
        {
            final Class<? extends Entity> selection = editor.getSelectedEntity();
            if (selection != null)
            {
                if (selection != lastSelection)
                {
                    lastSelectionEntity = editor.world.factoryEntity.create(selection);
                    lastSelection = selection;
                }
                final int tw = map.getTileWidth();
                final int th = map.getTileHeight();
                final int width = lastSelectionEntity.getWidth();
                final int height = lastSelectionEntity.getHeight();
                final int h = UtilMath.getRounded(height, tw) - th;
                final int x = UtilMath.getRounded(mouseX, tw);
                final int y = UtilMath.getRounded(mouseY, th) - h;

                g.setColor(ColorRgba.PURPLE);
                g.drawRect(x, y, width, height, true);
            }
        }
    }

    /**
     * Start the selection.
     * 
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    private void beginSelection(int mx, int my)
    {
        if (!selecting)
        {
            final int sx = UtilMath.getRounded(mx, map.getTileWidth());
            final int sy = UtilMath.getRounded(my, map.getTileHeight());
            selectStartX = sx;
            selectStartY = sy;
            selectEndX = sx;
            selectEndY = sy;
            selecting = true;
            selected = false;
        }
    }

    /**
     * Update the active selection.
     * 
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    private void updateSelection(int mx, int my)
    {
        if (selecting)
        {
            selectEndX = UtilMath.getRounded(mx + map.getTileWidth() / 2, map.getTileWidth());
            selectEndY = UtilMath.getRounded(my + map.getTileHeight() / 2, map.getTileHeight());
            selecting = true;
            selected = true;
        }
    }

    /**
     * Terminate current selection.
     * 
     * @param mx The mouse x.
     * @param my The mouse y.
     */
    private void endSelection(int mx, int my)
    {
        if (selecting)
        {
            int sx = selectStartX;
            int sy = selectStartY;
            int ex = UtilMath.getRounded(mx, map.getTileWidth());
            int ey = UtilMath.getRounded(my, map.getTileHeight());

            // Ensure selection is positive
            if (ex < sx)
            {
                final int tmp = sx;
                sx = ex;
                ex = tmp;
            }
            if (ey > sy)
            {
                final int tmp = sy;
                sy = ey;
                ey = tmp;
            }
            selectStartX = sx;
            selectStartY = sy;
            selectEndX = ex;
            selectEndY = ey;
            selecting = false;
        }
    }

    /**
     * Update the mouse.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateMouse(int mx, int my)
    {
        mouseX = mx;
        mouseY = my;
        repaint();
    }

    /**
     * Reset the selection.
     */
    private void resetSelection()
    {
        selectStartX = -1;
        selectStartY = -1;
        selectEndX = -1;
        selectEndY = -1;
        selecting = false;
    }

    /**
     * Check if entity is hit.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return <code>true</code> if hit, <code>false</code> else.
     */
    private Entity hitEntity(int x, int y)
    {
        final int mx = UtilMath.getRounded(x, map.getTileWidth());
        final int my = UtilMath.getRounded(getHeight() - y - 1, map.getTileHeight());
        for (final Entity entity : handlerEntity.list())
        {
            if (hitEntity(entity, mx, my, mx + map.getTileWidth(), my + map.getTileHeight()))
            {
                return entity;
            }
        }
        return null;
    }

    /**
     * Check if entity is hit.
     * 
     * @param entity The entity to check.
     * @param x1 First point x.
     * @param y1 First point y.
     * @param x2 Second point x.
     * @param y2 Second point y.
     * @return <code>true</code> if hit, <code>false</code> else.
     */
    private boolean hitEntity(Entity entity, int x1, int y1, int x2, int y2)
    {
        if (entity != null)
        {
            final int x = UtilMath.getRounded(entity.getLocationIntX() - entity.getWidth() / 2, map.getTileWidth())
                    - editor.getOffsetViewH();
            final int y = UtilMath.getRounded(entity.getLocationIntY(), map.getTileHeight()) - editor.getOffsetViewV();
            final Rectangle r1 = Geom.createRectangle(x1, y1, x2 - x1, y2 - y1);
            final Rectangle r2 = Geom.createRectangle(x, y, entity.getWidth(), entity.getHeight());
            if (r1.intersects(r2))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Select all entities.
     */
    private void selectEntities()
    {
        for (final Entity entity : handlerEntity.list())
        {
            entity.setSelection(false);
            final int th = map.getTileHeight();
            final int offy = getHeight() - UtilMath.getRounded(getHeight(), th);
            final int sx = UtilMath.getRounded(selectStartX, map.getTileWidth());
            final int sy = UtilMath.getRounded(getHeight() - selectStartY - offy, th);
            final int ex = UtilMath.getRounded(selectEndX, map.getTileWidth());
            final int ey = UtilMath.getRounded(getHeight() - selectEndY - offy, th);
            if (hitEntity(entity, sx, sy, ex, ey))
            {
                entity.setSelection(true);
            }
        }
    }

    /**
     * Unselect entities.
     */
    private void unSelectEntities()
    {
        for (final Entity entity : handlerEntity.list())
        {
            entity.setSelection(false);
        }
    }

    /**
     * Set the entity location.
     * 
     * @param entity The entity reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param side 1 for place, -1 for move.
     */
    private void setEntityLocation(Entity entity, int x, int y, int side)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        entity.teleport(UtilMath.getRounded(x + (side == 1 ? 0 : 1) * entity.getWidth() / 2 + tw / 2, tw) + side
                * entity.getWidth() / 2, UtilMath.getRounded(y + th / 2, th));
    }

    /**
     * Get the list of selected entities.
     * 
     * @param first Get only the first element.
     * @return The selected entities.
     */
    private List<Entity> getSelectedEnties(boolean first)
    {
        final List<Entity> list = new ArrayList<>(0);
        for (final Entity entity : handlerEntity.list())
        {
            if (entity.isSelected())
            {
                list.add(entity);
                if (first)
                {
                    return list;
                }
            }
        }
        return list;
    }

    /**
     * Render the world.
     * 
     * @param g The graphic output.
     */
    private void render(Graphic g)
    {
        final int width = getWidth();
        final int height = getHeight();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int hOff = editor.getOffsetViewH();
        final int vOff = editor.getOffsetViewV();
        final int areaX = UtilMath.getRounded(width, tw);
        final int areaY = UtilMath.getRounded(height, th);
        camera.setView(0, 0, areaX - tw, areaY);

        // Background
        g.setColor(ColorRgba.GRAY_LIGHT);
        g.drawRect(0, 0, width, height, true);

        // Map area
        g.setColor(ColorRgba.BLUE);
        g.drawRect(0, 0, areaX, areaY, true);

        // Renders
        if (!map.getPatterns().isEmpty())
        {
            map.render(g, camera);
        }
        drawEntities(g, hOff, vOff, height);
        g.setColor(ColorRgba.GREEN);
        g.drawRect(worldData.getStartX() - hOff, -worldData.getStartY() + vOff + UtilMath.getRounded(height, th)
                - Map.TILE_HEIGHT, Map.TILE_WIDTH, Map.TILE_HEIGHT, true);
        g.setColor(ColorRgba.RED);
        g.drawRect(worldData.getEndX() - hOff, -worldData.getEndY() + vOff + UtilMath.getRounded(height, th)
                - Map.TILE_HEIGHT, Map.TILE_WIDTH, Map.TILE_HEIGHT, true);

        for (final CoordTile p : worldData.getCheckpoints())
        {
            g.setColor(ColorRgba.YELLOW);
            g.drawRect(p.getX() - hOff, -p.getY() + vOff + UtilMath.getRounded(height, th) - Map.TILE_HEIGHT,
                    Map.TILE_WIDTH, Map.TILE_HEIGHT, true);
        }

        drawCursor(g, tw, th, areaX, areaY);
        WorldPanel.drawGrid(g, tw, th, areaX, areaY, ColorRgba.GRAY);
        drawSelection(g);
    }

    /*
     * JPanel
     */

    @Override
    public void paintComponent(Graphics gd)
    {
        final Graphic g = Core.GRAPHIC.createGraphic();
        g.setGraphic(gd);
        render(g);
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseEntered(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseExited(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        final int mx = event.getX();
        final int my = event.getY();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int h = UtilMath.getRounded(getHeight(), th) - map.getTileHeight();
        final int x = editor.getOffsetViewH() + UtilMath.getRounded(mx, tw);
        final int y = editor.getOffsetViewV() - UtilMath.getRounded(my, th) + h;
        clicking = true;

        switch (editor.getSelectionState())
        {
            case SELECT:
                if (event.getButton() == Mouse.LEFT)
                {
                    final Entity entity = hitEntity(mx, my);
                    editor.toolBar.entityEditor.setSelectedEntity(entity);
                    if (entity != null)
                    {
                        selected = false;
                        if (!entity.isSelected())
                        {
                            unSelectEntities();
                            entity.setSelection(true);
                        }
                    }
                    else
                    {
                        unSelectEntities();
                        beginSelection(mx, my);
                    }
                }
                break;
            case PLACE:
                if (hitEntity(mx, my) == null)
                {
                    unSelectEntities();
                    final Class<? extends Entity> selection = editor.getSelectedEntity();
                    if (selection != null)
                    {
                        final Entity entity = factoryEntity.create(selection);
                        setEntityLocation(entity, x, y, 1);
                        handlerEntity.add(entity);
                        handlerEntity.update();
                    }
                }
                break;
            case DELETE:
                final Entity entity = hitEntity(mx, my);
                if (entity != null)
                {
                    handlerEntity.remove(entity);
                    handlerEntity.update();
                }
                break;
            case PLAYER:
                switch (playerSelection)
                {
                    case PLACE_START:
                        worldData.setStarting(x, y);
                        break;
                    case PLACE_END:
                        worldData.setEnding(x, y);
                        break;
                    case ADD_CHECKPOINT:
                        worldData.addCheckpoint(x, y);
                        break;
                    case REMOVE_CHECKPOINT:
                        worldData.removeCheckpoint(worldData.getCheckpointAt(x, y));
                        break;
                    default:
                        throw new LionEngineException("Unknown selection: " + playerSelection);
                }
                break;
            default:
                throw new LionEngineException("Unknown selection: " + editor.getSelectionState());
        }
        updateMouse(mx, my);
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        final int mx = event.getX();
        final int my = event.getY();
        clicking = false;

        endSelection(mx, my);
        if (selected)
        {
            selectEntities();
        }
        for (final Entity entity : getSelectedEnties(false))
        {
            setEntityLocation(entity, entity.getLocationIntX(), entity.getLocationIntY(), -1);
        }
        moving = false;
        resetSelection();
        updateMouse(mx, my);
    }

    @Override
    public void mouseDragged(MouseEvent event)
    {
        final int th = map.getTileHeight();
        final int mx = event.getX();
        final int my = event.getY();
        final int areaY = UtilMath.getRounded(getHeight(), th);
        if (!moving)
        {
            movingOffsetX = UtilMath.getRounded(mouseX, map.getTileWidth()) - mx;
            movingOffsetY = my - UtilMath.getRounded(mouseY, th) - th;
            moving = true;
        }
        final int ox = mouseX + editor.getOffsetViewH() + movingOffsetX;
        final int oy = areaY - mouseY + editor.getOffsetViewV() - 1 + movingOffsetY;
        final int x = mx + editor.getOffsetViewH() + movingOffsetX;
        final int y = areaY - my + editor.getOffsetViewV() - 1 + movingOffsetY;
        updateSelection(mx, my);

        if (editor.getSelectionState() == SelectionType.SELECT)
        {
            for (final Entity entity : handlerEntity.list())
            {
                if (entity.isSelected())
                {
                    entity.teleport(entity.getLocationIntX() + x - ox, entity.getLocationIntY() + y - oy);
                }
            }
        }
        updateMouse(mx, my);
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
        final int th = map.getTileHeight();
        final int mx = event.getX();
        final int my = event.getY();
        final int areaY = UtilMath.getRounded(getHeight(), th);
        final int x = UtilMath.getRounded(mx, map.getTileWidth());
        final int y = UtilMath.getRounded(areaY - my - 1, th);

        for (final Entity entity : handlerEntity.list())
        {
            entity.setOver(false);
            if (hitEntity(entity, x, y, x + map.getTileWidth(), y + th))
            {
                entity.setOver(true);
            }
        }

        updateMouse(mx, my);
    }
}
