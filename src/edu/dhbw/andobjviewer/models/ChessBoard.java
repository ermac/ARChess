package edu.dhbw.andobjviewer.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Point;
import android.opengl.GLDebugHelper;
import android.opengl.GLUtils;
import android.os.Debug;

import edu.dhbw.andar.util.GraphicsUtil;
import edu.dhbw.andobjviewer.Config;
import edu.dhbw.andobjviewer.graphics.Model3D;
import edu.dhbw.andobjviewer.parser.ObjParser;
import edu.dhbw.andobjviewer.parser.ParseException;
import edu.dhbw.andobjviewer.util.AssetsFileUtil;
import edu.dhbw.andobjviewer.util.BaseFileUtil;

public class ChessBoard extends Model3D {

	private List<Piece> pieces = new ArrayList<Piece>();
	private PieceMarker pieceMarker = new PieceMarker();
	Projector projector = new Projector();
	
	float boxf[] = {
			
			-1.0f, -1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f, 
			1.0f, -1.0f, 0.0f, 
			1.0f, 1.0f, 0.0f,

			-1.0f, -1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f, 
			1.0f, -1.0f, 0.0f, 
			1.0f, 1.0f, 0.0f,

	};
	float normalsf[] = { 
			// FRONT
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			// BACK
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			
	};

	float colorsf[] = { 
			1, 1, 1, 1, 
			1, 1, 1, 1,
			1, 1, 1, 1,
			1, 1, 1, 1,
			          
			0, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, 0, 0,
			0, 0, 0, 0,
                       
			
			};

	private FloatBuffer box = GraphicsUtil.makeFloatBuffer(boxf);
	private FloatBuffer normals = GraphicsUtil.makeFloatBuffer(normalsf);
	private FloatBuffer colors = GraphicsUtil.makeFloatBuffer(colorsf);
	
	float mat_ambientf[] = {0.2f, 0.2f, 0.2f, 1.0f};
	float mat_flashf[] = { 0f, 0f, 0f, 1.0f };
	float mat_diffusef[] = {0.8f, 0.8f, 0.8f, 1.0f};
	float mat_flash_shinyf[] = { 50.0f };	
		
	private FloatBuffer mat_ambient = GraphicsUtil.makeFloatBuffer(mat_ambientf);
	private FloatBuffer mat_flash = GraphicsUtil.makeFloatBuffer(mat_flashf);
	private FloatBuffer mat_flash_shiny = GraphicsUtil.makeFloatBuffer(mat_flash_shinyf);
	private FloatBuffer mat_diffuse = GraphicsUtil.makeFloatBuffer(mat_diffusef);
	
	private float[] ambientlight1 = {0.2f, 0.2f, 0.2f, 1.0f};
	private float[] diffuselight1 = {0.8f, 0.8f, 0.8f, 1.0f};
	private float[] specularlight1 = {0f, 0f, 0f, 1f};
	private float[] lightposition1 = {0f, 0f, 20.0f,1f};
	
	
	private FloatBuffer lightPositionBuffer1 =  GraphicsUtil.makeFloatBuffer(lightposition1);
	private FloatBuffer specularLightBuffer1 = GraphicsUtil.makeFloatBuffer(specularlight1);
	private FloatBuffer diffuseLightBuffer1 = GraphicsUtil.makeFloatBuffer(diffuselight1);
	private FloatBuffer ambientLightBuffer1 = GraphicsUtil.makeFloatBuffer(ambientlight1);	
	
	private Resources resources;
	Point xy = new Point();
	private Model3D selectedPiece;

	public ChessBoard(Resources resources) {
		// TODO Auto-generated constructor stub
		super("chessboard", "chessboard.patt");
		this.resources = resources;
	}
	
	public void reset() {
		pieces.clear();
	}

	/*
	 * Adiciona peças na lista de peças, setando posições
	 * E carrega da pasta models/ os objetos adicionados 
	 * na lista conforme seu tipo.
	 */
	public void populate() {
		addPiece(new Piece(PieceType.PAWN, "peao5", new Position(0, 0), true,
				false));
//		addPiece(new Piece(PieceType.PAWN, "peao2", new Position(-5, 0), true,
//				false));
//		addPiece(new Piece(PieceType.PAWN, "peao3", new Position(-10, 0), true,
//				false));
//		addPiece(new Piece(PieceType.PAWN, "peao4", new Position(-15, 0), true,
//				false));
//		addPiece(new Piece(PieceType.PAWN, "peao5", new Position(-20, 0), true,
//				false));
//		addPiece(new Piece(PieceType.PAWN, "peao2", new Position(-25, 0), true,
//				false));
//		addPiece(new Piece(PieceType.PAWN, "peao3", new Position(-30, 0), true,
//				false));
//		addPiece(new Piece(PieceType.PAWN, "peao4", new Position(-35, 0), true,
//				false));

		BaseFileUtil fileUtil = null;
		fileUtil = new AssetsFileUtil(resources.getAssets());
		fileUtil.setBaseFolder("models/");
		for (Piece piece : getPieces()) {
			String modelFileName = piece.getModelFilename();
			// read the model file:
			ObjParser parser = new ObjParser(fileUtil);
			try {
				if (Config.DEBUG)
					Debug.startMethodTracing("ARChess");
				if (fileUtil != null) {
					BufferedReader fileReader = fileUtil
							.getReaderFromName(modelFileName);
					if (fileReader != null) {
						Model model = parser.parse(piece, fileReader);
						model.finalize();
					}
				}
				if (Config.DEBUG)
					Debug.stopMethodTracing();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public Piece getPiece(int x, int y) {
		// retorna a peça do tabuleiro na posição x,y
		return null;
	}

	public Model3D getSelectedPiece() {
		return selectedPiece;

	}

	public void addPiece(Piece piece) {
		pieces.add(piece);
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.dhbw.andar.ARObject#init(javax.microedition.khronos.opengles.GL10)
	 *  
	 */
	@Override
	public void init(GL10 gl) {
	
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambientLightBuffer1);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseLightBuffer1);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, specularLightBuffer1);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPositionBuffer1);
		gl.glEnable(GL10.GL_LIGHT1);

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);

		for (Piece piece : getPieces()) {
			int[] tmpTextureID = new int[1];
			for (Material material : piece.getMaterials().values()) {
				if (material.hasTexture()) {
					// load texture
					gl.glGenTextures(1, tmpTextureID, 0);
					gl.glBindTexture(GL10.GL_TEXTURE_2D, tmpTextureID[0]);
					piece.textureIDs.put(material, tmpTextureID[0]);
					GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0,
							material.getTexture(), 0);
					material.getTexture().recycle();
					gl.glTexParameterx(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
					gl.glTexParameterx(GL10.GL_TEXTURE_2D,
							GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
				}
			}

		}

		// transfer vertices to video memory
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.dhbw.andar.ARObject#draw(javax.microedition.khronos.opengles.GL10)
	 * Desenha tabuleiro e desenha peças.
	 */
	public final void draw(GL10 gl) {

		super.draw(gl);		
		
		gl.glTranslatef(150.0f, 0.0f, 0.0f);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glPushMatrix();
		
		// desenha tabuleiro
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnable(GL10.GL_COLOR_MATERIAL);

		/*Aumenta a escala do tabuleiro*/
		gl.glScalef(25f, 25f, 0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, box);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glTranslatef(2.0f, 0.0f, 0.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glPopMatrix();
		
		projector.setViewport(gl);
		xy = projector.getScreenCoords(getTransMatrix(), gl);
		
		/*desenha as peças*/
		gl.glPushMatrix();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisable(GL10.GL_COLOR_MATERIAL);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mat_flash);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mat_ambient);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mat_diffuse);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, mat_flash_shiny);
		
		for (Piece piece : getPieces()) {
			gl.glScalef(scale, scale, scale);
			gl.glTranslatef(piece.getPosition().getX() + 1, piece.getPosition().getY(), zpos);
			gl.glRotatef(xrot, 1, 0, 0);
			gl.glRotatef(yrot, 0, 1, 0);
			gl.glRotatef(zrot, 0, 0, 1);
			
			if (getPieceMarker().xy.equals(xy)){
				gl.glTranslatef(piece.getPosition().getX() + 100, piece.getPosition().getX() + 100, zpos);
			}
			
			// first draw non textured groups
			gl.glDisable(GL10.GL_TEXTURE_2D);
			int cnt = piece.nonTexturedGroups.length;
			for (int i = 0; i < cnt; i++) {
				Group group = piece.nonTexturedGroups[i];
				Material mat = group.getMaterial();
				if (mat != null) {
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
							mat_flash);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
							mat_ambient);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
							mat_diffuse);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS,
							mat_flash_shiny);
				}
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, group.vertices);
				gl.glNormalPointer(GL10.GL_FLOAT, 0, group.normals);
				gl.glDrawArrays(GL10.GL_TRIANGLES, 0, group.vertexCount);
			}
			
			/*Caso haja Matirials*/
			// now we can continue with textured ones
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			cnt = piece.texturedGroups.length;
			for (int i = 0; i < cnt; i++) {
				Group group = piece.texturedGroups[i];
				Material mat = group.getMaterial();
				if (mat != null) {
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
							mat.specularlight);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
							mat.ambientlight);
					gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
							mat.diffuselight);
					gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS,
							mat.shininess);
					if (mat.hasTexture()) {
						gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0,
								group.texcoords);
						gl.glBindTexture(GL10.GL_TEXTURE_2D, piece.textureIDs
								.get(mat).intValue());
					}
				}
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, group.vertices);
				gl.glNormalPointer(GL10.GL_FLOAT, 0, group.normals);
				gl.glDrawArrays(GL10.GL_TRIANGLES, 0, group.vertexCount);
			}	
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glPopMatrix();
		
		
	}

	public PieceMarker getPieceMarker() {
		return pieceMarker;
	}

	public void setPieceMarker(PieceMarker pieceMarker) {
		this.pieceMarker = pieceMarker;
	}
}
