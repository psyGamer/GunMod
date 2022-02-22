package dev.psygamer.gunmod.mixin;

import dev.psygamer.gunmod.GunManager;
import dev.psygamer.gunmod.item.GunItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	
	@Shadow @Final
	private Minecraft minecraft;
	
	@Shadow
	private double accumulatedDX;
	
	@Shadow
	private double accumulatedDY;
	
	@ModifyVariable(method = "turnPlayer", ordinal = 5,
			at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0,
					target = "Lnet/minecraft/client/MouseHandler;accumulatedDX:D"))
	public double onTurnPlayerFinalDX(double finalDX) {
		return GunManager.isAiming() && minecraft.options.getCameraType().isFirstPerson()
				? finalDX * GunItem.FOV_MODIFIER
				: finalDX;
	}
	
	@ModifyVariable(method = "turnPlayer", ordinal = 6,
			at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0,
					target = "Lnet/minecraft/client/MouseHandler;accumulatedDX:D"))
	public double onTurnPlayerFinalDY(double finalDY) {
		return GunManager.isAiming() && minecraft.options.getCameraType().isFirstPerson()
				? finalDY * GunItem.FOV_MODIFIER
				: finalDY;
	}
}
