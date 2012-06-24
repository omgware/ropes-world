package com.ropesworld;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.fullscreen = true;
		config.resizable = false;
		config.title = "Ropes World";
		config.vSyncEnabled = false;
		/*config.width = Toolkit.getDefaultToolkit().getScreenSize().width;
		config.height = Toolkit.getDefaultToolkit().getScreenSize().height;*/
		config.width = 800;
		config.height = 480;
		config.useGL20 = false;
		new LwjglApplication(new RopesWorld(), config);
	}

}
