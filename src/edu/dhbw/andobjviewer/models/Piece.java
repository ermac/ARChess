package edu.dhbw.andobjviewer.models;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.util.GraphicsUtil;
import edu.dhbw.andobjviewer.util.BaseFileUtil;

public class Piece implements Model {
	
	protected Group[] texturedGroups;
	protected Group[] nonTexturedGroups;
	public HashMap<Material, Integer> textureIDs = new HashMap<Material, Integer>();
	private Vector<Group> groups = new Vector<Group>();

	/**
	 * all materials
	 */
	protected HashMap<String, Material> materials = new HashMap<String, Material>();
	public int STATE = STATE_DYNAMIC;
	public static final int STATE_DYNAMIC = 0;
	public static final int STATE_FINALIZED = 1;
	
	
	//tipo da peça de xadrez, se é peão, bispo...
	private PieceType type = PieceType.PAWN;
	//nome do model
	private String name = type.name().toLowerCase();
	
	//the piece postion on chessboard
	private Position position = new Position(0, 0);
	
	private boolean isWhite = true;
	
	private boolean isSelected = false;
	
	float mat_ambientf[] = {0.2f, 0.2f, 0.2f, 1.0f};
	float mat_flashf[] = { 0f, 0f, 0f, 1.0f };
	float mat_diffusef[] = {0.8f, 0.8f, 0.8f, 1.0f};
	float mat_flash_shinyf[] = { 50.0f };	
		
	private FloatBuffer mat_ambient = GraphicsUtil.makeFloatBuffer(mat_ambientf);
	private FloatBuffer mat_flash = GraphicsUtil.makeFloatBuffer(mat_flashf);
	private FloatBuffer mat_flash_shiny = GraphicsUtil.makeFloatBuffer(mat_flash_shinyf);
	private FloatBuffer mat_diffuse = GraphicsUtil.makeFloatBuffer(mat_diffusef);
	
	public Piece() {
		// TODO Auto-generated constructor stub
	}

	public Piece(PieceType type, String name, boolean isWhite, boolean isSelected) {
		super();
		this.type = type;
		this.name = name;
		this.isWhite = isWhite;
		this.isSelected = isSelected;
	}
	
	public void addMaterial(Material mat) {
		// mat.finalize();
		materials.put(mat.getName(), mat);
	}

	public Material getMaterial(String name) {
		return materials.get(name);
	}

	public void addGroup(Group grp) {
		if (STATE == STATE_FINALIZED)
			grp.finalize();
		groups.add(grp);
	}

	public Vector<Group> getGroups() {
		return groups;
	}

	public void setFileUtil(BaseFileUtil fileUtil) {
		for (Iterator iterator = materials.values().iterator(); iterator
				.hasNext();) {
			Material mat = (Material) iterator.next();
			mat.setFileUtil(fileUtil);
		}
	}

	public HashMap<String, Material> getMaterials() {
		return materials;
	}
	
	/**
	 * convert all dynamic arrays to final non alterable ones.
	 */
	public void finalize() {
		if (STATE != STATE_FINALIZED) {
			STATE = STATE_FINALIZED;
			for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
				Group grp = (Group) iterator.next();
				grp.finalize();
				grp.setMaterial(materials.get(grp.getMaterialName()));
			}
			for (Iterator<Material> iterator = materials.values().iterator(); iterator
					.hasNext();) {
				Material mtl = iterator.next();
				mtl.finalize();
			}
		}
		materials.put("default", new Material("default"));
		// separate texture from non textured groups for performance reasons
		Vector<Group> groups = getGroups();
		Vector<Group> texturedGroups = new Vector<Group>();
		Vector<Group> nonTexturedGroups = new Vector<Group>();
		for (Iterator<Group> iterator = groups.iterator(); iterator.hasNext();) {
			Group currGroup = iterator.next();
			if (currGroup.isTextured()) {
				texturedGroups.add(currGroup);
			} else {
				nonTexturedGroups.add(currGroup);
			}
		}
		this.texturedGroups = texturedGroups.toArray(new Group[texturedGroups
				.size()]);
		this.nonTexturedGroups = nonTexturedGroups
				.toArray(new Group[nonTexturedGroups.size()]);
	}
	
	public void draw(GL10 gl) {

		/* desenha as peças */
		gl.glPushMatrix();
		
		gl.glDisable(GL10.GL_COLOR_MATERIAL);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, mat_flash);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, mat_ambient);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, mat_diffuse);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS,
				mat_flash_shiny);
		
		gl.glScalef(0.5f, 0.5f, 0.5f);
		gl.glTranslatef(getPosition().getX() + 2, getPosition().getY(), 2);
		gl.glRotatef(90, 1, 0, 0);
		gl.glRotatef(0, 0, 1, 0);
		gl.glRotatef(0, 0, 0, 1);
		
//		 if (getPieceMarker().xy.equals(xy)){
//		 gl.glTranslatef(getPosition().getX() + 100, getPosition().getX() +
//		 100, zpos);
//		 }

		// first draw non textured groups
		gl.glDisable(GL10.GL_TEXTURE_2D);
		int cnt = nonTexturedGroups.length;
		for (int i = 0; i < cnt; i++) {
			Group group = nonTexturedGroups[i];
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
		
		gl.glPopMatrix();
		
		
	}
	

	public PieceType getType() {
		return type;
	}

	public void setType(PieceType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public boolean isWhite() {
		return isWhite;
	}

	public void setWhite(boolean isWhite) {
		this.isWhite = isWhite;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public String algebraicNotationPosition() {
		//implementar 
		throw new UnsupportedOperationException();
	}

	public String getModelFilename() {
		return type.name().toLowerCase() + ".obj";
	}

	public String getPatternName() {
		if (type == PieceType.PIECEMARKER) {
			return "chessboard.patt";
		}
		return "patt.hiro";
	}

}
