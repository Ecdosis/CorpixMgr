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
 *  along with Corpix. If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Desmond Schmidt 2016
 */

package corpixmgr.handler;

import corpixmgr.constants.Params;
import corpixmgr.exception.CorpixException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import corpixmgr.CorpixWebApp;
import java.io.File;
import org.json.simple.JSONObject;

/**
 * Try to delete an image
 * @author desmond
 */
public class CorpixDeleteHandler extends CorpixPostHandler
{
    String url;
    protected void processField( String fieldName, String contents )
    {
        if ( fieldName.equals(Params.DOCID) )
            docid = contents;
        else if ( fieldName.equals(Params.URL) )
            url = contents;
    }
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws CorpixException
    {
        try
        {
            parseImportParams( request );
            JSONObject jObj = new JSONObject();
            String path = CorpixWebApp.webRoot+"/corpix/"+docid+url;
            File file = new File(path);
            if ( file.exists() )
            {
                if ( file.canWrite() )
                {
                    file.delete();
                    jObj.put("success",true);
                    jObj.put("message","Deleted "+url);
                }
                else
                {
                    jObj.put("message","Can't delete "+url);
                    jObj.put("success",false);
                }
            }
            else
            {
                jObj.put("message",url+" doesn't exist");
                jObj.put("success",false);
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(jObj.toJSONString());
        }
        catch ( Exception e )
        {
            throw new CorpixException(e);
        }
    }
}
