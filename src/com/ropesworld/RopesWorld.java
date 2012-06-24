package com.ropesworld;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ropesworld.levels.area1.*;


public class RopesWorld extends Game {
	GameScreen gameScreen;
	GameScreen currentLevel;
	int currentAreaNum;
	int currentLevelNum;

	@Override
	public void create() {
		switchToLevel(1, 1);
	}
	
	public GameScreen switchToGame() {
		if (gameScreen == null)
			gameScreen = new GameScreen(this);
		return gameScreen;
	}
	
	public void switchToLevel(int area, int level) {
		switch (area) {
			case 1:
				switch (level) {
					case 1:
						currentLevel = new Level1(this);
						break;
					case 2:
						currentLevel = new Level2(this);
						break;
					case 3:
						currentLevel = new Level3(this);
						break;
					case 4:
						currentLevel = new Level4(this);
						break;
					case 5:
						currentLevel = new Level5(this);
						break;
					case 6:
						currentLevel = new Level6(this);
						break;
					case 7:
						currentLevel = new Level7(this);
						break;
					case 8:
						currentLevel = new Level8(this);
						break;
					case 9:
						currentLevel = new Level9(this);
						break;
				}
		}
		if (currentLevel == null)
			Gdx.app.exit();
		currentAreaNum = area;
		currentLevelNum = level;
		setScreen(currentLevel);
	}
	
	public void resetScreen() {
		if (currentLevel == null)
			Gdx.app.exit();
		setScreen(currentLevel);
	}
	
	public void nextLevel() {
		switchToLevel(currentAreaNum, ++currentLevelNum);
	}
	
	public void exit() {
		currentLevel.world.dispose();
		Gdx.app.exit();
	}

}
