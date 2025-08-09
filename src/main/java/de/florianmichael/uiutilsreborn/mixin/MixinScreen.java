/*
 * This file is part of UI-Utils-Reborn - https://github.com/FlorianMichael/UI-Utils-Reborn
 * Copyright (C) 2022-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.uiutilsreborn.mixin;

import de.florianmichael.uiutilsreborn.UIUtilsReborn;
import de.florianmichael.uiutilsreborn.widget.ExploitButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.joml.Matrix4f;
import java.awt.Color;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Shadow public int width;

    @Shadow public abstract List<? extends Element> children();

    @Shadow protected MinecraftClient client;

    @Shadow protected TextRenderer textRenderer;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"))
    private void hookFeatureButtons(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if (!UIUtilsReborn.isEnabled()) return;

        final List<ExploitButtonWidget> buttons = UIUtilsReborn.fromScreen((Screen) (Object) this);

        if (buttons.isEmpty()) return;

        int buttonHeight = 0;
        for (ExploitButtonWidget next : buttons) {
            next.setX(this.width - next.getWidth() - UIUtilsReborn.BOUND);
            next.setY(UIUtilsReborn.BOUND + buttonHeight);

            this.addDrawableChild(next);
            buttonHeight += UIUtilsReborn.BUTTON_DIFF;
        }
    }


    @Inject(method = "render", at = @At("HEAD"))
    private void drawInfo(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!UIUtilsReborn.isEnabled() || this.client.player == null || UIUtilsReborn.fromScreen((Screen) (Object) this).isEmpty()) return;

        int x = this.client.getWindow().getScaledWidth() - 5;
        int y = this.client.getWindow().getScaledHeight() - textRenderer.fontHeight - 5;

        String info1 = Text.translatable("gui.ui-utils-reborn.info1").getString() + client.player.currentScreenHandler.syncId;
        String info2 = Text.translatable("gui.ui-utils-reborn.info2").getString() + client.player.currentScreenHandler.getRevision();

        context.drawText(textRenderer, info1, x - textRenderer.getWidth(info1), y - 10, Color.WHITE.getRGB(), false);
        context.drawText(textRenderer, info2, x - textRenderer.getWidth(info2), y, Color.WHITE.getRGB(), false);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void hideFeatureButtons(CallbackInfo ci) {
        if (UIUtilsReborn.isEnabled()) return;

        for (Element child : children()) {
            if (child instanceof ExploitButtonWidget buttonWidget) {
                buttonWidget.visible = false;
            }
        }
    }

}
