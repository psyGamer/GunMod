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
	
	var invalid = false
		private set
	
	init {
		this.shotUpdateVector = viewVector * GunItem.BULLET_SPEED_BPT
		this.step = 0
		
		this.position = startVector
	}
	
	fun advanceStep() {
		this.position = this.nextPosition
		this.step++
	}
	
	fun markInvalid() {
		this.invalid = true
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
			
			val entityHitResult = shootEntityRayCast(shot.shooter, rayCast) { !it.isSpectator && it is LivingEntity }
			val blockHitResult = shootBlockRayCast(shot.shooter, rayCast)
			
			if (entityHitResult == null || blockHitResult == null)
				shot.markInvalid() // There will be no more steps after this one
			
			if (blockHitResult != null) {
				val entityHitPoint = entityHitResult?.location
				val blockHitPoint = blockHitResult.location
				
				if (entityHitPoint == null)
					return@forEach
				
				val firstHitPoint = firstVectorOnLine(rayCast, entityHitPoint, blockHitPoint)
				
				if (firstHitPoint !== entityHitPoint)
					return@forEach
			}
			if (entityHitResult == null)
				return@forEach
			
			handleHit(entityHitResult.entity as LivingEntity, shot)
		}
		currentShots.forEach(Shot::advanceStep)
		currentShots.removeIf { it.invalid || it.step >= GunItem.MAX_SHOT_STEPS }
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