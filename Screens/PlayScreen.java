package Abyss.Screens;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import Abyss.Creature;
import Abyss.CreatureFactory;
import Abyss.PlayerAi;
import Abyss.World;
import Abyss.WorldBuilder;
import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {
	
/* * * * * * * * * * * * Envrionment Initialization * * * * * * * * * * * * */
	 //Creates Our world
	 private World world;
	 private int screenWidth;
	 private int screenHeight;
	 public static Creature player;
	 
	 private void createWorld(){
		 world = new WorldBuilder(90, 31)
			 .makeCaves()
             .build();
	   }
	 
	 //Message List
	 private List<String> messages;
	 
	 //PlayScreen Constructor
	 public PlayScreen(){
		 screenWidth = 80;
		 screenHeight = 21;
		 messages = new ArrayList<String>();
		 createWorld();
		 
		 CreatureFactory creatureFactory = new CreatureFactory(world);
		 createCreatures(creatureFactory);
	  }
	 
	 
	 private void createCreatures(CreatureFactory creatureFactory){
		 	//Make the Player
		    player = creatureFactory.newPlayer(messages);
		  
		    //Make some Goblins
		    for (int i = 0; i < 52; i++){
		        creatureFactory.newGoblin();
		    }
		    //Make some Fungi
		    for (int i = 0; i < 384; i++){
		        creatureFactory.newFungus();
		    }
		    //Make some Moles
		    for (int i = 0; i < 12; i++){
		        creatureFactory.newMole();
		    }
		    //Make the Reaper(s)
		    for (int i = 0; i < PlayerAi.level; i++){
		        creatureFactory.newReaper();
		    }
		}
	 
/* * * * * * * Tracker Variables * * * * */
//Are we alive?
boolean alive = true;
boolean levelup = false;

/* * * * * * Output Display * * * * * * */
	 public void displayOutput(AsciiPanel terminal) {
		 
	        //Terminal Labels
	        terminal.writeCenter("Exp " + PlayerAi.xp + " / " + PlayerAi.reqXp, 22);
	        terminal.writeCenter("-- press [escape] to lose or [enter] to win --", 23);
	        
	        //Health / Mana
	        String manaPool = String.format(" %3d/%3d MP", player.mana(), player.maxMana());
	        terminal.write(manaPool, 1, 23);
	        String healthPool = String.format(" %3d/%3d HP", player.hp(), player.maxHp());
	        terminal.write(healthPool, 1, 22);
	        
	        
	        int left = getScrollX();
	        int top = getScrollY();
	        displayTiles(terminal, left, top);
	        terminal.write(player.glyph(), player.x - left, player.y - top, player.color());
	        
	        /* * * * * * * * Events * * * * * * * * * */
	       
	        //Ready to level up?
	        if (PlayerAi.xp >= PlayerAi.reqXp ) {
	     		terminal.write("Time To Level", 1, 20);
	     		terminal.write("Press p", 1, 21);
	     		levelup = true;
	     	}
	        if (PlayerAi.hp <= 0 ) {
	     		alive = false;
	     	}
	        
	        /* * * * * * * * * * * * * * * * * * * * */
	        displayMessages(terminal, messages);
	    }
	 
	    public Screen respondToUserInput(KeyEvent key) {
	    	//Game over?
	    	if (alive == false) { 
	    		return new LoseScreen();
	    	}
	        switch (key.getKeyCode()){
	        case KeyEvent.VK_C: return new CharacterScreen();
	        
	        //Movement
	        case KeyEvent.VK_LEFT:
	        case KeyEvent.VK_A: player.moveBy(-1, 0); break;
	        case KeyEvent.VK_RIGHT:
	        case KeyEvent.VK_D: player.moveBy( 1, 0); break;
	        case KeyEvent.VK_UP:
	        case KeyEvent.VK_W: player.moveBy( 0,-1); break;
	        case KeyEvent.VK_DOWN:
	        case KeyEvent.VK_S: player.moveBy( 0, 1); break;
	        
	        //Spells
	        case KeyEvent.VK_1: player.heal();
	        }
	        
	        //Ready to level
	        if (levelup == true) {
	        	switch (key.getKeyCode()){
		        case KeyEvent.VK_P: return new LevelUpScreen();
	        	}
	        }
	        
	        
			return this;
	    }
	 
	 //Displays Tiles
	 private void displayTiles(AsciiPanel terminal, int left, int top) {
	        for (int x = 0; x < screenWidth; x++){
	            for (int y = 0; y < screenHeight; y++){
	                int wx = x + left;
	                int wy = y + top;

	                Creature creature = world.creature(wx, wy);
	                if (creature != null)
	                    terminal.write(creature.glyph(), creature.x - left, creature.y - top, creature.color());
	                else
	                    terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
	            }
	        }
	    }
	 
	 //Display Messages
	 private void displayMessages(AsciiPanel terminal, List<String> messages) {
		    int top = screenHeight - messages.size();
		    for (int i = 0; i < messages.size(); i++){
		        terminal.writeCenter(messages.get(i), top + i);
		    }
		    messages.clear();
		}

	 
	 
	 //Scrolling Methods
	 public int getScrollX() {
		    return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth));
		}
	 public int getScrollY() {
		    return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight));
		}
	


}
