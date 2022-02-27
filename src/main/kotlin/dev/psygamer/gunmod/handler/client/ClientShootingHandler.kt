package dev.psygamer.gunmod.handler.client

import net.minecraft.client.Minecraft
import dev.psygamer.gunmod.network.sendToServer
import dev.psygamer.gunmod.network.to_server.ServerboundShootGunPacket

object ClientShootingHandler {
	
	fun shootGun() {
		val player = Minecraft.getInstance().player ?: return
		
		ServerboundShootGunPacket(player).sendToServer()
	}
}