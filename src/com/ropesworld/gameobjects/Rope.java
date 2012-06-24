package com.ropesworld.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

public class Rope {
	
	public Body attachBodyA;
	public Body attachBodyB;
	public ArrayList<Body> bodies = new ArrayList<Body>();
	public World world;
	public boolean isTramp;

	public Rope(Body attachBodyA, Body attachBodyB, World world) {
		this(attachBodyA, attachBodyB, world, 0, false, false);
	}

	public Rope(Body attachBodyA, Body attachBodyB, World world, boolean elastic) {
		this(attachBodyA, attachBodyB, world, 0, elastic, false);
	}

	public Rope(Body attachBodyA, Body attachBodyB, World world, int extraLength, boolean elastic) {
		this(attachBodyA, attachBodyB, world, extraLength, elastic, false);
	}
		
	public Rope(Body attachBodyA, Body attachBodyB, World world, int extraLength, boolean elastic, boolean enableCollision) {
		this.attachBodyA = attachBodyA;
		this.attachBodyB = attachBodyB;
		this.world = world;
		this.isTramp = enableCollision;
		float generationStep = 1.5f;
		RopeJointDef rj = new RopeJointDef();
		rj.localAnchorA.x = -0.5f;
		rj.localAnchorB.x = 0.5f;
		rj.maxLength = 0.3f;
		DistanceJointDef dj = new DistanceJointDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.125f);
		CircleShape shape2 = new CircleShape();
		shape2.setRadius(0.2f);
		FixtureDef fd = new FixtureDef();
		fd.isSensor = !enableCollision;
		if (!elastic)
			fd.shape = shape;
		else {
			fd.shape = shape2;
			generationStep = 0.51f;
			dj.frequencyHz = 10.0f;
			dj.dampingRatio = 1.0f;
		}
		attachBodyA.setAngularDamping(1.0f);
		attachBodyB.setAngularDamping(1.0f);
		Vector2 distanceVect = new Vector2(attachBodyB.getPosition());
		distanceVect.sub(attachBodyA.getPosition());
		distanceVect.nor();
		distanceVect.mul(0.5f);
		Body prevBody = attachBodyA;
		Body tempBody;
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 50.0f;
		Vector2 nextBodyPosition = new Vector2(attachBodyA.getPosition());
		int dist = (int)(attachBodyA.getPosition().dst(attachBodyB.getPosition()));
		if (extraLength > 100)
			dist = extraLength - 100;
		bodies.add(attachBodyA);
		if (extraLength > 0 && extraLength < 100)
			fd.density = 30 /  ((((float)dist)/1.5f) + (100-extraLength));
		fd.density = 30 /  (((float)dist)/1.5f);
		if (fd.density < 5.0f)
			fd.density = 5.0f;
		if (enableCollision)
			fd.density = 10;
		//fd.density = dist;
		for (float i = 0; i < dist; i+=generationStep) {
			if (enableCollision)
				nextBodyPosition.add(distanceVect.x, distanceVect.y);
			else
				nextBodyPosition.add(distanceVect.x*2, distanceVect.y*2);
			bd.angle = (float)(Math.toRadians(distanceVect.angle()));
			bd.position.set(nextBodyPosition);
			tempBody = world.createBody(bd);
			/*fd.density -= 0.8f;
			if (fd.density < 1.0f)
				fd.density = 1.0f;*/
			tempBody.createFixture(fd);
			if (!elastic) {
				rj.bodyA = prevBody;
				rj.bodyB = tempBody;
				world.createJoint(rj);
			}
			else {
				dj.initialize(prevBody, tempBody, prevBody.getPosition(), tempBody.getPosition());
				dj.length = 0;
				world.createJoint(dj);
			}
			bodies.add(tempBody);
			prevBody = tempBody;
		}
		
		if (extraLength > 0 && extraLength < 100) {
			for (int i=0; i<extraLength; i++) {
				nextBodyPosition.add(distanceVect.x*2, distanceVect.y*2);
				bd.angle = (float)(Math.toRadians(distanceVect.angle()));
				bd.position.set(nextBodyPosition);
				tempBody = world.createBody(bd);
				tempBody.createFixture(fd);
				if (!elastic) {
					rj.bodyA = prevBody;
					rj.bodyB = tempBody;
					world.createJoint(rj);
				}
				else {
					dj.initialize(prevBody, tempBody, prevBody.getPosition(), tempBody.getPosition());
					world.createJoint(dj);
				}
				bodies.add(tempBody);
				prevBody = tempBody;
			}
		}
		
		// Last attachment
		if (!elastic) {
			rj.bodyA = prevBody;
			rj.bodyB = attachBodyB;
			world.createJoint(rj);
		}
		else {
			dj.initialize(prevBody, attachBodyB, prevBody.getPosition(), attachBodyB.getPosition());
			world.createJoint(dj);
		}

		shape.dispose();
	}
	
	public boolean checkPoint(Vector2 point) {
		for (Body b : bodies) {
			if (b.getPosition().dst2(point) < 2)
				return true;
		}
		return false;
	}

	public void dispose() {
		for (Body b : bodies) {
			while(!b.getJointList().isEmpty()) {
				 world.destroyJoint(b.getJointList().get(0).joint);	
			}
			b.setGravityScale(3);
		}
	}
}
