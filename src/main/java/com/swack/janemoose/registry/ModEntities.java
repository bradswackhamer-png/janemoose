package com.swack.janemoose.registry;

import com.swack.janemoose.JaneMooseMod;
import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, JaneMooseMod.MOD_ID);

    public static final ResourceKey<EntityType<?>> TRACTOR_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(JaneMooseMod.MOD_ID, "tractor"));

    public static final DeferredHolder<EntityType<?>, EntityType<JaneMooseTractorEntity>> TRACTOR =
            ENTITIES.register("tractor", () -> EntityType.Builder.<JaneMooseTractorEntity>of(JaneMooseTractorEntity::new, MobCategory.MISC)
                    .sized(1.8F, 1.8F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build(TRACTOR_KEY));

    public static final ResourceKey<EntityType<?>> ATTACHMENT_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(JaneMooseMod.MOD_ID, "attachment"));

    public static final DeferredHolder<EntityType<?>, EntityType<JaneMooseAttachmentEntity>> ATTACHMENT =
            ENTITIES.register("attachment", () -> EntityType.Builder.<JaneMooseAttachmentEntity>of(JaneMooseAttachmentEntity::new, MobCategory.MISC)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
                    .build(ATTACHMENT_KEY));
}
