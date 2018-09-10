package scripts.tutorials;


import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Player;

import java.util.Comparator;

@Script.Manifest(name = "ChickenKill", properties = "author=nomivore; topic=1341279; client=4;", description = "Kills chickens")
public class ChickenKill extends PollingScript<ClientContext> {
    @Override
    public void poll() {
//        attackChicken();
        attackNpc("Chicken");
    }

    public static final int CHICKEN_ID = 1234;
    public boolean attackChicken() {
        Npc chicken = ctx.npcs.select().id(CHICKEN_ID).nearest().poll();
        chicken.click();
        return ctx.players.local().inCombat();
    }


    public boolean attackNpc(String npcName) {
        Player me = ctx.players.local();
        //if I am interacting with an npc named "npcName"
        //OR
        //an npc is attacking (interacting with) me
        //EXIT FUNCTION
        if (me.interacting().name().equals(npcName)
                || !ctx.npcs.select().select(npc->npc.interacting().equals(me)).isEmpty()) return true;


        Npc npcToAttack = ctx.npcs
                .select()   //select a new set of all loaded npcs
                .name(npcName)  //only select npcs with the correct name
                .nearest()  //sort by nearest
                .sort((n1,n2)->Boolean.compare(n1.inViewport(),n2.inViewport()))
                            //sort by npcs that are visible first
                .limit(5)   //limit results to 5 npcs
                .select(npc->!npc.interacting().valid() && //ignore npcs that are interacting with someone
                        npc.healthPercent() > 0 &&  //only select alive npcs
                        npc.tile().matrix(ctx).reachable()) //only select reachable npcs
                .poll();
        if (!npcToAttack.inViewport()) ctx.camera.turnTo(npcToAttack);
        npcToAttack.interact("Attack",npcName);

        //Check every 250ms 4 times if interacting
        //If interacting, return true.
//        return Condition.wait(()->me.interacting().name().equals(npcName),250,4);

        //MORE ADVANCED
        //If not interacting within 250ms, return false
        //If function is called rapidly, will spam click until interacting
        return !Condition.wait(()->!me.interacting().name().equals(npcName),250,4);
    }

}
