package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public record AdvancementTask(
    String id, Set<ResourceLocation> advancements
) implements QuestTask<Advancement, ByteTag, AdvancementTask> {

    public static final QuestTaskType<AdvancementTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Advancement input) {
        return storage().of(progress, advancements.contains(input.getId()));
    }

    @Override
    public float getProgress(ByteTag progress) {
        return storage().readBoolean(progress) ? 1.0F : 0.0F;
    }

    @Override
    public BooleanTaskStorage storage() {
        return BooleanTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<AdvancementTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<AdvancementTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "advancement");
        }

        @Override
        public Codec<AdvancementTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                CodecExtras.set(ResourceLocation.CODEC).fieldOf("advancements").forGetter(AdvancementTask::advancements)
            ).apply(instance, AdvancementTask::new));
        }
    }
}
