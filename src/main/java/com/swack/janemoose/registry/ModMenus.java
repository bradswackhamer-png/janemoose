package com.swack.janemoose.registry;

import com.swack.janemoose.JaneMooseMod;
import com.swack.janemoose.gui.AttachmentMenu;
import com.swack.janemoose.gui.TractorMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

    public static final ResourceKey<MenuType<?>> ATTACHMENT_MENU_KEY =
            ResourceKey.create(net.minecraft.core.registries.Registries.MENU, Identifier.fromNamespaceAndPath(JaneMooseMod.MOD_ID, "attachment_inventory"));

    public static final DeferredHolder<MenuType<?>, MenuType<AttachmentMenu>> ATTACHMENT_MENU =
            MENUS.register("attachment_inventory", () -> IMenuTypeExtension.create(AttachmentMenu::new));
}
