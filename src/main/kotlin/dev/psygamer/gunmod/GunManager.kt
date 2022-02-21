package dev.psygamer.gunmod

import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.ai.attributes.*
import net.minecraft.world.entity.player.Player
import net.minecraftforge.client.event.*
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.lwjgl.glfw.GLFW.*
import dev.psygamer.gunmod.item.GunItem
import dev.psygamer.gunmod.network.send
import dev.psygamer.gunmod.network.to_server.SShootWeaponPacket

@Mod.EventBusSubscriber
object GunManager {
	
	@JvmStatic
	@get:JvmName("isAiming")
	var aiming = false
		private set
	
	@SubscribeEvent
	fun onInput(event: InputEvent.RawMouseEvent) {
		if (event.button != GLFW_MOUSE_BUTTON_1 &&
			event.button != GLFW_MOUSE_BUTTON_2 || !isIngame()
		) return
		
		val player = Minecraft.getInstance().player
		val usedItem = player!!.mainHandItem
		
		if (usedItem.item !is GunItem)
			return
		
		if (event.button == GLFW_MOUSE_BUTTON_1 && event.action == GLFW_PRESS)
			fire(player)
		else if (event.button == GLFW_MOUSE_BUTTON_2)
			handleAiming(player, event)
		
		event.isCanceled = event.action == GLFW_PRESS && event.button == GLFW_MOUSE_BUTTON_1
	}
	
	@SubscribeEvent
	fun onFOVModifier(event: FOVModifierEvent) {
		event.newfov =
			if (aiming && Minecraft.getInstance().options.cameraType.isFirstPerson)
				event.fov - 30
			else
				event.fov
	}
	
	private fun handleAiming(player: Player, event: InputEvent.RawMouseEvent) {
		if (event.action == GLFW_PRESS) {
			if (player.attributes.hasModifier(Attributes.MOVEMENT_SPEED, GunItem.SPEED_MODIFIER_AIMING_UUID))
				return
			
			aiming = true
			player.attributes.getInstance(Attributes.MOVEMENT_SPEED)
					?.addTransientModifier(AttributeModifier(GunItem.SPEED_MODIFIER_AIMING_UUID, "Speed modifier", 0.5,
															 AttributeModifier.Operation.MULTIPLY_TOTAL))
		} else if (event.action == GLFW_RELEASE) {
			aiming = false
			player.attributes.getInstance(Attributes.MOVEMENT_SPEED)
					?.removeModifier(GunItem.SPEED_MODIFIER_AIMING_UUID)
		}
	}
	
	private fun fire(player: LocalPlayer) {
		SShootWeaponPacket(player).send()
	}
	
	private fun isIngame(): Boolean {
		val mc = Minecraft.getInstance()
		
		if (mc.player == null || mc.screen != null || mc.overlay != null || !mc.mouseHandler.isMouseGrabbed)
			return false
		
		return mc.isWindowActive
	}
}