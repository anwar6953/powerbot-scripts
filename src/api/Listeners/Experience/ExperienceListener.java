package api.Listeners.Experience;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA
 * User: Anthony
 * Date: 11/16/15
 */

public interface ExperienceListener extends EventListener {

    void onExperienceChanged(ExperienceEvent experienceEvent);

}