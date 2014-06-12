package edu.dhbw.andobjviewer.models;


public class Square{
	private Position position = new Position(0, 0);
	private Piece piece = null;
	
	public Square(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	
	
	
}
