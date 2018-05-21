package api;

import org.powerbot.script.rt4.*;

public class Magic extends org.powerbot.script.rt4.Magic {
    public Magic(org.powerbot.script.rt4.ClientContext ctx) {
        super(ctx);
    }

    public boolean isSelected(org.powerbot.script.rt4.Magic.MagicSpell spell) {
        return ctx.game.tab() == Game.Tab.MAGIC && ctx.magic.component(spell).textureId() != spell.texture() &&
                ctx.magic.component(spell).borderThickness() != 0;
    }
}
