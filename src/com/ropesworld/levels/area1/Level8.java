package com.ropesworld.levels.area1;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ropesworld.GameScreen;
import com.ropesworld.RopesWorld;

public class Level8 extends GameScreen {

	public Level8(RopesWorld g) {
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
		
		createMainChar(-7,24);
		createBigMama(0,2);
		
		/** ROPES **/
		createRope(-7, 29);
		createRope(0, 29);
		createRope(0, 22, 2);
		createRope(0, 15, 1);
		
		/** STARS **/
		createStar(6, 22);
		createStar(-7, 14);
		createStar(6, 14);
		
		/** ENEMY **/
		createSimpleEnemy(-3, 16);
		createSimpleEnemy(-1.5f, 16);
		createSimpleEnemy(0, 16);
		createSimpleEnemy(1.5f, 16);
		createSimpleEnemy(3, 16);
		createSimpleEnemy(-3, 8);
		createSimpleEnemy(-1.5f, 8);
		createSimpleEnemy(0, 8);
		createSimpleEnemy(1.5f, 8);
		createSimpleEnemy(3, 8);
	}

}
