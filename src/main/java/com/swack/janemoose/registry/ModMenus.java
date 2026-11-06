package com.swack.janemoose.registry;

import com.swack.janemoose.JaneMooseMod;
import com.swack.janemoose.gui.AttachmentMenu;
import com.swack.janemoose.gui.BucketTruckMenu;
import com.swack.janemoose.gui.FermentationBarrelMenu;
import com.swack.janemoose.gui.TractorMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, JaneMooseMod.MOD_ID);

    public static final ResourceKey<MenuType<?>> TRACTOR_MENU_KEY =
            ResourceKey.create(net.minecraft.core.registries.Registries.MENU, Identifier.fromNamespaceAndPath(JaneMooseMod.MOD_ID, "tractor_dashboard"));

    public static final DeferredHolder<MenuType<?>, MenuType<TractorMenu>> TRACTOR_MENU =
            MENUS.register("tractor_dashboard", () -> IMenuTypeExtension.create(TractorMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BucketTruckMenu>> BUCKET_TRUCK_MENU =
            MENUS.register("bucket_truck_dashboard", () -> IMenuTypeExtension.create(BucketTruckMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<com.swack.janemoose.gui.RoadPaverMenu>> ROAD_PAVER_MENU =
            MENUS.register("road_paver", () -> IMenuTypeExtension.create(com.swack.janemoose.gui.RoadPaverMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<com.swack.janemoose.gui.DieselRoadPaverMenu>> DIESEL_ROAD_PAVER_MENU =
            MENUS.register("diesel_road_paver", () -> IMenuTypeExtension.create(com.swack.janemoose.gui.DieselRoadPaverMenu::new));

    public static final ResourceKey<MenuType<?>> ATTACHMENT_MENU_KEY =
            ResourceKey.create(net.minecraft.core.registries.Registries.MENU, Identifier.fromNamespaceAndPath(JaneMooseMod.MOD_ID, "attachment_inventory"));

    public static final DeferredHolder<MenuType<?>, MenuType<AttachmentMenu>> ATTACHMENT_MENU =
            MENUS.register("attachment_inventory", () -> IMenuTypeExtension.create(AttachmentMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<FermentationBarrelMenu>> FERMENTATION_BARREL_MENU =
            MENUS.register("fermentation_barrel", () -> new MenuType<>(FermentationBarrelMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
