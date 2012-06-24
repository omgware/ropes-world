package com.ropesworld.levels.area1;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ropesworld.GameScreen;
import com.ropesworld.RopesWorld;

public class Level9 extends GameScreen {

	public Level9(RopesWorld g) {
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
		
		createMainChar(-23,26);
		createBigMama(15,15);
		
		/** ROPES **/
		createRope(-23, 28);
		createRope(-16, 28);
		createDynamicRope(-12.5f, 7, 5);
		createDynamicRope(-9, 25, 4);
		
		/** TRAMP **/
		createTramp(-4, 7, 4, 6);
		
		/** STARS **/
		createStar(0, 8);
		createStar(-9, 8);
		createStar(8, 18);
		
		/** GRAVITY BALL **/
		createGravityBall(-12.5f, 2);
	}

}
