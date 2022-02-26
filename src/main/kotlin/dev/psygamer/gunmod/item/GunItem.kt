package dev.psygamer.gunmod.item

import net.minecraft.world.item.Item
import dev.psygamer.gunmod.GunMod

class GunItem(properties: Properties) : Item(properties) {
	
	companion object {
		
		// TODO: Make this dynamic based on the gun
		const val FOV_MODIFIER = 0.1f
		const val HOLDING_SPEED_MODIFIER = 0.8f
		const val AIMING_SPEED_MODIFIER = 0.2f
		const val BULLET_SPEED_BPT = 2962.0 / 3.6 / 20 // 2962km/h | 41.139 blocks / tick
		const val MAX_SHOT_STEPS = 5
	}
	
	init {
		this.setRegistryName(GunMod.MODID, "gunitem")
	}
}