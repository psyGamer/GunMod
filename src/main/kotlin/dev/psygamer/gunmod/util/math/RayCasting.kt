package dev.psygamer.gunmod.util.math

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.*

fun shootBlockRayCast(shooter: Entity, line: Line, filter: (BlockState) -> Boolean = { true }): BlockHitResult? {
	val level: Level = shooter.level
	val area = AABB(line.start, line.stop).inflate(1.0)
	
	for (blockPos in BlockPos.betweenClosedStream(area)) {
		val blockState = level.getBlockState(blockPos)
		
		if (!filter(blockState))
			continue
		
		val shape = blockState.getCollisionShape(level, blockPos)
		return shape.clip(line.start, line.stop, blockPos) ?: continue
	}
	
	return null
}

fun shootEntityRayCast(shooter: Entity, line: Line, filter: (Entity) -> Boolean = { !it.isSpectator }): EntityHitResult? {
	val area = AABB(line.start, line.stop).inflate(1.0)
	return ProjectileUtil.getEntityHitResult(shooter, line.start, line.stop, area,
											 filter, Double.MAX_VALUE)
}