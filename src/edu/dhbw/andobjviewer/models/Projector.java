package edu.dhbw.andobjviewer.models;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.graphics.Point;
import android.opengl.GLU;

public class Projector {
	int[] viewport = new int[4];
	float[] modelview = new float[16];
	float[] projection = new float[16];
	float[] vector = new float[4];

	public void setViewport(GL10 gl) {
		gl.glGetIntegerv(GL11.GL_VIEWPORT, viewport, 0);
	}

	public synchronized Point getScreenCoords(double[] transMat, GL10 gl) {
		GL11 gl11 = (GL11) gl;

		gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelview, 0);
		gl11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection, 0);

		GLU.gluProject((float) transMat[0], (float) transMat[1],(float) transMat[1], modelview, 0, projection, 0, viewport, 0, vector, 0);

		Point p = new Point((int) vector[0], (int) (viewport[3] - vector[1]));
		return p;
	}
}