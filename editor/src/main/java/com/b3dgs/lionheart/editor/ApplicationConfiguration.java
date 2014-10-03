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

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.palette.PalettePart;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectImportHandler;
import com.b3dgs.lionengine.editor.project.Property;
import com.b3dgs.lionheart.Level;

/**
 * Configure the editor with the right name.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ApplicationConfiguration
{
    /** Import project argument. */
    private static final String ARG_IMPORT = "-import";

    /** Part service reference. */
    @Inject
    EPartService partService;
    /** Application reference. */
    @Inject
    private MApplication application;

    /**
     * Execute the injection.
     * 
     * @param eventBroker The event broker service.
     */
    @PostConstruct
    public void execute(IEventBroker eventBroker)
    {
        Property.LEVEL.addExtension(Level.FILE_FORMAT);

        final MWindow existingWindow = application.getChildren().get(0);
        existingWindow.setLabel(Activator.PLUGIN_NAME);

        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new AppStartupCompleteEventHandler());
    }

    /**
     * Handler called on startup complete.
     */
    private class AppStartupCompleteEventHandler
            implements EventHandler
    {
        /**
         * Constructor.
         */
        public AppStartupCompleteEventHandler()
        {
            // Nothing to do
        }

        /**
         * Check if there is a project to import.
         */
        private void checkProjectImport()
        {
            final String[] args = Platform.getApplicationArgs();
            for (int i = 0; i < args.length; i++)
            {
                if (ApplicationConfiguration.ARG_IMPORT.equals(args[i]))
                {
                    i++;
                    if (i < args.length)
                    {
                        importProject(args[i]);
                    }
                }
            }
        }

        /**
         * Import a project from a path.
         * 
         * @param projectPath The project path.
         */
        private void importProject(String projectPath)
        {
            final File path = new File(projectPath);
            try
            {
                final Project project = Project.create(path.getCanonicalFile());
                ProjectImportHandler.importProject(project, partService);
            }
            catch (final IOException exception)
            {
                Verbose.exception(getClass(), "importProject", exception);
            }
        }

        /*
         * EventHandler
         */

        @Override
        public void handleEvent(Event event)
        {
            final PalettePart palette = UtilEclipse.getPart(partService, PalettePart.ID, PalettePart.class);
            final CheckpointsView checkpoints = new CheckpointsView(partService);
            palette.addPalette("Checkpoints", checkpoints);

            checkProjectImport();
        }
    }
}
