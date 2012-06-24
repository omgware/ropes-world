package com.ropesworld;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.ropesworld.gameobjects.DynamicRopeStandObject;
import com.ropesworld.gameobjects.GameObject;
import com.ropesworld.gameobjects.GenericUseObject;
import com.ropesworld.gameobjects.MainCharObject;
import com.ropesworld.gameobjects.ObjectInfo;
import com.ropesworld.gameobjects.Piece;
import com.ropesworld.gameobjects.Rope;
import com.ropesworld.gameobjects.StarObject;

public class GameScreen implements Screen, InputProcessor {

	private GL10 gl = null;
	public static final int WORLD_WIDTH = 48;
	public static final int WORLD_HEIGHT = 32;
	public int LEVEL_WIDTH = 48;
	public int LEVEL_HEIGHT = 32;
	public int CAMERA_RIGHT_LIMIT;
	public int CAMERA_LEFT_LIMIT;
	public int CAMERA_UP_LIMIT;
	public int CAMERA_DOWN_LIMIT;
	public static final float GRAVITY_DEFAULT 			= -13.0f;
	public static final boolean IS_DESKTOP 				= true;
	public static final float FORCEMUL_DEFAULT 			= IS_DESKTOP ? 60000 : 60000 / 70;
	public static final float FRICTION_DAMPING 			= IS_DESKTOP ? 0.0001f : 0.005f;
	public static final Vector2 ZERO_VECTOR				= new Vector2(0,0);
	public static final boolean ALLOW_DRAG				= false; 
	public static final float TICKS_PER_SECOND 			= 60;
	public static final float SKIP_TICKS 				= 1 / TICKS_PER_SECOND;
	public static final int MAX_FRAMESKIP 				= 5;
	
	public boolean drawBodies = true;
	public boolean drawJoints = false;
	public boolean drawAAAB = false;
	public float gravity;
	public Vector2 gravityVector = new Vector2();
	public float forceMul;
	public float dragForceMult = 1000.0f;
	public RopesWorld game;
	
	public ArrayList<Piece> pieceTemplates = new ArrayList<Piece>();
	public ArrayList<Rope> ropes = new ArrayList<Rope>();
	public OrthographicCamera camera;
	public Box2DDebugRenderer renderer;
	public SpriteBatch batch;
	public BitmapFont font;
	public World world;
	public Body groundBody;
	public MouseJoint mouseJoint;
	public MainCharObject mainChar;
	public GameObject bigMama;
	public ArrayList<StarObject> stars = new ArrayList<StarObject>(3);
	public ArrayList<GenericUseObject> gravityBalls = new ArrayList<GenericUseObject>();
	public ArrayList<DynamicRopeStandObject> dynamicRopeStands = new ArrayList<DynamicRopeStandObject>();
	
	// General status variables
	public boolean isDragging;
	public float destroyOutOfWorldTimeCheck;
	public boolean gameover;
	public boolean winner;
	public boolean replay;
	public boolean triggerNextLevel;
	public int starsOwned;
	public float timeStep;
	public float nextGameTick;
	public int loops;
	public float interpolation;
	
	// Camera Status variables
	public float zoom = 1.0f;
	public long doubleTapWindow;
	public boolean cameraMoving;
	public float rotatingFactor;
	public boolean allowRotation = false;
	public boolean cameraRotated;
	
	// Ropes status variables
	public float updateWindow;
	
	// Char Status variables
	public boolean destroyMainCharJoints;
	public boolean destroyGravityBall;
	
	// for pinch-to-zoom
	public int numberOfFingers = 0;
	public int fingerOnePointer;
	public int fingerTwoPointer;
	public float distance;
	public float factor;
	public float lastDistance = 0;
	public Vector3 fingerOne = new Vector3();
	public Vector3 fingerTwo = new Vector3();
	
	
	// Temp variables
	public Fixture tempFixture = null;
	public Vector2 forceDirection = new Vector2();
	public Piece newPiece;
	public Piece newPiece2;
	public Body hitBody = null;
	public BodyDef def = new BodyDef();
	public FixtureDef fd = new FixtureDef();
	public Body logicHitBody = null;
	public Body tempBody = null;
	public Body tempBody2 = null;
	public Vector2 tmp = new Vector2();
	public Vector2 target = new Vector2();
	public Vector3 testPoint = new Vector3();
	public Vector2 testPoint2D = new Vector2();
	public Iterator<Body> bodyIterator = null;
	float tempFloat;
	
	
	public GameScreen(RopesWorld g) {
    	this(g, GRAVITY_DEFAULT, FORCEMUL_DEFAULT);
    }
	
	public GameScreen(RopesWorld g, float gravity, float forceMul) {
    	this(g, gravity, forceMul, 1000.0f, true, false, false);
    }
	
	public GameScreen(RopesWorld g, float gravity, float forceMul, float dragForceMult, boolean drawBodies, boolean drawJoints, boolean drawAAAB) {
    	game = g;
    	this.gravity = gravity;
    	gravityVector.set(0, gravity);
    	this.forceMul = forceMul;
    	this.dragForceMult = dragForceMult;
    	this.drawBodies = drawBodies;
    	this.drawJoints = drawJoints;
    	this.drawAAAB = drawAAAB;
    }
	
	public void setCameraLimits() {
		if (LEVEL_WIDTH > WORLD_WIDTH/2) {
			CAMERA_RIGHT_LIMIT = LEVEL_WIDTH/3;
			CAMERA_LEFT_LIMIT = -LEVEL_WIDTH/3;
		}
		else {
			CAMERA_RIGHT_LIMIT = 0;
			CAMERA_LEFT_LIMIT = 0;
		}
    	CAMERA_UP_LIMIT = LEVEL_HEIGHT/2 - 1;
    	CAMERA_DOWN_LIMIT = LEVEL_HEIGHT/2 - 1;
	}

	public void createWorld (World world) {
		// Create ground cage
		{
			ChainShape shape = new ChainShape();
			shape.createLoop(new Vector2[] {new Vector2(-WORLD_WIDTH, 0), new Vector2(WORLD_WIDTH, 0), new Vector2(WORLD_WIDTH, WORLD_HEIGHT*2 - 1), new Vector2(-WORLD_WIDTH, WORLD_HEIGHT*2 - 1)});
			fd.shape = shape;
			fd.friction = 0.8f;
			fd.restitution = 0.3f;
			BodyDef bd = new BodyDef();
			Body cage = world.createBody(bd);
			cage.createFixture(fd);
			// dispose shape
			shape.dispose();
		}

		// Populate level with elements
		// All the other elements
		createBodyAndFixture(getNewPieceInstanceFromTemplate(2), -19, 24);
		createBodyAndFixture(getNewPieceInstanceFromTemplate(3), -19, 19);
		createBodyAndFixture(getNewPieceInstanceFromTemplate(3), -10, 51);
		createBodyAndFixture(getNewPieceInstanceFromTemplate(3), -35, 51);
		createBodyAndFixture(getNewPieceInstanceFromTemplate(3), 0, 12);
		createBodyAndFixture(getNewPieceInstanceFromTemplate(4), 18, 16);
		// Enemies
		createBodyAndFixture(getNewPieceInstanceFromTemplate(15), 8, 24);
		// Main Char
		newPiece = getNewPieceInstanceFromTemplate(5).setMainChar(true);
		createBodyAndFixture(newPiece, -3, 14);
		mainChar = new MainCharObject(newPiece, newPiece.body);
		// Main char + Little Box -> Rope
		newPiece = getNewPieceInstanceFromTemplate(14);
		createBodyAndFixture(newPiece, 13, 18);
		ropes.add(new Rope(mainChar.body, newPiece.body, world));
		if (ropes.get(ropes.size() - 1).attachBodyA == mainChar.body)
			mainChar.ropes.add(ropes.get(ropes.size() - 1));
		// Another two boxes -> rope
		newPiece = getNewPieceInstanceFromTemplate(14);
		createBodyAndFixture(newPiece, -10, 48);
		newPiece.body.setType(BodyType.StaticBody);
		newPiece2 = getNewPieceInstanceFromTemplate(14);
		createBodyAndFixture(newPiece2, -20, 38);
		ropes.add(new Rope(newPiece.body, newPiece2.body, world));
		// Another three boxes -> rope
		newPiece2 = getNewPieceInstanceFromTemplate(14);
		createBodyAndFixture(newPiece2, -35, 38);
		newPiece = getNewPieceInstanceFromTemplate(14);
		createBodyAndFixture(newPiece, -30, 48);
		newPiece.body.setType(BodyType.StaticBody);
		ropes.add(new Rope(newPiece.body, newPiece2.body, world));
		newPiece = getNewPieceInstanceFromTemplate(14);
		createBodyAndFixture(newPiece, -40, 48);
		newPiece.body.setType(BodyType.StaticBody);
		ropes.add(new Rope(newPiece.body, newPiece2.body, world));
		
	}
	
	public void createRope(float x, float y) {
		createRope(x, y, 0);
	}
	
	public void createRope(float x, float y, boolean elastic) {
		createRope(x, y, 0, elastic);
	}
	
	public void createRope(float x, float y, int length) {
		createRope(x, y, length, false);
	}
	
	public void createRope(float x, float y, int length, boolean elastic) {
		newPiece = getNewPieceInstanceFromTemplate(16);
		createBodyAndFixture(newPiece, x, y);
		ropes.add(new Rope(newPiece.body, mainChar.body, world, length, elastic));
	}
	
	public void createRope(Body stand, int length) {
		ropes.add(new Rope(stand, mainChar.body, world, length, false));
	}
	
	public void createRope(Body stand, int length, boolean elastic) {
		ropes.add(new Rope(stand, mainChar.body, world, length, elastic));
	}
	
	public void createTramp(float x1, float y1, float x2, float y2) {
		def.type = BodyType.StaticBody;
		def.angle = (float)Math.toRadians(-90);
		CircleShape shape = new CircleShape();
		shape.setRadius(0.1f);
		fd.shape = shape;
		fd.isSensor = true;
		def.position.x = x1;
		def.position.y = y1;
		tempBody = world.createBody(def);
		tempBody.createFixture(fd);
		def.position.x = x2;
		def.position.y = y2;
		tempBody2 = world.createBody(def);
		tempBody2.createFixture(fd);
		ropes.add(new Rope(tempBody, tempBody2, world, 0, true, true));
	}
	
	public void createStar(float x, float y) {
		newPiece = getNewPieceInstanceFromTemplate(19);
		createBodyAndFixture(newPiece, x, y);
		stars.add(new StarObject(newPiece, newPiece.body));
	}
	
	public void createDynamicRope(float x, float y, float radius) {
		newPiece = getNewPieceInstanceFromTemplate(17);
		createBodyAndFixture(newPiece, x, y);
		dynamicRopeStands.add(new DynamicRopeStandObject(newPiece, newPiece.body, world, radius));
	}
	
	public void createMainChar(float x, float y) {
		newPiece = getNewPieceInstanceFromTemplate(5).setMainChar(true);
		createBodyAndFixture(newPiece, x, y);
		mainChar = new MainCharObject(newPiece, newPiece.body);
	}
	
	public void createBigMama(float x, float y) {
		newPiece = getNewPieceInstanceFromTemplate(18).setSensor(true);
		createBodyAndFixture(newPiece, x, y);
		bigMama = new GameObject(newPiece, newPiece.body);
	}
	
	public void createSimpleEnemy(float x, float y) {
		createBodyAndFixture(getNewPieceInstanceFromTemplate(15), x, y);
	}
	
	public void createGravityBall(float x, float y) {
		newPiece = getNewPieceInstanceFromTemplate(20);
		createBodyAndFixture(newPiece, x, y);
		gravityBalls.add(new GenericUseObject(newPiece, newPiece.body));
	}
	
	public Body createBodyAndFixture(Piece piece, float x, float y) {
		piece.pos.x = x;
		piece.pos.y = y;
		def.position.x = x;
		def.position.y = y;
		def.type = piece.type;
		def.angle = piece.angle;
		Body body = world.createBody(def);
		if (body.getType() == BodyType.StaticBody) {
			fd.shape = piece.shape;
			tempFixture = body.createFixture(fd);
		}
		else {
			tempFixture = body.createFixture(piece.shape, 5);
			tempFixture.setFriction(0.6f);
			tempFixture.setRestitution(0.4f);
		}
		tempFixture.setSensor(piece.isSensor);
		piece.setBody(body);
		// introduce ObjectInfo as UserData
		if (body.getType() == BodyType.DynamicBody || body.getType() == BodyType.KinematicBody) {
			tempFixture.getBody().setUserData(new ObjectInfo(piece));
			if (piece.shape instanceof CircleShape) {
				((ObjectInfo)tempFixture.getBody().getUserData()).isSphere = true;
			}
			if (piece.isMainChar) {
				((ObjectInfo)tempFixture.getBody().getUserData()).isMainChar = true;
			}
		}
		return body;
	}

	/** Create and save body templates **/
	public void setupPieces() {
		starsOwned = 0;
		// Reallocate arrays
		pieceTemplates = new ArrayList<Piece>(60);
		addNewPieceTemplate((new Piece(0.5f, 1.5f, 0, BodyType.StaticBody)).setSensor(true)); // 0
		addNewPieceTemplate((new Piece(0.5f, 1.5f, 0, BodyType.StaticBody)).setSensor(true)); // 1
		/** Box (2,4) DynamicBody **/
		addNewPieceTemplate(new Piece(1, 2, 0, BodyType.DynamicBody)); // 2
		/** Box (8,2) StaticBody **/
		addNewPieceTemplate(new Piece(4, 1, 0, BodyType.StaticBody)); // 3
		/** Box (8,2) StaticBody **/
		addNewPieceTemplate(new Piece(6, 1, 0, BodyType.StaticBody)); // 4
		/** Circle 1.0 DynamicBody **/
		addNewPieceTemplate(new Piece(1, BodyType.DynamicBody)); // 5
		/** Circle 2.0 DynamicBody **/
		addNewPieceTemplate(new Piece(2, BodyType.DynamicBody)); // 6
		addNewPieceTemplate((new Piece(1.5f, 0.5f, 0, BodyType.StaticBody)).setSensor(true)); // 7
		addNewPieceTemplate((new Piece(1.5f, 0.5f, 0, BodyType.StaticBody)).setSensor(true)); // 8
		/** Mini Circle **/
		addNewPieceTemplate(new Piece(0.5f, BodyType.DynamicBody)); // 9
		/** Mini Circle **/
		addNewPieceTemplate(new Piece(0.3f, BodyType.DynamicBody)); // 10
		/** Mini Circle **/
		addNewPieceTemplate(new Piece(0.1f, BodyType.KinematicBody)); // 11
		/** Mini Circle Sensor**/
		addNewPieceTemplate(new Piece(0.3f, BodyType.DynamicBody)).setSensor(true); // 12
		/** Box (20,30) DynamicBody **/
		addNewPieceTemplate(new Piece(10, 15, 0, BodyType.DynamicBody)); // 13
		/** Box (1,1) DynamicBody **/
		addNewPieceTemplate(new Piece(1, 1, 0, BodyType.DynamicBody)); // 14
		/** Diamond ENEMY (1,1) **/
		addNewPieceTemplate((new Piece(0.5f, 0.5f, 45, BodyType.KinematicBody)).setEnemy(true)); // 15
		/** Rope Fixed Circle **/
		addNewPieceTemplate((new Piece(0.5f, BodyType.StaticBody)).setSensor(true).setAngle(90)); // 16
		/** Rope Dynamic Circle **/
		addNewPieceTemplate((new Piece(0.7f, BodyType.StaticBody)).setSensor(true).setAngle(90)); // 17
		/** Big Mama **/
		addNewPieceTemplate(new Piece(3, 2, 0, BodyType.StaticBody)); // 18
		/** Star **/
		addNewPieceTemplate((new Piece(1.0f, BodyType.KinematicBody)).setSensor(true)); // 19
		/** Gravity Ball **/
		addNewPieceTemplate((new Piece(2.0f, BodyType.KinematicBody)).setSensor(true)); // 20
	}
	
	public Piece getNewPieceInstanceFromTemplate(int templateIndex) {
		return new Piece(pieceTemplates.get(templateIndex));
	}

	@Override
	public void show() {
		// Setup all the game elements once
		setupPieces();
		// setup the camera. In Box2D we operate on a
		// meter scale, pixels won't do it. So we use
		// an orthographic camera with a viewport of
		// 48 meters in width and 32 meters in height.
		// We also position the camera so that it
		// looks at (0,16) (that's where the middle of the
		// screen will be located).
		camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
		camera.position.set(0, (WORLD_HEIGHT/2)-1, 0);
		setCameraLimits();

		// create the debug renderer
		renderer = new Box2DDebugRenderer(drawBodies, drawJoints, drawAAAB, true);

		// create the world
		world = new World(new Vector2(0, gravity), true);

		// we also need an invisible zero size ground body
		// to which we can connect the mouse joint
		BodyDef bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);

		// call abstract method to populate the world
		createWorld(world);

		batch = new SpriteBatch(1000);
		font = new BitmapFont();
		
		// register ourselfs as an InputProcessor
		Gdx.input.setInputProcessor(this);
		nextGameTick = 0;
		timeStep = 0;
	}

	@Override
	public void render(float deltaTime) {
		// update the world with a fixed time step
		//long startTime = System.nanoTime();
		timeStep += deltaTime;
		loops = 0;
        while(timeStep > nextGameTick && loops < MAX_FRAMESKIP) {
			performLogic(SKIP_TICKS);
	    	world.step(SKIP_TICKS, 3, 3);
            nextGameTick += SKIP_TICKS;
            loops++;
        }
        //interpolation = (timeStep + SKIP_TICKS - nextGameTick) / SKIP_TICKS;
        
		//float updateTime = (System.nanoTime() - startTime) / 1000000000.0f;
		//startTime = System.nanoTime();
		if (gl == null)
			gl = Gdx.app.getGraphics().getGL10();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if (cameraMoving) {
			tmp.set(testPoint2D);
			tmp.sub(camera.position.x, camera.position.y);
			tmp.mul(0.001f);
			updateCameraPosition(camera.position.x + tmp.x, camera.position.y + tmp.y);
		}
		else if (mainChar != null)
			updateCameraPosition();
		if (allowRotation && rotatingFactor != 0) {
			camera.rotate(rotatingFactor * 0.02f, 0, 0, 1);
			gravityVector.rotate(rotatingFactor * 0.02f);
			world.setGravity(gravityVector);
			cameraRotated = true;
		}
		camera.zoom = zoom;
		camera.update();
		camera.apply(gl);

		renderer.render(world, camera.combined);
		//float renderTime = (System.nanoTime() - startTime) / 1000000000.0f;

		batch.begin();
		font.draw(batch,  "GAME SCREEN " + "fps:" + Gdx.graphics.getFramesPerSecond() + ", deltaTime: " + deltaTime/*+ ", update: " + updateTime + ", render: " + renderTime*/, 0, 15);
		font.draw(batch,  "(" + (int)testPoint.x + "," + (int)testPoint.y + ")", 10, Gdx.graphics.getHeight());
		if (Gdx.input.getAccelerometerX() > 0 || Gdx.input.getAccelerometerY() > 0) {
			font.drawMultiLine(batch, "accelX: " + Gdx.input.getAccelerometerX() + "\n" + "accelY: " + Gdx.input.getAccelerometerY() + "\n"
					+ "accelZ: " + Gdx.input.getAccelerometerZ() + "\n", 0, 110);
		}
		font.draw(batch,  "REPLAY", Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight());
		font.draw(batch,  "NEXT", Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight());
		if (winner) {
			font.draw(batch,  "YOU WIN!", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		}
		else if (gameover) {
				font.draw(batch,  "YOU LOSE!", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
			}
		batch.end();
	}
	
	private void updateCameraPosition() {
		tmp.set(mainChar.body.getPosition());
		updateCameraPosition(tmp.x, tmp.y);
	}
	
	private void updateCameraPosition(float x, float y) {
		// limit the view on the level angles
		if (x > CAMERA_RIGHT_LIMIT)
			x = CAMERA_RIGHT_LIMIT;
		else if (x < CAMERA_LEFT_LIMIT)
			x = CAMERA_LEFT_LIMIT;
		if (y > CAMERA_UP_LIMIT)
			y = CAMERA_UP_LIMIT;
		else if (y < CAMERA_DOWN_LIMIT)
			y = CAMERA_DOWN_LIMIT;
		camera.position.set(x, y, 0);
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean keyDown(int keycode) {
		// Rotate Camera
		if (keycode == Input.Keys.RIGHT)
			rotatingFactor = 1.0f;
		else if (keycode == Input.Keys.LEFT)
			rotatingFactor = -1.0f;
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// Change Zoom
		if (keycode == Input.Keys.PLUS)
			zoom -= 0.2f;
		else if (keycode == Input.Keys.MINUS)
			zoom += 0.2f;
		
		// Rotate Camera
		else if ((keycode == Input.Keys.RIGHT) || (keycode == Input.Keys.LEFT))
			rotatingFactor = 0.0f;
		
		// Exit
		else if (keycode == Input.Keys.ESCAPE)
			game.exit();
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			// if the hit point is inside the fixture of the body
			// we report it
			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// Replay button
		if (x > (Gdx.graphics.getWidth() - 60) && y < 20) {
			replay = true;
			return false;
		}
		// Next button
		if (x > (Gdx.graphics.getWidth() - 110) && x < (Gdx.graphics.getWidth() - 70) && y < 20)
			triggerNextLevel = true;
		// for pinch-to-zoom
		numberOfFingers++;
		if (!IS_DESKTOP) {
			if (numberOfFingers == 1) {
				fingerOnePointer = pointer;
				fingerOne.set(x, y, 0);
			}
			else if (numberOfFingers == 2) {
				fingerTwoPointer = pointer;
				fingerTwo.set(x, y, 0);
				lastDistance = fingerOne.dst2(fingerTwo);
				return false;
			}
		}
		// translate the mouse coordinates to world coordinates
		camera.unproject(testPoint.set(x, y, 0));
		testPoint2D.x = testPoint.x;
		testPoint2D.y = testPoint.y;
		// Drag Mode
		if (ALLOW_DRAG) {
			hitBody = null;
			world.QueryAABB(callback, testPoint.x - 0.0001f, testPoint.y - 0.0001f, testPoint.x + 0.0001f, testPoint.y + 0.0001f);
			if (hitBody == groundBody) hitBody = null;
			// ignore kinematic bodies, they don't work with the mouse joint
			if (hitBody != null && (hitBody.getType() == BodyType.KinematicBody || hitBody.getType() == BodyType.StaticBody)) return false;
			if (hitBody != null) {
				MouseJointDef def = new MouseJointDef();
				def.bodyA = groundBody;
				def.bodyB = hitBody;
				def.collideConnected = true;
				def.target.set(testPoint.x, testPoint.y);
				def.maxForce = dragForceMult * hitBody.getMass();
	
				mouseJoint = (MouseJoint)world.createJoint(def);
				hitBody.setAwake(true);
				isDragging = true;
			}
		}
		if (!isDragging) {
			if ((System.currentTimeMillis() - doubleTapWindow) < 150) {
				cameraMoving = true;
			}
		}
		// Gravity Ball pop check
		if (mainChar.gravityBall != null) {
			if (mainChar.gravityBall.body.getPosition().dst2(testPoint2D) < 5) {
				destroyGravityBall = true;
			}
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// for pinch-to-zoom
		if (!IS_DESKTOP) {
			if (numberOfFingers == 2) {
				if (pointer == fingerOnePointer)
				       fingerOne.set(x, y, 0);
				if (pointer == fingerTwoPointer)
				       fingerTwo.set(x, y, 0);
				distance = fingerOne.dst2(fingerTwo);
				//factor = distance / lastDistance / 4;
				if (lastDistance > distance)
					zoom += 0.01f;
				else if (lastDistance < distance)
					zoom -= 0.01f;
				lastDistance = distance;
				return false;
			}
		}
		// if a mouse joint exists we simply update
		// the target of the joint based on the new
		// mouse coordinates
		camera.unproject(testPoint.set(x, y, 0));
		testPoint2D.x = testPoint.x;
		testPoint2D.y = testPoint.y;
		if (mouseJoint != null) {
			mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
		}
		
		// Rope Check
		if (!isDragging && !cameraMoving && !destroyGravityBall) {
			Rope tempRope = null;
			for (Rope rope : ropes) {
				if (!rope.isTramp) {
					if (rope.checkPoint(testPoint2D)) {
						tempRope = rope;
					}
				}
			}
			if (tempRope != null) {
				ropes.remove(tempRope);
				tempRope.dispose();
				if (mainChar.gravityBall != null) {
					mainChar.body.setGravityScale(-2.0f);
					mainChar.body.setLinearDamping(4.0f);
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		camera.unproject(testPoint.set(x, y, 0));
		testPoint2D.x = testPoint.x;
		testPoint2D.y = testPoint.y;
		// if a mouse joint exists we simply destroy it
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		hitBody = null;
		
		// for pinch-to-zoom     
		 numberOfFingers--;
		// just some error prevention... clamping number of fingers (ouch! :-)
		 if(numberOfFingers<0)
		        numberOfFingers = 0;
		lastDistance = 0;
		isDragging = false;
		cameraMoving = false;
		destroyGravityBall = false;
		doubleTapWindow = System.currentTimeMillis();
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		camera.unproject(testPoint.set(x, y, 0));
		testPoint2D.x = testPoint.x;
		testPoint2D.y = testPoint.y;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		renderer.dispose();
		pieceTemplates.clear();
		ropes.clear();
		stars.clear();
		gravityBalls.clear();
		dynamicRopeStands.clear();
	}

	@Override
	public void pause() {
		// some error prevention...
		 numberOfFingers = 0;
	}

	@Override
	public void hide() {
		dispose();
	}
	
	public Piece addNewPieceTemplate(Piece piece) {
		pieceTemplates.add(piece);
		return piece;
	}

	public void createMouseJoint() {
		MouseJointDef def = new MouseJointDef();
		def.bodyA = groundBody;
		def.bodyB = hitBody;
		def.collideConnected = true;
		def.target.set(testPoint.x, testPoint.y);
		def.maxForce = dragForceMult * hitBody.getMass();
		
		mouseJoint = (MouseJoint)world.createJoint(def);
		hitBody.setAwake(true);
	}
	
	private void destroyMainCharJoints() {
		/*while(!mainChar.body.getJointList().isEmpty()) {
			 world.destroyJoint(mainChar.body.getJointList().get(0).joint);	
		}*/
		if (mainChar.ropes.size() > 0) {
			mainChar.disposeRopes();
		}
		destroyMainCharJoints = false;
	}
	
	public QueryCallback enemyCallback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			if (fixture.getBody().getUserData() != null) {
				if (((ObjectInfo)fixture.getBody().getUserData()).pieceInfo.isEnemy) {
					gameover = true;
					mainChar.body.setType(BodyType.StaticBody);
					return false;
				}
			}
			return true;
		}
	};
	
	public QueryCallback starsCallback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			for (StarObject star : stars) {
				if (star.body == fixture.getBody()) {
					star.owned = true;
					star.body.setTransform(-LEVEL_WIDTH + 2 + (starsOwned * 3), LEVEL_HEIGHT - 3, 0);
					starsOwned++;
					return false;
				}
			}
			return true;
		}
	};
	
	public QueryCallback gravityBallsCallback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			for (GenericUseObject ball : gravityBalls) {
				if (ball.body == fixture.getBody()) {
					ball.used = true;
					mainChar.gravityBall = ball;
					mainChar.body.setGravityScale(-3.0f);
					mainChar.body.setLinearDamping(5.0f);
					return false;
				}
			}
			return true;
		}
	};
	
	public void performLogic(float deltaTime) {
		/** GAME STATUSES **/
		if (replay) {
			replay = false;
			winner = false;
			gameover = false;
			triggerNextLevel = false;
			game.resetScreen();
		}
		if (triggerNextLevel) {
			triggerNextLevel = false;
			replay = false;
			winner = false;
			gameover = false;
			game.nextLevel();
		}
		if (winner || gameover)
			return;
		
		/** CHAR STATUS AND MOVEMENT **/
		if (mainChar != null) {
			if (destroyMainCharJoints)
				destroyMainCharJoints();
			// Gameover if char goes beyond level limits
			if ( (mainChar.body.getPosition().x > LEVEL_HEIGHT + 20) || (mainChar.body.getPosition().x < -LEVEL_HEIGHT -20)
					|| (mainChar.body.getPosition().y > LEVEL_HEIGHT*2 + 20) || (mainChar.body.getPosition().y < -20)) {
						gameover = true;
						return;
			}
			// Winner if he goes to Big Mama
			if (bigMama != null && bigMama.body.getPosition().dst2(mainChar.body.getPosition()) < 5) {
				winner = true;
				mainChar.body.setType(BodyType.StaticBody);
				for (Rope rope : ropes) {
					rope.dispose();
				}
				return;
			}
			// Movement when on rope is faster
			if (ropes.size() > 0) {
				//System.out.println("vel: " + tempFloat);
				if (!cameraRotated) { 
					tempFloat = mainChar.body.getLinearVelocity().len2();
					if (tempFloat > 10 && tempFloat < 30) {
						//mainChar.body.applyForceToCenter(mainChar.body.getLinearVelocity().x * deltaTime * 300, mainChar.body.getLinearVelocity().y * deltaTime * 300);
						mainChar.body.applyForceToCenter(mainChar.body.getLinearVelocity().x * deltaTime * (50 + 50*ropes.size()), 0);
					}
				}
				// Tramps have a bit more friction than other bodies
				for (Rope r: ropes) {
					if (r.isTramp) {
						for (Body b : r.bodies) {
							//System.out.println("y: " + b.getLinearVelocity().y);
							if (b.getLinearVelocity().y < -5 && b.getPosition().dst2(mainChar.body.getPosition()) < 2) {
								b.setLinearVelocity(b.getLinearVelocity().x * 1.5f, b.getLinearVelocity().y * 1.5f);
							}
							if (b.getLinearVelocity().len2() > 1)
								b.setLinearVelocity(b.getLinearVelocity().x * 0.98f, b.getLinearVelocity().y * 0.98f);
						}
					}
				}
			}
			
			/** ENEMY CHECKS **/
			world.QueryAABB(enemyCallback, mainChar.body.getPosition().x - mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
					mainChar.body.getPosition().y - mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
					mainChar.body.getPosition().x + mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
					mainChar.body.getPosition().y + mainChar.body.getFixtureList().get(0).getShape().getRadius());
			
			/** STARS CHECK **/
			world.QueryAABB(starsCallback, mainChar.body.getPosition().x - mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
					mainChar.body.getPosition().y - mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
					mainChar.body.getPosition().x + mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
					mainChar.body.getPosition().y + mainChar.body.getFixtureList().get(0).getShape().getRadius());

			/** GRAVITY BALLS CHECK **/
			if (gravityBalls.size() > 0 && mainChar.gravityBall == null) {
				world.QueryAABB(gravityBallsCallback, mainChar.body.getPosition().x - mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
						mainChar.body.getPosition().y - mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
						mainChar.body.getPosition().x + mainChar.body.getFixtureList().get(0).getShape().getRadius(), 
						mainChar.body.getPosition().y + mainChar.body.getFixtureList().get(0).getShape().getRadius());
			}
			else if (mainChar.gravityBall != null) {
				mainChar.gravityBall.body.setTransform(mainChar.body.getPosition(), 0);
			}
			if (destroyGravityBall && mainChar.gravityBall != null) {
				gravityBalls.remove(mainChar.gravityBall);
				world.destroyBody(mainChar.gravityBall.body);
				mainChar.gravityBall = null;
				mainChar.body.setGravityScale(1);
				mainChar.body.setLinearDamping(0);
			}
		}
		
		/** ROPES CHECKS **/
		/*updateWindow += deltaTime;
		if (updateWindow >= 0.001f) {
			for (Rope rope : ropes) {
				rope.update(deltaTime);
			}
			updateWindow = 0;
		}*/
		for (DynamicRopeStandObject stand : dynamicRopeStands) {
			if (!stand.used && stand.checkPoint(mainChar.body.getPosition())) {
				createRope(stand.body, 100 + (int)stand.radius);
				stand.used = true;
			}
		}
		logicHitBody = null;
		
		/** PHYSICS **/
		destroyOutOfWorldTimeCheck += deltaTime;
		if (destroyOutOfWorldTimeCheck >= 20) {
			destroyOutOfWorldTimeCheck = 0;
			bodyIterator = world.getBodies();
			while (bodyIterator.hasNext()) {
				logicHitBody = bodyIterator.next();
				// DESTROY OUT OF WORLD OBJECT
				if ( (logicHitBody.getPosition().x > LEVEL_HEIGHT + 20) || (logicHitBody.getPosition().x < -LEVEL_HEIGHT -20)
						|| (logicHitBody.getPosition().y > LEVEL_HEIGHT*2 + 20) || (logicHitBody.getPosition().y < -20)) {
						world.destroyBody(logicHitBody);
				}
				
				/*if (logicHitBody.getType() != BodyType.DynamicBody || logicHitBody.getUserData() == null || (logicHitBody.getUserData() != null && (!((ObjectInfo)logicHitBody.getUserData()).isSphere)))
					continue;
				// apply air friction
				logicHitBody.setLinearVelocity(logicHitBody.getLinearVelocity().mul(1 - (FRICTION_DAMPING / logicHitBody.getMass())));
				logicHitBody.setAngularVelocity(logicHitBody.getAngularVelocity() * (1 - (FRICTION_DAMPING / logicHitBody.getMass())));*/
			}
		}
		if (allowRotation && (Gdx.input.getAccelerometerX() > 0 || Gdx.input.getAccelerometerY() > 0)) {
			gravityVector.set(Gdx.input.getAccelerometerY() * 1.35f,-Gdx.input.getAccelerometerX() * 1.35f);
			world.setGravity(gravityVector);
		}
	}

}
