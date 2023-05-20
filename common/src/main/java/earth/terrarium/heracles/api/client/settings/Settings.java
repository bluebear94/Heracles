package earth.terrarium.heracles.api.client.settings;

import earth.terrarium.heracles.api.client.settings.tasks.DimensionTaskSettings;
import earth.terrarium.heracles.api.client.settings.tasks.ItemTaskSettings;
import earth.terrarium.heracles.api.client.settings.tasks.KillEntityTaskSettings;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.defaults.ChangedDimensionTask;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;

import java.util.IdentityHashMap;
import java.util.Map;

public final class Settings {

    private static final Map<QuestTaskType<?>, SettingInitializer<?>> TASK_FACTORIES = new IdentityHashMap<>();
    private static final Map<QuestRewardType<?>, SettingInitializer<?>> REWARD_FACTORIES = new IdentityHashMap<>();

    public static <T extends QuestTask<?, ?, T>> void register(QuestTaskType<T> type, SettingInitializer<T> factory) {
        TASK_FACTORIES.put(type, factory);
    }

    public static <T extends QuestReward<T>> void register(QuestRewardType<T> type, SettingInitializer<T> factory) {
        REWARD_FACTORIES.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends QuestTask<?, ?, T>> SettingInitializer<T> getFactory(QuestTaskType<T> type) {
        if (!TASK_FACTORIES.containsKey(type)) {
            return null;
        }
        return (SettingInitializer<T>) TASK_FACTORIES.get(type);
    }

    @SuppressWarnings("unchecked")
    public static <T extends QuestReward<T>> SettingInitializer<T> getFactory(QuestRewardType<T> type) {
        if (!REWARD_FACTORIES.containsKey(type)) {
            return null;
        }
        return (SettingInitializer<T>) REWARD_FACTORIES.get(type);
    }

    static {
        register(ChangedDimensionTask.TYPE, DimensionTaskSettings.INSTANCE);
        register(KillEntityQuestTask.TYPE, KillEntityTaskSettings.INSTANCE);
        register(GatherItemTask.TYPE, ItemTaskSettings.INSTANCE);
    }
}