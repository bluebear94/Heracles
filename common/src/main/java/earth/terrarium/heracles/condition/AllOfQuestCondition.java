package earth.terrarium.heracles.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.List;
import java.util.stream.Stream;

public record AllOfQuestCondition(List<Either<QuestTask, QuestCondition>> tasks) implements QuestCondition {
    public static final String KEY = "all_of";

    public static final MapCodec<AllOfQuestCondition> MAP_CODEC = QuestCondition.simpleCodec(AllOfQuestCondition::new);

    @Override
    public boolean isAcquired(Stream<QuestTask> criteria) {
        return criteria.allMatch(this::isAcquired);
    }

    @Override
    public Codec<AllOfQuestCondition> codec() {
        return MAP_CODEC.codec();
    }
}
