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
package edu.dhbw.andobjviewer.graphics;

import java.io.Serializable;
import java.io.Writer;
import android.util.Log;
import edu.dhbw.andar.ARObject;

/**
 * represents a 3d model.
 * 
 * @author tobi
 * 
 */
public abstract class Model3D extends ARObject implements Serializable {

	// position/rotation/scale
	public float xrot = 90;
	public float yrot = 0;
	public float zrot = 0;
	public float xpos = 0;
	public float ypos = 0;
	public float zpos = 0;
	public float scale = 10f;
	public String name;
	
	public static final int STATE_DYNAMIC = 0;
    public int STATE = STATE_DYNAMIC;
    public static final int STATE_FINALIZED = 1;
	
	public Model3D(String name, String patternName) {
		super(name, patternName, 80.0, new double[] { 0, 0 });
		this.name = name;
	}


	public void setScale(float f) {
		this.scale += f;
		if (this.scale < 0.0001f)
			this.scale = 0.0001f;
	}

	public void setXrot(float dY) {
		this.xrot += dY;
	}

	public void setYrot(float dX) {
		this.yrot += dX;
	}

	public void setXpos(float f) {
		this.xpos += f;
	}

	public void setYpos(float f) {
		this.ypos += f;
	}	

	protected Writer log = new LogWriter();

	/**
	 * write stuff to Android log
	 * 
	 * @author Tobias Domhan
	 * 
	 */
	class LogWriter extends Writer {

		@Override
		public void close() {
			flushBuilder();
		}

		@Override
		public void flush() {
			flushBuilder();
		}

		@Override
		public void write(char[] buf, int offset, int count) {
			for (int i = 0; i < count; i++) {
				char c = buf[offset + i];
				if (c == '\n') {
					flushBuilder();
				} else {
					mBuilder.append(c);
				}
			}
		}

		private void flushBuilder() {
			if (mBuilder.length() > 0) {
				Log.e("OpenGLCam", mBuilder.toString());
				mBuilder.delete(0, mBuilder.length());
			}
		}

		private StringBuilder mBuilder = new StringBuilder();

	}


}
