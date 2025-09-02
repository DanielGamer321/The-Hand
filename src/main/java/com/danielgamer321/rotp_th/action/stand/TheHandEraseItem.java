package com.danielgamer321.rotp_th.action.stand;

import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.danielgamer321.rotp_th.init.InitStands;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TheHandEraseItem extends StandEntityAction {
    public static final StandPose ERASE_ITEM_POSE = new StandPose("ERASE_ITEM");

    public TheHandEraseItem(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (!(user instanceof PlayerEntity)) {
            return ActionConditionResult.NEGATIVE;
        }
        ItemStack itemToErase = user.getOffhandItem();
        if (itemToErase.getItem() == Items.BEDROCK || itemToErase.getItem() == Items.BARRIER) {
            return conditionMessage("not_possible_erase_article");
        }
        else if (itemToErase == null || itemToErase.isEmpty()) {
            return conditionMessage("item_offhand");
        }
        return super.checkSpecificConditions(user, power, target);
    }

    @Override
    public void onTaskSet(World world, StandEntity standEntity, IStandPower standPower, Phase phase, StandEntityTask task, int ticks) {
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            thehand.setErase(true);
        }
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        LivingEntity user = userPower.getUser();
        if (user != null) {
            if (!world.isClientSide()) {
                ItemStack itemToErase = itemToErase(user);
                if (!itemToErase.isEmpty()) {
                    itemToErase.shrink(Math.min(itemToErase.getCount(), InitStands.THE_HAND_ERASURE_BARRAGE.get().isUnlocked(userPower) ? 4 : 2));
                }
            }
        }
    }

    private ItemStack itemToErase(LivingEntity entity) {
        return entity.getOffhandItem();
    }

    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        if (!world.isClientSide()) {
            TheHandEntity thehand = (TheHandEntity) standEntity;
            thehand.setErase(false);
        }
    }
}
