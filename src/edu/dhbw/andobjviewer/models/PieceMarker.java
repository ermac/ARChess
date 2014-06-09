package edu.dhbw.andobjviewer.models;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.util.GraphicsUtil;
import edu.dhbw.andobjviewer.graphics.Model3D;

public class PieceMarker extends Model3D {

	float boxf[] = {
	-1.0f, -1.0f, 0.0f, -1.0f};
	
	float normalsf[] = {
			// FRONT
			0.0f, 0.0f, 1.0f, 0.0f
	};

	float colorsf[] = { 0, 0, 1, 1	};

	private FloatBuffer box = GraphicsUtil.makeFloatBuffer(boxf);
	private FloatBuffer normals = GraphicsUtil.makeFloatBuffer(normalsf);
	private FloatBuffer colors = GraphicsUtil.makeFloatBuffer(colorsf);

	public PieceMarker() {
		super("piecemarker", "piecemarker.patt");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(GL10 arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void draw(GL10 gl) {
		// TODO Auto-generated method stub
		super.draw(gl);

		gl.glDisable(GL10.GL_TEXTURE_2D);

		// desenha selecionador
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);

		gl.glTranslatef(0f, 0f, 2f);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, box);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisable(GL10.GL_COLOR_MATERIAL);
	}

}
