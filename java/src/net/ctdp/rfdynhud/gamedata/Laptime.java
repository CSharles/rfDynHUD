package net.ctdp.rfdynhud.gamedata;

public class Laptime
{
    private final int lap;
    float sector1 = -1f;
    float sector2 = -1f;
    float sector3 = -1f;
    
    boolean isOutLap = false;
    Boolean isInLap = null;
    
    public final int getLap()
    {
        return ( lap );
    }
    
    public final float getSector1()
    {
        return ( sector1 );
    }
    
    public final float getSector2()
    {
        return ( sector2 );
    }
    
    public final float getSector1And2()
    {
        return ( sector1 + sector2 );
    }
    
    public final float getSector2( boolean includingSector1 )
    {
        if ( includingSector1 )
            return ( getSector1And2() );
        
        return ( getSector2() );
    }
    
    public final float getSector3()
    {
        return ( sector3 );
    }
    
    public final float getLapTime()
    {
        if ( ( sector1 < 0f ) || ( sector3 < 0f ) || ( sector3 < 0f ) )
            return ( -1 );
        
        return ( sector1 + sector2 + sector3 );
    }
    
    /**
     * Gets whether this lap is an outlap. If this information is not yet available, null is returned.
     * 
     * @return whether this lap is an outlap. If this information is not yet available, null is returned.
     */
    public final Boolean isOutlap()
    {
        return ( isOutLap );
    }
    
    /**
     * Gets whether this lap is an inlap. If this information is not yet available, null is returned.
     * 
     * @return whether this lap is an inlap. If this information is not yet available, null is returned.
     */
    public final Boolean isInlap()
    {
        return ( isInLap );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( "lap: " + getLap() + ", sec1: " + getSector1() + ", sec2: " + getSector2() + ", sec3: " + getSector3() + ", laptime: " + getLapTime() + ( isOutlap() == Boolean.TRUE ? ", OUTLAP" : ( isOutlap() == null ? ", UNKNOWN" : ", REGULAR" ) ) + ( isInlap() == Boolean.TRUE ? ", INLAP" : ( isInlap() == null ? ", UNKNOWN" : ", REGULAR" ) ) );
    }
    
    public Laptime( int lap )
    {
        this.lap = lap;
    }
}