package com.ropesworld.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;

public class GenericUseObject extends GameObject {

	public boolean used;

	public GenericUseObject(Piece pieceInfo, Body body) {
		super(pieceInfo, body);
	}
}
