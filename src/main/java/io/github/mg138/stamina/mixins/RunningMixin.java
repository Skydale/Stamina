package io.github.mg138.stamina.mixins;

import io.github.mg138.stamina.stamina.StaminaManager;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class RunningMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(
            at = @At("TAIL"),
            method = "onPlayerMove(Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket;)V"
    )
    public void stamina_onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (this.player.isSprinting()) {
            StaminaManager.INSTANCE.reduceStamina(this.player, 5);
        }
    }
}
