package entity;


import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import tile.Grave;
import tile.MapManager;

public class Player extends Entity implements MouseMotionListener,MouseListener {
    GamePanel gp;
    KeyHandler keyH;
    MapManager map;
    Square hitbox;
    ArrayList<Grave> g;
    
    int mouseX, mouseY;
    BufferedImage img = null;
    BufferedImage[] animations;
    int aniTick=0,aniIndex=0,aniSpeed = 17;
    int pixelMC = 32;
    public int health = 3;
    boolean []direction_collision = {true,true,true,true};

    long lastShotTime = 0;
    int shootingCooldown = 500;

    public Player(GamePanel gp, KeyHandler keyH, MapManager map) {
        this.gp = gp;
        this.keyH = keyH;
        this.map = map;
        setDefaultValues();
        getPlayerImage();
        gp.addMouseMotionListener(this);
        gp.addMouseListener(this);
        img = down;

        this.hitbox = new Square(x+ pixelMC , y +pixelMC , width, width);
    }
    
    public void setDefaultValues(){
        x = gp.screenWidth/2;
        y = gp.screenHeight/2;
        width = 32;
        height = 32; 

        speed = 3.5;
        direction="down";
    }
    public void loadAnimations(){
        if(img !=null){
            animations = new BufferedImage[5];
            for(int i=0;i < animations.length; i++)
                animations[i] = img.getSubimage(i*32, 0, 32, 32);
        }
    }

    public void updateAnimationTick(){
        aniTick++;
        if(aniTick>=aniSpeed){
            aniTick = 0;
            aniIndex++;
            if(aniIndex >= animations.length) 
                aniIndex = 0;
        }
    }
    public void getPlayerImage(){
        try{
            up = ImageIO.read(getClass().getResourceAsStream("/res/player/cowboy_attack_up_spritesheet.png"));
            down = ImageIO.read(getClass().getResourceAsStream("/res/player/cowboy_attack_down_spritesheet.png"));
            left = ImageIO.read(getClass().getResourceAsStream("/res/player/cowboy_attack_left_spritesheet.png"));
            right = ImageIO.read(getClass().getResourceAsStream("/res/player/cowboy_attack_right_spritesheet.png"));
         }catch(IOException e){
            e.printStackTrace();
            
         }
    }
    
    public void notcollision(){
        int playerX = this.getplayerX();
        int playerY = this.getplayerY();
        for (Grave grave : g) {
            int graveX = grave.getX();
            int graveY = grave.getY();
            Square graveHitbox = grave.getHitbox();  
            Square playerHitbox = this.getHitbox();
            
    
            if (playerHitbox.intersects(graveHitbox)) {
                // System.out.println("Collision detected with grave!");
                double angle = Math.atan2(graveY - playerY - 32, graveX - playerX - 32);
                
                if (Math.abs(angle) < Math.PI / 4) { // right
                    direction_collision[0] = false;
                } else if (Math.abs(angle) > Math.PI * 3 / 4) {  //left
                    direction_collision[1] = false;
                } else if (angle < 0) {  // up
                    direction_collision[2] = false;
                } else {  // down
                    direction_collision[3] = false;
                }
            }
        }
    }
    public void update(){
        //reset direction_collision
        for(int i=0;i<4;i++){
            direction_collision[i] = true;
        }
        notcollision();
        
        if(keyH.rightPressed == true && x < gp.screenWidth - pixelMC*2 && direction_collision[0]){
            x += speed;
        }
        if(keyH.leftPressed == true && x > - pixelMC && direction_collision[1]){
            x -= speed;
        }
        if(keyH.upPressed == true && y > - pixelMC && direction_collision[2]){
            y -= speed;
        }
        if(keyH.downPressed == true && y < gp.screenHeight - pixelMC*2 && direction_collision[3]){
            y += speed;
        }
        
        hitbox.setPosition(x+ pixelMC, y+ pixelMC);
        
        loadAnimations();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        hitbox.setPosition(x, y); // อัพเดตตำแหน่งของ hitbox ด้วย
    }

    public Square getHitbox(){
        return hitbox;
    }
    public int getLife(){
        return health;
    }
    public int getplayerX(){
        return x;
    }
    public int getplayerY(){
        return y;
    }

    public void setgrave_hitbox(int mapnum){
        map.ChangeMap(mapnum);
        g = (ArrayList)map.graves.clone();
        System.out.println(g);
        for(Grave g : g){
            System.out.println("grave"+g.getX()+" "+g.getY());
        }
    }
    public void draw(Graphics2D g2){
        
        if (img == null) {
            System.out.println("Failed to load sprite sheet!");
                return; 
        }
        if (animations == null) {
            loadAnimations();
        }
        updateAnimationTick();
        
        g2.drawImage(animations[aniIndex],x,y,gp.tileSize*3,gp.tileSize*3,null);
        hitbox.drawRectangle(g2,32,32);
        for (Grave g : g) {
            g.getHitbox().drawRectangle(g2, 32, 32);
        }
    }

    @Override
    public void takeDamage() {
        health -= 1;
        System.out.println("Player health: " + health);
        if (health <= 0) {
            System.out.println("Player is dead!");
            // ทำการหยุดเกมหรือรีเซ็ตสถานะของเกมได้ที่นี่
            gp.gameState = gp.gameoverState;
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        double angle = Math.atan2(mouseY - y - pixelMC, mouseX - x - pixelMC); // Math.atan2 = find radians
        if (Math.abs(angle) < Math.PI / 4) { // angle < 45°
            img = right;
            direction = "right";
        } else if (Math.abs(angle) > Math.PI * 3 / 4) {  // angle > 135°
            img = left;
            direction = "left";
        } else if (angle < 0) {
            img = up;
            direction = "up";
        } else {
            img = down;
            direction = "down";
        }
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        long currentTime = System.currentTimeMillis();

        // Check if the cooldown period has passed
        if (currentTime - lastShotTime >= shootingCooldown) {
            gp.fireBullet(x + pixelMC, y + pixelMC, direction);

            lastShotTime = currentTime;

            System.out.println("Bullet fired.");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}


