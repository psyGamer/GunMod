package dev.psygamer.gunmod.network

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.network.*
import dev.psygamer.gunmod.GunMod
import dev.psygamer.gunmod.network.to_server.ServerboundShootGunPacket

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object PacketHandler {
	
	private const val PROTOCOL_VERSION = "1"
	
	private val INSTANCE = NetworkRegistry.newSimpleChannel(
		ResourceLocation(GunMod.MODID, "packet"),
		{ PROTOCOL_VERSION }, { it == PROTOCOL_VERSION }, { it == PROTOCOL_VERSION })
	
	private var packetID = 0
	
	private inline fun <reified MSG : IPacket> registerPacket(decoder: IPacketDecoder<MSG>) {
		@Suppress("INACCESSIBLE_TYPE")
		INSTANCE.registerMessage(packetID++, MSG::class.java,
								 { msg, buffer -> msg.encode(buffer) },
								 { buffer -> decoder.decode(buffer) },
								 { msg, context ->
									 context.get().enqueueWork { msg.handle(context.get()) }
								 })
	}
	
	@SubscribeEvent
	fun onRegister(event: FMLCommonSetupEvent) {
		registerPacket(ServerboundShootGunPacket.Decoder)
	}
	
	fun sendToServer(packet: IServerPacket) {
		INSTANCE.sendToServer(packet)
	}
	
	fun sendToClient(packet: IClientPacket, player: ServerPlayer) {
		INSTANCE.send(PacketDistributor.PLAYER.with { player }, packet)
	}
	
	fun sendToAllClients(packet: IClientPacket) {
		INSTANCE.send(PacketDistributor.ALL.noArg(), packet)
	}
}