package toast.client.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.RandomStringUtils;
import toast.client.event.EventImpl;
import toast.client.event.events.player.EventUpdate;
import toast.client.modules.Module;
import toast.client.utils.TimerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spammer extends Module {
    private List<String> lines = new ArrayList<>();
    private int lineCount = 0;

    public Spammer() {
        super("Spammer", Category.MISC,-1);
                this.addModesCustomName("AntiSpam", "Off", "Random");
                this.addNumberOption("Delay", 2.0D, 10.0D, 0.0D);
    }
    private TimerUtil timer = new TimerUtil();

    @Override
    public void onEnable() {
        super.onEnable();
        lines = Arrays.asList("someone", "fix", "config", "for", "spammer", "k", "thx", "bye");
    }

    @EventImpl
    public void onTick(EventUpdate event) {
        if(lines.isEmpty() || mc.player == null) return;
        if (this.timer.isDelayComplete((long) (this.getDouble("Delay") * 1000L))) {
            this.timer.setLastMS();
            if(this.getString("AntiSpam").equals("Off")) {
                mc.player.sendChatMessage(lines.get(lineCount));
            } else if(this.getString("AntiSpam").equals("Random")) {
                mc.player.sendChatMessage(lines.get(lineCount)+" ["+RandomStringUtils.randomAlphanumeric(20).toLowerCase()+"]");
            }

            if(lineCount >= lines.size() -1) {
                lineCount = 0;
            } else {
                lineCount++;
            }

        }

    }
}
