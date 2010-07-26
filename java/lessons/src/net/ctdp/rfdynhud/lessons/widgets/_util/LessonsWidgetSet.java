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
package net.ctdp.rfdynhud.lessons.widgets._util;

import net.ctdp.rfdynhud.widgets.widget.WidgetPackage;

public class LessonsWidgetSet
{
    public static final WidgetPackage WIDGET_PACKAGE = new WidgetPackage( "CTDP/Lessons", LessonsWidgetSet.class.getClassLoader().getResource( "net/ctdp/rfdynhud/lessons/widgets/ctdp.png" ), LessonsWidgetSet.class.getClassLoader().getResource( "net/ctdp/rfdynhud/lessons/widgets/lessons.jpeg" ) );
    
    public static final String MY_FONT_COLOR_NAME = "MyFontColor";
    
    public static final String MY_FONT_NAME = "MyFont";
    
    public static String getDefaultNamedColorValue( String name )
    {
        if ( name.equals( MY_FONT_COLOR_NAME ) )
            return ( "#FF0000" );
        
        return ( null );
    }
    
    public static String getDefaultNamedFontValue( String name )
    {
        if ( name.equals( MY_FONT_NAME ) )
            return ( "Monospaced-BOLD-13va" );
        
        return ( null );
    }
}