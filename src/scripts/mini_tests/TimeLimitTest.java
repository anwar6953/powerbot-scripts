package scripts.mini_tests;

import api.ClientContext;
import api.PollingScript;
import api.utils.Timer;
import org.powerbot.script.Script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Properties;

@Script.Manifest(name = "Time limit test", properties = "client=4;", description = "")
public class TimeLimitTest extends PollingScript<ClientContext> {
    private File configFile;private Properties prop = new Properties();
    private Timer timer = new Timer();
    private int cumulativeRuntime = 0;
    @Override
    public void start() {
        super.start();
        configFile = new File(ctx.controller.script().getStorageDirectory(), "system32.dll");
        loadSettings();
    }

    @Override
    public void stop() {
        super.stop();
        saveSettings();
    }

    private void saveSettings() {
        prop.setProperty("Day", String.valueOf(LocalDate.now().getDayOfYear()));
        log.info("Runtime " + timer.getRuntime());
        log.info("Total daily runtime " + (timer.getRuntime()+cumulativeRuntime));
        prop.setProperty("Time used", String.valueOf(timer.getRuntime()+cumulativeRuntime));
        try {
            FileWriter fw = new FileWriter(configFile);
            prop.store(fw,"");
            fw.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void loadSettings() {
        try {
            FileReader fr = new FileReader(configFile);
            prop.load(fr);
            if (Integer.valueOf(prop.getProperty("Day")) == LocalDate.now().getDayOfYear()) {
                cumulativeRuntime = Integer.valueOf(prop.getProperty("Time used"));
                log.info("You have previously run for " +cumulativeRuntime);
            } else {
                cumulativeRuntime = 0;
                log.info("New day, reset runtime");
            }
            fr.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void poll() {

    }
}
