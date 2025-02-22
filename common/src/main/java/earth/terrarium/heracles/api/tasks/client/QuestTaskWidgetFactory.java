package earth.terrarium.heracles.api.tasks.client;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.nbt.Tag;

public interface QuestTaskWidgetFactory<I, S extends Tag, T extends QuestTask<I, S, T>> {

    DisplayWidget create(String quest, T task, TaskProgress<S> progress);

    default DisplayWidget createAndCast(String quest, QuestTask<?, S, ?> task, TaskProgress<S> progress) {
        return create(quest, this.cast(task), progress);
    }

    @SuppressWarnings("unchecked")
    default T cast(QuestTask<?, S, ?> task) {
        return (T) task;
    }
}
