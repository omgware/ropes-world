package com.ropesworld.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;

public class MainCharObject extends GameObject {
	
	public Joint joint1;
	public Joint joint2;
	public ArrayList<Rope> ropes = new ArrayList<Rope>();
	public GenericUseObject gravityBall;

	public MainCharObject(Piece pieceInfo, Body body) {
		super(pieceInfo, body);
	}
	
	public void disposeRopes() {
		for (Rope r: ropes)
			r.dispose();
	}
}
