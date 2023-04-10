import java.awt.Graphics;
import agents.controllers.MarioAIBase;
import engine.core.Entity;
import engine.core.EntityType;
import engine.core.IEnvironment;
import engine.core.LevelScene;
import engine.core.Tile;
import engine.graphics.VisualizationComponent;
import engine.input.*;

import java.util.ArrayList;
import java.util.List;

// Code your custom agent here!

public class MyAgent extends MarioAIBase {
	private boolean enemyAhead() {
		return     entities.danger(1, 0) || entities.danger(1, -1) 
				|| entities.danger(2, 0) || entities.danger(2, -1);
				//|| entities.danger(3, 0) || entities.danger(3, -1);
	}

	private boolean enemyAdown() {
		return     entities.danger(1, 1)
				|| entities.danger(2, 1)
				|| entities.danger(3, 1);
	}

	private boolean enemyAbove() {
		return     entities.danger(0, -2) || entities.danger(1, -2)
				|| entities.danger(0, -3) || entities.danger(1, -3);
	
	}
	private boolean brickAhead() {
		return     tiles.brick(1, 0) || tiles.brick(1, -1) 
				|| tiles.brick(2, 0) || tiles.brick(2, -1)
				|| tiles.brick(3, 0) || tiles.brick(3, -1);
	}

	private boolean pitAhead() {
		return     (tiles.emptyTile(1, 1) && tiles.emptyTile(1, 2))
				|| (tiles.emptyTile(2, 1) && tiles.emptyTile(2, 2))
				|| (   tiles.tile(1, 1) == Tile.COIN_ANIM || tiles.tile(1, 2) == Tile.COIN_ANIM
					|| tiles.tile(2, 1) == Tile.COIN_ANIM || tiles.tile(2, 2) == Tile.COIN_ANIM);
	}
	private float spikyHorizontalSpeed(List<Entity> l){
		for (Entity e : l){
			if (e.type == EntityType.SPIKY){
				return e.speed.x;
			}
		}
		return 0;
	}
	private boolean spikiesBeyond() {
		return 	   
						
				   entities.entityType(4, 3) == EntityType.SPIKY
				|| entities.entityType(4, 2) == EntityType.SPIKY
				|| entities.entityType(4, 1) == EntityType.SPIKY
				|| entities.entityType(4, 0) == EntityType.SPIKY
				|| entities.entityType(4, -1) == EntityType.SPIKY
				|| entities.entityType(4, -2) == EntityType.SPIKY
				|| entities.entityType(4, -3) == EntityType.SPIKY
				//|| entities.entityType(5, 3) == EntityType.SPIKY
				//|| entities.entityType(5, 2) == EntityType.SPIKY
				//|| entities.entityType(5, 1) == EntityType.SPIKY
				//|| entities.entityType(5, 0) == EntityType.SPIKY
				|| entities.entityType(5, -1) == EntityType.SPIKY
				|| entities.entityType(5, -4) == EntityType.SPIKY
				//|| entities.entityType(5, -3) == EntityType.SPIKY
				|| entities.entityType(6, -3) == EntityType.SPIKY
				//|| entities.entityType(7, 3) == EntityType.SPIKY
				//|| entities.entityType(7, 2) == EntityType.SPIKY
				//|| entities.entityType(7, 1) == EntityType.SPIKY
				//|| entities.entityType(7, 0) == EntityType.SPIKY
				|| entities.entityType(7, -1) == EntityType.SPIKY
				//|| entities.entityType(7, -2) == EntityType.SPIKY
				//|| entities.entityType(7, -3) == EntityType.SPIKY
				//|| entities.entityType(8, 3) == EntityType.SPIKY
				//|| entities.entityType(8, 2) == EntityType.SPIKY
				//|| entities.entityType(8, 1) == EntityType.SPIKY
				|| entities.entityType(8, 0) == EntityType.SPIKY
				|| entities.entityType(9, 1) == EntityType.SPIKY;
				//|| entities.entityType(10, -2) == EntityType.SPIKY
				//|| entities.entityType(11, -3) == EntityType.SPIKY;
				
				//|| (entities.entityType(4, 0) == EntityType.SPIKY && spikyHorizontalSpeed(entities.allAt(4, 0)) > 0)
				//|| (entities.entityType(8, 0) == EntityType.SPIKY && spikyHorizontalSpeed(entities.allAt(8, 0)) < 0);
	}
	private boolean spikiesBelow() {
		return     entities.entityType(-1, 2) == EntityType.SPIKY || entities.entityType(-1, 3) == EntityType.SPIKY
				|| entities.entityType(0, 2) == EntityType.SPIKY || entities.entityType(0, 3) == EntityType.SPIKY
				|| entities.entityType(1, 2) == EntityType.SPIKY || entities.entityType(1, 3) == EntityType.SPIKY;	
	}

	
	

	@Override
	public void debugDraw(VisualizationComponent vis, LevelScene level,	IEnvironment env, Graphics g) {
		super.debugDraw(vis, level, env, g);
		if (mario == null) return;

		String debug = "";
		if (enemyAbove()) {
			debug += "|ENEMY ABOVE|";
		}
		
		if (enemyAdown()) {
			debug += "|ENEMY ADOWN|";
		}
		
		if (enemyAhead()) {
			debug += "|ENEMY AHEAD|";
		}
		if (brickAhead()) {
			debug += "|BRICK AHEAD|";
		}
		if (mario != null && mario.onGround) {
			debug += "|ON GROUND|";
		}
		

		// EXAMPLE DEBUG VISUALIZATION
		//String debug = "MY DEBUG STRING";
		VisualizationComponent.drawStringDropShadow(g, debug, 0, 26, 1);
	}


	
	Boolean emergentReverse = false;
	Boolean emergentSpeed = false;
	Boolean simultaneousButton = false;
    // Called on each tick to find out what action(s) Mario should take.
	@Override
	public MarioInput actionSelectionAI() {
        MarioInput input = new MarioInput();
        // ALWAYS RUN RIGHT
		if (!emergentReverse){
			input.press(MarioKey.RIGHT);
		}
			
		
		
		// ALWAYS SPEED RUN
		//input.press(MarioKey.SPEED);
		
		// IF (ENEMY || BRICK AHEAD) => JUMP
        if (mario.mayJump && (enemyAhead() || brickAhead() || pitAhead()) && !spikiesBeyond()){
			//input.press(MarioKey.LEFT);
			input.press(MarioKey.JUMP);
		}
            
		
		// Keep jumping to go as high as possible.
		if (mario.isJumping()) {
			input.press(MarioKey.JUMP);	
		}
		//
		if ((mario.isFalling() && enemyAdown())){
			input.press(MarioKey.LEFT);
		}
		// Emergent Avoidance
		if (enemyAbove()){
			emergentReverse = true;
		}
		if (spikiesBelow()){
			emergentSpeed = true;
		}
		if (emergentReverse){
			if (mario.speed.x > -1){
				
				if (simultaneousButton){
					input.press(MarioKey.LEFT);
					simultaneousButton = !simultaneousButton;
				}
				else{
					input.press(MarioKey.SPEED);
					simultaneousButton = !simultaneousButton;
				}
				
				//input.press(MarioKey.LEFT);
				//input.press(MarioKey.SPEED);
					
			}
			else{
				emergentReverse = false;
			}
		}
		
		if (emergentSpeed){
				input.press(MarioKey.SPEED);
				if (mario.speed.x > 6.5){
					emergentSpeed = false;
				}
				
		}
		// SHOOT WHENEVER POSSIBLE
		if (mario.mayShoot) {
            // TOGGLE SPEED BUTTON
            if (!lastInput.isPressed(MarioKey.SPEED))
                input.press(MarioKey.SPEED);    
        }
			

        return input;
	}
}
