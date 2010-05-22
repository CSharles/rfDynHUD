package net.ctdp.rfdynhud.gamedata;

import net.ctdp.rfdynhud.editor.EditorPresets;
import net.ctdp.rfdynhud.util.RFactorEventsManager;

/**
 * 
 * 
 * @author Marvin Froehlich
 */
public class LiveGameData
{
    private boolean realtimeMode = false;
    
    private final VehiclePhysics physics = new VehiclePhysics();
    private VehicleSetup setup = null;
    
    private final TelemetryData telemetryData;
    private final ScoringInfo scoringInfo;
    private final GraphicsInfo graphicsInfo;
    private final CommentaryRequestInfo commentaryInfo;
    
    private final ProfileInfo profileInfo;
    private final ModInfo modInfo;
    private final TrackInfo trackInfo;
    
    void setRealtimeMode( boolean realtimeMode )
    {
        this.realtimeMode = realtimeMode;
    }
    
    public final boolean isInRealtimeMode()
    {
        return ( realtimeMode );
    }
    
    public final VehiclePhysics getPhysics()
    {
        return ( physics );
    }
    
    void setSetup( VehicleSetup setup )
    {
        this.setup = setup;
    }
    
    public final VehicleSetup getSetup()
    {
        return ( setup );
    }
    
    public final TelemetryData getTelemetryData()
    {
        return ( telemetryData );
    }
    
    public final ScoringInfo getScoringInfo()
    {
        return ( scoringInfo );
    }
    
    public final GraphicsInfo getGraphicsInfo()
    {
        return ( graphicsInfo );
    }
    
    public final CommentaryRequestInfo getCommentaryRequestInfo()
    {
        return ( commentaryInfo );
    }
    
    public final ModInfo getModInfo()
    {
        return ( modInfo );
    }
    
    public final ProfileInfo getProfileInfo()
    {
        return ( profileInfo );
    }
    
    public final TrackInfo getTrackInfo()
    {
        return ( trackInfo );
    }
    
    void applyEditorPresets( EditorPresets editorPresets )
    {
        telemetryData.applyEditorPresets( editorPresets );
        scoringInfo.applyEditorPresets( editorPresets );
        setup.applyEditorPresets( editorPresets );
    }
    
    public LiveGameData( RFactorEventsManager eventsManager )
    {
        this.telemetryData = new TelemetryData( this, eventsManager );
        this.scoringInfo = new ScoringInfo( this, eventsManager );
        this.graphicsInfo = new GraphicsInfo( this );
        this.commentaryInfo = new CommentaryRequestInfo( this );
        
        this.profileInfo = new ProfileInfo();
        this.modInfo = new ModInfo( profileInfo );
        this.trackInfo = new TrackInfo( profileInfo );
        
        eventsManager.setGameData( this );
    }
}
