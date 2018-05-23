package api;

import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Interactive;

public class Camera extends org.powerbot.script.rt4.Camera {
    public Camera(org.powerbot.script.rt4.ClientContext arg0) {
        super(arg0);
    }

    public <T extends Interactive & Nameable & InteractiveEntity & Identifiable & Validatable & Actionable>
    void pitchTurn(T obj) {
        ctx.camera.turnTo(obj);
        ctx.camera.pitch(0);
    }
}
