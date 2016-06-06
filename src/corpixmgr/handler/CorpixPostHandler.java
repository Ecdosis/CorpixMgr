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

import calliope.core.Utils;
import calliope.core.handler.Handler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import calliope.core.constants.Formats;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import corpixmgr.exception.CorpixException;
import corpixmgr.constants.Params;
import corpixmgr.constants.Service;
import org.json.simple.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Handle posting or saving of project data
 * @author desmond
 */
public class CorpixPostHandler extends Handler
{
    String docid;
    String encoding;
    String format;
    String fileName;
    String title;
    String delete;
    byte[] fileData;
    JSONObject userdata;
    
    public CorpixPostHandler()
    {
        this.encoding = "UTF-8";
        this.format = Formats.MIME_MARKDOWN;
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
     * Process a field we recognise
     * @param fieldName the field's name
     * @param contents its contents
     */
    protected void processField( String fieldName, String contents )
    {
    }
    /**
     * Parse the import params from the request
     * @param request the http request
     */
    protected void parseImportParams( HttpServletRequest request ) throws CorpixException
    {
        try
        {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            //System.out.println("Parsing import params");
            if ( isMultipart )
            {
                FileItemFactory factory = new DiskFileItemFactory();
                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);
                // Parse the request
                List items = upload.parseRequest( request );
                for ( int i=0;i<items.size();i++ )
                {
                    FileItem item = (FileItem) items.get( i );
                    if ( item.isFormField() )
                    {
                        String fieldName = item.getFieldName();
                        if ( fieldName != null )
                        {
                            String contents = item.getString("UTF-8");
                            processField(fieldName,contents);
                        }
                    }
                    else if ( item.getName().length()>0 )
                    {
                        fileName = item.getName();
                        InputStream is = item.getInputStream();
                        ByteArrayOutputStream bh = new ByteArrayOutputStream();
                        while ( is.available()>0 )
                        {
                            byte[] b = new byte[is.available()];
                            is.read( b );
                            bh.write( b );
                        }
                        fileData = bh.toByteArray();
                    }
                }
            }
            else
            {
                Map tbl = request.getParameterMap();
                Set<String> keys = tbl.keySet();
                Iterator<String> iter = keys.iterator();
                while ( iter.hasNext() )
                {
                    String key = iter.next();
                    String[] values = (String[])tbl.get(key);
                    for ( int i=0;i<values.length;i++ )
                        processField( key, values[i]);
                }
            }
        }
        catch ( Exception e )
        {
            throw new CorpixException( e );
        }
    }
    /**
     * Handle a POST request
     * @param request the raw request
     * @param response the response we will write to
     * @param urn the rest of the URL after stripping off the context
     * @throws ProjectException 
     */
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws CorpixException
    {
        try
        {
            String first = Utils.first(urn);
            urn = Utils.pop(urn);
            if ( first.equals(Service.DELETE) )
                new CorpixDeleteHandler().handle(request, response, urn);
            else if ( first.equals(Service.ADD) )
                new CorpixAddHandler().handle(request, response, urn);
            else
                throw new Exception("Unrecognised service "+first);
        }
        catch ( Exception e )
        {
            try {
                response.getWriter().println( "Status: 500; Exception "+e.getMessage());
            } 
            catch (Exception ex )
            {}
            System.out.println(e.getMessage() );
            throw new CorpixException(e);
        }
    }
}
