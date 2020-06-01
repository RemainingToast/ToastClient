package toast.client.gui.screens.altmanager;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

public class AltManagerScreen extends Screen {

    public ButtonWidget loginButt;


    public AltManagerScreen() {
        super(new LiteralText("Account Manager [0]"));
    }

    protected void init() {
        super.init();

        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 75, 20, ("Add"), (buttonWidget) -> {

        }));
        loginButt = this.addButton(new ButtonWidget(this.width / 2 - 75, this.height - 52, 70, 20, ("Login"), (buttonWidget) -> {
            loginButt.active = false;
        }));

        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 52, 70, 20, ("Direct"), (buttonWidget) -> {

        }));

        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 75, 20, ("Random"), (buttonWidget) -> {

        }));
        this.addButton(new ButtonWidget(this.width / 2 - 76, this.height - 28, 150, 20, ("Back"), (buttonWidget) -> {
            minecraft.openScreen(new TitleScreen());
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4 + 76, this.height - 52, 75, 20, ("Delete"), (buttonWidget) -> {

        }));
        this.addButton(new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, ("Edit"), (buttonWidget) -> {
        }));
    }

    private void refresh() {

    }

    private void removeAlt(boolean confirmedAction) {

    }

    private void editAlt(boolean confirmedAction) {

    }

    private void addAlt(boolean confirmedAction) {

    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return false;
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 16777215);
        super.render(mouseX, mouseY, delta);
    }
}
