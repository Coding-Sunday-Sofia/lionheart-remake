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

import javax.swing.JPanel;

import com.b3dgs.lionheart.Editor;

/**
 * State bar implementation (giving information on the current editor states.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class StateBar
        extends JPanel
{
    /** Uid. */
    private static final long serialVersionUID = -1306034890537499369L;
    /** Editor reference. */
    private final Editor editor;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public StateBar(Editor editor)
    {
        this.editor = editor;
        final Dimension size = new Dimension(getWidth(), 16);
        setPreferredSize(size);
        setMinimumSize(size);
    }

    @Override
    public void paintComponent(Graphics gd)
    {
        StringBuilder state = new StringBuilder("Location: [");
        state.append(editor.getOffsetViewInTileV());
        state.append(" | ");
        state.append(editor.getOffsetViewInTileH());
        state.append("]");
        gd.drawString(state.toString(), 16, 12);

        state = new StringBuilder("Pointer: ");
        state.append(editor.getSelectionState().getDescription());
        gd.drawString(state.toString(), 128, 12);

        state = new StringBuilder("Selection: ");
        if (editor.getSelectedEntity() != null)
        {
            state.append(editor.getSelectedEntity().toString());
        }
        else
        {
            state.append("none");
        }
        gd.drawString(state.toString(), 240, 12);
        gd.drawString(Editor.VERSION, editor.getWidth() - 45, 12);
    }
}
