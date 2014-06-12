package edu.dhbw.andobjviewer.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Loader.ForceLoadContentObserver;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.GLDebugHelper;
import android.opengl.GLUtils;
import android.os.Debug;
import android.util.Log;

import edu.dhbw.andar.util.GraphicsUtil;
import edu.dhbw.andobjviewer.Config;
import edu.dhbw.andobjviewer.graphics.Model3D;
import edu.dhbw.andobjviewer.parser.ObjParser;
import edu.dhbw.andobjviewer.parser.ParseException;
import edu.dhbw.andobjviewer.util.AssetsFileUtil;
import edu.dhbw.andobjviewer.util.BaseFileUtil;

public class ChessBoard extends Model3D {

	private PieceMarker pieceMarker = new PieceMarker();
	private Projector projector = new Projector();
	
	
	private TimeSpan dTimeSpan; //start dragging time span
	private boolean isDragging = false;
	private Square dragginFrom;
	private Rect pieceMarkerRect;
	private Rect squareRect; 
	
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
	
	private float[] model = new float[16];
	private float[] topleft = new float[16]; 
	private float[] topright = new float[16]; 
	private float[] bottomright = new float[16];
	private float[] bottomleft = new float[16];
	
	private Point topleftp;
	private Point toprightp;
	private Point bottomrightp;
	private Point bottomleftp;
	
	private Resources resources;
	Point point = new Point();
	private Model3D selectedPiece;
	private float L = 2.0f;
	ArrayList<Square> squares = new ArrayList<Square>();
	Rect c = new Rect(bottomleftp.x, topleftp.y, toprightp.x, bottomrightp.y);

	public ChessBoard(Resources resources) {
		// TODO Auto-generated constructor stub
		super("chessboard", "chessboard.patt");
		this.resources = resources;
	}
	
//	public void reset() {
//		pieces.clear();
//	}

	/*
	 * Adiciona peças na lista de peças, setando posições
	 * E carrega da pasta models/ os objetos adicionados 
	 * na lista conforme seu tipo.
	 */
	public void populate() {
		for (int i = 0; i < 8; i ++){
			for (int j = 0; j < 8; j ++){
				Square s = new Square(new Position(i,j));
				squares.add(s);
			}
		}
		
//		addPiece(new Piece(PieceType.PAWN, "peao5", true, false), new Position(0, 0));
//		addPiece(new Piece(PieceType.PAWN, "peao5", true, false), new Position(0, 7));
//		addPiece(new Piece(PieceType.PAWN, "peao2", true, false), new Position(7, 0));
		addPiece(new Piece(PieceType.PAWN, "peao3", true, false), new Position(7, 7));
//		addPiece(new Piece(PieceType.PAWN, "peao4", true, false), new Position(4, 0));
//		addPiece(new Piece(PieceType.PAWN, "peao5", true, false), new Position(5, 0));
//		addPiece(new Piece(PieceType.PAWN, "peao2", true, false), new Position(6, 0));

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

//	public Piece getPiece(int x, int y) {
//		// retorna a peça do tabuleiro na posição x,y
//		return null;
//	}

	public Model3D getSelectedPiece() {
		return selectedPiece;
	}

	public void addPiece(Piece piece, Position position) {
		squares.get(position.y * 8 + position.x).setPiece(piece);
	}

	public List<Piece> getPieces() {
		List<Piece> list = new ArrayList<Piece>();
		for (Square s : squares){
			if (s.getPiece() != null)
				list.add(s.getPiece());
		}
		return list;
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
	
	float[] toFloatArray(double[] arr) {
		  if (arr == null) return null;
		  int n = arr.length;
		  float[] ret = new float[n];
		  
		  for (int i = 0; i < n; i++) {
			  ret[i] = (float)arr[i];
		  }
		  return ret;
	}

	
	public boolean canMove(GL10 gl){
		GL11 gl11 = (GL11) gl;
		Projector projectorSquare = new Projector();
		projectorSquare.setViewport(gl11); 
<<<<<<< HEAD
		
=======
	  
>>>>>>> baa3877dcf4dc43df654a1b29844815dd7de0b5e
		gl11.glPushMatrix();
		gl11.glTranslatef(-1,-1,0);
		bottomleftp = projectorSquare.getScreenCoords(getTransMatrix(), gl11);
		gl11.glTranslatef(L,0,0);
		bottomrightp = projectorSquare.getScreenCoords(getTransMatrix(), gl11);
		gl11.glTranslatef(0,L,0);
		toprightp = projectorSquare.getScreenCoords(getTransMatrix(), gl11); 
		gl11.glTranslatef(-L,0,0);
		topleftp = projectorSquare.getScreenCoords(getTransMatrix(), gl11);
		gl11.glPopMatrix();

		Rect a = new Rect(bottomleftp.x, topleftp.y, toprightp.x, bottomrightp.y);
		Rect b = new Rect(getPieceMarker().pointPM.x, getPieceMarker().pointPM.y,
				getPieceMarker().pointPM.x, getPieceMarker().pointPM.y);
		
		return a.contains(b);
	}
	
	public void movePiece(Square origin, Square destination){
		destination.setPiece(origin.getPiece());
		origin.setPiece(null);
	}

	
	/*
	 * (non-Javadoc)
	 * @see edu.dhbw.andar.ARObject#draw(javax.microedition.khronos.opengles.GL10)
	 * Desenha tabuleiro e desenha peças.
	 */
	public final void draw(GL10 gl) {

		super.draw(gl);
		projector.setViewport(gl);
		
		gl.glTranslatef(150.0f, 0.0f, 0.0f);
		gl.glDisable(GL10.GL_TEXTURE_2D);
				
		/*Aumenta a escala do tabuleiro*/
		gl.glScalef(25f, 25f, 25f);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		gl.glPushMatrix();
		for (int i = 0; i < 8; i++) {
			boolean even = i % 2 == 0;
			for (int j = 0; j < 8; j++) {
				Square square = squares.get(i * 8 + j);
				if (square.hasPiece()) {
				   square.getPiece().draw(gl);
				}
				gl.glEnable(GL10.GL_COLOR_MATERIAL);
				if (even)
					gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
				else
					gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				
				if (canMove(gl)) {
					//movePiece(square, squares.get(0));
					gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
				}	
								
				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, box);
				gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
<<<<<<< HEAD
				Rect a = new Rect(bottomleftp.x, topleftp.y, toprightp.x, bottomrightp.y);
				Rect b = new Rect(getPieceMarker().pointPM.x,getPieceMarker().pointPM.y,
						getPieceMarker().pointPM.x,getPieceMarker().pointPM.y);
				
				int k = 90000;
				if (Rect.intersects(a, b) && piece != null) {
					while (k > 1){ 
						if (Rect.intersects(a, b)){
							movePiece(squares.get(i * 8 + j), squares.get(0));
						}
						k--;
					}
				}	
				
				long startTime = System.currentTimeMillis();
				long elapsedTime = 0L;
				while (elapsedTime < 2 * 60 * 10) {
					// perform db poll/check
					elapsedTime = (new Date()).getTime() - startTime;
				}
				
=======

>>>>>>> baa3877dcf4dc43df654a1b29844815dd7de0b5e
				gl.glTranslatef(L, 0.0f, 0.0f);
				even = !even;
			}
			gl.glTranslatef(0.0f, L, 0.0f);
			gl.glTranslatef(-L * 8, 0.0f, 0.0f);
		}
		gl.glPopMatrix();
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
		
	}

	public PieceMarker getPieceMarker() {
		return pieceMarker;
	}

	public void setPieceMarker(PieceMarker pieceMarker) {
		this.pieceMarker = pieceMarker;
	}
}
