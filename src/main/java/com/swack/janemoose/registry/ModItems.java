package com.swack.janemoose.registry;

import com.swack.janemoose.JaneMooseMod;
import com.swack.janemoose.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(JaneMooseMod.MOD_ID);

    // Tractor & Tools
    public static final DeferredItem<TractorItem> TRACTOR =
            ITEMS.registerItem("tractor", TractorItem::new, p -> p.stacksTo(1));

    public static final DeferredItem<DieselCanisterItem> DIESEL_CANISTER =
            ITEMS.registerItem("diesel_canister", DieselCanisterItem::new, p -> p.stacksTo(16));

    public static final DeferredItem<EthanolBucketItem> ETHANOL_BUCKET =
            ITEMS.registerItem("ethanol_bucket", EthanolBucketItem::new, p -> p.stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET));

    public static final DeferredItem<net.minecraft.world.item.BlockItem> FERMENTATION_BARREL =
            ITEMS.registerSimpleBlockItem("fermentation_barrel", ModBlocks.FERMENTATION_BARREL);

    public static final DeferredItem<WrenchItem> WRENCH =
            ITEMS.registerItem("wrench", WrenchItem::new, p -> p.stacksTo(1));

    // Cultivators
    public static final DeferredItem<CultivatorAttachmentItem> WOODEN_CULTIVATOR =
            ITEMS.registerItem("wooden_cultivator", p -> new CultivatorAttachmentItem(p, 1), p -> p.stacksTo(1));

    public static final DeferredItem<CultivatorAttachmentItem> IRON_CULTIVATOR =
            ITEMS.registerItem("iron_cultivator", p -> new CultivatorAttachmentItem(p, 2), p -> p.stacksTo(1));

    public static final DeferredItem<CultivatorAttachmentItem> DIAMOND_CULTIVATOR =
            ITEMS.registerItem("diamond_cultivator", p -> new CultivatorAttachmentItem(p, 3), p -> p.stacksTo(1));

    // Harvesters
    public static final DeferredItem<HarvesterAttachmentItem> WOODEN_HARVESTER =
            ITEMS.registerItem("wooden_harvester", p -> new HarvesterAttachmentItem(p, 1), p -> p.stacksTo(1));

    public static final DeferredItem<HarvesterAttachmentItem> IRON_HARVESTER =
            ITEMS.registerItem("iron_harvester", p -> new HarvesterAttachmentItem(p, 2), p -> p.stacksTo(1));

    public static final DeferredItem<HarvesterAttachmentItem> DIAMOND_HARVESTER =
            ITEMS.registerItem("diamond_harvester", p -> new HarvesterAttachmentItem(p, 3), p -> p.stacksTo(1));

    // Planters
    public static final DeferredItem<PlanterAttachmentItem> WOODEN_PLANTER =
            ITEMS.registerItem("wooden_planter", p -> new PlanterAttachmentItem(p, 1), p -> p.stacksTo(1));

    public static final DeferredItem<PlanterAttachmentItem> IRON_PLANTER =
            ITEMS.registerItem("iron_planter", p -> new PlanterAttachmentItem(p, 2), p -> p.stacksTo(1));

    public static final DeferredItem<PlanterAttachmentItem> DIAMOND_PLANTER =
            ITEMS.registerItem("diamond_planter", p -> new PlanterAttachmentItem(p, 3), p -> p.stacksTo(1));
}
