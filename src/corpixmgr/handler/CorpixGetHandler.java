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

import calliope.core.handler.GetHandler;
import calliope.core.Utils;
import calliope.core.exception.ImageException;
import calliope.core.image.MimeType;
import corpixmgr.constants.Service;
import java.io.File;
import corpixmgr.exception.*;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Get a file in the misc collection. No versions, but maybe links.
 * @author desmond
 */
public class CorpixGetHandler extends GetHandler
{
    protected String docid;
    /**
     * Given a file name derive its mimetype
     * @param f the file
     * @return the mimetype as a string
     */
    protected static String mimeTypeFromFile( File f ) throws ImageException
    {
        try
        {
            Path p = f.toPath();
            String name;
            if ( Files.isSymbolicLink(p) )
            {
                Path target = Files.readSymbolicLink(p);
                name = target.getFileName().toString();
            }
            else
                name = f.getName();
            return MimeType.getContentType( name );
        }
        catch ( Exception e )
        {
            throw new ImageException( e );
        }
    }
    /**
     * Strip out escaped slashes and replace with ordinary slashes
     * @param str the str to strip
     * @return the stripped string
     */
    protected String strip( String str )
    {
        return str.replaceAll("\\\\/", "/");
    }
    /**
     * Get a miscellaneous paratextual document, image (binary) or text
     * @param request the servlet request
     * @param response the servlet response
     * @param urn the docID, stripped of its prefix
     * @throws MiscException 
     */
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws CorpixException
    {
        try
        {
            String first = Utils.first(urn);
            urn = Utils.pop(urn);
            if ( first.equals(Service.LIST) )
                new CorpixListHandler().handle(request,response,urn);
            else if (first.equals(Service.THUMBNAIL) )
                new CorpixThumbnailHandler().handle(request,response,urn);
            else if (first.equals(Service.METADATA) )
                new CorpixGetMetadata().handle(request,response,urn);
            else
                throw new Exception("Unknown service "+first);
        }
        catch ( Exception ioe )
        {
            throw new CorpixException( ioe );
        }
    }
}