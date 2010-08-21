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
package net.ctdp.rfdynhud.render;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import net.ctdp.rfdynhud.util.NumberUtil;

import sun.awt.image.ByteInterleavedRaster;

/**
 * <p>
 * The {@link ImageTemplate} is a container for image data configured through a property.
 * </p>
 * 
 * <p>
 * You can get a scaled representation of the image or directly draw a scaled version
 * to another image.
 * </p>
 * 
 * @author Marvin Froehlich (CTDP)
 */
public class ImageTemplate
{
    long lastModified = -1L;
    long fileSize = -1L;
    
    private final BufferedImage bufferedImage;
    
    /**
     * Gets the base width of the image. This can be the physical image size
     * or the size defined in an SVG image.
     * 
     * @return the base width of the image.
     */
    public final int getBaseWidth()
    {
        return ( bufferedImage.getWidth() );
    }
    
    /**
     * Gets the base height of the image. This can be the physical image size
     * or the size defined in an SVG image.
     * 
     * @return the base height of the image.
     */
    public final int getBaseHeight()
    {
        return ( bufferedImage.getHeight() );
    }
    
    /**
     * Gets the base aspect ratio of the image.
     * 
     * @return the base aspect ratio of the image.
     */
    public final float getBaseAspect()
    {
        return ( getBaseWidth() / (float)getBaseHeight() );
    }
    
    private void copyPixels( TextureImage2D texture )
    {
        ByteInterleavedRaster raster = (ByteInterleavedRaster)bufferedImage.getData();
        int[] byteOffsets = raster.getDataOffsets();
        byte[] srcBytes = raster.getDataStorage();
        byte[] data;
        
        /*
        if ( ( ByteOrderManager.RED == byteOffsets[0] ) && ( ByteOrderManager.GREEN == byteOffsets[1] ) && ( ByteOrderManager.BLUE == byteOffsets[2] ) && ( ByteOrderManager.ALPHA == byteOffsets[3] ) )
        {
            texture = TextureImage2D.createOfflineTexture( bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getColorModel().hasAlpha(), srcBytes );
        }
        else
        */
        {
            int pixelStride = ( bufferedImage.getColorModel().hasAlpha() ? 4 : 3 );
            //data = new byte[ bufferedImage.getWidth() * bufferedImage.getHeight() * pixelStride ];
            data = texture.getData();
            
            int offset = 0;
            for ( int j = 0; j < bufferedImage.getHeight(); j++ )
            {
                for ( int i = 0; i < bufferedImage.getWidth(); i++ )
                {
                    data[offset + ByteOrderManager.RED] = srcBytes[offset + byteOffsets[0]];
                    data[offset + ByteOrderManager.GREEN] = srcBytes[offset + byteOffsets[1]];
                    data[offset + ByteOrderManager.BLUE] = srcBytes[offset + byteOffsets[2]];
                    if ( pixelStride == 4 )
                        data[offset + ByteOrderManager.ALPHA] = srcBytes[offset + byteOffsets[3]];
                    offset += pixelStride;
                }
            }
            
            //texture = TextureImage2D.createOfflineTexture( bi.getWidth(), bi.getHeight(), bi.getColorModel().hasAlpha(), data );
        }
    }
    
    /**
     * Draws a scaled representation of this image template to the given texture image.
     * 
     * @param sx
     * @param sy
     * @param sw
     * @param sh
     * @param dx
     * @param dy
     * @param dw
     * @param dh
     * @param texture
     * @param clearBefore
     */
    public void drawScaled( int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh, TextureImage2D texture, boolean clearBefore )
    {
        if ( ( bufferedImage.getData() instanceof ByteInterleavedRaster ) && ( bufferedImage.getColorModel().hasAlpha() == texture.hasAlphaChannel() ) && ( sx == 0 ) && ( sy == 0 ) && ( sw == getBaseWidth() ) && ( sh == getBaseHeight() ) && ( dx == 0 ) && ( dy == 0 ) && ( dw == getBaseWidth() ) && ( dh == getBaseHeight() ) && clearBefore )
        {
            copyPixels( texture );
        }
        else
        {
            if ( clearBefore )
                texture.clear( dx, dy, dw, dh, false, null );
            
            Texture2DCanvas texCanvas = texture.getTextureCanvas();
            
            Object oldInterpolation = texCanvas.getRenderingHint( RenderingHints.KEY_INTERPOLATION );
            texCanvas.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
            
            texCanvas.drawImage( bufferedImage, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw, sy + sh );
            
            if ( oldInterpolation == null )
                texCanvas.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
            else
                texCanvas.setRenderingHint( RenderingHints.KEY_INTERPOLATION, oldInterpolation );
        }
    }
    
    /**
     * Draws a scaled representation of this image template to the given texture image.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param texture
     * @param clearBefore
     */
    public void drawScaled( int x, int y, int width, int height, TextureImage2D texture, boolean clearBefore )
    {
        drawScaled( 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), x, y, width, height, texture, clearBefore );
    }
    
    /**
     * Gets a scaled representation of this image template.
     * 
     * @param width
     * @param height
     * @param usePowerOfTwoSize if true, the created texture is created with power of two width and height (with used size set to the desired values).
     *                          This is useful in editor mode avoid constant recreations.
     * 
     * @return a scaled representation of this image template.
     */
    public TextureImage2D getScaledTextureImage( int width, int height, boolean usePowerOfTwoSize )
    {
        int maxWidth = usePowerOfTwoSize ? NumberUtil.roundUpPower2( width ) : width;
        int maxHeight = usePowerOfTwoSize ? NumberUtil.roundUpPower2( height ) : height;
        
        TextureImage2D texture = TextureImage2D.createDrawTexture( maxWidth, maxHeight, width, height, bufferedImage.getColorModel().hasAlpha() );
        
        drawScaled( 0, 0, width, height, texture, true );
        
        return ( texture );
    }
    
    /**
     * Gets a scaled representation of this image template.
     * If the possibleResult is non null and has the correct size, it is returned.
     * 
     * @param width
     * @param height
     * @param possibleResult
     * @param tryToResize if true, the passed in texture is resized to the given size, if the max size is sufficient.
     *                    This is useful in editor mode avoid constant recreations.
     * 
     * @return a scaled representation of this image template.
     */
    public final TextureImage2D getScaledTextureImage( int width, int height, TextureImage2D possibleResult, boolean tryToResize )
    {
        int oldW = -1;
        int oldH = -1;
        if ( possibleResult != null )
        {
            oldW = possibleResult.getWidth();
            oldH = possibleResult.getHeight();
        }
        
        TextureImage2D texture = TextureImage2D.getOrCreateDrawTexture( width, height, true, possibleResult, tryToResize );
        
        if ( ( ( oldW > 0 ) || ( oldH > 0 ) ) && ( texture == possibleResult ) )
        {
            texture.clear( 0, 0, Math.max( oldW, width ), Math.max( oldH, height ), false, null );
            
            drawScaled( 0, 0, width, height, texture, false );
        }
        else
        {
            drawScaled( 0, 0, width, height, texture, true );
        }
        
        return ( texture );
    }
    
    /**
     * Gets a representation of this image template. If this template encapsulates
     * a fixed sized image, pixel data is copied directly, otherwise it is drawed at the base size.
     * 
     * @return a representation of this image template with base size.
     */
    public final TextureImage2D getTextureImage()
    {
        return ( getScaledTextureImage( getBaseWidth(), getBaseHeight(), false ) );
    }
    
    /**
     * Gets a {@link TransformableTexture} with this image drawn onto it.
     * 
     * @param width
     * @param height
     * @param usePowerOfTwoSize if true, the created texture is created with power of two width and height (with used size set to the desired values).
     *                          This is useful in editor mode to avoid constant recreations.
     * 
     * @return a {@link TransformableTexture} with this image drawn onto it.
     */
    public TransformableTexture getScaledTransformableTexture( int width, int height, boolean usePowerOfTwoSize )
    {
        TransformableTexture texture = new TransformableTexture( width, height, 0, 0, 0, 0, 0f, 1f, 1f, usePowerOfTwoSize );
        
        drawScaled( 0, 0, width, height, texture.getTexture(), true );
        
        return ( texture );
    }
    
    /**
     * Gets a {@link TransformableTexture} with this image drawn onto it.
     * If the possibleResult is non null and has the correct size, it is returned.
     * 
     * @param width
     * @param height
     * @param possibleResult
     * @param tryToResize if true, the passed in texture is resized to the given size, if the max size is sufficient.
     *                    This is useful in editor mode avoid constant recreations.
     * 
     * @return a {@link TransformableTexture} with this image drawn onto it.
     */
    public TransformableTexture getScaledTransformableTexture( int width, int height, TransformableTexture possibleResult, boolean tryToResize )
    {
        final boolean usePowerOfTwoSizes = tryToResize;
        int width2 = usePowerOfTwoSizes ? NumberUtil.roundUpPower2( width ) : width;
        int height2 = usePowerOfTwoSizes ? NumberUtil.roundUpPower2( height ) : height;
        
        if ( possibleResult != null )
        {
            if ( usePowerOfTwoSizes )
            {
                if ( ( width2 == possibleResult.getTexture().getMaxWidth() ) && ( height2 == possibleResult.getTexture().getMaxHeight() ) )
                {
                    if ( ( width != possibleResult.getTexture().getWidth() ) || ( height != possibleResult.getTexture().getHeight() ) )
                    {
                        int oldW = possibleResult.getTexture().getWidth();
                        int oldH = possibleResult.getTexture().getHeight();
                        
                        possibleResult.getTexture().resize( width, height );
                        
                        possibleResult.getTexture().clear( 0, 0, Math.max( oldW, width ), Math.max( oldH, height ), false, null );
                        drawScaled( 0, 0, width, height, possibleResult.getTexture(), false );
                    }
                    
                    return ( possibleResult );
                }
            }
            else if ( ( width == possibleResult.getTexture().getWidth() ) || ( height == possibleResult.getTexture().getHeight() ) )
            {
                return ( possibleResult );
            }
        }
        
        return ( getScaledTransformableTexture( width, height, usePowerOfTwoSizes ) );
    }
    
    /**
     * Gets a {@link TransformableTexture} with this image drawn onto it using base size.
     * 
     * @return a {@link TransformableTexture} with this image drawn onto it.
     */
    public TransformableTexture getTransformableTexture()
    {
        return ( getScaledTransformableTexture( getBaseWidth(), getBaseHeight(), false ) );
    }
    
    public ImageTemplate( BufferedImage bufferedImage )
    {
        this.bufferedImage = bufferedImage;
    }
}
