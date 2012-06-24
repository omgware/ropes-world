package com.ropesworld.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;

public class StarObject extends GameObject {

	public boolean owned;

	public StarObject(Piece pieceInfo, Body body) {
		super(pieceInfo, body);
	}
}
