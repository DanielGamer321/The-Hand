package com.danielgamer321.rotp_th.init;

import com.danielgamer321.rotp_th.RotpTheHandAddon;
import com.danielgamer321.rotp_th.entity.stand.stands.*;
import com.danielgamer321.rotp_th.action.stand.*;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.stand.*;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject;
import com.github.standobyte.jojo.power.impl.stand.StandInstance.StandPart;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mod.StoryPart;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import static com.github.standobyte.jojo.init.ModEntityTypes.ENTITIES;

public class InitStands {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Action<?>> ACTIONS = DeferredRegister.create(
            (Class<Action<?>>) ((Class<?>) Action.class), RotpTheHandAddon.MOD_ID);
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<StandType<?>> STANDS = DeferredRegister.create(
            (Class<StandType<?>>) ((Class<?>) StandType.class), RotpTheHandAddon.MOD_ID);
    
 // ======================================== The Hand ========================================
    
    public static final RegistryObject<StandEntityAction> THE_HAND_PUNCH = ACTIONS.register("the_hand_punch",
            () -> new StandEntityLightAttack(new StandEntityLightAttack.Builder()
                    .punchSound(InitSounds.THE_HAND_PUNCH_LIGHT)));
    
    public static final RegistryObject<StandEntityAction> THE_HAND_BARRAGE = ACTIONS.register("the_hand_barrage",
            () -> new StandEntityMeleeBarrage(new StandEntityMeleeBarrage.Builder()
                    .barrageHitSound(InitSounds.THE_HAND_BARRAGE)));
    
    public static final RegistryObject<StandEntityHeavyAttack> THE_HAND_KICK = ACTIONS.register("the_hand_kick",
            () -> new StandEntityHeavyAttack(new StandEntityHeavyAttack.Builder()
                    .resolveLevelToUnlock(1)
                    .punchSound(InitSounds.THE_HAND_KICK_HEAVY)
                    .partsRequired(StandPart.ARMS)));
    
    public static final RegistryObject<StandEntityHeavyAttack> THE_HAND_HEAVY_PUNCH = ACTIONS.register("the_hand_heavy_punch",
            () -> new StandEntityHeavyAttack(new StandEntityHeavyAttack.Builder()
                    .punchSound(InitSounds.THE_HAND_PUNCH_HEAVY)
                    .partsRequired(StandPart.ARMS)
                    .setFinisherVariation(THE_HAND_KICK)
                    .shiftVariationOf(THE_HAND_PUNCH).shiftVariationOf(THE_HAND_BARRAGE)));

    public static final RegistryObject<StandEntityHeavyAttack> THE_HAND_ERASE = ACTIONS.register("the_hand_erase",
            () -> new TheHandErase(new TheHandErase.Builder().holdToFire(15, true).staminaCost(150).standUserWalkSpeed(1F).standPerformDuration(1)
                    .resolveLevelToUnlock(2)
                    .standPose(TheHandErase.ERASE_POSE)
                    .punchSound(() -> null).swingSound(InitSounds.THE_HAND_ERASE)
                    .partsRequired(StandPart.ARMS)));

    public static final RegistryObject<StandEntityMeleeBarrage> THE_HAND_ERASURE_BARRAGE = ACTIONS.register("the_hand_erasure_barrage",
            () -> new TheHandErasureBarrage(new TheHandErasureBarrage.Builder().staminaCostTick(12F).cooldown(43)
                    .resolveLevelToUnlock(4)
                    .autoSummonStand()
                    .standPose(TheHandErasureBarrage.ERASURE_BARRAGE_POSE)
                    .barrageSwingSound(InitSounds.THE_HAND_ERASURE_BARRAGE).barrageHitSound(null)
                    .partsRequired(StandPart.ARMS)));
    
    public static final RegistryObject<StandEntityAction> THE_HAND_BLOCK = ACTIONS.register("the_hand_block",
            () -> new StandEntityBlock());

    public static final RegistryObject<StandEntityAction> THE_HAND_ERASE_ITEM = ACTIONS.register("the_hand_erase_item",
            () -> new TheHandEraseItem(new TheHandEraseItem.Builder().holdType().staminaCostTick(1F)
                    .resolveLevelToUnlock(3)
                    .standOffsetFromUser(0.667, 0.2, 0).standPose(TheHandEraseItem.ERASE_ITEM_POSE)
                    .partsRequired(StandPart.ARMS)
                    .standSound(InitSounds.THE_HAND_ERASURE_BARRAGE)));


    public static final EntityStandRegistryObject<EntityStandType<StandStats>, StandEntityType<TheHandEntity>> STAND_THE_HAND =
            new EntityStandRegistryObject<>("the_hand",
                    STANDS,
                    () -> new EntityStandType.Builder<>()
                            .color(0xEDEEF0)
                            .storyPartName(StoryPart.DIAMOND_IS_UNBREAKABLE.getName())
                            .leftClickHotbar(
                                    THE_HAND_PUNCH.get(),
                                    THE_HAND_BARRAGE.get(),
                                    THE_HAND_ERASE.get(),
                                    THE_HAND_ERASURE_BARRAGE.get()
                            )
                            .rightClickHotbar(
                                    THE_HAND_BLOCK.get(),
                                    THE_HAND_ERASE_ITEM.get()
                            )
                            .defaultStats(StandStats.class, new StandStats.Builder()
                                    .power(12.0)
                                    .speed(11.0, 12.0)
                                    .range(2.0, 4.0)
                                    .durability(10.0)
                                    .precision(8.0, 10.0)
                                    .randomWeight(1)
                            )
                            .addSummonShout(InitSounds.OKUYASU_THE_HAND)
                            .addOst(InitSounds.THE_HAND_OST)
                            .build(),

                    ENTITIES,
                    () -> new StandEntityType<TheHandEntity>(TheHandEntity::new, 0.65F, 1.95F)
                            .summonSound(InitSounds.THE_HAND_SUMMON)
                            .unsummonSound(InitSounds.THE_HAND_UNSUMMON))
                    .withDefaultStandAttributes();
}
