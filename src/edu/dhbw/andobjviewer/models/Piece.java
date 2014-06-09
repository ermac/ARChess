package edu.dhbw.andobjviewer.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

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
	
	public Piece() {
		// TODO Auto-generated constructor stub
	}

	public Piece(PieceType type, String name, Position position,
			boolean isWhite, boolean isSelected) {
		super();
		this.type = type;
		this.name = name;
		this.position = position;
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
