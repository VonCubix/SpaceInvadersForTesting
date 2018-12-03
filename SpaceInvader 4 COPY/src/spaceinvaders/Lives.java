/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

/**
 *
 * @author cstuser
 */
public class Lives extends GameObject{   
    public Lives(Vector2D position, Vector2D velocity, double height, double width)
    {
        super(position, velocity, new Vector2D(0,0), height, width);
        rectangle.setFill(AssetManager.getHeartImage());
    }
   
}