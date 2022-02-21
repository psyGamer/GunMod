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

fun <MSG : IPacket> identityDecoder(msg: MSG): IPacketDecoder<MSG> {
	return object : IPacketDecoder<MSG> {
		override fun decode(buffer: FriendlyByteBuf): MSG {
			return msg
		}
	}
}

fun IServerPacket.send() {
	PacketHandler.sendToServer(this)
}

fun IClientPacket.sendToAll() {
	PacketHandler.sendToAllClients(this)
}

fun IClientPacket.sendTo(client: ServerPlayer) {
	PacketHandler.sendToClient(this, client)
}