package toast.client.gui.screens.altmanager;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ChatUtil;
import toast.client.utils.LoginUtil;

import java.net.IDN;
import java.util.function.Predicate;

public class DirectLoginScreen extends Screen {
    private final BooleanConsumer callback;
    private final Screen parent;
    private final Predicate<String> addressTextFilter = (string) -> {
        if (ChatUtil.isEmpty(string)) {
            return true;
        } else {
            String[] strings = string.split(":");
            if (strings.length == 0) {
                return true;
            } else {
                try {
                    String string2 = IDN.toASCII(strings[0]);
                    return true;
                } catch (IllegalArgumentException var3) {
                    return false;
                }
            }
        }
    };
    private ButtonWidget buttonAdd;
    //    private final ServerInfo server;
    private TextFieldWidget passwordField;
    private TextFieldWidget emailField;
    private ButtonWidget resourcePackOptionButton;

    public DirectLoginScreen(Screen parent, BooleanConsumer callback) {
        super(new LiteralText("Direct Login"));
        this.parent = parent;
        this.callback = callback;
//        this.server = server;
    }

    public void tick() {
        this.emailField.tick();
        this.passwordField.tick();
    }

    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.emailField = new TextFieldWidget(this.font, this.width / 2 - 100, 66, 200, 20, "Email");
        this.emailField.setSelected(true);
//        this.emailField.setText(this.server.name);
        this.emailField.setChangedListener(this::onClose);
        this.children.add(this.emailField);
        this.passwordField = new TextFieldWidget(this.font, this.width / 2 - 100, 106, 200, 20, "Password");
        this.passwordField.setMaxLength(128);
//        this.passwordField.setText(this.server.address);
        this.passwordField.setTextPredicate(this.addressTextFilter);
        this.passwordField.setChangedListener(this::onClose);
        this.children.add(this.passwordField);
        this.buttonAdd = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20, "Login", (buttonWidget) -> {
            this.addAndClose();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20, "Back", (buttonWidget) -> {
            this.onClose();
            this.callback.accept(false);
        }));
        this.updateButtonActiveState();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.passwordField.getText();
        String string2 = this.emailField.getText();
        this.init(client, width, height);
        this.passwordField.setText(string);
        this.emailField.setText(string2);
    }

    private void onClose(String text) {
        this.updateButtonActiveState();
    }

    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }

    private void addAndClose() {
        if (LoginUtil.loginWithPass(emailField.getText(), passwordField.getText())) {
            System.out.println("Login Success!");
            onClose();
        } else {
            System.out.println("Login Failed!");
        }
    }

    public void onClose() {
        this.updateButtonActiveState();
        this.minecraft.openScreen(this.parent);
    }

    private void updateButtonActiveState() {
        String string = this.passwordField.getText();
        boolean bl = !string.isEmpty() && string.split(":").length > 0 && string.indexOf(32) == -1;
        this.buttonAdd.active = bl && !this.emailField.getText().isEmpty();
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 17, 16777215);
        this.drawString(this.font, "Email", this.width / 2 - 100, 53, 10526880);
        this.drawString(this.font, "Password", this.width / 2 - 100, 94, 10526880);
        this.emailField.render(mouseX, mouseY, delta);
        this.passwordField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }

}
