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
import java.io.FileOutputStream;
import org.json.simple.JSONObject;

/**
 * Try to delete an image
 * @author desmond
 */
public class CorpixAddHandler extends CorpixPostHandler
{
    String subPath;
    protected void processField( String fieldName, String contents )
    {
        System.out.println("Parsing filed "+fieldName);
        if ( fieldName.equals(Params.DOCID) )
            docid = contents;
        else if ( fieldName.equals(Params.SUBPATH) )
            subPath = contents;
    }
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws CorpixException
    {
        try
        {
            parseImportParams( request );
            JSONObject jObj = new JSONObject();
            String path = CorpixWebApp.webRoot+"/corpix/"+docid+subPath+"/"+fileName;
            File file = new File(path);
            if ( file.createNewFile() )
            {
                if ( file.canWrite() )
                {
                    if ( fileData != null )
                    {
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(fileData);
                        fos.close();
                        jObj.put("success",true);
                        jObj.put("message","Wrote "+file.length()+" bytes to "+path);
                    }
                    else
                    {
                        jObj.put("success",false);
                        jObj.put("message", "No file uploaded, none written:"+path);
                    }
                }
                else
                {
                    jObj.put("message","Couldn't write "+path);
                    jObj.put("success",false);
                }
            }
            else
            {
                jObj.put("message","Couldn't create "+path);
                jObj.put("success",false);
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(strip(jObj.toJSONString()));
        }
        catch ( Exception e )
        {
            throw new CorpixException(e);
        }
    }
}
