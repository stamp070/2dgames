package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import entity.Bullet;
import entity.Ghost;
import entity.Player;
import object.OBJ_heart;
import tile.MapManager;


public class GamePanel extends JPanel implements Runnable{
    final int originalTileSize = 16;
    final int scale = 2;
    
    public final int tileSize = originalTileSize * scale; 
    public final int maxScreenCol = 30;
    public final int maxScreenRow = 21;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    
    int wavemonster = 1;
    int survive_time = 0;

    int FPS = 60;

    public int gameState = 0;
    public final int titleState = 0;
    public final int choosemapState = 1;
    public final int playState = 2;
    public final int gameoverState = 3;
    
    OBJ_heart heart = new OBJ_heart(this);
    MapManager map = new MapManager(this,0);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this,keyH,map);

    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Ghost> ghosts = new ArrayList<>();

    // Button
    JButton playButton;
    JButton retryButton;
    JButton map1Button;
    JButton map2Button;
    JButton map3Button;
    
    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        
    }
    public void setupgame(){
        gameState = playState;
    }
    
    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        
        while(gameThread != null){
            
            currentTime = System.nanoTime();
            
            delta += (currentTime - lastTime) / drawInterval;
            timer += ((currentTime - lastTime));
            lastTime = currentTime;
            
            if(delta >- 1){
                update();
                repaint();
                delta--;
                drawCount++;
            }
            
            if(timer >=1000000000){
                System.out.println("FPS: "+drawCount);
                drawCount = 0;
                timer = 0;
                for(Ghost ghost: ghosts){
                    System.out.println("playerX:"+player.getplayerX()+" playerY:"+player.getplayerY()+" ghostX:"+ghost.getGhostX()+" ghostY:"+ghost.getGhostY());
                }
                survive_time++;
            }
        }
    }
    
    public void update(){
        if(gameState == playState){
            player.update();
            
            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                bullet.update();

                // Remove bullets that are out of screen bounds
                if (bullet.isOffScreen()) {
                    bullets.remove(i);
                    i--; // Adjust index after removing an element
                }
            }
            
            for (int i = 0; i < ghosts.size(); i++) {
                Ghost ghost = ghosts.get(i);
                if (!ghost.isAlive) {
                    ghosts.remove(i);
                    i--;
                } else {
                    // เช็คว่าผีถูกยิงหรือไม่
                    for (Bullet bullet : bullets) {
                        // ถ้ากระสุนชนกับผี ให้ลดเลือดของผี
                        if ((bullet.getbulletX() <= ghost.x+48 && bullet.getbulletX() >= ghost.x) && (bullet.getbulletY() <= ghost.y+48 && bullet.getbulletY() >= ghost.y)) {
                            ghost.takeDamage();
                            bullets.remove(bullet);
                            break; // ออกจากลูปเมื่อยิงผี
                        }
                    }
                    
                }
            }
            
            for (Ghost ghost : ghosts) {
                // Check collision
                if (player.getHitbox().intersects(ghost.getHitbox())) {
                    player.takeDamage();  
                    System.out.println("Collision detected with ghost!");
                    
                    int playerX = player.getplayerX();
                    int playerY = player.getplayerY();
                    int ghostX = ghost.getGhostX();
                    int ghostY = ghost.getGhostY();
                    
                    int knockbackDistance = 30;
                    
                    double angle = Math.atan2(ghostY - playerY - 32, ghostX - playerX - 32);
                    System.out.println(angle);
                    
                    if (Math.abs(angle) < Math.PI / 4) { 
                        player.setPosition(playerX - knockbackDistance, playerY);
                    } else if (Math.abs(angle) > Math.PI * 3 / 4) {  
                        player.setPosition(playerX + knockbackDistance, playerY);
                    } else if (angle < 0) {  
                        player.setPosition(playerX, playerY + knockbackDistance);
                    } else {  
                        player.setPosition(playerX, playerY - knockbackDistance);
                    }
                }
            }
            
            if (ghosts.isEmpty()) {
                startNewWave(); // เริ่ม Wave ใหม่เมื่อไม่มีผีเหลืออยู่
            }
            if(survive_time <= 30){
                wavemonster = 1;
            }else if(survive_time <=100){
                wavemonster = 2;
            }else if(survive_time <=200){
                wavemonster = 4;
            }
            else if(survive_time <=400){
                wavemonster = 6;
            }
            repaint();
            }
    }
    @Override
    public void paintComponent(Graphics g){
        
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D)g;

        //title screen
        if(gameState== titleState){
            drawTitleScreen(g2);
        }
        //choose map
        if(gameState== choosemapState){
            drawChooseMapScreen(g2);
        }
        //play screen
        if(gameState== playState){
            drawPlayScreen(g2);
        }
        //game over
        if(gameState== gameoverState){
            drawGameover(g2);
        }

        repaint();
    }
    public void drawPlayerLife(Graphics2D g2){
        int x = this.tileSize/2;
        int y = this.tileSize;
        int i = 0;

        while(i<3){
            g2.drawImage(heart.heart_blank,x,y,null);
            i++;
            x+=this.tileSize;
        }

        //reset;
        x = this.tileSize/2;
        y = this.tileSize;
        i=0;
        
        //Draw current life
        while(i<player.health){
            g2.drawImage(heart.heart_full, x, y, null);
            i++;
            x+=this.tileSize;
        }
    }
    public void drawTimer(Graphics2D g2){
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 30));
        g2.setColor(Color.WHITE);
        g2.drawString("Timer : ", 750, 64);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 30));
        g2.drawString(""+survive_time, 890, 64);
    }
    
    public void drawPlayScreen(Graphics2D g2){
        map.draw(g2);

        drawTimer(g2);
        
        ArrayList<Bullet> bulletsCopy = new ArrayList<>(bullets);
        ArrayList<Ghost> ghostsCopy = new ArrayList<>(ghosts);

        for (Bullet bullet : bulletsCopy) {
            bullet.draw(g2);
        }

        for (Ghost ghost : ghostsCopy) {
            ghost.draw(g2);
        }
        
        player.draw(g2);

        drawPlayerLife(g2);

        g2.dispose();
    }

    public void drawTitleScreen(Graphics2D g2) {
        BufferedImage bg = null;
        BufferedImage playerimg = null;
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/res/bg/bg.png"));
            playerimg = ImageIO.read(getClass().getResourceAsStream("/res/player/cowboy_attack_down_spritesheet.png"));
        } catch (Exception e) {
            System.err.println("Error loading title screen or player image: " + e.getMessage());
        }
    
        g2.drawImage(bg, 0, 0, null);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 96F));
        g2.setColor(Color.WHITE);
        g2.drawString("Jack is Cowboy", 130, 150);
        g2.drawImage(playerimg.getSubimage(0, 0, 32, 32), 360 ,180 , tileSize*8 ,tileSize*8 , null);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 50F));
    
        if (gameState == titleState && playButton == null) { 
            playButton = new JButton("Play Game");
            playButton.setFont(new Font("Arial", Font.BOLD, 30));
            playButton.setBounds(390, 450, 200, 60); 
            playButton.setFocusable(false);
            playButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    survive_time=0;
                    gameState = choosemapState;  // เปลี่ยนสถานะเกมเป็นเล่นเกม
                    remove(playButton);     // ลบปุ่มออกจากหน้าจอ
                    playButton = null;      // ตั้งค่า playButton ให้เป็น null
                    revalidate();           // อัปเดตเลย์เอาท์ใหม่
                    repaint();              // วาดหน้าจอใหม่
                    requestFocusInWindow();
                }
            });
            setLayout(null); 
            add(playButton); // เพิ่มปุ่มในหน้าจอ
        }
    }

    public void drawChooseMapScreen(Graphics2D g2){
        BufferedImage bg = null;
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/res/bg/bg.png"));
        } catch (Exception e) {
            System.err.println("Error loading title screen or player image: " + e.getMessage());
        }
        g2.drawImage(bg, 0, 0, null);

        if (gameState == choosemapState && map1Button == null && map2Button == null  ) {
            map1Button = new JButton("Grass Field");
            map1Button.setFont(new Font("Arial", Font.BOLD, 30));
            map1Button.setBounds(390, 200, 200, 60); 
            map1Button.setFocusable(false);
            map1Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    map.ChangeMap(0);
                    player.setgrave_hitbox(0);
                    
                    survive_time=0;
                    gameState = playState; 
                    remove(map1Button);     
                    remove(map2Button);     
                    remove(map3Button);     
                    
                    map1Button = null;      
                    map2Button = null;     
                    map3Button = null;     
                    revalidate();          
                    repaint();             
                    requestFocusInWindow();
                }
            });
            map2Button = new JButton("The Graveyard 1");
            map2Button.setFont(new Font("Arial", Font.BOLD, 30));
            map2Button.setBounds(350, 300, 275, 60); 
            map2Button.setFocusable(false);
            map2Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    map.ChangeMap(1);
                    player.setgrave_hitbox(1);

                    survive_time=0;
                    gameState = playState;
                    remove(map1Button);     
                    remove(map2Button);     
                    remove(map3Button);     
                    
                    map1Button = null;      
                    map2Button = null;     
                    map3Button = null;     
                    revalidate();           
                    repaint();              
                    requestFocusInWindow();
                }
            });
            
            map3Button = new JButton("The Graveyard 2");
            map3Button.setFont(new Font("Arial", Font.BOLD, 30));
            map3Button.setBounds(350, 400, 275, 60); 
            map3Button.setFocusable(false);
            map3Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    map.ChangeMap(1);
                    player.setgrave_hitbox(2);

                    survive_time=0;
                    gameState = playState;
                    remove(map1Button);     
                    remove(map2Button);     
                    remove(map3Button);     
                    
                    map1Button = null;      
                    map2Button = null;     
                    map3Button = null;     
                    revalidate();           
                    repaint();              
                    requestFocusInWindow();
                }
            });
            setLayout(null); 
            add(map1Button); 
            add(map2Button); 
            add(map3Button); 
        }
        
    }
    
    public void drawGameover(Graphics2D g2){
        for(Ghost ghost: ghosts){
            ghost.setRandomSpawnLocation();
        }
        BufferedImage bg = null;
        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/res/bg/bg.png"));

        } catch (Exception e) {
            System.err.println("Error loading title screen or player image: " + e.getMessage());
        }
        g2.drawImage(bg, 0, 0, null);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 96F));
        g2.setColor(Color.WHITE);
        g2.drawString("😭 Game Over 😭", 105, 150);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD , 50F));

        if (gameState == gameoverState && retryButton == null) {
            retryButton = new JButton("Retry");
            retryButton.setFont(new Font("Arial", Font.BOLD, 30));
            retryButton.setBounds(380, 380, 200, 60); 
            retryButton.setFocusable(false);
            retryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    survive_time=0;
                    gameState = playState;
                    player.health = 3;
                    remove(retryButton);
                    retryButton = null;
                    revalidate();
                    repaint();
                    requestFocusInWindow();
                    
                }
                
            });
            setLayout(null); 
            add(retryButton); // เพิ่มปุ่มในหน้าจอ
        }
    }
    public void fireBullet(int x,int y,String direction){
        bullets.add(new Bullet(this ,x,y,direction));
    }
    public void startNewWave() {
        for (int i = 0; i < wavemonster; i++) { // Number of ghosts per wave
            Ghost ghost = new Ghost(this, player);
            ghosts.add(ghost);
            Thread ghostThread = new Thread(ghost);
            ghostThread.start(); // Start the ghost thread
        }
    }

}
