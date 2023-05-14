package earth.terrarium.heracles.client.widgets.icon.background;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.client.widgets.upload.UploadModalItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class IconBackgroundModal extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/uploading.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 173;

    private double scrollAmount = 0;

    private final List<BackgroundModalItem> items = new ArrayList<>();
    private Consumer<ResourceLocation> callback;

    public IconBackgroundModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);
    }

    @Override
    protected void renderBackground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderUtils.bindTexture(TEXTURE);

        pose.pushPose();
        pose.translate(0, 0, 150);
        Gui.fill(pose, 0, 15, screenWidth, screenHeight, 0x80000000);
        Gui.blit(pose, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);

        renderChildren(pose, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int textX = (WIDTH - font.width("Choose Background")) / 2;
        font.draw(pose, "Choose Background", x + textX, y + 6, 0x404040);

        int y = this.y + 19;
        int x = this.x + 8;
        int tempY = y;
        tempY -= scrollAmount;

        try (var scissor = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, x, y, 152, 130)) {
            for (BackgroundModalItem item : items) {
                boolean hovering = mouseY >= y && mouseY <= y + 148;
                item.render(pose, scissor.stack(), x, tempY, mouseX, mouseY, hovering);
                tempY += 28;
            }
        }

        pose.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        int x = screenWidth / 2 - (WIDTH / 2);
        int y = screenHeight / 2 - (HEIGHT / 2);

        if (mouseX < x || mouseX > x + WIDTH || mouseY < y || mouseY > y + HEIGHT) {
            setVisible(false);
        }
        if (mouseX >= x + WIDTH - 18 && mouseX <= x + WIDTH - 7 && mouseY >= y + 5 && mouseY <= y + 16) {
            setVisible(false);
        }

        y += 19;
        x += 8;
        int tempY = y;
        tempY -= scrollAmount;

        for (BackgroundModalItem item : items) {
            if (mouseY >= y && mouseY <= y + 148) {
                if (mouseX >= x && mouseX <= x + UploadModalItem.WIDTH && mouseY >= tempY && mouseY <= tempY + 28) {
                    if (callback != null) {
                        callback.accept(item.texture());
                    }
                    return true;
                }
            }
            tempY += 28;
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollAmount * 10, 0.0D, Math.max(0, (this.items.size() * 28) - 130));
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible &&
            mouseX >= (screenWidth / 2f) - (WIDTH / 2f) && mouseX <= (screenWidth / 2f) + (WIDTH / 2f) &&
            mouseY >= (screenHeight / 2f) - (HEIGHT / 2f) && mouseY <= (screenHeight / 2f) + (HEIGHT / 2f);
    }

    public void update(Collection<ResourceLocation> textures, Consumer<ResourceLocation> callback) {
        this.items.clear();
        textures.forEach(texture -> this.items.add(new BackgroundModalItem(texture)));
        this.callback = callback;
    }
}