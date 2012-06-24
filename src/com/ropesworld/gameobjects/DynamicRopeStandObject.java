package com.ropesworld.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class DynamicRopeStandObject extends GenericUseObject {

	public float radius;

	public DynamicRopeStandObject(Piece pieceInfo, Body body, World world, float radius) {
		super(pieceInfo, body);
		this.radius = radius;
		// create a sensor circle indicating the radius
		BodyDef def = new BodyDef();
		FixtureDef fd = new FixtureDef();
		def.position.set(body.getPosition());
		def.type = BodyType.StaticBody;
		Body bod = world.createBody(def);
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		fd.shape = shape;
		fd.isSensor = true;
		bod.createFixture(fd);
	}
	
	public boolean checkPoint(Vector2 point) {
		return point.dst2(body.getPosition()) <= radius*radius;
	}
}
