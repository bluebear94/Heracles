package earth.terrarium.heracles.forge;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.defaults.*;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.Map;

@Mod(Heracles.MOD_ID)
public class HeraclesForge {

    public HeraclesForge() {
        Heracles.setConfigPath(FMLPaths.CONFIGDIR.get());
        Heracles.init();

        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onAdvancementEarn);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onTick);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onItemUse);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onItemInteract);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onBlockInteract);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onEntityInteract);
        MinecraftForge.EVENT_BUS.addListener(HeraclesForge::onEntityDeath);

        if (FMLEnvironment.dist.isClient()) {
            HeraclesForgeClient.init();
        }
    }

    private static void onEntityDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getEntity(), KillEntityQuestTask.TYPE);
    }

    private static void onServerStarting(ServerAboutToStartEvent event) {
        Heracles.setRegistryAccess(event.getServer()::registryAccess);
        QuestProgressHandler.setupChanger();
    }

    private static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getAdvancement(), AdvancementTask.TYPE);
    }

    private static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.player.tickCount % 20 != 0) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        Map<Structure, LongSet> structures = player.serverLevel().structureManager().getAllStructuresAt(player.getOnPos());

        progress.testAndProgressTaskType(player, player.serverLevel().getBiome(player.getOnPos()), BiomeTask.TYPE);
        progress.testAndProgressTaskType(player, player, LocationTask.TYPE);

        if (!structures.isEmpty()) {
            progress.testAndProgressTaskType(player, structures.keySet(), StructureTask.TYPE);
        }
    }

    private static void onItemUse(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getItem(), ItemUseTask.TYPE);
    }

    private static void onItemInteract(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getItemStack(), ItemInteractTask.TYPE);
    }

    private static void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, new BlockSourceImpl(player.serverLevel(), event.getPos()), BlockInteractTask.TYPE);
    }

    private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        QuestProgressHandler.getProgress(player.server, player.getUUID())
            .testAndProgressTaskType(player, event.getTarget(), EntityInteractTask.TYPE);
    }
}
