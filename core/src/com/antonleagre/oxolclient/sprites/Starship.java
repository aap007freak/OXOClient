package com.antonleagre.oxolclient.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Anton Leagre on 19/12/2015.
 * You can contact me on: anton.leagre@hotmail.com
 * All rights reserved
 */
public class Starship extends Sprite{
    Vector2 previousPos;

    public Starship(Texture texture){
        super(texture);
        previousPos = new Vector2(getX(),getY());
    }

    public boolean hasMoved(){

        if(previousPos.x != getX()|| previousPos.y != getY()){
            previousPos.y = getY();
            previousPos.x = getX();
            return true;
        }
        return false;
    }
}
