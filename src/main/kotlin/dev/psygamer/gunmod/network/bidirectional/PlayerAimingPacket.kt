package dev.psygamer.gunmod.network.bidirectional

import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Player
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent.Context
import java.util.*
import dev.psygamer.gunmod.handler.AimingHandler
import dev.psygamer.gunmod.network.*

class PlayerAimingPacket(
	private val playerUUID: UUID,
	private val aiming: Boolean,
) : IBidirectionalPacket {
	
	object Decoder : IPacketDecoder<PlayerAimingPacket> {
		
		override fun decode(buffer: FriendlyByteBuf): PlayerAimingPacket {
			return PlayerAimingPacket(buffer.readUUID(), buffer.readBoolean())
		}
	}
	
	override fun encode(buffer: FriendlyByteBuf) {
		buffer.writeUUID(this.playerUUID)
		buffer.writeBoolean(this.aiming)
	}
	
	override fun handle(context: Context) {
		val player = DistExecutor.safeRunForDist<Player?>(
			{ // Client
				DistExecutor.SafeSupplier { return@SafeSupplier Minecraft.getInstance().level?.getPlayerByUUID(this.playerUUID) }
			},
			{ // Server
				DistExecutor.SafeSupplier { return@SafeSupplier context.sender }
			}
		) ?: return
		
		if (player.uuid != playerUUID)
			return
		
		AimingHandler.setPlayerAiming(player, this.aiming)
	}
}