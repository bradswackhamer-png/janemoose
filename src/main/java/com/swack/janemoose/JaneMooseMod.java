package com.swack.janemoose;

import com.swack.janemoose.client.JaneMooseClient;
import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import com.swack.janemoose.registry.ModBlockEntities;
import com.swack.janemoose.registry.ModBlocks;
import com.swack.janemoose.registry.ModCreativeTabs;
import com.swack.janemoose.registry.ModEntities;
import com.swack.janemoose.registry.ModItems;
import com.swack.janemoose.registry.ModMenus;
import com.swack.janemoose.registry.ModSounds;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(JaneMooseMod.MOD_ID)
public class JaneMooseMod {
    public static final String MOD_ID = "janemoose";
    public static final Logger LOGGER = LoggerFactory.getLogger(JaneMooseMod.class);

    public JaneMooseMod(IEventBus modEventBus) {
        LOGGER.info("Initializing Jane Moose - Nothing runs like a moose!");

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);

        modEventBus.register(com.swack.janemoose.network.ModMessages.class);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::entityAttributes);

        NeoForge.EVENT_BUS.register(JaneMooseMod.class);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            JaneMooseClient.registerClient(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Jane Moose common setup complete.");
    }

    private void entityAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntities.TRACTOR.get(), JaneMooseTractorEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof JaneMooseTractorEntity || entity instanceof JaneMooseAttachmentEntity) {
            event.setCanceled(true);
        } else if (entity != null && entity.getVehicle() instanceof JaneMooseTractorEntity) {
            event.setCanceled(true);
        }
    }
}
