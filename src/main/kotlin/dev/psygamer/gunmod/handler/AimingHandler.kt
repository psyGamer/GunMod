package dev.psygamer.gunmod.handler

import net.minecraft.world.entity.player.Player
import dev.psygamer.gunmod.item.GunItem

object AimingHandler {
	
	private val aimingPlayers = mutableMapOf<Player, Boolean>()
	
	fun setPlayerAiming(player: Player, aiming: Boolean) {
		this.aimingPlayers[player] = aiming
	}
	
	fun isPlayerAiming(player: Player): Boolean {
		if (player.mainHandItem.item !is GunItem)
			setPlayerAiming(player, false)
		
		return this.aimingPlayers[player] ?: false
	}
}