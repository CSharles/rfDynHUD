package net.ctdp.rfdynhud.widgets;

import net.ctdp.rfdynhud.gamedata.LiveGameData;

public class __WCPrivilegedAccess
{
    public static final void setJustLoaded( WidgetsConfiguration config, LiveGameData gameData )
    {
        config.setJustLoaded( gameData );
    }
    
    public static final void setGameResolution( int gameResX, int gameResY, WidgetsConfiguration widgetsConfig )
    {
        widgetsConfig.setGameResolution( gameResX, gameResY );
    }
}