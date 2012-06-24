package com.ropesworld.gameobjects;

import com.badlogic.gdx.physics.box2d.Body;

public class GameObject {
	public Piece pieceInfo;
	public Body body;
	
	public GameObject (Piece pieceInfo, Body body) {
		this.pieceInfo = pieceInfo;
		this.body = body;
	}
}
