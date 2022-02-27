package dev.psygamer.gunmod.handler.client

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import dev.psygamer.gunmod.item.GunItem
import dev.psygamer.gunmod.util.*

@EventBusSubscriber(Dist.CLIENT)
object ClientInputHandler {
	
	private var prevAimingState = false
	private var shootGunPending = false
	
	@SubscribeEvent
	fun onRenderTick(event: TickEvent.RenderTickEvent) {
		if (LOCAL_PLAYER?.mainHandItem?.item !is GunItem || !isIngame())
			return
		
		val options = MINECRAFT.options
		
		if (options.keyUse.isDown && !ClientAimingHandler.isAiming())
			ClientAimingHandler.setAiming(true)
		else if (!options.keyUse.isDown && ClientAimingHandler.isAiming())
			ClientAimingHandler.setAiming(false)
		
		if (options.keyAttack.isDown)
			shootGunPending = true
	}
	
	@SubscribeEvent
	fun onClientTick(event: TickEvent.ClientTickEvent) {
		if (prevAimingState != ClientAimingHandler.isAiming())
			ClientAimingHandler.sendUpdatePacket()
		prevAimingState = ClientAimingHandler.isAiming()
		
		if (shootGunPending)
			ClientShootingHandler.shootGun()
		shootGunPending = false
	}
	
	private fun isIngame(): Boolean {
		if (MINECRAFT.player == null || MINECRAFT.screen != null ||
			MINECRAFT.overlay != null || !MINECRAFT.mouseHandler.isMouseGrabbed
		)
			return false
		
		return MINECRAFT.isWindowActive
	}
}