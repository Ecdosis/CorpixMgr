/*
 * This file is part of Corpix.
 *
 *  Corpix is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Corpix is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Corpix.  If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Desmond Schmidt 2016
 */
package corpixmgr.handler;

import corpixmgr.CorpixWebApp;
import corpixmgr.exception.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import corpixmgr.constants.Params;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.OutputStream;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

/**
 * Generate thumbnails for a given image
 * @author desmond
 */
public class CorpixThumbnailHandler extends CorpixGetHandler
{
    int maxWidth;
    int maxHeight;
    String url;
    String docid;
    /**
     * Reduce an image to the specified size 
     * @param before the image to be transformed
     * @param afterWidth the width after transformation
     * @param afterHeight its height after transformation
     * @return the scaled image
     */
    BufferedImage scaleImage( BufferedImage before, int afterWidth, int afterHeight )
    {
        int w = before.getWidth();
        int h = before.getHeight();
        BufferedImage after = new BufferedImage(afterWidth, afterHeight, before.getType());
        AffineTransform at = new AffineTransform();
        double ratio = (double)afterHeight/(double)h;
        at.scale(ratio, ratio);
        AffineTransformOp scaleOp = 
        new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return scaleOp.filter(before, after);
    }
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws CorpixException
    {
        try
        {
            docid = request.getParameter(Params.DOCID);
            url = request.getParameter(Params.URL);
            String maxWidthStr = request.getParameter(Params.MAXWIDTH);
            if ( maxWidthStr != null )
                maxWidth = Integer.parseInt(maxWidthStr);
            else
                maxWidth = 200;
            String maxHeightStr = request.getParameter(Params.MAXHEIGHT);
            if ( maxHeightStr != null )
                maxHeight = Integer.parseInt(maxHeightStr);
            else
                maxHeight = 200;
            String path = CorpixWebApp.webRoot+"/corpix/"+docid+"/"+url;
            File f = new File(path);
            if ( f.exists() )
            {
                BufferedImage img = ImageIO.read(new File(path));
                int height = img.getHeight();
                int width = img.getWidth();
                if ( width > maxWidth )
                {
                    height = (height*maxWidth)/width;
                    width = maxWidth;
                }
                if ( height > maxHeight )
                {
                    width = (width*maxHeight)/height;
                    height = maxHeight;
                }
//                FileWriter fw = new FileWriter("/tmp/corpix.log");
//                fw.write("width="+width+" maxWidth="+maxWidth+" height="+height+" maxHeight="+maxHeight);
//                fw.close();
                BufferedImage scaled = scaleImage(img, width, height );
                response.setContentType(mimeTypeFromFile(f));
                OutputStream sos = response.getOutputStream();
                boolean res = ImageIO.write(scaled, "jpg", sos);
                if ( !res )
                    System.out.println("Couldn't write "+path );
                sos.close();
            }
            else
                throw new FileNotFoundException("no file "+url);
        }
        catch ( Exception e )
        {
            throw new CorpixException(e);
        }
    }
}
