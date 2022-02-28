package dev.psygamer.gunmod.handler.client

import net.minecraft.client.Minecraft
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import dev.psygamer.gunmod.item.GunItem
import dev.psygamer.gunmod.network.sendToServer
import dev.psygamer.gunmod.network.to_server.ServerboundShootGunPacket

@EventBusSubscriber
object ClientShootingHandler {
	
	private var shotTimeout = 0
	
	fun shootGun() {
		if (shotTimeout > 0)
			return
		
		val player = Minecraft.getInstance().player ?: return
		
		ServerboundShootGunPacket(player).sendToServer()
		shotTimeout = GunItem.SHOT_DELAY_TICKS
	}
	
	@SubscribeEvent
	fun onClientTick(event: TickEvent.ClientTickEvent) {
		shotTimeout = maxOf(0, --shotTimeout)
	}
}