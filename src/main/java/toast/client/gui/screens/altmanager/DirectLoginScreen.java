package toast.client.gui.screens.altmanager;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import toast.client.utils.LoginUtil;

public class DirectLoginScreen extends Screen {
    protected TextRenderer font = minecraft.textRenderer;
    private TextFieldWidget emailField;
    private TextFieldWidget passwordField;
    private String email;
    private String password;


    protected DirectLoginScreen() {
        super(new LiteralText("Direct Login"));
    }
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void tick() {
        this.emailField.tick();
    }

    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        System.out.println("Password Field");
        this.passwordField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.translate("selectWorld.enterName")) {
            protected String getNarrationMessage() {
                return super.getNarrationMessage() + ". " + I18n.translate("selectWorld.resultFolder");
            }
        };
        this.passwordField.setText(this.password);
        this.passwordField.setChangedListener((string) -> {
            this.password = string;
//            this.passwordField.getText();
        });
        System.out.println("Email Field");
        this.addButton(new TextFieldWidget(this.font,this.width / 2 - 200, this.height / 4 + 120 + 12, 150, 20, "Email"));
        this.emailField.setText(email);
        this.emailField.setChangedListener((string) -> {
            this.email = this.emailField.getText();
        });
        this.children.add(this.emailField);
        this.emailField.setVisible(true);
//        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.toTitle"), (buttonWidget) -> {
//            this.minecraft.openScreen(new TitleScreen());
//        }));
//        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, I18n.translate("menu.quit"), (buttonWidget) -> {
//            this.minecraft.scheduleStop();
//        }));
        LoginUtil.loginWithPass(email,password);
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, -1);
        super.render(mouseX, mouseY, delta);
        this.emailField.render(mouseX, mouseY, delta);
    }

}
