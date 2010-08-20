/**
 * Copyright (C) 2009-2010 Cars and Tracks Development Project (CTDP).
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.ctdp.rfdynhud.lessons.widgets.lesson8;

import java.io.IOException;

import net.ctdp.rfdynhud.editor.EditorPresets;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.lessons.widgets._util.LessonsWidgetSet;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.properties.WidgetPropertiesContainer;
import net.ctdp.rfdynhud.render.BorderWrapper;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.TransformableTexture;
import net.ctdp.rfdynhud.util.WidgetsConfigurationWriter;
import net.ctdp.rfdynhud.widgets.WidgetsConfiguration;
import net.ctdp.rfdynhud.widgets.widget.Widget;
import net.ctdp.rfdynhud.widgets.widget.WidgetPackage;

/**
 * This Widget shows the order of execution of a Widget's callback methods.
 * 
 * There are several event methods, that are not executed in a specific order,
 * but when events occur. Check all the methods with an "on" prefix.
 * 
 * @author Marvin Froehlich (CTDP)
 */
public class Lesson8Widget_ExecutionOrder extends Widget
{
    @Override
    public int getVersion()
    {
        return ( composeVersion( 1, 0, 0 ) );
    }
    
    @Override
    public WidgetPackage getWidgetPackage()
    {
        return ( LessonsWidgetSet.WIDGET_PACKAGE );
    }
    
    @Override
    public void afterConfigurationLoaded( WidgetsConfiguration widgetsConfig, LiveGameData gameData, EditorPresets editorPresets )
    {
        super.afterConfigurationLoaded( widgetsConfig, gameData, editorPresets );
        
        //log( "afterConfigurationLoaded()" );
        
        // This method is exectuted at the beginning and only once.
    }
    
    @Override
    protected TransformableTexture[] getSubTexturesImpl( LiveGameData gameData, EditorPresets editorPresets, int widgetInnerWidth, int widgetInnerHeight )
    {
        //log( "getSubTextures()" );
        
        // This method is exectuted after afterConfigurationLoaded() and only once.
        
        return ( null );
    }
    
    @Override
    public void updateVisibility( boolean clock1, boolean clock2, LiveGameData gameData, EditorPresets editorPresets )
    {
        //log( "updateVisibility()" );
        
        // This method is exectuted 1st and each frame.
        
        super.updateVisibility( clock1, clock2, gameData, editorPresets );
    }
    
    @Override
    protected void initialize( boolean clock1, boolean clock2, LiveGameData gameData, EditorPresets editorPresets, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        //log( "initialize()" );
        
        // This method is exectuted 2nd and only once, if not forced.
    }
    
    @Override
    protected boolean checkForChanges( boolean clock1, boolean clock2, LiveGameData gameData, EditorPresets editorPresets, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        //log( "checkForChanges()" );
        
        // This method is exectuted 3rd and each frame.
        
        return ( false );
    }
    
    @Override
    protected void drawBorder( boolean isEditorMode, BorderWrapper border, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        //log( "drawBorder()" );
        
        // This method is exectuted 4th and each frame.
        
        super.drawBorder( isEditorMode, border, texture, offsetX, offsetY, width, height );
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, EditorPresets editorPresets, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        //log( "drawBackground()" );
        
        // This method is exectuted 5th and each frame.
        
        super.drawBackground( gameData, editorPresets, texture, offsetX, offsetY, width, height, isRoot );
    }
    
    @Override
    protected void drawWidget( boolean clock1, boolean clock2, boolean needsCompleteRedraw, LiveGameData gameData, EditorPresets editorPresets, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        //log( "drawWidget()" );
        
        // This method is exectuted 6th and last and each frame.
    }
    
    @Override
    public void beforeConfigurationCleared( WidgetsConfiguration widgetsConfig, LiveGameData gameData, EditorPresets editorPresets )
    {
        //log( "beforeConfigurationCleared()" );
        
        // This method is executed when the configuration is unloaded.
        
        super.beforeConfigurationCleared( widgetsConfig, gameData, editorPresets );
    }
    
    @Override
    public void saveProperties( WidgetsConfigurationWriter writer ) throws IOException
    {
        super.saveProperties( writer );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
    }
    
    @Override
    public void getProperties( WidgetPropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
    }
    
    public Lesson8Widget_ExecutionOrder( String name )
    {
        super( name, 5.0f, 5.0f );
    }
}
