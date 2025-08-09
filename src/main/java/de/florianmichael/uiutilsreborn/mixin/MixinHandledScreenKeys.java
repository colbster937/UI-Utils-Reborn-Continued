package de.florianmichael.uiutilsreborn.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreenKeys {
    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    private void keyPressed(int key, int scan, int mods, CallbackInfoReturnable<Boolean> cir) {
        Screen s = (Screen)(Object)this;
        Element f = s.getFocused();
        if (f instanceof TextFieldWidget tf && tf.isFocused()) {
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) return;
            tf.keyPressed(key, scan, mods);
            cir.setReturnValue(true);
        }
    }
}
