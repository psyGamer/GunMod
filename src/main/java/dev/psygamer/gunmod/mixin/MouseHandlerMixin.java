package dev.psygamer.gunmod.mixin;

import dev.psygamer.gunmod.GunManager;

import net.minecraft.client.MouseHandler;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("ALL")
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	
	@Shadow
	private double accumulatedDX;
	
	@Shadow
	private double accumulatedDY;
	
	@ModifyVariable(method = "turnPlayer", ordinal = 5,
			at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0,
					target = "Lnet/minecraft/client/MouseHandler;accumulatedDX:D"))
	public double onTurnPlayerFinalDX(double finalDX) {
		return GunManager.isAiming() ? finalDX * 0.1 : finalDX;
	}
	
	@ModifyVariable(method = "turnPlayer", ordinal = 6,
			at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0,
					target = "Lnet/minecraft/client/MouseHandler;accumulatedDX:D"))
	public double onTurnPlayerFinalDY(double finalDY) {
		return GunManager.isAiming() ? finalDY * 0.1 : finalDY;
	}

//	@ModifyVariable()
//	@Inject(method = "turnPlayer",
//			at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0,
//					target = "Lnet/minecraft/client/MouseHandler;accumulatedDX:D"),
//			locals = LocalCapture.CAPTURE_FAILHARD)
//	public void onTurnPlayer(
//			CallbackInfo info,
//			double d0, double d1, double sensitivity,
//			double d5, double d6, double finalDX, double finalDY
//	) {
//		Pair<Double, Double> pair = GunManager.mouseDXY(sensitivity, this.accumulatedDX, this.accumulatedDY);
//
//		finalDX = pair.getFirst();
//		finalDY = pair.getSecond();
//	}
}
