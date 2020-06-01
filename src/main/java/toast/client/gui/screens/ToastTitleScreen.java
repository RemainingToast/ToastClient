package toast.client.gui.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

public class ToastTitleScreen extends Screen {

    public ToastTitleScreen() {
        super(new LiteralText("Toast Client™ Title Screen coming soon™"));
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.toTitle"), (buttonWidget) -> {
            this.minecraft.openScreen(new TitleScreen());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, I18n.translate("menu.quit"), (buttonWidget) -> {
            this.minecraft.scheduleStop();
        }));
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}
