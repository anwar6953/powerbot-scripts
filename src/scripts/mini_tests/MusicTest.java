package scripts.mini_tests;

import api.ClientContext;
import api.PollingScript;
import org.powerbot.script.Condition;
import org.powerbot.script.MenuCommand;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Script;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@Script.Manifest(name = "MusicTest", properties = "client=4;", description = "")
public class MusicTest extends PollingScript<ClientContext> implements PaintListener {
    private final String fileName = "soup.wav";
    Clip clip;
    @Override
    public void start() {
        ctx.properties.setProperty("login.disable", "true");
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(ctx.properties.getProperty("sdn.host"));
        sb.append("/1750755/resources/");
        sb.append(fileName);
        File f = download(sb.toString(),"music.wav");
        try {
            clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(f);
            clip.open(inputStream);
            clip.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void poll() {
        log.info("Polling");
        Condition.sleep(5000);
            clip.loop(1);
    }
    private List<String> strings = new ArrayList<>();
    @Override
    public void repaint(Graphics g) {
        strings.clear();
        if (clip == null) return;
        strings.add("Open " + clip.isOpen());
        Utils.simplePaint(g,strings);
    }
}