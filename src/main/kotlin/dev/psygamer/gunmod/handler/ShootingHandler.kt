package dev.psygamer.gunmod.handler

import net.minecraft.world.damagesource.IndirectEntityDamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import dev.psygamer.gunmod.item.GunItem
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
	
	private val currentShots = mutableListOf<Shot>()
	private val shotTimeouts = mutableMapOf<LivingEntity, Int>()
	
	fun shootGun(shot: Shot) {
		if (shotTimeouts.containsKey(shot.shooter) && shotTimeouts[shot.shooter]!! > 0)
			return
		
		currentShots.add(shot)
		shotTimeouts[shot.shooter] = GunItem.SHOT_DELAY_TICKS
	}
	
	@SubscribeEvent
	fun onServerTick(event: TickEvent.ServerTickEvent) {
		for (shotTimeout in shotTimeouts)
			shotTimeouts[shotTimeout.key] = maxOf(0, shotTimeout.value - 1)
		
		currentShots.forEach { shot ->
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
		currentShots.forEach(Shot::advanceStep)
		currentShots.removeIf { it.step >= GunItem.MAX_SHOT_STEPS }
	}
	
	private fun handleHit(entity: LivingEntity, shot: Shot) {
		val damage = 30 / (2 pow shot.step).toFloat()
		
		@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") // The getter of the direct entity is nullable
		val source = IndirectEntityDamageSource("gun", null, shot.shooter)
		
		// Avoid the invulnerability timer
		val prevInvulnerableTime = entity.invulnerableTime
		entity.invulnerableTime = 0
		entity.hurt(source, damage)
		entity.invulnerableTime = prevInvulnerableTime
	}
}