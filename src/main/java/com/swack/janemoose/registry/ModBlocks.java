package com.swack.janemoose.registry;

import com.swack.janemoose.JaneMooseMod;
import com.swack.janemoose.block.FermentationBarrelBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(JaneMooseMod.MOD_ID);

    public static final DeferredBlock<FermentationBarrelBlock> FERMENTATION_BARREL =
            BLOCKS.registerBlock("fermentation_barrel", FermentationBarrelBlock::new);
}
