package dev.psygamer.gunmod.handler.client

import net.minecraft.client.Minecraft
import dev.psygamer.gunmod.handler.AimingHandler
import dev.psygamer.gunmod.item.GunItem
import dev.psygamer.gunmod.network.bidirectional.PlayerAimingPacket
import dev.psygamer.gunmod.network.sendToServer

object ClientAimingHandler {
	
	private var aiming = false
	
	@JvmStatic
	fun setAiming(aiming: Boolean) {
		val localPlayer = Minecraft.getInstance().player ?: return
		
		this.aiming = aiming
		AimingHandler.setPlayerAiming(localPlayer, aiming)
	}
	
	@JvmStatic
	fun isAiming(): Boolean {
		if (Minecraft.getInstance().player?.mainHandItem?.item !is GunItem)
			this.aiming = false
		return this.aiming
	}
	
	fun sendUpdatePacket() {
		val player = Minecraft.getInstance().player ?: return
		PlayerAimingPacket(player.uuid, isAiming()).sendToServer()
	}
}