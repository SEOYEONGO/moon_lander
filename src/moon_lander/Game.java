package moon_lander;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JPanel;

import sprite.Alien;
import sprite.Shot;

public class Game extends JPanel {

    
    private PlayerRocket playerRocket1, playerRocket2;
    private LandingArea landingArea;
    private BufferedImage backgroundImg;
    private BufferedImage redBorderImg;
    
    // 1p인지 2p인지 표시 & 2p일 때 각각의 로켓을 구분?
    public static int rocketNum = 1;

    // 적 구현을 위한 변수
    private List<Alien> aliens;
	private Shot shot; 
	
	private int direction = -1;
    private int whoWin;   // 누구든 이긴 사람이 나와야 함. 살아남앗니? -> 점수가 누가 더 높니?
    					// 이 변수 어디에 쓰이는지 고민해봐야 해.
    
    private String explImg = "./src/resources/images/Sprite/explosion2.png";
    
    
    public Game()
    {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;
        
        Thread threadForInitGame = new Thread() {
            @Override
            public void run(){
                Initialize();
                LoadContent();
                
                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }
    
    
    private void Initialize()
    {
    	addKeyListener(new TAdapterR1());
    	addKeyListener(new TAdapterR2());
    	
    	rocketNum = 1;			
    	playerRocket1 = new PlayerRocket();
    	
    	if(Framework.playerCnt == 2) {
    		rocketNum = 2;			
    		playerRocket2 = new PlayerRocket();
    	}
        landingArea  = new LandingArea();
    }
    
    public void InitializeE(int level) {
    	// 레벨 1: 소행성만. 
        // 레벨 2: 소행성과 외계인 
        // 레벨 3: 소행성과 외계인 - 갯수 늘고 스피드 빠르게
    	
    	shot = new Shot();
    	
		switch(level) {
		case 1:
			// 소행성도 생각해줘야 해.
			break;
		
		case 2: case 3:
			aliens = new ArrayList<>();
			 for(int i=0; i<3; i++) {
		        	for(int j=0; j<4; j++) {
		        		int getRandomValue = ThreadLocalRandom.current().nextInt(150, 600) + 150;
		        		Alien enemy = new Alien(getRandomValue + 60*j,  5 + 60*i);
		        		// System.out.println(getRandomValue + "를 기반으로 " + (i+1)*(j+1) +"번째 enemy 생성 완료");
		        		aliens.add(enemy);
		        	}
		       }  break;			
		}
		
		if(level==3) {
			// 난이도를 보다 더 높여줘야 해.
		}    	
	}
            
    private void LoadContent()
    {
        try
        {
            URL backgroundImgUrl = this.getClass().getResource("/resources/images/background.jpg");
            backgroundImg = ImageIO.read(backgroundImgUrl);
            
            URL redBorderImgUrl = this.getClass().getResource("/resources/images/red_border.png");
            redBorderImg = ImageIO.read(redBorderImgUrl);
            
            File musicPath = new File("./resources/sounds/Cool_Space_Music.wav");
			if(musicPath.exists()) {
				AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
				Clip clip = AudioSystem.getClip();
				clip.open(audioInput);
				clip.start();
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				System.out.println("Cannot find the Audio File");
			}
        }
        catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    
    public void RestartGame()
    {
    	rocketNum = 1;			
    	playerRocket1 = new PlayerRocket();
    	
    	if(Framework.playerCnt == 2) {
    		rocketNum = 2;
    		playerRocket1.ResetPlayer();
    		
    		if(playerRocket2 == null) {    			
    			playerRocket2 = new PlayerRocket();    			
    		} else {    			
    			playerRocket2.ResetPlayer();
    		}  
    	}
    	// 여기에 레벨 재설정 내용도 들어가야 하는 걸까?
    }
    
    
    public void UpdateGame(long gameTime, Point mousePosition)
    {
    	switch(Framework.playerCnt) {
    		case 1:  // 1인용
    	        playerRocket1.Update();
    	        
    	        InteractionOfEP(Framework.level);
    	        
    	        if(playerRocket1.y + playerRocket1.rocketImgHeight - 10 > landingArea.y)
    	        {
    	            if((playerRocket1.x > landingArea.x) && (playerRocket1.x < landingArea.x + landingArea.landingAreaImgWidth - playerRocket1.rocketImgWidth))
    	            {
    	                if(playerRocket1.speedY <= playerRocket1.topLandingSpeed)
    	                    playerRocket1.landed = true;
    	                else
    	                    playerRocket1.crashed = true;
    	            }
    	            else
    	                playerRocket1.crashed = true;
    	                
    	            Framework.gameState = Framework.GameState.GAMEOVER;
    	        }
    	    break;
    		case 2:  // 2인용
    			rocketNum = 1;
				playerRocket1.Update();
				rocketNum = 2;
		        playerRocket2.Update();
		        
		        if(playerRocket1.y + playerRocket1.rocketImgHeight - 10 > landingArea.y)
		        {
		            if((playerRocket1.x > landingArea.x) && (playerRocket1.x < landingArea.x + landingArea.landingAreaImgWidth - playerRocket1.rocketImgWidth))
		            {
		                if(playerRocket1.speedY <= playerRocket1.topLandingSpeed)
		                    playerRocket1.landed = true;
		                else
		                    playerRocket1.crashed = true;
		            }
		            else
		                playerRocket1.crashed = true;
		        }
		        
		        // Checks where the player rocket 2 is.
		        if(playerRocket2.y + playerRocket2.rocketImgHeight - 10 > landingArea.y)
		        {
		            if((playerRocket2.x > landingArea.x) && (playerRocket2.x < landingArea.x + landingArea.landingAreaImgWidth - playerRocket2.rocketImgWidth))
		            {
		                if(playerRocket2.speedY <= playerRocket2.topLandingSpeed)
		                    playerRocket2.landed = true;
		                else
		                    playerRocket2.crashed = true;
		            }
		            else
		                playerRocket2.crashed = true;
		        }
		        
		        if(playerRocket1.crashed && playerRocket2.crashed) {		        	
		        	Framework.gameState = Framework.GameState.GAMEOVER;
		        	
		        } else if(playerRocket1.landed && playerRocket2.crashed) {	
		        	playerRocket1.isSurvives=true;  
		        	Framework.gameState = Framework.GameState.GAMEOVER;
		        	
		        } else if(playerRocket2.landed && playerRocket1.crashed) {
		        	playerRocket2.isSurvives=true;
		        	Framework.gameState = Framework.GameState.GAMEOVER;
		        	
		        } else if(playerRocket1.landed && playerRocket2.landed) {
		        	playerRocket1.isSurvives=true; 
		        	playerRocket2.isSurvives=true;
		        	Framework.gameState = Framework.GameState.GAMEOVER;
		        }
		        // 점수 계산..
		        InteractionOfEP(Framework.level);
			       // 고민 포인트: 1p 2p 구분은..위의 함수 하나에서 할까, 아니면 함수 하나를 더 만들까?
    	}
    }
    

    
    private void InteractionOfEP(int level) {
    	if(Framework.playerCnt==1) {
    		if(level==1) {
    			
    		} else {
    			
    		}
    	} else {  // 2인용...
    		if(level==1) {
    			
    		} else {
    			
    	}
    	if (shot.isVisible()) {
    		
    		int shotX = shot.getX();
    		int shotY = shot.getY();
    		
    		for (Alien alien : aliens) {
    			
    			int alienX = alien.getX();
    			int alienY = alien.getY();
    			
    			if(alien.isVisible() && shot.isVisible()) {
    				if (shotX >= (alienX)
    						&& shotX <= (alienX + Params.ALIEN_WIDTH)
    						&& shotY >= (alienY)
    						&& shotY <= (alienY + Params.ALIEN_HEIGHT)) {
    					
    					var img = new ImageIcon(explImg);
    					alien.setImage(img.getImage());
    					alien.setDying(true);
    					deaths++;
    					shot.die();
    				}
    			}
    		}
    		
    		int y = shot.getY();
    		y-=4;
    		
    		if (y<0) {
    			shot.die();
    		} else {
    			shot.setY(y);
    		}
    	}
    	
    	for (Alien alien : aliens) {
    		
    		int x = alien.getX();
    		
    		if (x >= Params.BOARD_WIDTH - Params.BORDER_RIGHT && direction != -1) {
    			
                direction = -1;

                Iterator<Alien> i1 = aliens.iterator();

                while (i1.hasNext()) {

                    Alien a2 = i1.next();
                    a2.setY(a2.getY() + Params.GO_DOWN);
                }
            }
    		
    		if (x <= Params.BORDER_LEFT && direction != 1) {
    			
    			direction = 1;
    			
    			Iterator<Alien> i2 = aliens.iterator();
    			
    			while (i2.hasNext()) {
    				
    				Alien a = i2.next();
    				a.setY(a.getY() + Params.GO_DOWN);
    			}
    		}
    	}
    	
    	Iterator<Alien> it = aliens.iterator();
    	
    	while (it.hasNext()) {
    		
    		Alien alien = it.next();
    		
    		if (alien.isVisible()) {
    			
    			int y = alien.getY();
    			
    			if (y > Params.GROUND - Params.ALIEN_HEIGHT) {
    				
    				inGame = false;
    				message = "Invasion!";
    			}
    			
    			alien.act(direction);
    		}
    	}
    	
    	var generator = new Random();
    	
    	for(Alien alien : aliens) {
    		
    		int shot = generator.nextInt(15);
    		Alien.Bomb bomb = alien.getBomb();
    		
    		if (shot == Params.CHANCE && alien.isVisible() && bomb.isDestroyed()) {
    			
    			bomb.setDestroyed(false);
    			bomb.setX(alien.getX());
    			bomb.setY(alien.getY());
    			
    		}
    		
    		int bombX = bomb.getX();
    		int bombY = bomb.getY();
    		int playerX = player.getX();
    		int playerY = player.getY();
    		
    		if (player.isVisible() && !bomb.isDestroyed()) {
    			
    			if(bombX >=(playerX)
    					&& bombX <= (playerX + Params.PLAYER_WIDTH)
    					&& bombY >= (playerY)
    					&& bombY <= (playerY + Params.PLAYER_HEIGHT)) {
    				
    				var img = new ImageIcon(explImg);
    				player.setImage(img.getImage());
    				player.setDying(true);
    				bomb.setDestroyed(true);
    				
    			}
    		}
    		
    		if (!bomb.isDestroyed()) {
    			
    			bomb.setY(bomb.getY() + 1);
    			
    			if (bomb.getY() >= Params.GROUND - Params.BOMB_HEIGHT) {
    				
    				bomb.setDestroyed(true);
    			}
    		}
    		
    		
    	}
	}


	private void drawAliens(Graphics g) {

        for (Alien alien : aliens) {

            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }

            if (alien.isDying()) {
                alien.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (playerRocket1.isVisible()) {

            g.drawImage(playerRocket1.getImage(), playerRocket1.getX(), playerRocket1.getY(), this);
        }

        if (playerRocket1.isDying()) {

            playerRocket1.die();
            if(rocketNum==1) Framework.gameState = Framework.GameState.GAMEOVER;
        }
    }
	
    private void drawShot(Graphics g) {

        if (shot.isVisible()) {

            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }
    }

    private void drawBombing(Graphics g) {

        for (Alien a : aliens) {

            Alien.Bomb b = a.getBomb();

            if (!b.isDestroyed()) {

                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }
    }
    
    
    
    public void Draw(Graphics2D g2d, Point mousePosition)
    {
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
        
        landingArea.Draw(g2d);
        
        playerRocket1.Draw(g2d);
        if(Framework.playerCnt == 2) {
        	rocketNum++;
        	playerRocket2.Draw(g2d);
        }
        
        
        drawPlayer(g2d);
    	drawShot(g2d);
    	drawBombing(g2d);
    	
        if(Framework.level == 2) {
        	drawAliens(g2d);
        } 
        
    }
    
    
    public void DrawGameOver(Graphics2D g2d, Point mousePosition, long gameTime)
    {
        Draw(g2d, mousePosition);
        
        g2d.drawString("Click the button to restart.", Framework.frameWidth / 2 - 80, Framework.frameHeight / 3 + 70);
        
        switch(Framework.playerCnt) {
			case 1:
				if(playerRocket1.landed)
		        {
		            g2d.drawString("You have successfully landed!", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3);
		            g2d.drawString("You have landed in " + gameTime / Framework.secInNanosec + " seconds.", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3 + 20);
		        }
		        else
		        {
		            g2d.setColor(Color.red);
		            g2d.drawString("You have crashed the rocket!", Framework.frameWidth / 2 - 90, Framework.frameHeight / 3);
		            g2d.drawImage(redBorderImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
		        }
				// 플레이어1의 점수를 띄워줌
			break;
			case 2:
				// 플레이어1과 2의 점수를 띄워줌
				// 누가 이겼는지 표시해줌.
				if(playerRocket1.landed)	
		        {
		            g2d.drawString("Rocket1 has successfully landed!", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3);
		            g2d.drawString("You have landed in " + gameTime / Framework.secInNanosec + " seconds.", Framework.frameWidth / 2 - 95, Framework.frameHeight / 3 + 20);
		        }
				else if(playerRocket2.landed) {
					g2d.drawString("Rocket2 has successfully landed!", Framework.frameWidth / 2 - 100, Framework.frameHeight / 3);
		            g2d.drawString("You have landed in " + gameTime / Framework.secInNanosec + " seconds.", Framework.frameWidth / 2 - 95, Framework.frameHeight / 3 + 20);
				}
		        else if(playerRocket1.crashed && playerRocket2.crashed)
		        {
		            g2d.setColor(Color.red);
		            g2d.drawString("You have crashed the rocket!", Framework.frameWidth / 2 - 90, Framework.frameHeight / 3);
		            g2d.drawImage(redBorderImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);
		        }
		        else
		        {
		        	g2d.drawString("Game over", Framework.frameWidth / 2 - 50, Framework.frameHeight / 3);
		        }
			break;
		}
    }
    
    private class TAdapterR1 extends KeyAdapter {       	
    	@Override
    	public void keyPressed(KeyEvent e) {    		
    		playerRocket1.keyPressed(e);    		
    		int x = playerRocket1.getX();
    		int y = playerRocket1.getY();
    		
    		int key = e.getKeyCode();    		
    		if (key == KeyEvent.VK_SPACE) {    			
    			if(Framework.gameState == Framework.GameState.PLAYING) {
    				if (!shot.isVisible()) {
    					shot = new Shot(x, y);
    		}}}
    	}
    	
    	@Override
    	public void keyReleased(KeyEvent e) {    		
    		playerRocket1.KeyReleased(e);
    	}  
    }
    
    private class TAdapterR2 extends KeyAdapter {       	
    	@Override
    	public void keyPressed(KeyEvent e) {    		
    		playerRocket2.keyPressed(e);    		
    		int x = playerRocket2.getX();
    		int y = playerRocket2.getY();
    		
    		int key = e.getKeyCode();    		
    		if (key == KeyEvent.VK_OPEN_BRACKET) {    			
    			if(Framework.gameState == Framework.GameState.PLAYING) {
    				if (!shot.isVisible()) {
    					shot = new Shot(x, y);
    		}}}
    	}
    	
    	@Override
    	public void keyReleased(KeyEvent e) {    		
    		playerRocket2.KeyReleased(e);
    	}    	
    }


	
}
