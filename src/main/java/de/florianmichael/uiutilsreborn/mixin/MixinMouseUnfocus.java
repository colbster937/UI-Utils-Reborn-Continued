package de.florianmichael.uiutilsreborn.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MixinMouseUnfocus {

    @Inject(method = "onMouseButton(JIII)V", at = @At("HEAD"))
    private void unfocusOnClick(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen == null || window != mc.getWindow().getHandle()) return;

        double x = ((Mouse)(Object)this).getX() * (double) mc.getWindow().getScaledWidth()  / (double) mc.getWindow().getWidth();
        double y = ((Mouse)(Object)this).getY() * (double) mc.getWindow().getScaledHeight() / (double) mc.getWindow().getHeight();

        Screen s = (Screen) mc.currentScreen;
        Element f = s.getFocused();

        if (f instanceof TextFieldWidget tf && tf.isFocused()) {
            if (!tf.isMouseOver(x, y)) {
                tf.setFocused(false);
                s.setFocused(null);
            }
        }
    }
}
