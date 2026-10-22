package com.swack.janemoose.registry;

import com.swack.janemoose.JaneMooseMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, JaneMooseMod.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> TRACTOR_ENGINE = registerSound("tractor_engine");
    public static final DeferredHolder<SoundEvent, SoundEvent> TRACTOR_HORN = registerSound("tractor_horn");
    public static final DeferredHolder<SoundEvent, SoundEvent> ATTACHMENT_CLICK = registerSound("attachment_click");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(JaneMooseMod.MOD_ID, name);
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
