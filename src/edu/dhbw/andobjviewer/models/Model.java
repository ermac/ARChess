/**
	Copyright (C) 2010  Tobias Domhan

    This file is part of AndObjViewer.

    AndObjViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AndObjViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AndObjViewer.  If not, see <http://www.gnu.org/licenses/>.
 
 */
package edu.dhbw.andobjviewer.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import edu.dhbw.andobjviewer.util.BaseFileUtil;

public interface Model extends Serializable{
	

    public static final int STATE_DYNAMIC = 0;
    public int STATE = STATE_DYNAMIC;
    public static final int STATE_FINALIZED = 1;
	

	public void addMaterial(Material mat);
	public Material getMaterial(String name);
	public void addGroup(Group grp);
	public Vector<Group> getGroups();
	public void setFileUtil(BaseFileUtil fileUtil);
	public HashMap<String, Material> getMaterials();

	
	/**
	 * convert all dynamic arrays to final non alterable ones.
	 */
	public void finalize();	
}
