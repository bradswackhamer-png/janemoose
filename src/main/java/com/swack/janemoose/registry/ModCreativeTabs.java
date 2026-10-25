package com.swack.janemoose.registry;

import com.swack.janemoose.JaneMooseMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, JaneMooseMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> JANE_MOOSE_TAB =
            CREATIVE_MODE_TABS.register("janemoose_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.janemoose"))
                    .icon(() -> new ItemStack(ModItems.TRACTOR.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TRACTOR.get());
                        output.accept(ModItems.FERMENTATION_BARREL.get());
                        output.accept(ModItems.DIESEL_CANISTER.get());
                        output.accept(ModItems.ETHANOL_BUCKET.get());
                        output.accept(ModItems.WRENCH.get());

                        output.accept(ModItems.WOODEN_CULTIVATOR.get());
                        output.accept(ModItems.IRON_CULTIVATOR.get());
                        output.accept(ModItems.DIAMOND_CULTIVATOR.get());

                        output.accept(ModItems.WOODEN_HARVESTER.get());
                        output.accept(ModItems.IRON_HARVESTER.get());
                        output.accept(ModItems.DIAMOND_HARVESTER.get());

                        output.accept(ModItems.WOODEN_PLANTER.get());
                        output.accept(ModItems.IRON_PLANTER.get());
                        output.accept(ModItems.DIAMOND_PLANTER.get());
                    })
                    .build());
}
