package de.florianmichael.uiutilsreborn.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreenKeys {
    @Shadow private boolean ignoreTypedCharacter;

    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    private void keyPressed(int key, int scan, int mods, CallbackInfoReturnable<Boolean> cir) {
        Screen s = (Screen)(Object)this;
        Element f = s.getFocused();
        if (f instanceof TextFieldWidget tf && tf.isFocused()) {
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) return;

            tf.keyPressed(key, scan, mods);
            this.ignoreTypedCharacter = true;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "charTyped(CI)Z", at = @At("HEAD"), cancellable = true)
    private void charTyped(char chr, int mods, CallbackInfoReturnable<Boolean> cir) {
        Screen s = (Screen)(Object)this;
        Element f = s.getFocused();
        if (f instanceof TextFieldWidget tf && tf.isFocused() && tf.charTyped(chr, mods)) {
            this.ignoreTypedCharacter = true;
            cir.setReturnValue(true);
        }
    }
}
