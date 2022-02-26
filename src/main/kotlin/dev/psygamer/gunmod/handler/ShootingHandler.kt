package dev.psygamer.gunmod.handler

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import dev.psygamer.gunmod.item.GunItem
import dev.psygamer.gunmod.network.send
import dev.psygamer.gunmod.network.to_server.SShootWeaponPacket
import dev.psygamer.gunmod.util.math.*

class Shot(val shooter: LivingEntity, startVector: Vec3, viewVector: Vec3) {
	
	private val shotUpdateVector: Vec3
	
	var step: Int
		private set
	var position: Vec3
		private set
	val nextPosition
		get() = position + shotUpdateVector
	
	init {
		this.shotUpdateVector = viewVector * GunItem.BULLET_SPEED_BPT
		this.step = 0
		
		this.position = startVector
	}
	
	fun advanceStep() {
		this.position = this.nextPosition
		this.step++
	}
}

@EventBusSubscriber
object ShootingHandler {
	
	private val currentActiveShots = mutableListOf<Shot>()
	
	fun shootGunClientSide() {
		val localPlayer = Minecraft.getInstance().player
		if (localPlayer != null)
			SShootWeaponPacket(localPlayer).send()
	}
	
	fun shootGunServerSide(shot: Shot) {
		currentActiveShots.add(shot)
	}
	
	@SubscribeEvent
	fun onServerTick(event: TickEvent.ServerTickEvent) {
		currentActiveShots.forEach { shot ->
			val rayCast = Line(shot.position, shot.nextPosition)
			
			val entityHitResult = shootEntityRayCast(shot.shooter, rayCast)
								  { !it.isSpectator && it is LivingEntity } ?: return@forEach
			val blockHitResult = shootBlockRayCast(shot.shooter, rayCast)
			
			if (blockHitResult != null) {
				val entityHitPoint = entityHitResult.location
				val blockHitPoint = blockHitResult.location
				
				val firstHitPoint = firstVectorOnLine(rayCast, entityHitPoint, blockHitPoint)
				
				if (firstHitPoint !== entityHitPoint)
					return@forEach
			}
			
			handleHit(entityHitResult.entity as LivingEntity, shot)
		}
		currentActiveShots.forEach(Shot::advanceStep)
		currentActiveShots.removeIf { it.step >= GunItem.MAX_SHOT_STEPS }
	}
	
	private fun handleHit(entity: LivingEntity, shot: Shot) {
		entity.kill()
	}
}
