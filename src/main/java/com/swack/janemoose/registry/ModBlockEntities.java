package com.swack.janemoose.registry;

import com.swack.janemoose.JaneMooseMod;
import com.swack.janemoose.block.entity.FermentationBarrelBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, JaneMooseMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL_BE =
            BLOCK_ENTITIES.register("fermentation_barrel",
                    () -> new BlockEntityType<>(FermentationBarrelBlockEntity::new, ModBlocks.FERMENTATION_BARREL.get()));
}
