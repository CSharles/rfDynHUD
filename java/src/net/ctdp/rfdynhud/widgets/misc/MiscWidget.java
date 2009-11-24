package net.ctdp.rfdynhud.widgets.misc;

import java.io.IOException;

import net.ctdp.rfdynhud.editor.hiergrid.FlaggedList;
import net.ctdp.rfdynhud.editor.properties.BooleanProperty;
import net.ctdp.rfdynhud.editor.properties.EnumProperty;
import net.ctdp.rfdynhud.editor.properties.Property;
import net.ctdp.rfdynhud.editor.properties.PropertyEditorType;
import net.ctdp.rfdynhud.gamedata.GamePhase;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.SessionType;
import net.ctdp.rfdynhud.gamedata.TopspeedRecorder;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.input.InputAction;
import net.ctdp.rfdynhud.render.Texture2DCanvas;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.NumberUtil;
import net.ctdp.rfdynhud.widgets._util.DrawnString;
import net.ctdp.rfdynhud.widgets._util.EnumValue;
import net.ctdp.rfdynhud.widgets._util.FloatValue;
import net.ctdp.rfdynhud.widgets._util.IntValue;
import net.ctdp.rfdynhud.widgets._util.Size;
import net.ctdp.rfdynhud.widgets._util.StringValue;
import net.ctdp.rfdynhud.widgets._util.TimingUtil;
import net.ctdp.rfdynhud.widgets._util.ValidityTest;
import net.ctdp.rfdynhud.widgets._util.WidgetsConfigurationWriter;
import net.ctdp.rfdynhud.widgets._util.DrawnString.Alignment;
import net.ctdp.rfdynhud.widgets.widget.Widget;

/**
 * The {@link MiscWidget} displays miscellaneous information like fastest lap, session time, top speed, etc..
 * 
 * @author Marvin Froehlich
 */
public class MiscWidget extends Widget
{
    public static enum LapDisplayType
    {
        CURRENT_LAP,
        LAPS_DONE,
        ;
    }
    
    private final EnumProperty<LapDisplayType> lapDisplayType = new EnumProperty<LapDisplayType>( this, "lapDisplayType", LapDisplayType.CURRENT_LAP );
    private long relTopspeedResetDelay = 5000000000L; // five seconds
    
    private final BooleanProperty displayScoring = new BooleanProperty( this, "displayScoring", true );
    private final BooleanProperty displayTiming = new BooleanProperty( this, "displayTiming", true );
    private final BooleanProperty displayVelocity = new BooleanProperty( this, "displayVelocity", true );
    
    private DrawnString scoringString1 = null;
    private DrawnString scoringString2 = null;
    private DrawnString scoringString3 = null;
    
    private DrawnString sessionTimeString = null;
    private DrawnString lapString = null;
    private DrawnString stintString = null;
    
    private DrawnString absTopspeedString = null;
    private DrawnString relTopspeedString = null;
    private DrawnString velocityString = null;
    
    private int oldStintLength = -1;
    private final StringValue leader = new StringValue();
    private boolean leaderValid = false;
    private final FloatValue fastestLap = new FloatValue( ValidityTest.GREATER_THAN, 0f );
    private final FloatValue sessionTime = new FloatValue( -1f, 0.1f );
    private final EnumValue<GamePhase> gamePhase = new EnumValue<GamePhase>();
    private final IntValue lapsCompleted = new IntValue();
    private float oldLapsRemaining = -1f;
    private float oldAbsTopspeed = -1f;
    private float oldRelTopspeed = -1f;
    private float relTopspeed = -1f;
    private long lastRelTopspeedTime = -1L;
    private float topspeed;
    private int velocity;
    private int oldVelocity = -1;
    
    private int[] colWidths2 = new int[ 2 ];
    private int[] colWidths3 = new int[ 3 ];
    private int totalColWidths = 0;
    private final Alignment[] velocityAlignment = new Alignment[] { Alignment.RIGHT, Alignment.LEFT, Alignment.LEFT };
    
    public void setRelTopspeedResetDelay( int delay )
    {
        this.relTopspeedResetDelay = delay * 1000000L;
    }
    
    public final int getRelTopspeedResetDelay()
    {
        return ( (int)( relTopspeedResetDelay / 1000000L ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onSessionStarted( boolean isEditorMode, SessionType sessionType, LiveGameData gameData )
    {
        super.onSessionStarted( isEditorMode, sessionType, gameData );
        
        oldAbsTopspeed = -1f;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onRealtimeEntered( boolean isEditorMode, LiveGameData gameData )
    {
        super.onRealtimeEntered( isEditorMode, gameData );
        
        oldStintLength = -1;
        
        leader.reset();
        leaderValid = false;
        
        fastestLap.reset();
        
        sessionTime.reset();
        gamePhase.reset();
        lapsCompleted.reset();
        oldLapsRemaining = -1f;
        
        oldRelTopspeed = -1f;
        relTopspeed = -1f;
        lastRelTopspeedTime = -1L;
        oldVelocity = -1;
        
        //forceReinitialization();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onBoundInputStateChanged( boolean isEditorMode, InputAction action, boolean state, int modifierMask )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkForChanges( boolean isEditorMode, boolean clock1, boolean clock2, LiveGameData gameData, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize( boolean isEditorMode, boolean clock1, boolean clock2, LiveGameData gameData, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        final java.awt.Font font = getFont();
        final boolean fontAntiAliased = isFontAntiAliased();
        final java.awt.Color fontColor = getFontColor();
        
        final int left = 2;
        final int center = width / 2;
        final int right = width - 2;
        final int top = -2;
        
        if ( displayScoring.getBooleanValue() )
        {
            scoringString1 = new DrawnString( left, top, Alignment.LEFT, false, font, fontAntiAliased, fontColor, null, null, null );
            scoringString2 = new DrawnString( null, scoringString1, left, 0, Alignment.LEFT, false, font, fontAntiAliased, fontColor, null, null, null );
            scoringString3 = new DrawnString( null, scoringString2, left, 0, Alignment.LEFT, false, font, fontAntiAliased, fontColor, null, null, null );
        }
        else
        {
            scoringString1 = null;
            scoringString2 = null;
            scoringString3 = null;
        }
        
        if ( displayTiming.getBooleanValue() )
        {
            if ( ( displayScoring.getBooleanValue() && displayVelocity.getBooleanValue() ) || ( !displayScoring.getBooleanValue() && !displayVelocity.getBooleanValue() ) )
            {
                if ( lapDisplayType.getEnumValue() == LapDisplayType.CURRENT_LAP )
                    lapString = new DrawnString( center, top, Alignment.CENTER, false, font, fontAntiAliased, fontColor, "Lap: ", null, null );
                else
                    lapString = new DrawnString( center, top, Alignment.CENTER, false, font, fontAntiAliased, fontColor, "Laps: ", null, null );
                
                stintString = new DrawnString( lapString, lapString, 0, 0, Alignment.CENTER, false, font, fontAntiAliased, fontColor, "Stint: ", null, null );
                sessionTimeString = new DrawnString( lapString, stintString, 0, 0, Alignment.CENTER, false, font, fontAntiAliased, fontColor, "Time: ", null, null );
            }
            else if ( !displayScoring.getBooleanValue() )
            {
                if ( lapDisplayType.getEnumValue() == LapDisplayType.CURRENT_LAP )
                    lapString = new DrawnString( left, top, Alignment.LEFT, false, font, fontAntiAliased, fontColor, "Lap: ", null, null );
                else
                    lapString = new DrawnString( left, top, Alignment.LEFT, false, font, fontAntiAliased, fontColor, "Laps: ", null, null );
                
                stintString = new DrawnString( lapString, lapString, 0, 0, Alignment.CENTER, false, font, fontAntiAliased, fontColor, "Stint: ", null, null );
                sessionTimeString = new DrawnString( lapString, stintString, left, 0, Alignment.CENTER, false, font, fontAntiAliased, fontColor, "Time: ", null, null );
            }
            else if ( !displayVelocity.getBooleanValue() )
            {
                if ( lapDisplayType.getEnumValue() == LapDisplayType.CURRENT_LAP )
                    lapString = new DrawnString( right, top, Alignment.RIGHT, false, font, fontAntiAliased, fontColor, "Lap: ", null, null );
                else
                    lapString = new DrawnString( right, top, Alignment.RIGHT, false, font, fontAntiAliased, fontColor, "Laps: ", null, null );
                
                stintString = new DrawnString( lapString, lapString, 0, 0, Alignment.CENTER, false, font, fontAntiAliased, fontColor, "Stint: ", null, null );
                sessionTimeString = new DrawnString( lapString, stintString, left, 0, Alignment.CENTER, false, font, fontAntiAliased, fontColor, "Time: ", null, null );
            }
        }
        else
        {
            sessionTimeString = null;
            lapString = null;
            stintString = null;
        }
        
        if ( displayVelocity.getBooleanValue() )
        {
            absTopspeedString = new DrawnString( right, top, Alignment.RIGHT, false, font, fontAntiAliased, fontColor, null, null, null );
            relTopspeedString = new DrawnString( null, absTopspeedString, right, 0, Alignment.RIGHT, false, font, fontAntiAliased, fontColor, null, null, null );
            velocityString = new DrawnString( null, relTopspeedString, right, 0, Alignment.RIGHT, false, font, fontAntiAliased, fontColor, null, null, null );
        }
        else
        {
            absTopspeedString = null;
            relTopspeedString = null;
            velocityString = null;
        }
    }
    
    private void updateScoringColWidths( TextureImage2D image )
    {
        colWidths2[0] = 0;
        colWidths2[1] = 0;
        int padding = 4;
        
        totalColWidths = scoringString1.getMaxColWidths( new String[] { "Leader:", leader.getValue() }, padding, image, colWidths2 );
        if ( fastestLap.isValid() )
        {
            totalColWidths = scoringString2.getMaxColWidths( new String[] { "Fastest Lap:", TimingUtil.getTimeAsString( fastestLap.getValue(), true ) }, padding, image, colWidths2 );
            totalColWidths = scoringString3.getMaxColWidths( new String[] { "", TimingUtil.getTimeAsString( fastestLap.getValue(), true ) }, padding, image, colWidths2 );
        }
        else
        {
            totalColWidths = scoringString2.getMaxColWidths( new String[] { "Fastest Lap:", TimingUtil.getTimeAsString( fastestLap.getValue(), true ) }, padding, image, colWidths2 );
            totalColWidths = scoringString3.getMaxColWidths( new String[] { "", "--:--.---" }, padding, image, colWidths2 );
        }
    }
    
    private void updateVelocityColWidths( TextureImage2D image )
    {
        colWidths3[0] = 0;
        colWidths3[1] = 0;
        colWidths3[2] = 0;
        int padding = 4;
        
        totalColWidths = absTopspeedString.getMaxColWidths( new String[] { "Abs. Topspeed:", NumberUtil.formatFloat( Math.max( oldAbsTopspeed, topspeed ), 1, true ), "km/h" }, padding, image, colWidths3 );
        totalColWidths = relTopspeedString.getMaxColWidths( new String[] { "Rel. Topspeed:", NumberUtil.formatFloat( relTopspeed, 1, true ), "km/h" }, padding, image, colWidths3 );
        totalColWidths = velocityString.getMaxColWidths( new String[] { "Velocity:", String.valueOf( velocity ), "km/h" }, padding, image, colWidths3 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( boolean isEditorMode, boolean clock1, boolean clock2, boolean needsCompleteRedraw, LiveGameData gameData, Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height )
    {
        final TextureImage2D image = texCanvas.getImage();
        final java.awt.Color backgroundColor = getBackgroundColor();
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if ( displayScoring.getBooleanValue() )
        {
            VehicleScoringInfo leaderVSI = scoringInfo.getVehicleScoringInfo( 0 );
            leader.update( leaderVSI.getDriverName() );
            VehicleScoringInfo fastestLapVSI = scoringInfo.getFastestLapVSI();
            String fastestLapper = fastestLapVSI.getDriverName();
            fastestLap.update( fastestLapVSI.getBestLapTime() );
            
            boolean colWidthsUpdated = false;
            
            boolean lv = ( leaderVSI.getBestLapTime() > 0f );
            if ( needsCompleteRedraw || leader.hasChanged() || ( lv != leaderValid ) )
            {
                leaderValid = lv;
                
                if ( !colWidthsUpdated )
                {
                    updateScoringColWidths( image );
                    colWidthsUpdated = true;
                }
                
                if ( scoringInfo.getSessionType().isRace() )
                {
                    if ( leaderValid )
                        scoringString1.draw( offsetX, offsetY, new String[] { "Leader:", leader.getValue() }, colWidths2, backgroundColor, image );
                    else
                        scoringString1.draw( offsetX, offsetY, new String[] { "Leader:", "N/A" }, colWidths2, backgroundColor, image );
                }
                else
                {
                    scoringString1.draw( offsetX, offsetY, "", backgroundColor, image );
                }
            }
            
            if ( needsCompleteRedraw || fastestLap.hasValidityChanged() || fastestLap.hasChanged() )
            {
                if ( !colWidthsUpdated )
                {
                    updateScoringColWidths( image );
                    colWidthsUpdated = true;
                }
                
                if ( fastestLap.isValid() )
                {
                    scoringString2.draw( offsetX, offsetY, new String[] { "Fastest Lap:", TimingUtil.getTimeAsString( fastestLap.getValue(), true ) }, colWidths2, backgroundColor, image );
                    scoringString3.draw( offsetX, offsetY, new String[] { "", "(" + fastestLapper + ")" }, colWidths2, backgroundColor, image );
                }
                else
                {
                    scoringString2.draw( offsetX, offsetY, new String[] { "Fastest Lap:", "--:--.---" }, colWidths2, backgroundColor, image );
                    scoringString3.draw( offsetX, offsetY, "", backgroundColor, image );
                }
            }
        }
        
        if ( displayTiming.getBooleanValue() )
        {
            gamePhase.update( scoringInfo.getGamePhase() );
            VehicleScoringInfo vsi = scoringInfo.getPlayersVehicleScoringInfo();
            lapsCompleted.update( vsi.getLapsCompleted() );
            final int maxLaps = scoringInfo.getMaxLaps();
            if ( maxLaps < Integer.MAX_VALUE / 2 )
            {
                float lapsRemaining = maxLaps - lapsCompleted.getValue();
                
                if ( true )
                {
                    lapsRemaining -= 1f;
                    lapsRemaining += 1f - vsi.getLapDistance() / scoringInfo.getTrackLength();
                }
                
                float rounded = Math.round( lapsRemaining * 10f );
                
                if ( needsCompleteRedraw || ( rounded != oldLapsRemaining ) || gamePhase.hasChanged())
                {
                    oldLapsRemaining = rounded;
                    
                    String string;
                    if ( ( scoringInfo.getSessionType() == SessionType.RACE ) && ( scoringInfo.getGamePhase() == GamePhase.FORMATION_LAP ) )
                    {
                        string = "0 / " + maxLaps + " / " + maxLaps;
                    }
                    else
                    {
                        if ( lapDisplayType.getEnumValue() == LapDisplayType.CURRENT_LAP )
                            string = ( lapsCompleted.getValue() + 1 ) + " / " + maxLaps + " / " + NumberUtil.formatFloat( lapsRemaining, 1, true );
                        else
                            string = lapsCompleted + " / " + maxLaps + " / " + NumberUtil.formatFloat( lapsRemaining, 1, true );
                    }
                    lapString.draw( offsetX, offsetY, string, backgroundColor, image );
                }
            }
            else if ( needsCompleteRedraw || lapsCompleted.hasChanged() )
            {
                String string;
                if ( lapDisplayType.getEnumValue() == LapDisplayType.CURRENT_LAP )
                    string = String.valueOf( lapsCompleted.getValue() + 1 );
                else
                    string = String.valueOf( lapsCompleted );
                lapString.draw( offsetX, offsetY, string, backgroundColor, image );
            }
            
            {
                int stintLength = (int)( vsi.getStintLength() * 10f );
                if ( needsCompleteRedraw || ( stintLength != oldStintLength ) )
                {
                    if ( vsi.isInPits() )
                    {
                        if ( oldStintLength < 0 )
                            stintString.draw( offsetX, offsetY, "N/A", backgroundColor, image );
                        else
                            stintString.draw( offsetX, offsetY, String.valueOf( Math.round( oldStintLength / 10f ) ), backgroundColor, image );
                    }
                    else
                    {
                        oldStintLength = stintLength;
                        
                        stintString.draw( offsetX, offsetY, String.valueOf( oldStintLength / 10f ), backgroundColor, image );
                    }
                }
            }
            
            sessionTime.update( gameData.getScoringInfo().getSessionTime() );
            float totalTime = gameData.getScoringInfo().getEndTime();
            if ( needsCompleteRedraw || ( clock1 && ( sessionTime.hasChanged( false ) || gamePhase.hasChanged( false ) ) ) )
            {
                sessionTime.setUnchanged();
                gamePhase.setUnchanged();
                
                if ( scoringInfo.getSessionType().isRace() && ( ( scoringInfo.getGamePhase() == GamePhase.FORMATION_LAP ) || ( totalTime < 0f ) || ( totalTime > 3000000f ) ) )
                {
                    sessionTimeString.draw( offsetX, offsetY, "--:--:--", backgroundColor, image );
                }
                else if ( scoringInfo.getSessionType().isTestDay() || ( totalTime < 0f ) || ( totalTime > 3000000f ) )
                {
                    String string = TimingUtil.getTimeAsString( sessionTime.getValue(), true, false );
                    sessionTimeString.draw( offsetX, offsetY, string, backgroundColor, image );
                }
                else
                {
                    String string = TimingUtil.getTimeAsString( sessionTime.getValue() - totalTime, true, false );
                    sessionTimeString.draw( offsetX, offsetY, string, backgroundColor, image );
                }
            }
        }
        
        if ( displayVelocity.getBooleanValue() )
        {
            float floatVelocity = gameData.getTelemetryData().getScalarVelocityKPH();
            velocity = Math.round( floatVelocity );
            topspeed = TopspeedRecorder.MASTER_TOPSPEED_RECORDER.getTopSpeed();
            
            if ( floatVelocity > relTopspeed )
            {
                relTopspeed = floatVelocity;
                lastRelTopspeedTime = scoringInfo.getSessionNanos();
            }
            else if ( ( lastRelTopspeedTime + relTopspeedResetDelay < scoringInfo.getSessionNanos() ) && ( floatVelocity < relTopspeed - 50f ) )
            {
                relTopspeed = floatVelocity;
                oldRelTopspeed = -1f;
                lastRelTopspeedTime = scoringInfo.getSessionNanos();
            }
            
            boolean colWidthsUpdated = false;
            
            if ( needsCompleteRedraw || ( clock2 && ( topspeed > oldAbsTopspeed ) ) )
            {
                if ( !colWidthsUpdated )
                {
                    updateVelocityColWidths( image );
                    colWidthsUpdated = true;
                }
                
                oldAbsTopspeed = topspeed;
                absTopspeedString.draw( offsetX - totalColWidths, offsetY, new String[] { "Abs. Topspeed:", NumberUtil.formatFloat( topspeed, 1, true ), "km/h" }, velocityAlignment, 4, colWidths3, backgroundColor, image );
            }
            
            if ( needsCompleteRedraw || ( clock2 && ( relTopspeed > oldRelTopspeed ) ) )
            {
                if ( !colWidthsUpdated )
                {
                    updateVelocityColWidths( image );
                    colWidthsUpdated = true;
                }
                
                oldRelTopspeed = relTopspeed;
                relTopspeedString.draw( offsetX - totalColWidths, offsetY, new String[] { "Rel. Topspeed:", NumberUtil.formatFloat( oldRelTopspeed, 1, true ), "km/h" }, velocityAlignment, 4, colWidths3, backgroundColor, image );
            }
            
            if ( needsCompleteRedraw || ( clock2 && ( velocity != oldVelocity ) ) )
            {
                if ( !colWidthsUpdated )
                {
                    updateVelocityColWidths( image );
                    colWidthsUpdated = true;
                }
                
                oldVelocity = velocity;
                velocityString.draw( offsetX - totalColWidths, offsetY, new String[] { "Velocity:", String.valueOf( velocity ), "km/h" }, velocityAlignment, 4, colWidths3, backgroundColor, image );
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveProperties( WidgetsConfigurationWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( displayScoring, "Display the scoring part of the Widget?" );
        writer.writeProperty( displayTiming, "Display the timing part of the Widget?" );
        writer.writeProperty( displayVelocity, "Display the velocity and top speed part of the Widget?" );
        writer.writeProperty( lapDisplayType, "The way the laps are displayed. Valid values: CURRENT_LAP, LAPS_DONE." );
        writer.writeProperty( "relTopspeedResetDelay", getRelTopspeedResetDelay(), "The delay after which the relative topspeed is resetted (in milliseconds)." );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadProperty( String key, String value )
    {
        super.loadProperty( key, value );
        
        if ( displayScoring.loadProperty( key, value ) );
        else if ( displayTiming.loadProperty( key, value ) );
        else if ( displayVelocity.loadProperty( key, value ) );
        else if ( lapDisplayType.loadProperty( key, value ) );
        else if ( key.equals( "relTopspeedResetDelay" ) )
            this.relTopspeedResetDelay = Integer.parseInt( value ) * 1000000L;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getProperties( FlaggedList propsList )
    {
        super.getProperties( propsList );
        
        FlaggedList props = new FlaggedList( "Specific", true );
        
        props.add( displayScoring );
        props.add( displayTiming );
        props.add( displayVelocity );
        props.add( lapDisplayType );
        
        props.add( new Property( "relTopspeedResetDelay", PropertyEditorType.INTEGER )
        {
            @Override
            public void setValue( Object value )
            {
                setRelTopspeedResetDelay( ( (Number)value ).intValue() );
            }
            
            @Override
            public Object getValue()
            {
                return ( getRelTopspeedResetDelay() );
            }
        } );
        
        propsList.add( props );
    }
    
    public MiscWidget( String name )
    {
        super( name, Size.PERCENT_OFFSET + 0.6625f, Size.PERCENT_OFFSET + 0.0583f );
    }
}
