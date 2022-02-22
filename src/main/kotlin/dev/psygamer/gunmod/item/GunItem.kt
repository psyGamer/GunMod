package dev.psygamer.gunmod.item

import net.minecraft.world.item.Item
import dev.psygamer.gunmod.GunMod

class GunItem(properties: Properties) : Item(properties) {
	
	companion object {
		
		val SPEED_MODIFIER_HOLDING_UUID: UUID = UUID.fromString("e8dcdb26-9037-11ec-b909-0242ac120002")
		val SPEED_MODIFIER_AIMING_UUID: UUID = UUID.fromString("b755fde3-c377-4d5c-8773-986f33fc0e08")
	}
	
	init {
		this.setRegistryName(GunMod.MODID, "gunitem")
	}
}