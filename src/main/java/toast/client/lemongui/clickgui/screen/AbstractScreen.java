package toast.client.lemongui.clickgui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

public abstract class AbstractScreen extends Screen {

    protected AbstractScreen(Text title) {
        super(title);
    }

    public Text getTitle() {
        return super.getTitle();
    }

    public String getNarrationMessage() {
        return super.getNarrationMessage();
    }

    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean shouldCloseOnEsc() {
        return super.shouldCloseOnEsc();
    }

    public void onClose() {
        super.onClose();
    }

    protected <T extends AbstractButtonWidget> T addButton(T button) {
        return super.addButton(button);
    }

    protected void renderTooltip(ItemStack stack, int x, int y) {
        super.renderTooltip(stack, x, y);
    }

    public List<String> getTooltipFromItem(ItemStack stack) {
        return super.getTooltipFromItem(stack);
    }

    public void renderTooltip(String text, int x, int y) {
        super.renderTooltip(text, x, y);
    }

    public void renderTooltip(List<String> text, int x, int y) {
        super.renderTooltip(text, x, y);
    }

    protected void renderComponentHoverEffect(Text component, int x, int y) {
        super.renderComponentHoverEffect(component, x, y);
    }

    protected void insertText(String text, boolean override) {
        super.insertText(text, override);
    }

    public boolean handleComponentClicked(Text text) {
        return super.handleComponentClicked(text);
    }

    public void sendMessage(String message) {
        super.sendMessage(message);
    }

    public void sendMessage(String message, boolean toHud) {
        super.sendMessage(message, toHud);
    }

    public void init(MinecraftClient client, int width, int height) {
        super.init(client, width, height);
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    public List<? extends Element> children() {
        return super.children();
    }

    protected void init() {
        super.init();
    }

    public void tick() {
        super.tick();
    }

    public void removed() {
        super.removed();
    }

    public void renderBackground() {
        super.renderBackground();
    }

    public void renderBackground(int alpha) {
        super.renderBackground(alpha);
    }

    public void renderDirtBackground(int alpha) {
        super.renderDirtBackground(alpha);
    }

    public boolean isPauseScreen() {
        return super.isPauseScreen();
    }

    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }

    protected boolean isValidCharacterForName(String name, char character, int cursorPos) {
        return super.isValidCharacterForName(name, character, cursorPos);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY);
    }

    public Element getFocused() {
        return super.getFocused();
    }

    public void setFocused(Element focused) {
        super.setFocused(focused);
    }

    protected void hLine(int i, int j, int k, int l) {
        super.hLine(i, j, k, l);
    }

    protected void vLine(int i, int j, int k, int l) {
        super.vLine(i, j, k, l);
    }

    protected void fillGradient(int top, int left, int right, int bottom, int color1, int color2) {
        super.fillGradient(top, left, right, bottom, color1, color2);
    }

    public void drawCenteredString(TextRenderer textRenderer, String str, int centerX, int y, int color) {
        super.drawCenteredString(textRenderer, str, centerX, y, color);
    }

    public void drawRightAlignedString(TextRenderer textRenderer, String str, int rightX, int y, int color) {
        super.drawRightAlignedString(textRenderer, str, rightX, y, color);
    }

    public void drawString(TextRenderer textRenderer, String str, int x, int y, int color) {
        super.drawString(textRenderer, str, x, y, color);
    }

    public void blit(int x, int y, int u, int v, int width, int height) {
        super.blit(x, y, u, v, width, height);
    }

    public int getBlitOffset() {
        return super.getBlitOffset();
    }

    public void setBlitOffset(int i) {
        super.setBlitOffset(i);
    }

    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        return Optional.empty();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseScrolled(double d, double e, double amount) {
        return false;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char chr, int keyCode) {
        return false;
    }

    public void setInitialFocus(Element element) {

    }

    public void focusOn(Element element) {

    }

    public boolean changeFocus(boolean bl) {
        return false;
    }

    public void mouseMoved(double mouseX, double mouseY) {

    }




}

