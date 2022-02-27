package dev.psygamer.gunmod.handler.client

import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import dev.psygamer.gunmod.item.GunItem

@EventBusSubscriber(Dist.CLIENT)
object ClientInputHandler {
	
	private var prevAimingState = false
	private var shootGunPending = false
	
	@SubscribeEvent
	fun onRenderTick(event: TickEvent.RenderTickEvent) {
		if (Minecraft.getInstance().player?.mainHandItem?.item !is GunItem || !isIngame())
			return
		
		val options = Minecraft.getInstance().options
		
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
		val mc = Minecraft.getInstance()
		if (mc.player == null || mc.screen != null || mc.overlay != null || !mc.mouseHandler.isMouseGrabbed)
			return false
		
		return mc.isWindowActive
	}
}