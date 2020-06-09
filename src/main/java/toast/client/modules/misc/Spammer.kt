package toast.client.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.RandomStringUtils;
import toast.client.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.utils.FileManager;
import toast.client.utils.Logger;
import toast.client.utils.TimerUtil;

import java.util.ArrayList;
import java.util.List;

public class Spammer extends Module {
    private List<String> lines = new ArrayList<>();
    private int currentLine = 0;
    private final TimerUtil timer = new TimerUtil();

    public Spammer() {
        super("Spammer", "Spams messages in chat from a file.", Category.MISC, -1);
        this.settings.addSlider("Delay", 0.0D, 1.0D, 20.0D);
        this.settings.addBoolean("AntiSpam", false);
        this.settings.addSlider("AntiSpam length", 1, 20, 25);
    }

    public void onEnable() {
        timer.reset();
        lines = FileManager.readFile(FileManager.createFile("spammer.txt"));
        if (lines == null || lines.size() < 1) {
            Logger.message("spammer.txt was empty!", Logger.WARN, false);
            this.setEnabled(false);
        }
    }

    public void onDisable() {
        timer.reset();
    }

    @Subscribe
    public void onTick(EventUpdate event) {
        if (lines.isEmpty() || mc.player == null) return;
        if (this.timer.isDelayComplete((long) (this.getDouble("Delay") * 1000L))) {
            this.timer.setLastMS();
            if (!this.getBool("AntiSpam")) {
                mc.player.sendChatMessage(lines.get(currentLine));
            } else if (this.getBool("AntiSpam")) {
                mc.player.sendChatMessage(lines.get(currentLine) + " [" + RandomStringUtils.randomAlphanumeric((int) this.getDouble("AntiSpam length")).toLowerCase() + "]");
            }
            if (currentLine >= lines.size() - 1) {
                currentLine = 0;
            } else {
                currentLine++;
            }

        }

    }
}
