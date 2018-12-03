/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author Family Desktop
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    AnchorPane pane;

    //Elements in the prompt for Victory--------
    @FXML
    Pane paneVictory;

    @FXML
    Button buttonCloseVic;

    @FXML
    Label labelVictory;

    //------------------
    //Elements in the prompt for defeat--------
    @FXML
    Pane paneDefeat;

    @FXML
    Button buttonCloseDef;

    @FXML
    Label labelDefeat;

    //-----------------
    @FXML
    Button buttonNewGame;

    @FXML
    private Label MouseXvalue;

    @FXML
    ImageView life1;

    @FXML
    ImageView life2;

    @FXML
    ImageView life3;
    
    @FXML
    Label labelScore;

    private ArrayList<GameObject> objectList = new ArrayList<>();
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private ArrayList<ProjectileEnemy> projectiles_enemy = new ArrayList<>();
    private ArrayList<Lives> arrListHearts = new ArrayList<>();
    private Enemies[][] enemies = new Enemies[5][9];
    private ArrayList<Shield> shield = new ArrayList<>();
    private SpaceShip_Main SpaceShip = null;

    //Space ship Projectile Management
    public int projectile_count = 0;
    final float PROJECTILE_COOLDOWN = 1.0f;
    float nextProjectileTime = 0.0f;

    private double lastFrameTime = 0.0;

    //Enemy Projectile Management
    public int projectile_enemy_count = 0;
    final float PROJECTILE_COOLDOWN_ENEMY = 3.0f;
    float nextEnemyProjectileTime = 2.0f;
    boolean[] ifRowIsFull = new boolean[8];
    boolean isReadyToFire = true;
    boolean[] isAlive = new boolean[32];
    boolean hasWon = false;
    int numberOfEnemies = -1;
    public List<Point2D> readyToShootAliens_x_y = new ArrayList<Point2D>();

    //boolean value used for when the user wins
    public boolean gameWon = false;
    public boolean gameLost = false;

    public int spaceship_Life = 3;

    public int dead_enemies = 0;

    public void addToPane(Node node) {
        pane.getChildren().add(node);
    }

    public void removeFromPane(GameObject node) {
        pane.getChildren().remove(node);
    }

    //This method is called when the player wins
    public void victory() throws Exception {
        try {
     
            paneVictory.setBackground(AssetManager.getBackgroundImageVictory());
            paneVictory.setVisible(true);
            buttonCloseVic.setDisable(false);
            gameWon = true;
        } catch (Exception e) {
            throw e;
        }
    }

    public void defeat() throws Exception {
        try {         
            
            paneDefeat.setBackground(AssetManager.getBackgroundImageVictory());
            paneDefeat.setVisible(true);
            buttonCloseDef.setDisable(false);
            gameLost = true;
        } catch (Exception e) {
            throw e;
        }
    }

    
    @FXML
    public void closeButtonAction(Event event) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }
   
    @FXML
    public void mouseClickedShoot(MouseEvent event) {
        if (nextProjectileTime <= 0.0f) {
            nextProjectileTime = PROJECTILE_COOLDOWN;
            if (projectile_count < 0) {
                projectile_count = 0;
            }

            //Center the Projectile
            double prj_posX = SpaceShip.getPosition().getX() + 10;
            double prj_posY = SpaceShip.getPosition().getY() + 50;

            Vector2D init_position = new Vector2D(prj_posX, prj_posY);
            Vector2D velocity = new Vector2D(0, -600);

            AssetManager.getShootingSound().play();
            projectiles.add(new Projectile(init_position, velocity, 10, 30));
            addToPane(projectiles.get(projectile_count).getRectangle());
            objectList.add(projectiles.get(projectile_count));
        }
    }

    //Make the aliens shoot
    public void enemyShoot() {
        nextEnemyProjectileTime = PROJECTILE_COOLDOWN_ENEMY;

        if (projectile_enemy_count < 0) {
            projectile_enemy_count = 0;
        }

        for (int i = 0; i < 8; i++) {

            if (enemies[enemies.length - 2][i] != null) {
                ifRowIsFull[i] = true;

            } else {
                ifRowIsFull[i] = false;
            }
        }

        for (int i = 0; i < ifRowIsFull.length; i++) {

            if (ifRowIsFull[i] == false) {
                isReadyToFire = false;

            }
        }

        if (isReadyToFire) {

            Random rand = new Random();
            int position_x = rand.nextInt(7);

            double prj_posX = enemies[enemies.length - 2][position_x].getPosition().getX() + 25;
            double prj_posY = enemies[enemies.length - 2][position_x].getPosition().getY() + 5;

            //Center the Projectile
            Vector2D init_position = new Vector2D(prj_posX, prj_posY);
            Vector2D velocity = new Vector2D(0, 400);

            AssetManager.getShootingSound().play();
            projectiles_enemy.add(new ProjectileEnemy(init_position, velocity, 10, 30));
            addToPane(projectiles_enemy.get(projectile_enemy_count).getRectangle());
            objectList.add(projectiles_enemy.get(projectile_enemy_count));

        } else if (isReadyToFire == false) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 8; j++) {

                    if (enemies[i][j] != null && enemies[i + 1][j] == null) {

                        readyToShootAliens_x_y.add(new Point2D.Double(i, j));

                    }

                }
            }
            Random rand = new Random();
            int random_enemy = rand.nextInt(readyToShootAliens_x_y.size());

            double prj_posX = enemies[(int) readyToShootAliens_x_y.get(random_enemy).getX()][(int) readyToShootAliens_x_y.get(random_enemy).getY()].getPosition().getX() + 25;
            double prj_posY = enemies[(int) readyToShootAliens_x_y.get(random_enemy).getX()][(int) readyToShootAliens_x_y.get(random_enemy).getY()].getPosition().getY() + 5;

            //Center the Projectile
            Vector2D init_position = new Vector2D(prj_posX, prj_posY);
            Vector2D velocity = new Vector2D(0, 400);

            AssetManager.getShootingSound().play();
            projectiles_enemy.add(new ProjectileEnemy(init_position, velocity, 10, 30));
            addToPane(projectiles_enemy.get(projectile_enemy_count).getRectangle());
            objectList.add(projectiles_enemy.get(projectile_enemy_count));

        }

    }

    @FXML
    public void mouseMoved(MouseEvent event) {
        double x = event.getSceneX();

        SpaceShip.setPosition(new Vector2D(event.getX(), 500));
    }

    public Enemies getRightMostEnemy() throws Exception {
        try {
            for (int c = 8; c >= 0; c--) {
                for (int r = 4; r >= 0; r--) {
                    if (enemies[r][c] != null) {
                        return enemies[r][c];
                    }
                }
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public Enemies getLeftMostEnemy() throws Exception {
        try {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 8; j++) {
                    if (enemies[i][j] != null) {
                        return enemies[i][j];
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lastFrameTime = 0.0f;
        long initialTime = System.nanoTime();

        AssetManager.preloadAllAssets();

        arrListHearts.add(new Lives(new Vector2D(680, 5), new Vector2D(0,0), 50, 50));
        arrListHearts.add(new Lives(new Vector2D(730, 5), new Vector2D(0,0), 50, 50));
        arrListHearts.add(new Lives(new Vector2D(780, 5), new Vector2D(0,0), 50, 50));
        
        

        //Set Music
        Media media = AssetManager.getBackgroundMusic();
        MediaPlayer mplayer = new MediaPlayer(media);
        mplayer.setVolume(0.2);
        mplayer.play();

        //Create and Initialize all objects
        SpaceShip = new SpaceShip_Main(new Vector2D(350, 500), new Vector2D(0, 0), 100, 100);
        shield.add(new Shield(new Vector2D(70, 410), 120, 40));
        shield.add(new Shield(new Vector2D(370, 410), 120, 40));
        shield.add(new Shield(new Vector2D(670, 410), 120, 40));

        int x_pos = 10;
        int y_pos = 20;

        for (int i = 0; i < 4; i++) {
            x_pos = 10;
            y_pos += 80;
            for (int j = 0; j < 8; j++) {

                enemies[i][j] = new Enemies(new Vector2D(x_pos += 70, y_pos), new Vector2D(20, 0), 40, 40, i);
            }
        }

        //Add background
        pane.setBackground(AssetManager.getBackgroundImage());
        
        //Add to the AnchorPane
        addToPane(arrListHearts.get(0).getRectangle());
        addToPane(arrListHearts.get(1).getRectangle());
        addToPane(arrListHearts.get(2).getRectangle());
        
        addToPane(SpaceShip.getRectangle());
        addToPane(shield.get(0).getRectangle());
        addToPane(shield.get(1).getRectangle());
        addToPane(shield.get(2).getRectangle());

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                addToPane(enemies[i][j].getRectangle());
            }
        }

        //Add to the list of all Objects in the scene
        objectList.add(SpaceShip);
        objectList.add(shield.get(0));
        objectList.add(shield.get(1));
        objectList.add(shield.get(2));
        
        objectList.add(arrListHearts.get(0));
        objectList.add(arrListHearts.get(1));
        objectList.add(arrListHearts.get(2));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                objectList.add(enemies[i][j]);
            }
        }

        new AnimationTimer() {
            @Override
            public void handle(long now) {

                // Time calculation                
                double currentTime = (now - initialTime) / 1000000000.0;
                double frameDeltaTime = currentTime - lastFrameTime;
                lastFrameTime = currentTime;

                nextProjectileTime -= frameDeltaTime;
                nextEnemyProjectileTime -= frameDeltaTime;

                for (GameObject obj : objectList) {
                    obj.update(frameDeltaTime);
                }

                //Animate Enemy Movement
                try {
                    if (getRightMostEnemy().getPosition().getX() >= 750) {
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 8; j++) {
                                if (enemies[i][j] != null) {
                                    enemies[i][j].setPosition(new Vector2D(enemies[i][j].getPosition().getX(), enemies[i][j].getPosition().getY()+5));
                                    enemies[i][j].setVelocity(new Vector2D(-30, 0));
                                }
                            }
                        }

                    } else if (getLeftMostEnemy().getPosition().getX() <= 10) {
                          for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 8; j++) {
                                if(enemies[i][j] != null)
                                {
                                    enemies[i][j].setPosition(new Vector2D(enemies[i][j].getPosition().getX(), enemies[i][j].getPosition().getY()+5));
                                    enemies[i][j].setVelocity(new Vector2D(30, 0));
                                }                            
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Make Enemy shoot
                if (nextEnemyProjectileTime <= 0.0f) {

                    enemyShoot();

                }

                //Collison Shield/Projectile            
                for (int i = 0; i < projectiles.size(); i++) {

                    Rectangle prj_rec = projectiles.get(i).getRectangle();
                    Bounds prj_rec_Bounds = prj_rec.getBoundsInParent();

                    Rectangle shield_rec1 = shield.get(0).getRectangle();
                    Bounds shield_rec_Bounds1 = shield_rec1.getBoundsInParent();

                    Rectangle shield_rec2 = shield.get(1).getRectangle();
                    Bounds shield_rec_Bounds2 = shield_rec2.getBoundsInParent();

                    Rectangle shield_rec3 = shield.get(2).getRectangle();
                    Bounds shield_rec_Bounds3 = shield_rec3.getBoundsInParent();

                    if (prj_rec_Bounds.intersects(shield_rec_Bounds1) || prj_rec_Bounds.intersects(shield_rec_Bounds2) || prj_rec_Bounds.intersects(shield_rec_Bounds3)) {

                        AssetManager.getEnnemyHitSound().play();

                        pane.getChildren().remove(projectiles.get(i).getRectangle());
                        projectiles.remove(i);

                    } else if (prj_rec.getY() + prj_rec.getHeight() > pane.getHeight() || prj_rec.getY() < 0) {
                        pane.getChildren().remove(projectiles.get(i).getRectangle());
                        projectiles.remove(i);

                    }

                }

                //Collision Projectile/Enemy
                for (int i = 0; i < projectiles.size(); i++) {

                    Rectangle prj_rec = projectiles.get(i).getRectangle();
                    Bounds prj_rec_Bounds = prj_rec.getBoundsInParent();

                    for (int j = 0; j < 4; j++) {
                        for (int k = 0; k < 8; k++) {
                            if (enemies[j][k] != null) {
                                Rectangle enemy_rec = enemies[j][k].getRectangle();
                                Bounds enemy_rec_Bounds = enemy_rec.getBoundsInParent();

                                if (prj_rec_Bounds.intersects(enemy_rec_Bounds)) {

                                    pane.getChildren().remove(enemies[j][k].getRectangle());
                                    enemies[j][k] = null;
                                    AssetManager.getEnnemyHitSound2().play();

                                    dead_enemies++;
                                    labelScore.setText("Score: " + dead_enemies);
                                    System.out.println("" + dead_enemies);

                                    pane.getChildren().remove(projectiles.get(i).getRectangle());
                                    projectiles.remove(i);

                                    //Detect If You Win                                 
                                    if (dead_enemies >= 32) {
                                        try {
                                            this.stop();
                                            victory();
                                            
                                        } catch (Exception ex) {
                                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }

                                } else if (prj_rec.getY() + prj_rec.getHeight() > pane.getHeight() || prj_rec.getY() < 0) {
                                    pane.getChildren().remove(projectiles.get(i).getRectangle());
                                    projectiles.remove(i);
                                }

                            }
                        }
                    }
                }

                //Collision Projectile_Enemy/Shield               
                for (int i = 0; i < projectiles_enemy.size(); i++) {

                    Rectangle prj_rec = projectiles_enemy.get(i).getRectangle();
                    Bounds prj_rec_Bounds = prj_rec.getBoundsInParent();

                    Rectangle shield_rec1 = shield.get(0).getRectangle();
                    Bounds shield_rec_Bounds1 = shield_rec1.getBoundsInParent();

                    Rectangle shield_rec2 = shield.get(1).getRectangle();
                    Bounds shield_rec_Bounds2 = shield_rec2.getBoundsInParent();

                    Rectangle shield_rec3 = shield.get(2).getRectangle();
                    Bounds shield_rec_Bounds3 = shield_rec3.getBoundsInParent();

                    if (prj_rec_Bounds.intersects(shield_rec_Bounds1) || prj_rec_Bounds.intersects(shield_rec_Bounds2) || prj_rec_Bounds.intersects(shield_rec_Bounds3)) {

                        AssetManager.getEnnemyHitSound().play();

                        pane.getChildren().remove(projectiles_enemy.get(i).getRectangle());
                        projectiles_enemy.remove(i);

                    } else if (prj_rec.getY() + prj_rec.getHeight() > pane.getHeight() || prj_rec.getY() < 0) {
                        pane.getChildren().remove(projectiles_enemy.get(i).getRectangle());
                        projectiles_enemy.remove(i);
                    }

                }

                //Collision Projectile_Enemy/Spaceship
                for (int i = 0; i < projectiles_enemy.size(); i++) {

                    Rectangle prj_rec = projectiles_enemy.get(i).getRectangle();
                    Bounds prj_rec_Bounds = prj_rec.getBoundsInParent();

                    if (SpaceShip != null) {
                        Rectangle enemy_rec = SpaceShip.getRectangle();
                        Bounds enemy_rec_Bounds = enemy_rec.getBoundsInParent();

                        if (prj_rec_Bounds.intersects(enemy_rec_Bounds)) {

                            pane.getChildren().remove(SpaceShip.getRectangle());
                            SpaceShip = null;

                            //Decrease lives
                            spaceship_Life--;

                            pane.getChildren().remove(projectiles_enemy.get(i).getRectangle());
                            projectiles_enemy.remove(i);

                            //Respawn Spaceship
                            SpaceShip = new SpaceShip_Main(new Vector2D(10, 500), new Vector2D(0, 0), 100, 100);
                            addToPane(SpaceShip.getRectangle());
                            objectList.add(SpaceShip);

                            //Count the number of lives
                            if (spaceship_Life == 2) {
                                arrListHearts.get(0).getRectangle().setVisible(false);
                            }

                            if (spaceship_Life == 1) {
                                arrListHearts.get(1).getRectangle().setVisible(false);
                            }

                            if (spaceship_Life == 0) {
                                arrListHearts.get(2).getRectangle().setVisible(false);

                                try {
                                    
                                    defeat();
                                    
                                    for (int j = 0; j < 4; j++) {
                                        for (int k = 0; k < 8; k++) {
                                            
                                            pane.getChildren().remove(enemies[j][k].getRectangle());
                                            enemies[j][k] = null;
                                        }
                                    }
                                    
                                    for (int j = 0; j < shield.size(); j++) {
                                        
                                        pane.getChildren().remove(shield.get(j).getRectangle());
                                        shield.remove(j);
                                    }
                                    
                                    SpaceShip = null;
                                    
                                    pane.getChildren().remove(SpaceShip.getRectangle());
                                    
                                    for (int j = 0; j < objectList.size(); j++) {
                                        //removeFromPane(objectList.get(j));
                                        pane.getChildren().removeAll(objectList);
                                        //pane.getChildren().remove(objectList.get(j));                                        
                                        
                                    }
                                    
                                    this.stop();
                                    
                                } catch (Exception ex) {
                                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        } else if (prj_rec.getY() + prj_rec.getHeight() > pane.getHeight() || prj_rec.getY() < 0) {
                            pane.getChildren().remove(projectiles_enemy.get(i).getRectangle());
                            projectiles_enemy.remove(i);
                        }

                    }

                }

            }

        }.start();
    }
}
