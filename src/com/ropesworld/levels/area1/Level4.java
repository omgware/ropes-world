package com.ropesworld.levels.area1;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ropesworld.GameScreen;
import com.ropesworld.RopesWorld;

public class Level4 extends GameScreen {

	public Level4(RopesWorld g) {
		super(g);
		LEVEL_WIDTH = 24;
		LEVEL_HEIGHT = 32;
	}
	
	@Override
	public void createWorld (World world) {
		// Create ground cage
		{
			ChainShape shape = new ChainShape();
			shape.createLoop(new Vector2[] {new Vector2(-LEVEL_WIDTH, 0), new Vector2(LEVEL_WIDTH, 0), new Vector2(LEVEL_WIDTH, LEVEL_HEIGHT - 1), new Vector2(-LEVEL_WIDTH, LEVEL_HEIGHT - 1)});
			fd.shape = shape;
			fd.friction = 0.8f;
			fd.restitution = 0.3f;
			fd.isSensor = true;
			BodyDef bd = new BodyDef();
			Body cage = world.createBody(bd);
			cage.createFixture(fd);
			// dispose shape
			shape.dispose();
		}
		
		createMainChar(-5,14);
		createBigMama(4,2);
		
		/** ROPES **/
		createRope(4, 15, 3);
		createRope(-5, 25);
		
		/** STARS **/
		createStar(-5, 9);
		createStar(4, 18);
		createStar(4, 28);
		
		/** GRAVITY BALLS **/
		createGravityBall(-5, 4);
	}

}
