package com.swack.janemoose.gui;

import com.swack.janemoose.entity.BaseRoadPaverEntity;
import com.swack.janemoose.entity.DieselRoadPaverEntity;
import com.swack.janemoose.network.RoadPaverActionPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DieselRoadPaverScreen extends AbstractContainerScreen<DieselRoadPaverMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("janemoose", "textures/gui/diesel_road_paver.png");

    private @Nullable EditBox nameBox;
    private boolean initializedName = false;
    private @Nullable Button toggleButton;
    private @Nullable Button reverseButton;
    private @Nullable Button lockButton;
    private @Nullable Button workButton;
    private @Nullable Button lightButton;
    private @Nullable Button hologramButton;

    public DieselRoadPaverScreen(DieselRoadPaverMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 210, 204);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        String currentVal = this.nameBox != null ? this.nameBox.getValue() : "";
        if (currentVal.isEmpty() && !this.initializedName) {
            this.initializedName = true;
            if (this.menu.getPaver() != null && this.menu.getPaver().hasCustomName()) {
                currentVal = this.menu.getPaver().getCustomName().getString();
            } else {
                currentVal = "Jane Moose Diesel Paver";
            }
        }

        this.nameBox = new EditBox(this.font, this.leftPos + 40, this.topPos - 18, 120, 14, Component.literal("Paver Name"));
        this.nameBox.setMaxLength(32);
        this.nameBox.setValue(currentVal);
        this.addRenderableWidget(this.nameBox);

        this.addRenderableWidget(
                Button.builder(Component.literal("Set"), button -> {
                    if (this.nameBox != null && this.menu.getPaver() != null) {
                        String newName = this.nameBox.getValue().trim();
                        sendAction(RoadPaverActionPayload.ACTION_RENAME, newName);
                        this.nameBox.setFocused(false);
                    }
                }).bounds(this.leftPos + 165, this.topPos - 18, 38, 14).build()
        );

        // Column 1: Start, Work Mode, Hologram
        this.toggleButton = this.addRenderableWidget(
                Button.builder(this.getToggleButtonLabel(), button -> {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
                    }
                }).bounds(this.leftPos + 124, this.topPos + 12, 38, 12).build()
        );

        this.workButton = this.addRenderableWidget(
                Button.builder(this.getWorkButtonLabel(), button -> {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 3);
                    }
                }).bounds(this.leftPos + 124, this.topPos + 26, 38, 12).build()
        );

        this.hologramButton = this.addRenderableWidget(
                Button.builder(this.getHologramButtonLabel(), button -> {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 5);
                    }
                }).bounds(this.leftPos + 124, this.topPos + 40, 38, 12).build()
        );

        // Column 2: Reverse, Light, Lock
        this.reverseButton = this.addRenderableWidget(
                Button.builder(this.getReverseButtonLabel(), button -> {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 1);
                    }
                }).bounds(this.leftPos + 165, this.topPos + 12, 38, 12).build()
        );

        this.lightButton = this.addRenderableWidget(
                Button.builder(this.getLightButtonLabel(), button -> {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 4);
                    }
                }).bounds(this.leftPos + 165, this.topPos + 26, 38, 12).build()
        );

        this.lockButton = this.addRenderableWidget(
                Button.builder(this.getLockButtonLabel(), button -> {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2);
                    }
                }).bounds(this.leftPos + 165, this.topPos + 40, 38, 12).build()
        );
    }

    private void sendAction(int action, String str) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null && this.menu.getPaver() != null) {
            mc.getConnection().send(new ServerboundCustomPayloadPacket(
                    new RoadPaverActionPayload(this.menu.getPaver().getId(), action, str)
            ));
        }
    }

    private Component getToggleButtonLabel() {
        int mode = this.menu.getRunMode();
        if (mode == BaseRoadPaverEntity.MODE_FORWARD) {
            return Component.literal("Halt").withStyle(ChatFormatting.RED);
        }
        return Component.literal("Start").withStyle(ChatFormatting.GREEN);
    }

    private Component getReverseButtonLabel() {
        int mode = this.menu.getRunMode();
        if (mode == BaseRoadPaverEntity.MODE_REVERSE) {
            return Component.literal("Rev").withStyle(ChatFormatting.YELLOW);
        }
        return Component.literal("Rev").withStyle(ChatFormatting.GRAY);
    }

    private Component getWorkButtonLabel() {
        return this.menu.isWorkMode()
                ? Component.literal("5x1 ON").withStyle(ChatFormatting.GREEN)
                : Component.literal("5x1 OFF").withStyle(ChatFormatting.GOLD);
    }

    private Component getLightButtonLabel() {
        return this.menu.isWorkLightEnabled()
                ? Component.literal("Light").withStyle(ChatFormatting.GOLD)
                : Component.literal("Light").withStyle(ChatFormatting.GRAY);
    }

    private Component getHologramButtonLabel() {
        return this.menu.isHologramEnabled()
                ? Component.literal("Holo ON").withStyle(ChatFormatting.AQUA)
                : Component.literal("Holo OFF").withStyle(ChatFormatting.GRAY);
    }

    private Component getLockButtonLabel() {
        int lock = this.menu.getCardinalLock();
        return switch (lock) {
            case BaseRoadPaverEntity.LOCK_NORTH -> Component.literal("Lock: N").withStyle(ChatFormatting.AQUA);
            case BaseRoadPaverEntity.LOCK_SOUTH -> Component.literal("Lock: S").withStyle(ChatFormatting.AQUA);
            case BaseRoadPaverEntity.LOCK_WEST -> Component.literal("Lock: W").withStyle(ChatFormatting.AQUA);
            case BaseRoadPaverEntity.LOCK_EAST -> Component.literal("Lock: E").withStyle(ChatFormatting.AQUA);
            default -> Component.literal("Lock: Off").withStyle(ChatFormatting.GRAY);
        };
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.toggleButton != null) this.toggleButton.setMessage(this.getToggleButtonLabel());
        if (this.reverseButton != null) this.reverseButton.setMessage(this.getReverseButtonLabel());
        if (this.workButton != null) this.workButton.setMessage(this.getWorkButtonLabel());
        if (this.lightButton != null) this.lightButton.setMessage(this.getLightButtonLabel());
        if (this.hologramButton != null) this.hologramButton.setMessage(this.getHologramButtonLabel());
        if (this.lockButton != null) this.lockButton.setMessage(this.getLockButtonLabel());
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        graphics.text(this.font, Component.literal("Fuel"), 8, 7, 0x404040, false);
        graphics.text(this.font, Component.literal("Pattern (5x1 Strip)"), 28, 8, 0x404040, false);
        graphics.text(this.font, Component.literal("Paving Material"), 24, 44, 0x404040, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = this.leftPos;
        int yo = this.topPos;

        graphics.fill(xo, yo - 22, xo + this.imageWidth, yo - 1, 0xE61F2E1E);
        graphics.outline(xo, yo - 22, this.imageWidth, 21, 0xFF367C2B);
        graphics.text(this.font, Component.literal("Diesel Paver:"), xo + 5, yo - 15, 0xFFFFDE00, true);

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        // Render Diesel Fuel Gauge under the fuel slot (xo + 8, yo + 40, w = 16, h = 9)
        float frac = this.menu.getFuelFraction();
        int fillH = (int) (frac * 9.0F);
        if (fillH > 0) {
            int gaugeColor = frac > 0.25F ? 0xFF4CAF50 : 0xFFE53935;
            graphics.fill(xo + 8, yo + 40 + 9 - fillH, xo + 24, yo + 49, gaugeColor);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        int xo = this.leftPos;
        int yo = this.topPos;

        if (mouseX >= xo + 7 && mouseX <= xo + 25 && mouseY >= yo + 20 && mouseY <= yo + 38) {
            if (this.hoveredSlot == null || this.hoveredSlot.getItem().isEmpty()) {
                graphics.setTooltipForNextFrame(Component.literal("Fuel Tank Slot: Insert Diesel Canister or Ethanol Bucket").withStyle(ChatFormatting.GOLD), mouseX, mouseY);
            }
        } else if (mouseX >= xo + 29 && mouseX <= xo + 47 && mouseY >= yo + 20 && mouseY <= yo + 38) {
            if (this.hoveredSlot == null || this.hoveredSlot.getItem().isEmpty()) {
                graphics.setTooltipForNextFrame(Component.literal("Filter 1 (Far-Left): 5x1 left edge block (empty = use materials)").withStyle(ChatFormatting.AQUA), mouseX, mouseY);
            }
        } else if (mouseX >= xo + 47 && mouseX <= xo + 65 && mouseY >= yo + 20 && mouseY <= yo + 38) {
            if (this.hoveredSlot == null || this.hoveredSlot.getItem().isEmpty()) {
                graphics.setTooltipForNextFrame(Component.literal("Filter 2 (Mid-Left): 5x1 inner-left block (empty = use materials)").withStyle(ChatFormatting.AQUA), mouseX, mouseY);
            }
        } else if (mouseX >= xo + 65 && mouseX <= xo + 83 && mouseY >= yo + 20 && mouseY <= yo + 38) {
            if (this.hoveredSlot == null || this.hoveredSlot.getItem().isEmpty()) {
                graphics.setTooltipForNextFrame(Component.literal("Filter 3 (Center): 5x1 center lane block (empty = use materials)").withStyle(ChatFormatting.AQUA), mouseX, mouseY);
            }
        } else if (mouseX >= xo + 83 && mouseX <= xo + 101 && mouseY >= yo + 20 && mouseY <= yo + 38) {
            if (this.hoveredSlot == null || this.hoveredSlot.getItem().isEmpty()) {
                graphics.setTooltipForNextFrame(Component.literal("Filter 4 (Mid-Right): 5x1 inner-right block (empty = use materials)").withStyle(ChatFormatting.AQUA), mouseX, mouseY);
            }
        } else if (mouseX >= xo + 101 && mouseX <= xo + 119 && mouseY >= yo + 20 && mouseY <= yo + 38) {
            if (this.hoveredSlot == null || this.hoveredSlot.getItem().isEmpty()) {
                graphics.setTooltipForNextFrame(Component.literal("Filter 5 (Far-Right): 5x1 right edge block (empty = use materials)").withStyle(ChatFormatting.AQUA), mouseX, mouseY);
            }
        } else if (mouseX >= xo + 7 && mouseX <= xo + 25 && mouseY >= yo + 39 && mouseY <= yo + 50) {
            int fuel = this.menu.getFuel();
            int total = this.menu.getFuelTotal();
            Component text = fuel > 0
                    ? Component.literal("Fuel (Diesel / Ethanol): " + fuel + " / " + total + " mB (" + (int)(this.menu.getFuelFraction() * 100) + "%)").withStyle(ChatFormatting.GOLD)
                    : Component.literal("Fuel Tank Empty (Insert Diesel or Ethanol)").withStyle(ChatFormatting.RED);
            graphics.setTooltipForNextFrame(text, mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            if (this.nameBox != null && this.nameBox.isFocused()) {
                this.nameBox.setFocused(false);
                return true;
            }
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
            return true;
        }
        if (this.nameBox != null && this.nameBox.isFocused()) {
            if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_NUMPADENTER) {
                if (this.menu.getPaver() != null) {
                    sendAction(RoadPaverActionPayload.ACTION_RENAME, this.nameBox.getValue().trim());
                }
                this.nameBox.setFocused(false);
                return true;
            }
            if (this.nameBox.keyPressed(event) || this.nameBox.canConsumeInput()) {
                return true;
            }
        }
        return super.keyPressed(event);
    }
}
