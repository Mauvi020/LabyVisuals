package net.labymod.addons.labyvisuals.hud;

import net.labymod.addons.labyvisuals.LabyVisualsAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.entity.player.Inventory;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.SimpleHudWidget;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.render.ItemStackVisualizer;
import net.labymod.api.client.render.draw.RectangleRenderer;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.client.world.item.ItemStack;

/**
 * Live inventory preview as a HUD widget: the main inventory (3x9), the hotbar
 * with the selected slot highlighted and optionally the armor slots.
 */
public class InventoryHudWidget extends SimpleHudWidget<HudWidgetConfig> {

  private static final int CELL = 18;
  private static final int ITEM = 16;
  private static final int COLS = 9;
  private static final int MAIN_ROWS = 3;
  private static final int ROW_GAP = 2;
  private static final int PADDING = 1;

  private static final int PANEL = 0xB4000000;
  private static final int SLOT_BORDER = 0x46000000;
  private static final int SLOT_BACKGROUND = 0x66000000;
  private static final int SELECTED_OUTLINE = 0xFFFFFFFF;

  private final LabyVisualsAddon addon;

  public InventoryHudWidget(LabyVisualsAddon addon) {
    super("inventoryhud", HudWidgetConfig.class);
    this.addon = addon;
    this.bindCategory(addon.widgetCategory());
  }

  @Override
  public boolean isVisibleInGame() {
    return this.addon.isEnabled() && this.addon.config().inventoryHud().get();
  }

  @Override
  public void render(RenderPhase renderPhase, ScreenContext context, boolean isEditorContext,
      HudSize hudSize) {
    if (renderPhase == RenderPhase.UPDATE_SIZE) {
      hudSize.set(this.width(), this.height());
      return;
    }

    if (!isEditorContext
        && (!this.addon.isEnabled() || !this.addon.config().inventoryHud().get())) {
      return;
    }

    ClientPlayer player = isEditorContext
        ? null : Laby.labyAPI().minecraft().getClientPlayer();
    boolean armor = this.addon.config().inventoryShowArmor().get();

    float width = this.width();
    float height = this.height();
    RectangleRenderer rectangles = Laby.references().rectangleRenderer();
    Stack stack = context.stack();

    rectangles.renderRectangle(stack, 0.0F, 0.0F, width, height, PANEL);

    float gridX = PADDING + (armor ? CELL : 0);
    float gridY = PADDING;
    float hotbarY = gridY + MAIN_ROWS * CELL + ROW_GAP;

    // Slot backgrounds
    if (armor) {
      for (int i = 0; i < 4; i++) {
        this.drawSlot(rectangles, stack, PADDING, gridY + i * CELL);
      }
    }
    for (int row = 0; row < MAIN_ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        this.drawSlot(rectangles, stack, gridX + col * CELL, gridY + row * CELL);
      }
    }
    for (int col = 0; col < COLS; col++) {
      this.drawSlot(rectangles, stack, gridX + col * CELL, hotbarY);
    }

    if (player != null) {
      Inventory inventory = player.inventory();
      boolean counts = this.addon.config().inventoryShowCounts().get();
      ItemStackVisualizer visualizer = Laby.references().itemStackVisualizer();

      if (armor) {
        // 39 = helmet ... 36 = boots (rendered top to bottom)
        for (int i = 0; i < 4; i++) {
          this.drawItem(visualizer, context, inventory.itemStackAt(39 - i), counts,
              PADDING, gridY + i * CELL);
        }
      }
      for (int row = 0; row < MAIN_ROWS; row++) {
        for (int col = 0; col < COLS; col++) {
          int slot = 9 + row * COLS + col;
          this.drawItem(visualizer, context, inventory.itemStackAt(slot), counts,
              gridX + col * CELL, gridY + row * CELL);
        }
      }
      for (int col = 0; col < COLS; col++) {
        this.drawItem(visualizer, context, inventory.itemStackAt(col), counts,
            gridX + col * CELL, hotbarY);
      }

      int selected = inventory.getSelectedIndex();
      if (selected >= 0 && selected < COLS) {
        this.drawOutline(rectangles, stack,
            gridX + selected * CELL, hotbarY, ITEM, ITEM, SELECTED_OUTLINE);
      }
    } else {
      // Editor preview: fake items
      rectangles.renderRectangle(stack, gridX + 2 * CELL + 3, gridY + 3, 10, 10, 0x50FFFFFF);
      rectangles.renderRectangle(stack, gridX + 5 * CELL + 3, gridY + CELL + 3, 10, 10, 0x50FFFFFF);
      rectangles.renderRectangle(stack, gridX + 12 * CELL + 3, gridY + 2 * CELL + 3, 10, 10,
          0x50FFFFFF);
      rectangles.renderRectangle(stack, gridX + 3 * CELL + 3, hotbarY + 3, 10, 10, 0x50FFFFFF);
      rectangles.renderRectangle(stack, gridX + 4 * CELL + 3, hotbarY + 3, 10, 10, 0x50FFFFFF);
    }
  }

  private void drawSlot(RectangleRenderer rectangles, Stack stack, float x, float y) {
    rectangles.renderRectangle(stack, x - 1, y - 1, ITEM + 2, ITEM + 2, SLOT_BORDER);
    rectangles.renderRectangle(stack, x, y, ITEM, ITEM, SLOT_BACKGROUND);
  }

  private void drawItem(ItemStackVisualizer visualizer, ScreenContext context,
      ItemStack itemStack, boolean counts, float x, float y) {
    if (itemStack == null || itemStack.getSize() <= 0) {
      return;
    }
    visualizer.submitItem(context, itemStack, (int) x, (int) y, counts);
  }

  private void drawOutline(RectangleRenderer rectangles, Stack stack, float x, float y, float w,
      float h, int color) {
    rectangles.renderRectangle(stack, x - 1, y - 1, w + 2, 1, color);
    rectangles.renderRectangle(stack, x - 1, y + h, w + 2, 1, color);
    rectangles.renderRectangle(stack, x - 1, y, 1, h, color);
    rectangles.renderRectangle(stack, x + w, y, 1, h, color);
  }

  private float width() {
    return PADDING + (this.addon.config().inventoryShowArmor().get() ? CELL : 0)
        + COLS * CELL + PADDING;
  }

  private float height() {
    return PADDING + MAIN_ROWS * CELL + ROW_GAP + CELL + PADDING;
  }
}