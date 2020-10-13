package net.glasslauncher.hmifabric.mixin;

import net.glasslauncher.hmifabric.Config;
import net.glasslauncher.hmifabric.GuiOverlay;
import net.glasslauncher.hmifabric.HowManyItems;
import net.glasslauncher.hmifabric.Utils;
import net.minecraft.client.gui.screen.container.ContainerBase;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerBase.class)
public class MixinContainerBase {

    @Inject(method = "keyPressed(CI)V", at = @At(value = "HEAD"))
    private void keyPressed(char character, int key, CallbackInfo ci) {
        if (Keyboard.getEventKey() == Config.toggleOverlay.key && Utils.keybindValid(Config.toggleOverlay)) {
            if (Utils.isGuiOpen(ContainerBase.class) && !GuiOverlay.searchBoxFocused()) {
                System.out.println("E");
                Config.overlayEnabled = !Config.overlayEnabled;
                Config.writeConfig();
                if (HowManyItems.thisMod.overlay != null) HowManyItems.thisMod.overlay.toggle();
            }
        }
    }
}
