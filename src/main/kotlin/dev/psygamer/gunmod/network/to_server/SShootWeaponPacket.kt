package dev.psygamer.gunmod.network.to_server

import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.*
import net.minecraftforge.network.NetworkEvent.Context
import org.jetbrains.annotations.Contract
import dev.psygamer.gunmod.network.*
import dev.psygamer.gunmod.util.*
import dev.psygamer.gunmod.util.math.*

class SShootWeaponPacket(position: Vec3, rotation: Vec2) : IServerPacket {
	
	constructor(shooter: LocalPlayer) : this(shooter.eyePosition, shooter.rotationVector)
	
	// Use clients position/rotation to ensure that the shots are accurate.
	private val position: Vec3
	private val rotation: Vec2
	
	init {
		this.position = position
		this.rotation = rotation
	}
	
	object Decoder : IPacketDecoder<SShootWeaponPacket> {
		
		override fun decode(buffer: FriendlyByteBuf): SShootWeaponPacket {
			return SShootWeaponPacket(buffer.readVec3(), buffer.readVec2())
		}
	}
	
	override fun encode(buffer: FriendlyByteBuf) {
		buffer.writeVec3(this.position)
		buffer.writeVec2(this.rotation)
	}
	
	override fun handle(context: Context) {
		if (context.sender == null || !isPacketValid(context.sender!!))
			return
		
		val rayCastLine = positionRotationToLine(this.position, pitchYawToUnitVector(this.rotation), 50.0)
		
		// We can return early here since there's nothing to do if we didn't even hit an entity.
		val entityHitResult = shootEntityRayCast(context.sender!!, rayCastLine) ?: return
		val blockHitResult = shootBlockRayCast(context.sender!!, rayCastLine)
		
		if (blockHitResult != null) {
			val entityHitPoint = entityHitResult.location
			val blockHitPoint = blockHitResult.location
			
			val firstHitPoint = firstVectorOnLine(rayCastLine, entityHitPoint, blockHitPoint)
			
			if (firstHitPoint !== entityHitPoint)
				return
		}
		
		entityHitResult.entity.kill()
	}
	
	@Contract("null -> false")
	private fun isPacketValid(sender: ServerPlayer): Boolean {
		if (sender.eyePosition.distanceToSqr(this.position) > 1.0)
			return false
		
		// We could verify the rotation too, however a hacked client could just rotate,
		// send the new rotation to the server and then shoot. Therefore, it would be useless.
		return true
	}
}