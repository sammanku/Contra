//Samarveer Manku
//Contra.java
//A simple Contra-style game with three weapons for the player and two types of enemies

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*; 
import java.io.*;

class Contra extends JFrame{
  GamePanel game= new GamePanel();

  public Contra(){
    super("Contra");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    add(game);
    pack();
    setVisible(true);

  }

  public static void main(String[] args) {
    Contra frame=new Contra();
  }
}

class GamePanel extends JPanel implements KeyListener, ActionListener,MouseListener,MouseMotionListener{
  int frame,screen,level;
  static BufferedImage mask;
  Point mouse;
  private Rectangle menuInstructions=new Rectangle(400,600,150,80), menuPlay=new Rectangle(200,600,150,80), backButton=new Rectangle(0,0,150,80);
  Image back;
  private boolean []keys;
  javax.swing.Timer myTimer;
  ArrayList<PUp>p;
  Player p1;
  ArrayList<Trooper>troopers;
  ArrayList<Turret>turrets;
  Door d;
  int offset;
  private static final int MENU=0,GAME=1,GAMEOVER=2,INSTRUCT=3;
  Font font=null;

  public GamePanel(){
    keys = new boolean[KeyEvent.KEY_LAST+1];
    screen=MENU;
    d=new Door(2507,349);
    level=0;
    back=new ImageIcon("menu.jpg").getImage();
    try {
      mask = ImageIO.read(new File("Mask1.png"));
    } 
    catch (IOException e) {
      System.out.println(e);
    }
    font=new Font("Comic Sans MS",Font.PLAIN,30);
    frame=0;
    setPreferredSize(new Dimension(800, 800));
    p1=new Player(300,0);
    turrets=loadTur("tur1.txt");
    troopers=loadTroop("Troop1.txt");
    p=loadPup("PUp1.txt");
    offset=0;
    myTimer = new javax.swing.Timer(15, this);
    myTimer.start();
    setFocusable(true);
    requestFocus();
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public ArrayList<Turret> loadTur(String name) {
     ArrayList<Turret>t= new ArrayList<Turret>();
     try{
      Scanner inFile = new Scanner(new File(name));
      while(inFile.hasNextInt()){
       int x = inFile.nextInt();
       int y = inFile.nextInt();
       int f = inFile.nextInt();
       t.add(new Turret(x,y,f));    
      }
     }
     catch(IOException ex){
      System.out.println(ex);
     }
     return t; 
    }
  
  public ArrayList<PUp> loadPup(String name) {
     ArrayList<PUp>t= new ArrayList<PUp>();
     try{
      Scanner inFile = new Scanner(new File(name));
      while(inFile.hasNextInt()){
       int x = inFile.nextInt();
       int y = inFile.nextInt();
       int w = inFile.nextInt();
       t.add(new PUp(x,y,w));    
      }
     }
     catch(IOException ex){
      System.out.println(ex);
     }
     return t; 
    }
  
  public ArrayList<Trooper> loadTroop(String name) {
     ArrayList<Trooper>t= new ArrayList<Trooper>();
     try{
      Scanner inFile = new Scanner(new File(name));
      while(inFile.hasNextInt()){
       int x = inFile.nextInt();
       int y = inFile.nextInt();
       t.add(new Trooper(x,y));    
      }
     }
     catch(IOException ex){
      System.out.println(ex);
     }
     return t; 
    }
  
 public static boolean clear(int x, int y){
    int WALL = 0xFFFF0000; 
    int wid = mask.getWidth(null);
    int hei = mask.getHeight(null);

    if(y<0){
      return true;
    }
    else if(x<0 || x>=wid || y>=hei){
      return false;
    }
    else{
      return mask.getRGB(x, y)!=WALL;
    }
  }
  
  private void move(){
    if(keys[KeyEvent.VK_UP] && p1.grounded){
      p1.jump();
    }
    if(keys[KeyEvent.VK_LEFT]){
      p1.move=true;
      p1.sprite=new ImageIcon("WalkLeft/WalkLeft"+frame/25+".png").getImage();
      if(p1.gx<=400 || p1.gx>d.dx-400){
        p1.move(-2);
      }
      else{
        p1.facing=0;
        offset-=2;
        p1.gx=p1.px+offset;
      }
    }
    else if(keys[KeyEvent.VK_RIGHT]){
      p1.move=true;
      p1.sprite=new ImageIcon("WalkRight/WalkRight"+frame/25+".png").getImage();
      if(p1.gx<400 || p1.gx>d.dx-400){
        p1.move(2);
      }
      else{
        p1.facing=1;
        offset+=2;
        p1.gx=p1.px+offset;
      }
    }
    else{
      p1.move=false;
    }
    p1.gravity();
  }

  private void reset(){
    if (level==1){
      try {
        mask = ImageIO.read(new File("Mask2.png"));
      } 
      catch (IOException e) {
        System.out.println(e);
      }
      offset=0;
      level=2;
      p1.px=300;
      p1.gx=p1.px;
      p1.py=300;
      turrets=loadTur("tur2.txt");
      p=loadPup("PUp2.txt");
      troopers=loadTroop("Troop2.txt");
      d=new Door(2668,434);
    }
    else if (level==2){
      try {
        mask = ImageIO.read(new File("Mask1.png"));
      } 
      catch (IOException e) {
        System.out.println(e);
      }
      offset=0;
      level=0;
      p1.score=0;
      p1.lives=5;
      p1.iFrames=0;
      p1.gunHeat=0;
      p1.bullets.clear();
      p1.fire.clear();
      p1.rockets.clear();
      p1.px=300;
      p1.gx=p1.px;
      p1.py=300;
      d=new Door(2507,349);
      turrets=loadTur("tur1.txt");
      p=loadPup("PUp1.txt");
      troopers=loadTroop("Troop1.txt");
      screen=MENU;
    }
  }
  
  public void update(){
    mouse = MouseInfo.getPointerInfo().getLocation();
    Point offset = getLocationOnScreen();
    mouse.translate(-offset.x, -offset.y);
  }
  
  @Override
 public void actionPerformed(ActionEvent e){
    System.out.println(mouse);
    if(p1.lives==0){
      offset=0;
      level=0;
      p1.score=0;
      p1.lives=5;
      p1.iFrames=0;
      p1.gunHeat=0;
      p1.bullets.clear();
      p1.fire.clear();
      p1.rockets.clear();
      p1.px=300;
      p1.gx=p1.px;
      p1.py=300;
      d=new Door(2507,349);
      turrets=loadTur("tur1.txt");
      p=loadPup("PUp1.txt");
      troopers=loadTroop("Troop1.txt");
      screen=GAMEOVER; 
    }
    if(level==1){
      back=new ImageIcon("Level1.png").getImage();
    }
    if(level==2){
      back=new ImageIcon("Level2.png").getImage();
    }
    frame++;
    if(frame==99){
      frame=0;
    }
    if(screen==GAME){
      if(d.collide(p1.px,p1.py)){
        reset();
      }
      d.offset(offset);
      p1.collide(new ArrayList<Bullet>());
      p1.shoot(keys[KeyEvent.VK_SPACE]);
      for(int i=0;i<p1.fire.size();i++){
        p1.fire.get(i).fade(p1.fire,p1);
      }
      for(int i=0;i<p.size();i++){
        p.get(i).offset(offset);
        if(p.get(i).collide(p1)){
          p1.weapon=p.get(i).weapon;
          p.remove(p.get(i));
        }
      }
      for(int i=0;i<troopers.size();i++){
        troopers.get(i).offset(offset);
        troopers.get(i).shoot(p1.px);
        troopers.get(i).move();
        troopers.get(i).collide(p1.rockets,p1.bullets,p1.fire);
        troopers.get(i).gravity();
        p1.collide(troopers.get(i).bullets);
        if(troopers.get(i).health<=0){
          troopers.remove(troopers.get(i));
          p1.score+=100;
        }
      }
      for(int i=0;i<turrets.size();i++){
        turrets.get(i).offset(offset);
        turrets.get(i).collide(p1.rockets,p1.bullets,p1.fire);
        turrets.get(i).shoot(p1.px,p1.py);
        p1.collide(turrets.get(i).bullets);
        if(turrets.get(i).health<=0){
          turrets.remove(turrets.get(i));
          p1.score+=200;
        }
      }
      move();
    }
    update();
    repaint();
 }

  @Override
 public void keyReleased(KeyEvent ke){
  int key = ke.getKeyCode();
  keys[key] = false;
 } 
 
 @Override
 public void keyPressed(KeyEvent ke){
  int key = ke.getKeyCode();
  keys[key] = true;
 }
 
 @Override
 public void keyTyped(KeyEvent ke){}
 
 @Override
 public void mouseDragged(MouseEvent e){
  mouse = e.getPoint();
}
 @Override
 public void mouseMoved(MouseEvent e){
  mouse = e.getPoint();
}  
 
 public void mouseClicked(MouseEvent e){}
 public void mouseEntered(MouseEvent e){}
 public void mouseExited(MouseEvent e){}
 @Override
 public void mousePressed(MouseEvent e){
   if(screen == MENU){//menu buttons
     if(menuInstructions.contains(mouse)){
       screen = INSTRUCT; 
     }
     if(menuPlay.contains(mouse)){
       level=1;
       screen = GAME;
     }
  }
   if(screen==INSTRUCT || screen==GAMEOVER){
     if(backButton.contains(mouse)){
       screen = MENU; 
     }
   }
 }
 
 public void mouseReleased(MouseEvent e){}
 
  public void paint(Graphics g){
    g.drawImage(back,0-offset,0,this);
    Graphics2D g2D = (Graphics2D)g;
    g2D.setFont(font);
    if(screen==GAME){
      for(int i=0;i<turrets.size();i++){
        turrets.get(i).draw(g);
      }
      p1.draw(g);
      for(int i=0;i<troopers.size();i++){
        troopers.get(i).draw(g);
      }
      for(int i=0;i<p.size();i++){
        p.get(i).draw(g);
      }
      d.draw(g);
    }
    else if(screen==MENU){
     back=new ImageIcon("menu.jpg").getImage();
     g.setColor(Color.ORANGE);
     g.fillRect(200,600,150,80);
     g.fillRect(400,600,150,80);
     g.setColor(Color.RED);
     g2D.drawString("Play",200,630);
     g2D.drawString("Instructions",400,630);
    }
    else if(screen==INSTRUCT){
      back=new ImageIcon("info.png").getImage();
      g.setColor(Color.ORANGE);
      g.fillRect(0,0,150,80);
      g.setColor(Color.RED);
      g2D.drawString("<--",0,30);
    }
    else if(screen==GAMEOVER){
      back=new ImageIcon("GameOver.png").getImage();
      g.setColor(Color.ORANGE);
      g.fillRect(0,0,150,80);
      g.setColor(Color.RED);
      g2D.drawString("<--",0,30);
    }
  }
}

class Door extends JPanel{//door to change level
 int dx,dy;
 Image sprite;
 Rectangle hitbox;
 public Door(int x, int y){
   dx=x;
   dy=y;
   sprite=new ImageIcon("door.png").getImage();
   hitbox=new Rectangle(dx,dy,16,25);
 }
 public void offset(int o){//adjust position on screen
   hitbox=new Rectangle(dx-o,dy,16,25);
 }
 public boolean collide(int x, int y){//check for collision with player
   if(hitbox.contains(x,y)){
    return true; 
   }
   else{
     return false;
   }
 }
 public void draw(Graphics g){
   g.drawImage(sprite,(int)hitbox.getX(),(int)hitbox.getY(),this);
 }
}

class Player extends JPanel implements KeyListener,ActionListener{
  int px,py,gx,vy,speed,facing,gunHeat,lives,iFrames,weapon,score,frame;
  Image sprite;
  private boolean[] keys;
  ArrayList<Bullet>bullets;
  ArrayList<Flame>fire;
  ArrayList<Rocket>rockets;
  Rectangle hitbox;
  boolean grounded,move;
  Font font=null;
  private final int LEFT=0, RIGHT=1,MAIN=2,BAZOOKA=3,FLAMETHROWER=4;

  public Player(int x, int y){
    keys = new boolean[KeyEvent.KEY_LAST+1];
    bullets=new ArrayList<Bullet>();
    fire=new ArrayList<Flame>();
    rockets=new ArrayList<Rocket>();
    px=x;
    gx=px;
    py=y;
    weapon=MAIN;
    frame=0;
    speed=0;
    score=0;
    lives=5;
    vy=0;
    facing=RIGHT;
    sprite=new ImageIcon("pRight.png").getImage();
    gunHeat=0;
    iFrames=0;
    hitbox=new Rectangle(px-10,py-10,20,20);
    grounded=false;
    move=false;
    font=new Font("Comic Sans MS",Font.PLAIN,10);
    setFocusable(true);
    requestFocus();
    addKeyListener(this);
  }
  
  public void collide(ArrayList<Bullet>enemyBullets){
    iFrames--;
    for(int i=0;i<enemyBullets.size();i++){
      Bullet currentBullet=enemyBullets.get(i);
      if( hitbox.contains(new Point(currentBullet.getX(),currentBullet.getY())) && iFrames<=0){
       enemyBullets.remove(enemyBullets.get(i));
       lives--;
       weapon=MAIN;
       iFrames=200;
      }
    }//handles collision with bullets
    if(!GamePanel.clear(gx,py+vy) && vy>0){
     grounded=true;
     gravity();
     vy=0;
    }
    else if(!GamePanel.clear(gx,py+vy) && vy<0){
      vy=-vy;
    }
    else if(!GamePanel.clear(gx+(int)(hitbox.getWidth()/2)+2,py)){
      if(gx>=400){
        px=400;
      }
    }
    else if(!GamePanel.clear(gx-(int)(hitbox.getWidth()/2)-2,py)){
      if(gx>=400){
        px=400;
      }
    }
    else{
      grounded=false;
    }
    hitbox=new Rectangle(px-10,py-10,20,20);
    //handles player-platform interaction
  }
  
   public void shoot(boolean shot){
    gunHeat--;
    if(gunHeat <= 0 && shot){
      if(weapon==MAIN){
        bullets.add(new Bullet(px,py,facing,4));
        gunHeat = 33;
      }
      else if(weapon==FLAMETHROWER){
        fire.add(new Flame(px,py,facing));
        gunHeat = 10;
      }
      else if(weapon==BAZOOKA){
        rockets.add(new Rocket(px,py,facing));
        gunHeat = 80;
      }
    }//handles shoot for each tye of weapon
    for(int i=0;i<bullets.size();i++){
      Bullet currentBullet=bullets.get(i);
      currentBullet.move();
      if(currentBullet.getX()>800 || currentBullet.getY()>800 || currentBullet.getX()<0 || currentBullet.getX()<0){
        bullets.remove(currentBullet);
      }
    }//moves the player's bullets
    for(int i=0;i<fire.size();i++){
      Flame f=fire.get(i);
      f.move();
      if(f.fx>800 || f.fy>800 || f.fx<0 || f.fy<0){
        fire.remove(f);
      }
    }//moves the fire trail
    for(int i=0;i<rockets.size();i++){
      rockets.get(i).move();
      if(rockets.get(i).rx>800 || rockets.get(i).ry>800 || rockets.get(i).rx<0 || rockets.get(i).ry<0){
        rockets.remove(rockets.get(i));
      }
    }//moves the rockets
  }
  
  public void move(int dir){//player movement
    speed=dir;
    if(dir/-1>0){
      facing=LEFT;
    }
    else{
      facing=RIGHT;
    }
    px+=dir;
    gx=px;
    hitbox=new Rectangle(px-10,py-10,20,20);//update the hitbox
  }

  public void gravity(){
    frame++;
    if(frame==100){
      frame=0;
    }
    if(py>800){
      py=-100;
      lives--;
      iFrames=100;
    }
    py+=vy;
    if(!move){
      if(facing==LEFT){
        sprite=new ImageIcon("pLeft.png").getImage();
      }
      else{
        sprite=new ImageIcon("pRight.png").getImage();
      }
    }
    if(grounded==false){
      if(facing==LEFT){
        sprite=new ImageIcon("airLeft/airLeft"+frame/25+".png").getImage();
      }
      else{
        sprite=new ImageIcon("airRight/airRight"+frame/25+".png").getImage();
      }
      vy+=1;
      if(vy>20){
        vy=20;
      }
    }
    else{
      vy=0;
    }
    hitbox=new Rectangle(px-10,py-10,20,20);//update the hitbox
  }

  public void jump(){
    grounded=false;
    vy=-20;
  }
  
  @Override
  public void actionPerformed(ActionEvent e){}
  
  @Override
  public void keyReleased(KeyEvent ke){
    int key = ke.getKeyCode();
    keys[key] = false;
  } 
  
  @Override
  public void keyPressed(KeyEvent ke){
    int key = ke.getKeyCode();
    keys[key] = true;
  }
  
  @Override
  public void keyTyped(KeyEvent ke){}
  
  public void draw(Graphics g){
    g.setColor(Color.RED);
    g.drawImage(sprite,(int)hitbox.getX(),(int)hitbox.getY()-12,this);
    Graphics2D g2D = (Graphics2D)g;
    g2D.setFont(font);
    g2D.drawString("Lives: "+lives,0,10);
    g2D.drawString("Score: "+score,0,40);
    if(iFrames>0){
      g2D.drawString(""+(double)iFrames/100,0,30);
    }
    if(weapon==MAIN){
      g2D.drawString("Weapon: gun",0,20);
      g.setColor(Color.YELLOW);
      for(int i=0;i<bullets.size();i++){
        bullets.get(i).draw(g);
      }
    }
    else if(weapon==FLAMETHROWER){
      g2D.drawString("Weapon: flamethrower",0,20);
      g.setColor(Color.ORANGE);
      for(int i=0;i<fire.size();i++){
        fire.get(i).draw(g);
      }
    }
    else if(weapon==BAZOOKA){
      g2D.drawString("Weapon: bazooka",0,20);
      g.setColor(Color.PINK);
      for(int i=0;i<rockets.size();i++){
        rockets.get(i).draw(g);
      }
    }
  }
}

class PUp extends JPanel{//power up
  private static final int BAZOOKA=3,FLAMETHROWER=4;//type of power up
  Image sprite;
  Rectangle hitbox;
  int px,py,weapon;
  public PUp(int x, int y, int w){
    sprite=new ImageIcon("PUp.png").getImage();
    px=x;
    py=y;
    weapon=w;
    hitbox=new Rectangle(px,py,20,20);
  }
  public void offset(int o){
    hitbox=new Rectangle(px-o,py,20,20);
  }
  public boolean collide(Player p){//set the player weapon equal to the power up weapon
    if(hitbox.contains(p.px,p.py)){
      p.weapon=weapon;
      return true;
    }
    else{
      return false;
    }
  }
  public void draw(Graphics g){
    g.drawImage(sprite,(int)hitbox.getX(),(int)hitbox.getY(),this);
  }
}

class Trooper extends JPanel{
  Image sprite;
  int tx,ty,vy,facing,gunHeat,health,frame;
  boolean grounded;
  Rectangle hitbox;
  ArrayList<Bullet>bullets;//list of all bullets shot by this trooper
  public static final int LEFT=0, RIGHT=1;
  public Trooper(int x, int y){
    tx=x;
    ty=y;
    vy=0;
    frame=0;
    gunHeat=0;
    health=100;
    facing=LEFT;
    bullets=new ArrayList<Bullet>();
    hitbox=new Rectangle(tx-10,ty-10,20,25);
    grounded=false;
  }
 public void shoot(int x){//shoot if player is 100 pixels or less in front
   gunHeat--;
   int nx=(int)hitbox.getX();
   if(facing==LEFT && nx-x<=100 && nx-x>0 && gunHeat<=0){
     gunHeat=35;
     bullets.add(new Bullet(nx,ty,facing,4));
   }
   else if(facing==RIGHT && x-nx<=100 && x-nx>0 && gunHeat<=0){
     gunHeat=35;
     bullets.add(new Bullet(nx,ty,facing,4));
   }
   for(int i=0;i<bullets.size();i++){
     Bullet currentBullet=bullets.get(i);
     currentBullet.move();
   }
 }
  public void move(){
    frame++;
    if(frame==99){
      frame=0;
    }
    if(facing==LEFT){
      tx-=2;
    }
    else{
      tx+=2;
    }
  }
  public void offset(int o){//adjust position
    hitbox=new Rectangle(tx-10-o,ty-10,20,25);
  }
  public void collide(ArrayList<Rocket>r,ArrayList<Bullet>b,ArrayList<Flame>f){
    if(!GamePanel.clear(tx,ty+10+vy)){
      grounded=true;
      gravity();
      vy=0;
      if(GamePanel.clear(tx+1,ty+1)){
        facing=LEFT;
      }
      else if(GamePanel.clear(tx-1,ty+1)){
        facing=RIGHT;
      }
    }
    else{
      grounded=false;
    }
    //direction change
    for(int i=0;i<r.size();i++){
      if(hitbox.contains(r.get(i).rx,r.get(i).ry)){
        health-=100;
        r.remove(r.get(i));
      }
    }
    for(int i=0;i<b.size();i++){
      if(hitbox.contains(b.get(i).bx,b.get(i).by)){
        health-=9;
        b.remove(b.get(i));
      }
    }
    for(int i=0;i<f.size();i++){
      if(hitbox.contains(f.get(i).fx,f.get(i).fy)){
        health-=11;
        f.remove(f.get(i));
      }
    }
    //damage check
  }
  public void gravity(){
    ty+=vy;
    if(grounded==false){
      vy+=1;
      if(vy>20){
        vy=20;
      }
    }
    else{
      vy=0;
    }
  }
  public void draw(Graphics g){
    if(facing==LEFT){
      sprite=new ImageIcon("WalkLeft/WalkLeft"+frame/25+".png").getImage();
    }
    else{
      sprite=new ImageIcon("WalkRight/WalkRight"+frame/25+".png").getImage();
    }
    g.drawImage(sprite,(int)hitbox.getX(),(int)hitbox.getY()-10,this);
    for(int i=0;i<bullets.size();i++){
      g.setColor(Color.RED);
      bullets.get(i).draw(g);
    }
  }
}

class Rocket extends JPanel{
  int rx,ry,dir,width;
  Image sprite;
  private final int LEFT=0, RIGHT=1;
  public Rocket(int x, int y, int facing){
    width=30;
    dir=facing;
    if(dir==LEFT){
      rx=x-width;
      sprite=new ImageIcon("missileL.png").getImage();
    }
    else{
      rx=x+width;
      sprite=new ImageIcon("missileR.png").getImage();
    }
    ry=y;
  }
  public void move(){
    if(dir==LEFT){
      rx-=4;
    }
    else{
      rx+=4;
    }
  }
  public void draw(Graphics g){
    g.drawImage(sprite,rx,ry-5,this);
  }
}

class Flame extends JPanel{
  int fx,fy,facing;
  Image sprite;
  Rectangle hitbox;
  private final int LEFT=0, RIGHT=1;
  public Flame(int x, int y, int f){
    fx=x;
    fy=y;
    facing=f;
    if(facing==LEFT){
      sprite=new ImageIcon("fireL.png").getImage();
    }
    else{
      sprite=new ImageIcon("fireR.png").getImage();
    }
    hitbox=new Rectangle(fx-5,fy-5,10,10);
  }
  public void fade(ArrayList<Flame>f, Player p){//limits the distance of the flames from the player
    for(int i=0;i<f.size();i++){
     if(f.get(i).fx-p.px>160 || p.px-f.get(i).fx>160){
       f.remove(i);
     }
    }
  }
  public void move(){
    if(facing==RIGHT){
      fx+=3;
    }
    else{
      fx-=3;
    }
  }
  public void draw(Graphics g){
    g.drawImage(sprite,fx-5,fy-5,this);
  }
}

class Turret extends JPanel{
 int tx,ty,facing, gunHeat,health;
 Image sprite;
 Rectangle hitbox;
 private final int LEFT=0,RIGHT=1;
 ArrayList<Bullet>bullets;
 
 public Turret(int x,int y, int dir){
   tx=x;
   ty=y;
   hitbox=new Rectangle(tx-15,ty-15,30,30);
   gunHeat=0;
   health=200;
   facing=dir;
   bullets=new ArrayList<Bullet>();
   if(facing==LEFT){
     sprite=new ImageIcon("turretL.png").getImage();
   }
   else{
     sprite=new ImageIcon("turretR.png").getImage();
   }
 }
 public void collide(ArrayList<Rocket>r,ArrayList<Bullet>b,ArrayList<Flame>f){
   for(int i=0;i<r.size();i++){
     if(hitbox.contains(r.get(i).rx,r.get(i).ry)){
       health-=100;
       r.remove(r.get(i));
     }
   }
   for(int i=0;i<b.size();i++){
     if(hitbox.contains(b.get(i).bx,b.get(i).by)){
       health-=9;
       b.remove(b.get(i));
     }
   }
   for(int i=0;i<f.size();i++){
     if(hitbox.contains(f.get(i).fx,f.get(i).fy)){
       health-=11;
       f.remove(f.get(i));
     }
   }
 }
 public void offset(int o){
    hitbox=new Rectangle(tx-15-o,ty-15,30,30);
  }
 public void shoot(int x, int y){
   gunHeat--;
   int nx=(int)hitbox.getX();
   if(facing==LEFT && nx-x<=200 && nx-x>0 && gunHeat<=0 && y>=ty-160 && y<=ty+160){
     gunHeat=50;
     if(y<ty-40){
       bullets.add(new Bullet(nx,ty,facing,2));
     }
     else if(y>ty+40){
       bullets.add(new Bullet(nx,ty,facing,3));
     }
     else{
       bullets.add(new Bullet(nx,ty,facing,4));
     }
   }
   else if(facing==RIGHT && x-nx<=200 && x-nx>0 && gunHeat<=0 && y>=ty-160 && y<=ty+160){
     gunHeat=50;
     if(y<ty-40){
       bullets.add(new Bullet(nx,ty,facing,2));
     }
     else if(y>ty+40){
       bullets.add(new Bullet(nx,ty,facing,3));
     }
     else{
       bullets.add(new Bullet(nx,ty,facing,4));
     }
   }
   for(int i=0;i<bullets.size();i++){
     Bullet currentBullet=bullets.get(i);
     currentBullet.move();
   }
 }
 
 public void draw(Graphics g){
  g.setColor(Color.BLUE);
  g.drawImage(sprite,(int)hitbox.getX(),(int)hitbox.getY(),this);
  g.setColor(Color.RED);
  for(int i=0;i<bullets.size();i++){
      bullets.get(i).draw(g);
    }
 }
 
}

class Bullet{
  int bx,by,dir,speed, angle;
  Rectangle hitbox;
  private final int LEFT=0, RIGHT=1,UP=2,DOWN=3;

  public Bullet(int x,int y,int facing, int a){
    bx=x;
    by=y;
    angle=a;//used to dtermine if the y-velocity should be affected
    dir=facing;
    speed=5;
    hitbox=new Rectangle(bx-5,by-5,10,10);
  }

  public void move(){
    double rise=Math.sin(Math.toRadians(45.0))*speed;//launch angle of 45 degrees
    if(dir==LEFT){
      if(angle==UP){
       by-=(int)rise;
       bx-=(int)rise;
      }
      else if(angle==DOWN){
       by+=(int)rise;
       bx-=(int)rise;
      }
      else{
        bx-=speed;
      }
    }
    else{
      if(angle==UP){
       by-=(int)rise;
       bx+=(int)rise;
      }
      else if(angle==DOWN){
       by+=(int)rise;
       bx+=(int)rise;
      }
      else{
        bx+=speed;
      }
    }
    
    hitbox=new Rectangle(bx-5,by-5,10,10);
  }
  
  public int getX(){
    return bx;
  }
  
  public int getY(){
    return by;
  }

  public void draw(Graphics g){
    g.fillOval((int)hitbox.getX(),(int)hitbox.getY(),10,10);
  }
}