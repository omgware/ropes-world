package com.ropesworld.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Spring {
	    public Body body1;
	    public Body body2;
	 
	    public float springConstant = 10000.0f;
	    public float springLength = 1.0f;
	    public float frictionConstant = 5.0f;
	    public float airFrictionConstant = 0.02f;
	    private Vector2 springVector = new Vector2();
	    private float r;
	    private Vector2 force = new Vector2();
	    private Vector2 gravity = new Vector2(0, -9.8f);
	 
	    public Spring(Body body1, Body body2, float springConstant, float springLength, float frictionConstant, float airFrictionConstant, float gravity) {
	    	/*this.springConstant = springConstant;
	    	this.springLength = springLength;
	    	this.frictionConstant = frictionConstant;
	    	this.airFrictionConstant = airFrictionConstant;
	    	this.gravity.set(0, gravity);*/
	    	this.body1 = body1;
	    	this.body2 = body2;
	    }
	 
	    public void solve(float deltaTime) {
	        springVector.set(body1.getPosition());
	        springVector.sub(body2.getPosition());
	        r = springVector.len();
	        force.set(0,0);
	        if (r != 0) {
	        	springVector.mul(1/r);
	        	force.add(-springVector.x * (r - springLength) * springConstant, -springVector.y * (r - springLength) * springConstant);
	        	force.add(-(body1.getLinearVelocity().x - body2.getLinearVelocity().x) * frictionConstant, -(body1.getLinearVelocity().y - body2.getLinearVelocity().x) * frictionConstant);
	        	force.mul(deltaTime);
        		body1.setLinearVelocity(body1.getLinearVelocity().x + force.x, body1.getLinearVelocity().y + force.y);
        		body2.setLinearVelocity(body2.getLinearVelocity().x - force.x, body2.getLinearVelocity().y - force.y);
	        	
	        	// Air Friction
	        	body1.setLinearVelocity(body1.getLinearVelocity().x - (body1.getLinearVelocity().x * airFrictionConstant * deltaTime), 
	        			body1.getLinearVelocity().y - (body1.getLinearVelocity().y * airFrictionConstant * deltaTime));
	        	body2.setLinearVelocity(body2.getLinearVelocity().x - (body2.getLinearVelocity().x * airFrictionConstant * deltaTime), 
	        			body2.getLinearVelocity().y - (body2.getLinearVelocity().y * airFrictionConstant * deltaTime));
	        }
	    }
}