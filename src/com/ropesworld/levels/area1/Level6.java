package com.ropesworld.levels.area1;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ropesworld.GameScreen;
import com.ropesworld.RopesWorld;

public class Level6 extends GameScreen {

	public Level6(RopesWorld g) {
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
		
		createMainChar(0,10);
		createBigMama(0,28);
		
		/** ROPES **/
		createRope(-7, 5, 2);
		createRope(7, 16, 4);
		createRope(0, 25);
		
		/** STARS **/
		createStar(0, 2);
		createStar(-4, 14);
		createStar(-4, 19);
		
		/** GRAVITY BALLS **/
		createGravityBall(0, 2);
	}

}
