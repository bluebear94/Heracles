package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.EnumSetting;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ItemTaskSettings implements SettingInitializer<GatherItemTask> {

    public static final ItemTaskSettings INSTANCE = new ItemTaskSettings();
    private static final EnumSetting<GatherItemTask.CollectionType> COLLECTION_TYPE = new EnumSetting<>(GatherItemTask.CollectionType.class, GatherItemTask.CollectionType.AUTOMATIC);

    @Override
    public CreationData create(@Nullable GatherItemTask object) {
        CreationData settings = new CreationData();
        settings.put("item", RegistryValueSetting.ITEM, getDefaultItem(object));
        settings.put("amount", IntSetting.ONE, getDefaultCount(object));
        settings.put("collection_type", COLLECTION_TYPE, Optionull.mapOrDefault(object, GatherItemTask::collectionType, GatherItemTask.CollectionType.AUTOMATIC));
        return settings;
    }

    @Override
    public GatherItemTask create(String id, GatherItemTask object, Data data) {
        RegistryValue<Item> item = data.get("item", RegistryValueSetting.ITEM).orElse(getDefaultItem(object));
        NbtPredicate old = Optionull.mapOrDefault(object, GatherItemTask::nbt, NbtPredicate.ANY);
        return new GatherItemTask(
            id, item, old,
            data.get("amount", IntSetting.ONE).orElse(getDefaultCount(object)),
            data.get("collection_type", COLLECTION_TYPE).orElse(Optionull.mapOrDefault(object, GatherItemTask::collectionType, GatherItemTask.CollectionType.AUTOMATIC))
        );
    }

    private static RegistryValue<Item> getDefaultItem(GatherItemTask object) {
        return Optionull.mapOrDefault(object, GatherItemTask::item, new RegistryValue<>(Either.left(Items.AIR.builtInRegistryHolder())));
    }

    private static int getDefaultCount(GatherItemTask object) {
        return Optionull.mapOrDefault(object, GatherItemTask::target, 1);
    }
}
