package com.danielgamer321.rotp_th.action.stand;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.StandPose;
import com.github.standobyte.jojo.entity.stand.StandRelativeOffset;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TheHandEraseItem extends StandEntityAction {
    public static final StandPose ERASE_ITEM_POSE = new StandPose("ERASE_ITEM");

    public TheHandEraseItem(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        ItemStack itemToErase = user.getOffhandItem();
        if (itemToErase == null || itemToErase.isEmpty()) {
            return conditionMessage("item_offhand");
        }
        return super.checkSpecificConditions(user, power, target);
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        LivingEntity user = userPower.getUser();
        if (user != null) {
            if (!world.isClientSide()) {
                ItemStack itemToErase = itemToErase(user);
                if (!itemToErase.isEmpty()) {
                    user.getOffhandItem().shrink(1);
                }
            }
        }
    }

    private ItemStack itemToErase(LivingEntity entity) {
        return entity.getOffhandItem();
    }
}
