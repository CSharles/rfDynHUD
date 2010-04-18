package net.ctdp.rfdynhud.widgets;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.ctdp.rfdynhud.editor.properties.BorderProperty;
import net.ctdp.rfdynhud.editor.properties.ColorProperty;
import net.ctdp.rfdynhud.editor.properties.FontProperty;
import net.ctdp.rfdynhud.editor.properties.Property;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.input.InputMappings;
import net.ctdp.rfdynhud.util.Logger;
import net.ctdp.rfdynhud.widgets._util.FlatWidgetPropertiesContainer;
import net.ctdp.rfdynhud.widgets._util.FontUtils;
import net.ctdp.rfdynhud.widgets.widget.Widget;
import net.ctdp.rfdynhud.widgets.widget.__WPrivilegedAccess;

import org.openmali.vecmath2.util.ColorUtils;

/**
 * The {@link WidgetsConfiguration} handles the drawing of all visible widgets.
 * 
 * @author Marvin Froehlich
 */
public class WidgetsConfiguration
{
    public static interface ConfigurationClearListener
    {
        public void beforeWidgetsConfigurationCleared( WidgetsConfiguration widgetsConfig );
    }
    
    private final ArrayList<Widget> widgets = new ArrayList<Widget>();
    private final HashMap<String, Widget> widgetsMap = new HashMap<String, Widget>();
    
    private final HashMap<String, Color> colorMap = new HashMap<String, Color>();
    private final HashMap<String, Font> fontMap = new HashMap<String, Font>();
    private final HashMap<String, String> fontStringMap = new HashMap<String, String>();
    private final HashMap<String, Boolean> fontVirtualMap = new HashMap<String, Boolean>();
    private final HashMap<String, String> borderMap = new HashMap<String, String>();
    
    private final HashMap<Class<? extends Widget>, Object> generalStores = new HashMap<Class<? extends Widget>, Object>();
    private final HashMap<String, Object> localStores = new HashMap<String, Object>();
    private final HashMap<String, Boolean> visibilities = new HashMap<String, Boolean>();
    
    private boolean needsCheckFixAndBake = true;
    
    private int gameResX = 1280;
    private int gameResY = 1024;
    
    private InputMappings inputMappings = null;
    
    private static String getLocalStoreKey( Widget widget )
    {
        return ( widget.getClass().getName() + "::" + widget.getName() );
    }
    
    /**
     * Finds a free name starting with 'baseName'.
     * 
     * @param baseName
     * 
     * @return the found free name.
     */
    public String findFreeName( String baseName )
    {
        for ( int i = 1; i < Integer.MAX_VALUE; i++ )
        {
            String name = baseName + i;
            boolean isFree = true;
            for ( int j = 0; j < widgets.size(); j++ )
            {
                if ( name.equals( widgets.get( j ).getName() ) )
                {
                    isFree = false;
                    break;
                }
            }
            
            if ( isFree )
                return ( name );
        }
        
        // Theoretically unreachable code!
        return ( null );
    }
    
    /**
     * Adds a new {@link Widget} to be drawn by this manager.
     * 
     * @param widget
     */
    public void addWidget( Widget widget )
    {
        widgets.add( widget );
        widgetsMap.put( widget.getName(), widget );
        __WPrivilegedAccess.setConfiguration( this, widget );
    }
    
    /**
     * Removes a {@link Widget} from the drawing process.
     * 
     * @param widget
     */
    public void removeWidget( Widget widget )
    {
        widgets.remove( widget );
        widgetsMap.remove( widget.getName() );
        localStores.put( getLocalStoreKey( widget ), widget.getLocalStore() );
        visibilities.put( getLocalStoreKey( widget ), widget.isVisible() );
        __WPrivilegedAccess.setConfiguration( null, widget );
    }
    
    /**
     * Removes all {@link Widget}s and clears all name- and aliass maps.
     */
    public void clear( LiveGameData gameData, ConfigurationClearListener clearListener )
    {
        if ( clearListener != null )
            clearListener.beforeWidgetsConfigurationCleared( this );
        
        //localStores.clear();
        
        for ( int i = 0; i < widgets.size(); i++ )
        {
            Widget widget = widgets.get( i );
            
            widget.beforeConfigurationCleared( this, gameData );
        }
        
        for ( int i = 0; i < widgets.size(); i++ )
        {
            Widget widget = widgets.get( i );
            
            localStores.put( getLocalStoreKey( widget ), widget.getLocalStore() );
            visibilities.put( getLocalStoreKey( widget ), widget.isVisible() );
            
            __WPrivilegedAccess.setConfiguration( null, widget );
        }
        
        widgets.clear();
        widgetsMap.clear();
        
        colorMap.clear();
        fontMap.clear();
        fontStringMap.clear();
        fontVirtualMap.clear();
        borderMap.clear();
    }
    
    /**
     * Gets the number of {@link Widget}s in this manager.
     * 
     * @return the number of {@link Widget}s in this manager.
     */
    public final int getNumWidgets()
    {
        return ( widgets.size() );
    }
    
    /**
     * Gets the index-th {@link Widget} from this manager.
     * 
     * @param index
     * 
     * @return the index-th {@link Widget} from this manager.
     */
    public final Widget getWidget( int index )
    {
        return ( widgets.get( index ) );
    }
    
    /**
     * Gets the {@link Widget} with the specified name from this manager.
     * 
     * @param name
     * 
     * @return the {@link Widget} with the specified name from this manager.
     */
    public final Widget getWidget( String name )
    {
        return ( widgetsMap.get( name ) );
    }
    
    /**
     * Sets the dirty flags on all {@link Widget}s.
     */
    public void setAllDirtyFlags()
    {
        for ( int i = 0; i < getNumWidgets(); i++ )
        {
            getWidget( i ).setDirtyFlag();
        }
    }
    
    void setInputMappings( InputMappings inputMappings )
    {
        this.inputMappings = inputMappings;
    }
    
    public final InputMappings getInputMappings()
    {
        return ( inputMappings );
    }
    
    void setJustLoaded( LiveGameData gameData )
    {
        this.needsCheckFixAndBake = true;
        
        for ( int i = 0; i < widgets.size(); i++ )
        {
            Widget widget = widgets.get( i );
            
            Object localStore = localStores.get( getLocalStoreKey( widget ) );
            if ( localStore != null )
            {
                __WPrivilegedAccess.setLocalStore( localStore, widget );
            }
            
            Boolean visibility = visibilities.get( getLocalStoreKey( widget ) );
            if ( visibility != null )
            {
                widget.setVisible( visibility );
            }
        }
        
        for ( int i = 0; i < widgets.size(); i++ )
        {
            Widget widget = widgets.get( i );
            
            widget.afterConfigurationLoaded( this, gameData );
        }
    }
    
    private void fixVirtualNamedFonts()
    {
        for ( String name : fontMap.keySet() )
        {
            Boolean virtual = fontVirtualMap.get( name );
            
            if ( virtual == Boolean.TRUE )
            {
                fontMap.put( name, FontUtils.parseFont( fontStringMap.get( name ), gameResY, false ) );
            }
        }
        
        resetAllFontProperties();
    }
    
    void setGameResolution( int gameResX, int gameResY )
    {
        this.gameResX = gameResX;
        this.gameResY = gameResY;
        
        fixVirtualNamedFonts();
    }
    
    public final int getGameResX()
    {
        return ( gameResX );
    }
    
    public final int getGameResY()
    {
        return ( gameResY );
    }
    
    /**
     * Checks, if all Widgets are within the game's bounds.
     * If not, they are moved and possibly resized to be in bounds.
     */
    protected void checkFixAndBakeConfiguration( boolean isEditorMode )
    {
        if ( !needsCheckFixAndBake )
            return;
        
        int n = getNumWidgets();
        for ( int i = 0; i < n; i++ )
        {
            Widget w = getWidget( i );
            
            if ( w.getPosition().getEffectiveX() < 0 )
            {
                if ( w.getPosition().getEffectiveY() < 0 )
                    w.getPosition().setEffectivePosition( 0, 0 );
                else
                    w.getPosition().setEffectivePosition( 0, w.getPosition().getEffectiveY() );
            }
            else if ( w.getPosition().getEffectiveY() < 0 )
            {
                w.getPosition().setEffectivePosition( w.getPosition().getEffectiveX(), 0 );
            }
            
            if ( w.getPosition().getEffectiveX() + w.getSize().getEffectiveWidth() >= gameResX )
            {
                int newX = gameResX - w.getSize().getEffectiveWidth();
                
                if ( newX < 0 )
                {
                    w.getPosition().setEffectivePosition( 0, w.getPosition().getEffectiveY() );
                    w.getSize().setEffectiveSize( gameResX, w.getSize().getEffectiveHeight() );
                }
                else
                {
                    w.getPosition().setEffectivePosition( newX, w.getPosition().getEffectiveY() );
                }
            }
            
            if ( w.getPosition().getEffectiveY() + w.getSize().getEffectiveHeight() >= gameResY )
            {
                int newY = gameResY - w.getSize().getEffectiveHeight();
                
                if ( newY < 0 )
                {
                    w.getPosition().setEffectivePosition( w.getPosition().getEffectiveX(), 0 );
                    w.getSize().setEffectiveSize( w.getSize().getEffectiveWidth(), gameResY );
                }
                else
                {
                    w.getPosition().setEffectivePosition( w.getPosition().getEffectiveX(), newY );
                }
            }
            
            w.getPosition().set( w.getPosition().getPositioning(), w.getPosition().getX(), w.getPosition().getY() );
            w.getSize().set( w.getSize().getWidth(), w.getSize().getHeight() );
            
            if ( !isEditorMode )
            {
                w.bake();
            }
        }
        
        needsCheckFixAndBake = false;
    }
    
    /**
     * Gets the general store object for the given Widget class.
     * 
     * @param widgetClass
     * 
     * @return the general store object for the given Widget class.
     */
    public final Object getGeneralStore( Class<? extends Widget> widgetClass )
    {
        Object generalStore = generalStores.get( widgetClass );
        
        if ( generalStore == null )
        {
            try
            {
                Widget widget = widgetClass.getConstructor( String.class ).newInstance( "" );
                
                generalStore = widget.getGeneralStore();
                generalStores.put( widgetClass, generalStore );
            }
            catch ( Throwable t )
            {
                Logger.log( t );
            }
        }
        
        return ( generalStore );
    }
    
    /**
     * Maps a new named color.
     * 
     * @param name
     * @param color
     * 
     * @return changed?
     */
    public boolean addNamedColor( String name, Color color )
    {
        Color old = this.colorMap.put( name, color );
        
        return ( !color.equals( old ) );
    }
    
    /**
     * Gets a named color from the map or <code>null</code>, if not found.
     * 
     * @param name
     * 
     * @return a named color from the map or <code>null</code>, if not found.
     */
    public final Color getNamedColor( String name )
    {
        return ( colorMap.get( name ) );
    }
    
    /**
     * Gets all currently mapped color names.
     * 
     * @return all currently mapped color names.
     */
    public final Set<String> getColorNames()
    {
        return ( colorMap.keySet() );
    }
    
    /**
     * Removes a mapped named color.
     * 
     * @param name
     * 
     * @return the previously mapped color.
     */
    public Color removeNamedColor( String name )
    {
        Color color = colorMap.remove( name );
        
        if ( color != null )
        {
            String colorString = ColorUtils.colorToHex( color );
            
            resetColors( name, colorString );
        }
        
        return ( color );
    }
    
    private static void renameColorPropertyValues( List<Property> list, String oldName, String newName )
    {
        for ( Property prop : list )
        {
            if ( prop instanceof ColorProperty )
            {
                ColorProperty colorProp = (ColorProperty)prop;
                if ( ( colorProp.getValue() != null ) && colorProp.getValue().equals( oldName ) )
                    colorProp.setValue( newName );
            }
        }
    }
    
    public void renameColor( String oldName, String newName )
    {
        Color color = colorMap.get( oldName );
        if ( color == null )
            return;
        
        colorMap.remove( oldName );
        colorMap.put( newName, color );
        
        FlatWidgetPropertiesContainer propsCont = new FlatWidgetPropertiesContainer();
        for ( Widget widget : widgets )
        {
            propsCont.clear();
            widget.getProperties( propsCont );
            
            renameColorPropertyValues( propsCont.getList(), oldName, newName );
        }
    }
    
    public void resetColors( String oldName, String newValue )
    {
        Color color = colorMap.get( oldName );
        if ( color == null )
            return;
        
        colorMap.remove( oldName );
        
        FlatWidgetPropertiesContainer propsCont = new FlatWidgetPropertiesContainer();
        for ( Widget widget : widgets )
        {
            propsCont.clear();
            widget.getProperties( propsCont );
            
            renameColorPropertyValues( propsCont.getList(), oldName, newValue );
        }
    }
    
    /**
     * Maps a new named font.
     * 
     * @param name
     * @param fontStr
     * 
     * @return changed?
     */
    public boolean addNamedFont( String name, String fontStr )
    {
        Font font = FontUtils.parseFont( fontStr, gameResY, false );
        boolean virtual = FontUtils.parseVirtualFlag( fontStr, false );
        
        Font oldFont = this.fontMap.put( name, font );
        this.fontStringMap.put( name, fontStr );
        Boolean oldVirt = this.fontVirtualMap.put( name, virtual );
        
        return ( !font.equals( oldFont ) || !oldVirt.equals( virtual ) );
    }
    
    /**
     * Gets a named font from the map or <code>null</code>, if not found.
     * 
     * @param name
     * 
     * @return a named font from the map or <code>null</code>, if not found.
     */
    public final Font getNamedFont( String name )
    {
        return ( fontMap.get( name ) );
    }
    
    /**
     * Gets a named font from the map or <code>null</code>, if not found.
     * 
     * @param name
     * 
     * @return a named font from the map or <code>null</code>, if not found.
     */
    public final String getNamedFontString( String name )
    {
        return ( fontStringMap.get( name ) );
    }
    
    /**
     * Gets a named font's virtual flag from the map or <code>null</code>, if not found.
     * 
     * @param name
     * 
     * @return a named font's virtual flag from the map or <code>null</code>, if not found.
     */
    public final Boolean getNamedFontVirtual( String name )
    {
        return ( fontVirtualMap.get( name ) );
    }
    
    /**
     * Gets all currently mapped font names.
     * 
     * @return all currently mapped font names.
     */
    public final Set<String> getFontNames()
    {
        return ( fontMap.keySet() );
    }
    
    /**
     * Removes a mapped named font.
     * 
     * @param name
     * 
     * @return the previously mapped font.
     */
    public Font removeNamedFont( String name )
    {
        Font font = fontMap.remove( name );
        String fontString = fontStringMap.remove( name );
        Boolean virtual = fontVirtualMap.remove( name );
        
        if ( font != null )
        {
            boolean antiAliased = FontUtils.parseAntiAliasFlag( fontString, false );
            fontString = FontUtils.getFontString( font, virtual, antiAliased );
            
            resetFonts( name, fontString );
        }
        
        return ( font );
    }
    
    private static void renameFontPropertyValues( List<Property> list, String oldName, String newName )
    {
        for ( Property prop : list )
        {
            if ( prop instanceof FontProperty )
            {
                FontProperty fontProp = (FontProperty)prop;
                if ( ( fontProp.getValue() != null ) && fontProp.getValue().equals( oldName ) )
                    fontProp.setValue( newName );
            }
        }
    }
    
    private static void resetAllFontProperties( List<Property> list )
    {
        for ( Property prop : list )
        {
            if ( prop instanceof FontProperty )
            {
                FontProperty fontProp = (FontProperty)prop;
                fontProp.setValue( fontProp.getValue() );
            }
        }
    }
    
    private void resetAllFontProperties()
    {
        FlatWidgetPropertiesContainer propsCont = new FlatWidgetPropertiesContainer();
        for ( Widget widget : widgets )
        {
            propsCont.clear();
            widget.getProperties( propsCont );
            
            resetAllFontProperties( propsCont.getList() );
        }
    }
    
    public void renameFont( String oldName, String newName )
    {
        Font font = fontMap.get( oldName );
        if ( font == null )
            return;
        
        fontMap.remove( oldName );
        fontMap.put( newName, font );
        fontStringMap.put( newName, fontStringMap.remove( oldName ) );
        fontVirtualMap.put( newName, fontVirtualMap.remove( oldName ) );
        
        FlatWidgetPropertiesContainer propsCont = new FlatWidgetPropertiesContainer();
        for ( Widget widget : widgets )
        {
            propsCont.clear();
            widget.getProperties( propsCont );
            
            renameFontPropertyValues( propsCont.getList(), oldName, newName );
            //break;
        }
    }
    
    public void resetFonts( String oldName, String newValue )
    {
        Font font = fontMap.get( oldName );
        if ( font == null )
            return;
        
        fontMap.remove( oldName );
        fontStringMap.remove( oldName );
        fontVirtualMap.remove( oldName );
        
        FlatWidgetPropertiesContainer propsCont = new FlatWidgetPropertiesContainer();
        for ( Widget widget : widgets )
        {
            propsCont.clear();
            widget.getProperties( propsCont );
            
            renameFontPropertyValues( propsCont.getList(), oldName, newValue );
            //break;
        }
    }
    
    /**
     * Maps a new border alias to its filename.
     * 
     * @param alias
     * @param border
     * 
     * @return changed?
     */
    public boolean addBorderAlias( String alias, String border )
    {
        String old = borderMap.put( alias, border );
        
        return ( !border.equals( old ) );
    }
    
    /**
     * Gets a border filename from the map or <code>null</code>, if not found.
     * 
     * @param alias
     * 
     * @return a border filename from the map or <code>null</code>, if not found.
     */
    public final String getBorderName( String alias )
    {
        return ( borderMap.get( alias ) );
    }
    
    /**
     * Gets all currently mapped font names.
     * 
     * @return all currently mapped font names.
     */
    public final Set<String> getBorderAliases()
    {
        return ( borderMap.keySet() );
    }
    
    /**
     * Removes a mapped border alias.
     * 
     * @param alias
     * 
     * @return the previously mapped border alias.
     */
    public String removeBorderAlias( String alias )
    {
        String border = borderMap.remove( alias );
        
        if ( border != null )
        {
            resetFonts( alias, border );
        }
        
        return ( border );
    }
    
    private static void renameBorderPropertyValues( List<Property> list, String oldName, String newName )
    {
        for ( Property prop : list )
        {
            if ( prop instanceof BorderProperty )
            {
                BorderProperty borderProp = (BorderProperty)prop;
                if ( ( borderProp.getValue() != null ) && borderProp.getValue().equals( oldName ) )
                    borderProp.setValue( newName );
            }
        }
    }
    
    public void renameBorder( String oldName, String newName )
    {
        String border = borderMap.get( oldName );
        if ( border == null )
            return;
        
        borderMap.remove( oldName );
        borderMap.put( newName, border );
        
        FlatWidgetPropertiesContainer propsCont = new FlatWidgetPropertiesContainer();
        for ( Widget widget : widgets )
        {
            propsCont.clear();
            widget.getProperties( propsCont );
            
            renameBorderPropertyValues( propsCont.getList(), oldName, newName );
            //break;
        }
    }
    
    public void resetBorders( String oldName, String newValue )
    {
        String border = borderMap.get( oldName );
        if ( border == null )
            return;
        
        borderMap.remove( oldName );
        
        FlatWidgetPropertiesContainer propsCont = new FlatWidgetPropertiesContainer();
        for ( Widget widget : widgets )
        {
            propsCont.clear();
            widget.getProperties( propsCont );
            
            renameBorderPropertyValues( propsCont.getList(), oldName, newValue );
            //break;
        }
    }
    
    /**
     * Creates a new {@link WidgetsConfiguration}.
     */
    public WidgetsConfiguration()
    {
    }
}
