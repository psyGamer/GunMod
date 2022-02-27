package dev.psygamer.gunmod.handler.client

import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.*
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import dev.psygamer.gunmod.item.GunItem
import dev.psygamer.gunmod.registry.ItemRegistry

@EventBusSubscriber
object ClientModifierHandler {
	
	// For mouse sensitivity changes see dev.psygamer.gunmod.mixin.MouseHandlerMixing
	
	@SubscribeEvent
	fun onFOVModifierEvent(event: FOVModifierEvent) {
		if (ClientAimingHandler.isAiming() && Minecraft.getInstance().options.cameraType.isFirstPerson)
			event.newfov = GunItem.FOV_MODIFIER
	}
	
	@SubscribeEvent
	fun onMovementInputUpdate(event: MovementInputUpdateEvent) {
		if (ClientAimingHandler.isAiming()) {
			event.input.forwardImpulse *= GunItem.AIMING_SPEED_MODIFIER
			event.input.leftImpulse *= GunItem.AIMING_SPEED_MODIFIER
			return // Only apply 1 modifier
		}
		
		if (event.player.mainHandItem.item == ItemRegistry.GUN_ITEM) {
			event.input.forwardImpulse *= GunItem.HOLDING_SPEED_MODIFIER
			event.input.leftImpulse *= GunItem.HOLDING_SPEED_MODIFIER
		}
	}
}