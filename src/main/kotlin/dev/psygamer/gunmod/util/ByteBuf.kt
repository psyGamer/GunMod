package dev.psygamer.gunmod.util

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.phys.*

fun FriendlyByteBuf.writeVec2(vector: Vec2) {
	this.writeFloat(vector.x)
	this.writeFloat(vector.y)
}

fun FriendlyByteBuf.readVec2(): Vec2 {
	return Vec2(this.readFloat(), this.readFloat())
}

fun FriendlyByteBuf.writeVec3(vector: Vec3) {
	this.writeDouble(vector.x)
	this.writeDouble(vector.y)
	this.writeDouble(vector.z)
}

fun FriendlyByteBuf.readVec3(): Vec3 {
	return Vec3(this.readDouble(), this.readDouble(), this.readDouble())
}