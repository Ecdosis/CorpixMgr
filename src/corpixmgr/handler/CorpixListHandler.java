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

import corpixmgr.exception.CorpixException;
import corpixmgr.constants.Params;
import corpixmgr.CorpixWebApp;
import java.io.File;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import calliope.core.constants.JSONKeys;
import java.util.Arrays;

/**
 * Retrieve a list of files and directories for the current project/subpath
 * @author desmond
 */
public class CorpixListHandler extends CorpixGetHandler 
{
    String subpath;
    public void handle( HttpServletRequest request, 
        HttpServletResponse response, String urn ) throws CorpixException
    {
        try
        {
            docid = request.getParameter(Params.DOCID);
            subpath = request.getParameter(Params.SUBPATH);
            if ( subpath == null )
                subpath = "/";
            String path = CorpixWebApp.webRoot+"/corpix/"+docid+subpath;
            File dir = new File(path);
            if ( dir.exists() )
            {
                File[] files = dir.listFiles();
                JSONArray jArr = new JSONArray();
                if ( !subpath.equals("/") )
                {
                    JSONObject parent = new JSONObject();
                    parent.put("isdir", true);
                    parent.put(JSONKeys.NAME, "..");
                    jArr.add(parent);
                }
                Arrays.sort(files,new FileComparator());
                for ( int i=0;i<files.length;i++ )
                {
                    JSONObject jObj = new JSONObject();
                    jObj.put(JSONKeys.DOCID, docid+subpath);
                    jObj.put( JSONKeys.NAME,files[i].getName());
                    if ( files[i].isDirectory())
                        jObj.put("isdir", true);
                    jArr.add(jObj);
                }
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getOutputStream().println(strip(jArr.toJSONString())); 
            }
            else
                throw new FileNotFoundException("dir "+dir.getPath()+" not foud");
        }
        catch ( Exception e )
        {
            throw new CorpixException(e);
        }
    }
}
