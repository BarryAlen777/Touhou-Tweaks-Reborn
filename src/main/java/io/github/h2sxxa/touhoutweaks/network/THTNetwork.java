package io.github.h2sxxa.touhoutweaks.network;

import io.github.h2sxxa.touhoutweaks.Consts;
import io.github.h2sxxa.touhoutweaks.config.THTConfigs;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 网络层（SimpleChannel 语义迁移）。
 *
 * 原版 TouhouTweaks 没有自定义网络包（音效全在客户端本地播放，多人模式听不到），
 * 按移植方案"SimpleChannel 语义迁移"，这里保留原触发时机与播放参数
 * （死亡事件 LOWEST 优先级、未取消时、master 声道、音量 1.0、音调 1.0），
 * 把"服务端检测到事件 → 客户端耳边播放"拆成两个 S2C 包：
 * - PlaySoundPayload：玩家死亡时发给死亡玩家，等价原版 onPlayerDeath；
 * - ServerSwitchPayload：登录同步与维度切换时下发服务器总开关。
 * 通道对原版客户端与缺失通道均放行（acceptMissingOr），单人/联机都不卡连接。
 */
public class THTNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Consts.MODID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION));

    /** 服务器总开关在客户端侧的镜像；默认 true = 原模组行为。 */
    public static volatile boolean serverAllowsDeathSound = true;
    public static volatile boolean serverAllowsPotionSound = true;

    /** 死亡音效播放包：字段为玩家实体 id（对应原版"该玩家死亡"这一触发主体）。 */
    public record PlaySoundPayload(int playerId, String soundId) {
        public void encode(FriendlyByteBuf buf) {
            buf.writeInt(playerId);
            buf.writeUtf(soundId);
        }

        public static PlaySoundPayload decode(FriendlyByteBuf buf) {
            return new PlaySoundPayload(buf.readInt(), buf.readUtf());
        }
    }

    /** 服务器开关同步包：登录与维度切换时发送（也用于服务器配置重载后的广播）。 */
    public record ServerSwitchPayload(boolean deathSound, boolean potionSound) {
        public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(deathSound);
            buf.writeBoolean(potionSound);
        }

        public static ServerSwitchPayload decode(FriendlyByteBuf buf) {
            return new ServerSwitchPayload(buf.readBoolean(), buf.readBoolean());
        }
    }

    public static void register() {
        int index = 0;
        CHANNEL.registerMessage(index++, PlaySoundPayload.class,
                PlaySoundPayload::encode, PlaySoundPayload::decode,
                THTNetwork::handlePlaySound,
                Optional.of(net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(index++, ServerSwitchPayload.class,
                ServerSwitchPayload::encode, ServerSwitchPayload::decode,
                THTNetwork::handleServerSwitch,
                Optional.of(net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        if (player != null) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
        }
    }

    public static void broadcastServerSwitches() {
        CHANNEL.send(PacketDistributor.ALL.noArg(),
                new ServerSwitchPayload(THTConfigs.serverDeathSound, THTConfigs.serverPotionSound));
    }

    private static void handlePlaySound(PlaySoundPayload message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> io.github.h2sxxa.touhoutweaks.events.ClientEvents
                .onRemoteSoundRequest(message.playerId(), message.soundId()));
        context.setPacketHandled(true);
    }

    private static void handleServerSwitch(ServerSwitchPayload message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            serverAllowsDeathSound = message.deathSound();
            serverAllowsPotionSound = message.potionSound();
        });
        context.setPacketHandled(true);
    }
}
