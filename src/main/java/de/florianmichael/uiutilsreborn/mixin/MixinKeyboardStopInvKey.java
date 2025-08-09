package de.florianmichael.uiutilsreborn.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class MixinKeyboardStopInvKey {
    @Inject(method = "onKey(JIIII)V", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen == null || window != mc.getWindow().getHandle()) return;

        Screen s = (Screen) mc.currentScreen;
        Element f = s.getFocused();
        boolean textFocused = f instanceof TextFieldWidget tf && tf.isFocused();
        if (!textFocused) return;

        if (mc.options.inventoryKey.matchesKey(key, scancode)) {
            ci.cancel();
        }
    }
}
