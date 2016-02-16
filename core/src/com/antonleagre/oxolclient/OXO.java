package com.antonleagre.oxolclient;

import com.antonleagre.oxolclient.sprites.Starship;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.sun.javafx.property.adapter.PropertyDescriptor;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This game was purely made to learn about the socket.io Libgdx integration,
 * code maybe sloppy, or wrong. Made after a tutorial by Brent Aureli.
  */
public class OXO extends ApplicationAdapter {
	//for our starship sprites
	SpriteBatch batch;
	//the conn to the serveer (starts at instantiating)
	private Socket socket;
	//our unique player id
	String id;
	//our player
	Starship player;
	Texture shipTexture;
	Texture oShipTexture;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		oShipTexture = new Texture("playerShip2.png");
		shipTexture = new Texture("playerShip.png");
		connectSocket();
		configSocketEvents();

	}
	@Override
	public void resize(int width, int height){
		//shouldn't be needing this, size is fixed
		// TODO: deprecated maybe?
		super.resize(width,height);
	}
	//we want to run this BEFORE update AND RENDER.
	public void handleInput(float dt){
		//we do this cause it will only initialize the player
		//after we connect to the server.  
		if(player != null){
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				// times delta cause lag compenstation
				player.setPosition(player.getX() + (-200 * dt), player.getY());
			}
			//same as above
			else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				player.setPosition(player.getX() + (100 * dt), player.getY());
			}
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleInput(Gdx.graphics.getDeltaTime());
		//grav ide
	

		batch.begin();
		if(player != null){
			player.draw(batch);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		super.dispose();
		shipTexture.dispose();
		oShipTexture.dispose();
	}
	private void connectSocket(){
		try{
			socket = IO.socket("http://localhost:8080");
			socket.connect();
		}catch (Exception e){
			// TODO: 19/12/2015
		}

	}
	private void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SOCKET.IO", "Connected to the server");
				player = new Starship(shipTexture);
			}
		}).on("getID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
				    id = data.getString("id");
                    Gdx.app.log("SOCKET.IO", "Mu id is: " + id);

				}catch (JSONException e){
					// TODO: 19/12/2015
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try{
                    JSONObject data = (JSONObject) args[0];
                    id = data.getString("id");
                    Gdx.app.log("SOCKET.IO", "Player connected with id: " + id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
	}
}
