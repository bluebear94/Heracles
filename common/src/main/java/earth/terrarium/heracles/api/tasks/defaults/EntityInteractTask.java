package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public record EntityInteractTask(
    String id, RegistryValue<EntityType<?>> entity, NbtPredicate nbt
) implements QuestTask<Entity, ByteTag, EntityInteractTask> {
    public static final QuestTaskType<EntityInteractTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Entity input) {
        return storage().of(progress, entity.is(input.getType().builtInRegistryHolder()) && nbt().matches(input));
    }

    @Override
    public float getProgress(ByteTag progress) {
        return storage().readBoolean(progress) ? 1 : 0;
    }

    @Override
    public BooleanTaskStorage storage() {
        return BooleanTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<EntityInteractTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<EntityInteractTask> {
        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "entity_interaction");
        }

        @Override
        public Codec<EntityInteractTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RegistryValue.codec(Registries.ENTITY_TYPE).fieldOf("entity").forGetter(EntityInteractTask::entity),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(EntityInteractTask::nbt)
            ).apply(instance, EntityInteractTask::new));
        }
    }
}
