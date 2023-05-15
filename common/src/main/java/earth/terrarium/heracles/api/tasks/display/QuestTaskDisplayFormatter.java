package earth.terrarium.heracles.api.tasks.display;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.defaults.ItemQuestTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.Optionull;
import net.minecraft.nbt.Tag;

import java.util.IdentityHashMap;
import java.util.Map;

public final class QuestTaskDisplayFormatter {

    private static final Map<QuestTaskType<?>, Formatter<?, ?, ?>> FORMATTERS = new IdentityHashMap<>();

    public static <I, S extends Tag, T extends QuestTask<I, S, T>> void register(QuestTaskType<T> type, Formatter<I, S, T> formatter) {
        FORMATTERS.put(type, formatter);
    }

    @SuppressWarnings("unchecked")
    public static <I, S extends Tag, T extends QuestTask<I, S, T>> Formatter<I, S, T> getFormatter(QuestTaskType<T> type) {
        if (!FORMATTERS.containsKey(type)) {
            return null;
        }
        return (Formatter<I, S, T>) FORMATTERS.get(type);
    }

    public static <T extends Tag> String create(QuestTask<?, T, ?> task, TaskProgress<T> progress) {
        return Optionull.mapOrDefault(getFormatter(task.type()), formatter -> formatter.castAndFormat(progress, task), "");
    }

    static {
        register(KillEntityQuestTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()), task.target()));
        register(ItemQuestTask.TYPE, (progress, task) -> String.format("%d/%d", task.storage().read(progress.progress()), task.target()));
    }

    @FunctionalInterface
    public interface Formatter<I, S extends Tag, T extends QuestTask<I, S, T>> {

        String format(TaskProgress<S> progress, T task);

        @SuppressWarnings("unchecked")
        default String castAndFormat(TaskProgress<S> progress, QuestTask<?, S, ?> task) {
            return format(progress, (T) task);
        }
    }
}