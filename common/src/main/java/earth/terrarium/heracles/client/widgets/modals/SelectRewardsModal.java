package earth.terrarium.heracles.client.widgets.modals;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.Mth;

import java.util.*;
import java.util.function.Consumer;

public class SelectRewardsModal extends BaseModal {

    private final Map<String, DisplayWidget> widgets = new LinkedHashMap<>();
    private final Set<String> selected = new HashSet<>();

    private Consumer<Set<String>> callback = null;
    private int maxSelectable = 1;

    private double scrollAmount;
    private int lastFullHeight;

    public SelectRewardsModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, (int) (screenWidth * 0.75f), (int) (screenHeight * 0.8f));

        addChild(Button.builder(ConstantComponents.Rewards.CLAIM_REWARD, b -> {
            this.setVisible(false);
            if (callback != null) {
                callback.accept(selected);
            }
        }).bounds(this.x + this.width - 58, this.y + this.height - 20, 50, 16).build());

        this.lastFullHeight = this.height - 42;
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitNineSliced(TEXTURE, x, y, width, height, 4, 4, 4, 4, 128, 128, 0, 0);
        graphics.blitNineSliced(TEXTURE, x + 7, y + 18, width - 14, height - 40, 1, 1, 1, 1, 128, 128, 128, 0);

        renderChildren(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawString(
            font,
            "Select " + maxSelectable + " Reward" + (maxSelectable > 1 ? "s" : ""), x + 10, y + 6, 0x404040,
            false
        );
        graphics.drawString(
            font,
            selected.size() + "/" + maxSelectable, x + 10, y + height - 15, 0x404040,
            false
        );

        int fullHeight = 0;
        int x = this.x + 10;
        int y = this.y + 19;
        int width = this.width - 20;
        int height = this.height - 46;


        try (var scissor = RenderUtils.createScissor(Minecraft.getInstance(), graphics, x - 2, y, width + 4, height)) {
            for (var entry : this.widgets.entrySet()) {
                DisplayWidget widget = entry.getValue();
                String id = entry.getKey();
                var itemheight = widget.getHeight(width) + 4;
                if (mouseY >= y - this.scrollAmount && mouseY < y + itemheight - this.scrollAmount && mouseY >= this.y + 19 && mouseY < this.y + this.height - 24 && mouseX >= this.x + 10 && mouseX < this.x + this.width - 10) {
                    CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
                }
                if (this.selected.contains(id)) {
                    graphics.renderOutline(x - 1, y + 1 - (int) this.scrollAmount, width + 2, itemheight - 2, 0xFFA8EFF0);
                }
                widget.render(graphics, scissor.stack(), x, y + 2 - (int) this.scrollAmount, width, mouseX, mouseY, this.isMouseOver(mouseX, mouseY), partialTick);
                y += itemheight;
                fullHeight += itemheight;
            }
            this.lastFullHeight = fullHeight;
        }
        this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0D, Math.max(0, this.lastFullHeight - height));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollAmount * 10, 0.0D, Math.max(0, this.lastFullHeight - this.height));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        if (result) return true;
        if (isMouseOver(mouseX, mouseY)) {
            int y = this.y + 19;
            for (var entry : this.widgets.entrySet()) {
                DisplayWidget widget = entry.getValue();
                String id = entry.getKey();
                var itemheight = widget.getHeight(this.width - 20) + 4;
                if (mouseY >= y - this.scrollAmount && mouseY <= y + itemheight - this.scrollAmount && mouseY >= this.y + 19 && mouseY < this.y + this.height - 24 && mouseX >= this.x + 10 && mouseX < this.x + this.width - 10) {
                    if (this.selected.contains(id)) {
                        this.selected.remove(id);
                    } else if (selected.size() < maxSelectable) {
                        this.selected.add(id);
                    }
                    return true;
                }
                y += itemheight;
            }
        }
        return false;
    }

    public void updateRewards(Collection<QuestReward<?>> rewards, int maxSelectable, Consumer<Set<String>> callback) {
        this.widgets.clear();
        this.selected.clear();
        this.maxSelectable = maxSelectable;
        this.callback = callback;
        for (QuestReward<?> reward : rewards) {
            DisplayWidget widget = QuestRewardWidgets.create(reward);
            if (widget == null) continue;
            this.widgets.put(reward.id(), widget);
        }
    }
}
