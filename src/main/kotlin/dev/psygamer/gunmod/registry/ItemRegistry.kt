package dev.psygamer.gunmod.registry

import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import dev.psygamer.gunmod.item.GunItem

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object ItemRegistry {
	
	val GUN_ITEM = GunItem(Item.Properties()
								   .stacksTo(1)
								   .rarity(Rarity.EPIC)
								   .durability(69)
	)
	
	@SubscribeEvent
	fun onItemRegistry(event: RegistryEvent.Register<Item>) {
		event.registry.register(GUN_ITEM)
	}
}