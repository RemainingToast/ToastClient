package toast.client.auth.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;

public class NoAuthScreen extends Screen {
    public NoAuthScreen(){
        super(new LiteralText("Unauthorized!"));
    }

    protected void init() {
        /*this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.toTitle"), (buttonWidget) -> {
            this.minecraft.openScreen(new TitleScreen());
        }));
        this.addButton(new ButtonWidget(minecraft.getWindow().getWidth() / 2 - this.width / 2 - 100, this.height / 4 - 60 + 60 + 60, 200, 20, I18n.translate("menu.quit"), (buttonWidget) -> {
            this.minecraft.scheduleStop();
        }));*/
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void render(int mouseX, int mouseY, float delta) {
        /*this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, this.height / 4 - 60 + 20, 16777215);
        this.drawCenteredString(this.font, "You cannot load in motherfucker!", this.width / 2, this.height / 4 - 60 + 60, 10526880);
        this.drawCenteredString(this.font, "You need to buy the fucking client.", this.width / 2, this.height / 4 - 60 + 60 + 18, 10526880);
        super.render(mouseX, mouseY, delta);*/
    }
}
