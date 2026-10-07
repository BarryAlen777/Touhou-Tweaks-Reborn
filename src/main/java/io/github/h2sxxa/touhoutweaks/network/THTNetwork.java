package io.github.h2sxxa.touhoutweaks.network;

import io.github.h2sxxa.touhoutweaks.Consts;
import io.github.h2sxxa.touhoutweaks.config.THTConfigs;
import io.github.h2sxxa.touhoutweaks.events.ClientEvents;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** NeoForge 1.21.1 载荷网络层。 */
public class THTNetwork {
    public static volatile boolean serverAllowsDeathSound = true;
    public static volatile boolean serverAllowsPotionSound = true;

    public record PlaySoundPayload(int playerId, String soundId) implements CustomPacketPayload {
        public static final Type<PlaySoundPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(Consts.MODID, "play_sound"));
        public static final StreamCodec<RegistryFriendlyByteBuf, PlaySoundPayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, PlaySoundPayload::playerId,
                        ByteBufCodecs.STRING_UTF8, PlaySoundPayload::soundId,
                        PlaySoundPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ServerSwitchPayload(boolean deathSound, boolean potionSound) implements CustomPacketPayload {
        public static final Type<ServerSwitchPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(Consts.MODID, "server_switch"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ServerSwitchPayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.BOOL, ServerSwitchPayload::deathSound,
                        ByteBufCodecs.BOOL, ServerSwitchPayload::potionSound,
                        ServerSwitchPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1").optional();
        registrar.playToClient(PlaySoundPayload.TYPE, PlaySoundPayload.STREAM_CODEC,
                (payload, context) -> ClientEvents.onRemoteSoundRequest(payload.playerId(), payload.soundId()));
        registrar.playToClient(ServerSwitchPayload.TYPE, ServerSwitchPayload.STREAM_CODEC,
                (payload, context) -> {
                    serverAllowsDeathSound = payload.deathSound();
                    serverAllowsPotionSound = payload.potionSound();
                });
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (player != null) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }

    public static void broadcastServerSwitches() {
        PacketDistributor.sendToAllPlayers(new ServerSwitchPayload(
                THTConfigs.serverDeathSound, THTConfigs.serverPotionSound));
    }
}
