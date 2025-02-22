package earth.terrarium.heracles.common.network.packets.quests;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.networking.PacketHelper;
import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record SyncQuestsPacket(Map<String, Quest> quests, List<String> groups) implements Packet<SyncQuestsPacket> {
    public static final ResourceLocation ID = new ResourceLocation(Heracles.MOD_ID, "sync_quests");
    public static final PacketHandler<SyncQuestsPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<SyncQuestsPacket> getHandler() {
        return HANDLER;
    }

    public static class Handler implements PacketHandler<SyncQuestsPacket> {
        private static final Codec<Map<String, Quest>> QUEST_MAP_CODEC = Codec.unboundedMap(Codec.STRING, Quest.CODEC);

        @Override
        public void encode(SyncQuestsPacket message, FriendlyByteBuf buffer) {
            PacketHelper.writeWithRegistryYabn(Heracles.getRegistryAccess(), buffer, QUEST_MAP_CODEC, message.quests(), true);
            buffer.writeCollection(message.groups(), FriendlyByteBuf::writeUtf);
        }

        @Override
        public SyncQuestsPacket decode(FriendlyByteBuf buffer) {
            return new SyncQuestsPacket(
                PacketHelper.readWithRegistryYabn(Heracles.getRegistryAccess(), buffer, QUEST_MAP_CODEC, true).get().orThrow(),
                buffer.readList(FriendlyByteBuf::readUtf)
            );
        }

        @Override
        public PacketContext handle(SyncQuestsPacket message) {
            return (player, level) -> ClientQuests.sync(message.quests(), message.groups());
        }
    }
}
