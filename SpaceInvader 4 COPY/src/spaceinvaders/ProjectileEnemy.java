/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

/**
 *
 * @author Family Desktop
 */
public class ProjectileEnemy extends GameObject{
      public ProjectileEnemy(Vector2D position, Vector2D velocity, double height, double width)
    {
        super(position, velocity, new Vector2D(0,0), height, width);
        rectangle.setFill(AssetManager.getProjectileImage_Enemy());
    }
}
