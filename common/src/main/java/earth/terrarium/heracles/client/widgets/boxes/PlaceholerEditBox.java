package earth.terrarium.heracles.client.widgets.boxes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class PlaceholerEditBox extends EditBox {

    private boolean bordered = true;

    public PlaceholerEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        super.renderWidget(graphics, i, j, f);

        if (this.isVisible() && this.getValue().isEmpty() && !this.isFocused()) {
            Font font = Minecraft.getInstance().font;
            String placeholder = this.getMessage().getString();
            int x = this.bordered ? this.getX() + 4 : this.getX();
            int y = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
            graphics.drawString(
                font,
                placeholder, x, y, 0x808080,
                false
            );
        }
    }

    @Override
    public void setBordered(boolean enableBackgroundDrawing) {
        super.setBordered(enableBackgroundDrawing);
        this.bordered = enableBackgroundDrawing;
    }
}
