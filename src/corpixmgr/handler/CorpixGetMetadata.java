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
import corpixmgr.constants.Params;
import corpixmgr.exception.CorpixException;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.*;

/**
 * Get basic metadata about an image
 * @author desmond
 */
public class CorpixGetMetadata extends CorpixGetHandler
{
    String url;
    ImageType it;
    int wd,ht;
    double compressionRatio;
    long fileLen;
    String actualImageType( int type )
    {
        switch ( type )
        {
            case BufferedImage.TYPE_INT_RGB:
                return "8-bit RGB color as ints";
            case BufferedImage.TYPE_INT_ARGB_PRE:
            case BufferedImage.TYPE_INT_ARGB:
                return "8-bit RGBA 3 bytes colour 1 byte alpha as ints";
            case BufferedImage.TYPE_INT_BGR:
                return "8-bit RGB color as ints";
            case BufferedImage.TYPE_3BYTE_BGR:
                return "3-byte packed RGB";
            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                return "4-byte lacked RGBA";
            case BufferedImage.TYPE_BYTE_INDEXED:
                return " indexed byte image";
            case BufferedImage.TYPE_BYTE_GRAY:
                return "unsigned byte grayscale image, non-indexed.";
            case BufferedImage.TYPE_USHORT_GRAY:
                return "unsigned short grayscale image, non-indexed";
            case BufferedImage.TYPE_BYTE_BINARY:
                return "packed 1, 2, or 4 bit";
            default:
                return "";
        }
    }
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws CorpixException
    {
        try
        {
            BufferedImage img = null;
            docid = request.getParameter(Params.DOCID);
            url = request.getParameter(Params.URL);
            if ( docid != null && url != null )
            {
                String path = CorpixWebApp.webRoot+"/corpix/"+docid+"/"+url;
                File f = new File(path);
                if ( f.exists() )
                {
                    img = ImageIO.read(f);
                    ht = img.getHeight();
                    wd = img.getWidth();
                    int type = img.getType();
                    int bpp=24;
                    switch ( type )
                    {
                        case BufferedImage.TYPE_INT_RGB:
                        case BufferedImage.TYPE_INT_ARGB:
                        case BufferedImage.TYPE_INT_ARGB_PRE:
                        case BufferedImage.TYPE_INT_BGR:
                        case BufferedImage.TYPE_3BYTE_BGR:
                        case BufferedImage.TYPE_4BYTE_ABGR:
                        case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                            bpp = 24;
                            it = ImageType.colour;
                            break;
                        case BufferedImage.TYPE_BYTE_INDEXED:
                            bpp = 8;
                            it = ImageType.colour;
                            break;
                        case BufferedImage.TYPE_BYTE_GRAY:
                            it = ImageType.greyscale;
                            bpp = 8;
                            break;
                        case BufferedImage.TYPE_USHORT_GRAY:
                            it = ImageType.greyscale;
                            bpp = 16;
                            break;
                        case BufferedImage.TYPE_BYTE_BINARY:
                            it = ImageType.binary;
                            bpp = 1;
                            break;
                    }
                    fileLen = f.length();
                    long imageSize = (bpp*wd*ht)/8;
                    compressionRatio = (double)fileLen/(double)imageSize;
                    long rounded = Math.round((1.0-compressionRatio)*10000.0);
                    compressionRatio = (double)rounded/100.0;
                }
                JSONObject jObj = new JSONObject();
                jObj.put("width",wd);
                jObj.put("height",ht);
                jObj.put("compression", compressionRatio);
                jObj.put("size", fileLen);
                jObj.put("type",it.toString());
                if ( img != null )
                    jObj.put("actualType", actualImageType(img.getType()));
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().println(jObj.toJSONString()); 
            }
            else
                throw new Exception("Missing docid or url parameter");
        }
        catch ( Exception e )
        {
            throw new CorpixException(e);
        }
    }      
}
