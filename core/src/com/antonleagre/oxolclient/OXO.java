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

import java.util.HashMap;

public class OXO extends ApplicationAdapter {
	SpriteBatch batch;
	private Socket socket;
	String id;
	Starship player;
	Texture shipTexture;
	Texture oShipTexture;
    HashMap<String, Starship> friendlyPlayers;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		oShipTexture = new Texture("playerShip2.png");
		shipTexture = new Texture("playerShip.png");
        friendlyPlayers = new HashMap<String, Starship>();
		connectSocket();
		configSocketEvents();

	}
	@Override
	public void resize(int width, int height){
		super.resize(width,height);
	}
	public void handleInput(float dt){
		if(player != null){
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				player.setPosition(player.getX() + (-200 * dt), player.getY());
			}
			else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				player.setPosition(player.getX() + (200 * dt), player.getY());
			}
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleInput(Gdx.graphics.getDeltaTime());

		batch.begin();
		if(player != null){
			player.draw(batch);
		}
        for(HashMap.Entry<String,Starship> entry : friendlyPlayers.entrySet()) {
            entry.getValue().draw(batch);
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
                    friendlyPlayers.put(id, new Starship(oShipTexture));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
	}
}
