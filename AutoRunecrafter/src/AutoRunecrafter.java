import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.utility.ConditionalSleep;

@ScriptManifest(author = "Lexhanatin", name = "AutoRunecrafter", info = "Just an empty script :(", version = 0.1, logo = "")
public final class AutoRunecrafter extends Script  {
	
	Area airAltar = new Area(2992, 3296, 2980, 3284);
	NPC portalNpc = null;
	RS2Object portalObj = null;
	Entity portalEntity;
	
    @Override
    public final int onLoop() throws InterruptedException {
    	if (canCraft()) {
    		log("craft");
    		craft();
    	}
    	else {
    		bank();
    	}
        return random(150, 200);
    }
    
    private void craft() {
    	if (getObjects().closest("Altar") != null) {
    		if(getInventory().isItemSelected()) {
    			getInventory().deselectItem();
    		}
    		else if (getInventory().contains("Rune essence")) {
    			if (getObjects().closest("Altar").interact("Craft-rune")) {
    				log("crafting");
		    		new ConditionalSleep(5000) {
		    			@Override
		    			public boolean condition() throws InterruptedException {
		    				return !getInventory().contains("Rune essence");
		    			}
		    		}.sleep();
		    	}
    		}
    	}
    	else if (getObjects().closest("Altar") == null) {
    		if (!airAltar.contains(myPosition())) {
    			log("walking");
	    		getWalking().webWalk(airAltar);
	    	}
	    	else if (airAltar.contains(myPosition())) {
	    		log("teleporting");
	    		if (teleport()) {
		    		new ConditionalSleep(5000) {
		    			@Override
		    			public boolean condition() throws InterruptedException {
		    				return getObjects().closest("Altar") != null;
		    			}
		    		}.sleep();
		    	}
	    	}
    	}
    }
    
    private boolean canCraft() {
    	return getInventory().contains("Air talisman", "Rune essence") && !getInventory().contains("Air rune");
    }
    
    private boolean teleport() {
    	log("teleport boolean");
    	getInventory().interact("Use", "Air talisman");
    	if(getInventory().isItemSelected()) {
    		log("Item selected");
			return getObjects().closest("Mysterious ruins").interact("Use");
    	}
    	return false;
	}
    
    private void bank() throws InterruptedException {
    	if (getObjects().closest("Altar") != null) {
    		log("leaving");
			portalNpc = getNpcs().closestThatContains("Portal");
    		portalObj = getObjects().closestThatContains("Portal");
    		
    		if (portalNpc != null || portalObj != null) {
    			log("Entity not null");
    			if (portalNpc != null) {
    				log("Portal is NPC");
    				portalEntity = portalNpc;
    			}
    			
    			if (portalObj != null) {
    				log("Portal is Object");
    				portalEntity = portalObj;
    			}
    			if (portalEntity != null) {
    				log("Portal Interaction");
    				if (portalEntity.interact("Exit", "Use")) {
    					new ConditionalSleep(3000) {
    			            @Override
    			            public boolean condition() throws InterruptedException {
    			                return getObjects().closest("Mysterious ruins") != null;
    			            }
    			        }.sleep();
    				}
    			}
    		}
    		else {
    			log ("Entity null");
    		}
		}
    	else if (!Banks.FALADOR_EAST.contains(myPosition())) {
    		getWalking().webWalk(Banks.FALADOR_EAST);
    	}
    	else if (!getBank().isOpen()) {
    		getBank().open();
    	}
    	else if (!getInventory().isEmptyExcept("Air talisman", "Rune essence")) {
    		getBank().depositAllExcept("Fly fishing rod", "Feather");
    	}
    	else if (getBank().contains("Air talisman", "Rune essence")) {
    		getBank().withdrawAll("Air talisman");
    		sleep(random(10, 300));
    		getBank().withdrawAll("Rune essence");
    	}
    }
}