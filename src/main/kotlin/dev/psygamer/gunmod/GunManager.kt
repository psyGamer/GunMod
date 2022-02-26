package dev.psygamer.gunmod

import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Player
import net.minecraftforge.client.event.*
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.lwjgl.glfw.GLFW.*
import dev.psygamer.gunmod.handler.ShootingHandler
import dev.psygamer.gunmod.item.GunItem
import dev.psygamer.gunmod.registry.ItemRegistry

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
			handleAiming(event)
		
		event.isCanceled = event.action == GLFW_PRESS && event.button == GLFW_MOUSE_BUTTON_1
	}
	
	/**
	 * Updates the aiming state if the player is no longer holding a gun.
	 * @return the current aiming state
	 */
	fun isAiming(player: Player): Boolean {
		if (aiming && player.mainHandItem.item !is GunItem)
			aiming = false
		
		return aiming
	}
	
	@SubscribeEvent
	fun onFOVModifier(event: FOVModifierEvent) {
		if (isAiming(event.entity) && Minecraft.getInstance().options.cameraType.isFirstPerson)
			event.newfov = GunItem.FOV_MODIFIER
	}
	
	@SubscribeEvent
	fun onMovementInputUpdate(event: MovementInputUpdateEvent) {
		if (isAiming(event.player)) {
			event.input.forwardImpulse *= GunItem.AIMING_SPEED_MODIFIER
			event.input.leftImpulse *= GunItem.AIMING_SPEED_MODIFIER
			return // Only apply 1 modifier
		}
		
		// Check if holding
		if (event.player.mainHandItem.item == ItemRegistry.GUN_ITEM) {
			event.input.forwardImpulse *= GunItem.HOLDING_SPEED_MODIFIER
			event.input.leftImpulse *= GunItem.HOLDING_SPEED_MODIFIER
		}
	}
	
	private fun handleAiming(event: InputEvent.RawMouseEvent) {
		if (event.action == GLFW_PRESS)
			aiming = true
		else if (event.action == GLFW_RELEASE)
			aiming = false
	}
	
	private fun fire(player: LocalPlayer) {
		//SShootWeaponPacket(player).send()
		ShootingHandler.shootGunClientSide()
	}
	
	private fun isIngame(): Boolean {
		val mc = Minecraft.getInstance()
		
		if (mc.player == null || mc.screen != null || mc.overlay != null || !mc.mouseHandler.isMouseGrabbed)
			return false
		
		return mc.isWindowActive
	}
}