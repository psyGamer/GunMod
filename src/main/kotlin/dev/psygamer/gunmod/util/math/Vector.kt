package dev.psygamer.gunmod.util.math

import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.*
import kotlin.math.sqrt

operator fun Vec2.unaryMinus(): Vec2 {
	return this.negated()
}

operator fun Vec2.plus(other: Vec2): Vec2 {
	return this.add(other)
}

operator fun Vec2.minus(other: Vec2): Vec2 {
	return Vec2(x - other.x, y - other.y)
}

operator fun Vec2.times(scalar: Float): Vec2 {
	return Vec2(x * scalar, y * scalar)
}

operator fun Vec2.times(other: Vec2): Vec2 {
	return Vec2(x * other.x, y * other.y)
}

operator fun Vec2.div(scalar: Float): Vec2 {
	return Vec2(x / scalar, y / scalar)
}

operator fun Vec2.div(other: Vec2): Vec2 {
	return Vec2(x / other.x, y / other.y)
}

operator fun Vec3.plus(other: Vec3): Vec3 {
	return this.add(other)
}

operator fun Vec3.minus(other: Vec3): Vec3 {
	return this.subtract(other)
}

operator fun Vec3.times(scalar: Double): Vec3 {
	return this.multiply(scalar, scalar, scalar)
}

operator fun Vec3.times(other: Vec3): Vec3 {
	return this.multiply(other)
}

operator fun Vec3.div(scalar: Double): Vec3 {
	return Vec3(x / scalar, y / scalar, z / scalar)
}

operator fun Vec3.div(other: Vec3): Vec3 {
	return Vec3(x / other.x, y / other.y, z / other.z)
}

fun Vec2.normalize(): Vec2 {
	val magnitude = sqrt(x * x + y * y)
	return if (magnitude < 1.0E-4) Vec2.ZERO else Vec2(x / magnitude, y / magnitude)
}

fun pitchYawToUnitVector(rotation: Vec2) =
	pitchYawToUnitVector(rotation.x, rotation.y)

/** @see Entity.calculateViewVector */
fun pitchYawToUnitVector(pitch: Float, yaw: Float): Vec3 {
	val pitchRadians: Float = pitch * (Math.PI.toFloat() / 180f)
	val yawRadians: Float = -yaw * (Math.PI.toFloat() / 180f)
	
	val pitchSin = Mth.sin(pitchRadians)
	val pitchCos = Mth.cos(pitchRadians)
	val yawSin = Mth.sin(yawRadians)
	val yawCos = Mth.cos(yawRadians)
	
	return Vec3((yawSin * pitchCos).toDouble(), (-pitchSin).toDouble(), (yawCos * pitchCos).toDouble())
}