package dev.psygamer.gunmod.util.math

import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

data class Line(val start: Vec3, val stop: Vec3)

fun entityPositionRotationToLine(entity: Entity, distance: Double) =
	positionRotationToLine(entity.eyePosition, entity.getViewVector(1.0f), distance)

fun positionRotationToLine(position: Vec3, rotation: Vec3, distance: Double): Line {
	return Line(start = position, stop = position + rotation * distance)
}

/**
 * - There Must be at least 1 point in [points]
 * - All points must be on the line.
 * @return a **reference** of the first vector on the line.
 */
fun Line.firstVector(vararg points: Vec3) =
	firstVectorOnLine(this, *points)

/**
 * - There Must be at least 1 point in [points]
 * - All points must be on the line.
 * @return a **reference** of the first vector on the line.
 */
fun firstVectorOnLine(line: Line, vararg points: Vec3): Vec3 {
	fun findBestPoint(
		startingBestValue: Double, valueGetter: (Vec3) -> Double, betterValuePredicate: (Double, Double) -> Boolean,
	): Vec3 {
		var bestValue = startingBestValue
		var bestPoint = points[0]
		
		for (point in points) {
			val value = valueGetter(point)
			if (betterValuePredicate(value, bestValue)) {
				bestValue = value
				bestPoint = point
			}
		}
		
		return bestPoint
	}
	
	if (points.isEmpty())
		throw IllegalArgumentException("points may not be an empty array!")
	
	// Based on which direction the line points we can check if a XY component is further along the line.
	val lineUnitVector = (line.stop - line.start).normalize()
	
	if (lineUnitVector.x != 0.0) {
		if (lineUnitVector.x > 0.0)
			return findBestPoint(Double.MAX_VALUE, { it.x }, { a, b -> a < b })
		return findBestPoint(-Double.MAX_VALUE, { it.x }, { a, b -> a > b })
	}
	if (lineUnitVector.y != 0.0) {
		if (lineUnitVector.y > 0.0)
			return findBestPoint(Double.MAX_VALUE, { it.y }, { a, b -> a < b })
		return findBestPoint(-Double.MAX_VALUE, { it.y }, { a, b -> a > b })
	}
	if (lineUnitVector.z != 0.0) {
		if (lineUnitVector.z > 0.0)
			return findBestPoint(Double.MAX_VALUE, { it.z }, { a, b -> a < b })
		return findBestPoint(-Double.MAX_VALUE, { it.z }, { a, b -> a > b })
	}
	
	return points[0]
}