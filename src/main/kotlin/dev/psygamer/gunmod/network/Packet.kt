package dev.psygamer.gunmod.network

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkEvent.Context

interface IPacket {
	
	fun encode(buffer: FriendlyByteBuf) {}
	
	fun handle(context: Context)
}

interface IPacketDecoder<T : IPacket> {
	
	fun decode(buffer: FriendlyByteBuf): T
}

interface IClientPacket : IPacket
interface IServerPacket : IPacket
interface IBidirectionalPacket : IClientPacket, IServerPacket

fun <MSG : IPacket> identityDecoder(msg: MSG): IPacketDecoder<MSG> {
	return object : IPacketDecoder<MSG> {
		override fun decode(buffer: FriendlyByteBuf): MSG {
			return msg
		}
	}
}

fun IServerPacket.sendToServer() =
	PacketHandler.sendToServer(this)

fun IClientPacket.sendToAllClients() =
	PacketHandler.sendToAllClients(this)

fun IClientPacket.sendToClient(client: ServerPlayer) =
	PacketHandler.sendToClient(this, client)