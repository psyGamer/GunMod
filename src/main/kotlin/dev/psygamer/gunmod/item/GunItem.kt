package dev.psygamer.gunmod.item

import com.google.common.collect.*
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.*
import net.minecraft.world.item.Item
import java.util.*
import dev.psygamer.gunmod.GunMod

class GunItem(properties: Properties) : Item(properties) {
	
	private val defaultModifiers: Multimap<Attribute, AttributeModifier>
	
	companion object {
		
		val SPEED_MODIFIER_HOLDING_UUID: UUID = UUID.fromString("e8dcdb26-9037-11ec-b909-0242ac120002")
		val SPEED_MODIFIER_AIMING_UUID: UUID = UUID.fromString("b755fde3-c377-4d5c-8773-986f33fc0e08")
	}
	
	init {
		this.setRegistryName(GunMod.MODID, "gunitem")
		
		// Apply speed modifier when holding the gun
		val builder = ImmutableMultimap.builder<Attribute, AttributeModifier>()
		builder.put(Attributes.MOVEMENT_SPEED, AttributeModifier(SPEED_MODIFIER_HOLDING_UUID, "Speed modifier", 0.8,
																 AttributeModifier.Operation.MULTIPLY_TOTAL))
		@Suppress("UNCHECKED_CAST") // WTF Kotlin
		this.defaultModifiers = builder.build() as Multimap<Attribute, AttributeModifier>
	}
	
	override fun getDefaultAttributeModifiers(pSlot: EquipmentSlot): Multimap<Attribute, AttributeModifier> {
		return defaultModifiers
	}
}