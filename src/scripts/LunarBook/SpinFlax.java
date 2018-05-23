package scripts.Magic.LunarBook;

import api.Bank;
import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.*;

import static scripts.Magic.LunarBook.LunarBook.*;

public class SpinFlax extends Task<ClientContext> {
    private final int MAGIC_WIDGET = 218,
                        SPELL_SPIN_FLAX = 142,
                        RUNE_NATURE = 561,
                        RUNE_ASTRAL = 9075,
                        FLAX = 1779;


    private Component spinFlax = ctx.widgets.component(MAGIC_WIDGET,SPELL_SPIN_FLAX);
    private int spellXP = 75;

    private boolean activate = true;

    private State state = State.WAIT;

    public SpinFlax(ClientContext ctx, PollingScript.Utils Utils) {
        super(ctx, Utils);
    }


    @Override
    public boolean activate() {
        return activate;
    }

    @Override
    public void execute() {
        state = getState();
        switch (state) {
            case WAIT:
                break;
            case WITHDRAW:
                if (ctx.bank.opened()) {
                    ctx.bank.depositAllExcept(FLAX,RUNE_NATURE,RUNE_ASTRAL);
                    countResources();
                    ctx.bank.withdrawUntil(FLAX, 25);
                    ctx.bank.withdrawUntil(RUNE_ASTRAL, Bank.Amount.ALL.getValue());
                    ctx.bank.withdrawUntil(RUNE_NATURE, Bank.Amount.ALL.getValue());
                    ctx.bank.close();
                } else {
                    ctx.bank.openNearbyBank();
                }
                break;
            case SPIN_FLAX:
                ctx.bank.close();
                if (ctx.chat.canContinue()) {
                    ctx.chat.clickContinue();
                    ctx.chat.clickContinue(true);
                }
                if (!spinFlax.visible() || !spinFlax.valid()) ctx.game.tab(Game.Tab.MAGIC);
                if (spinFlax.click()) {
                    Condition.wait(() -> ctx.players.local().animation() != -1, 300, 7);
                    Condition.wait(() -> ctx.players.local().animation() == -1, 300, 7);
                }
                productDone = expEarned / spellXP;
                break;
        }
    }


    private enum State {
        WAIT,WITHDRAW, SPIN_FLAX
    }

    private State getState() {
        if (hasReqs()) return State.SPIN_FLAX;
        if (!hasReqs()) return State.WITHDRAW;
        return State.WAIT;
    }

    private boolean hasReqs() {
        return !ctx.inventory.select().id(FLAX).isEmpty() &&
                !ctx.inventory.select().id(RUNE_ASTRAL).isEmpty() &&
                ctx.inventory.select().id(RUNE_NATURE).count(true) >= 2;
    }

    private void countResources() {
        if ( (ctx.bank.select().id(FLAX).isEmpty() && ctx.inventory.select().id(FLAX).isEmpty()) ||
                (ctx.bank.select().id(RUNE_ASTRAL).isEmpty() && ctx.inventory.select().id(RUNE_ASTRAL).isEmpty()) ||
                (ctx.bank.select().id(RUNE_NATURE).count(true) < 2 && ctx.inventory.select().id(RUNE_NATURE).count(true) < 2) )
            activate = false;
    }
}
