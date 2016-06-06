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
import java.util.Comparator;
import java.io.File;
/**
 * Comparator for sorting lists of files
 * @author desmond
 */
public class FileComparator implements Comparator<File>
{
    public int compare( File o1, File o2)
    {
        String name1 = o1.getName();
        String name2 = o2.getName();
        return name1.compareTo(name2);
    }
    public boolean equals(Object obj)
    {
        return obj instanceof FileComparator;
    }
}
