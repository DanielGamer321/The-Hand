package com.danielgamer321.rotp_th.client.render.entity.model.stand;

import com.danielgamer321.rotp_th.entity.stand.stands.TheHandEntity;
import com.danielgamer321.rotp_th.action.stand.TheHandErase;
import com.danielgamer321.rotp_th.action.stand.TheHandErasureBarrage;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.client.render.entity.model.stand.HumanoidStandModel;
import com.github.standobyte.jojo.client.render.entity.pose.*;
import com.github.standobyte.jojo.client.render.entity.pose.anim.PosedActionAnimation;
import com.github.standobyte.jojo.client.render.entity.pose.anim.barrage.StandOneHandedBarrageAnimation;
import com.github.standobyte.jojo.entity.stand.StandPose;

import com.github.standobyte.jojo.util.general.MathUtil;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.Hand;

// Made with Blockbench 4.6.5
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class TheHandModel extends HumanoidStandModel<TheHandEntity> {
	private final ModelRenderer rightPartForehead;
	private final ModelRenderer leftPartForehead;
	private final ModelRenderer spine1;
	private final ModelRenderer spine2;
	private final ModelRenderer leftPartTriangle;
	private final ModelRenderer rightPartTriangle;
	private final ModelRenderer head2;
	private final ModelRenderer rightPartForehead2;
	private final ModelRenderer leftPartForehead2;

	public TheHandModel() {
		super();

		addHumanoidBaseBoxes(null);
		texWidth = 128;
		texHeight = 128;

		head.texOffs(0, 0).addBox(-3.0F, -4.0F, -4.15F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		head.texOffs(0, 2).addBox(1.0F, -4.0F, -4.15F, 2.0F, 1.0F, 1.0F, 0.0F, true);
		head.texOffs(26, 1).addBox(-5.0F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		head.texOffs(26, 3).addBox(3.0F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		head.texOffs(34, 0).addBox(-4.0F, -8.1F, -4.0F, 8.0F, 8.0F, 8.0F, 0.2F, false);
		head.texOffs(67, 22).addBox(-4.0F, -3.195F, 1.0F, 8.0F, 1.0F, 3.0F, 0.2F, false);
		head.texOffs(67, 26).addBox(-4.0F, -4.195F, 0.73F, 8.0F, 2.0F, 1.0F, 0.2F, false);
		head.texOffs(67, 29).addBox(-4.0F, -4.1F, -1.727F, 8.0F, 4.0F, 1.0F, 0.2F, false);
		head.texOffs(85, 28).addBox(-4.365F, -1.1F, -1.73F, 1.0F, 1.0F, 1.0F, 0.2F, false);
		head.texOffs(85, 26).addBox(3.365F, -1.1F, -1.73F, 1.0F, 1.0F, 1.0F, 0.2F, false);
		head.texOffs(34, 17).addBox(-4.0F, -8.46F, -4.0F, 8.0F, 1.0F, 8.0F, -0.8399F, false);
		head.texOffs(67, 7).addBox(2.8342F, -8.1F, -4.0F, 1.0F, 8.0F, 1.0F, 0.2F, false);
		head.texOffs(80, 7).addBox(-3.8363F, -8.1F, -4.3634F, 1.0F, 8.0F, 1.0F, 0.2F, false);
		head.texOffs(76, 7).addBox(-3.8363F, -8.1F, -4.0F, 1.0F, 8.0F, 1.0F, 0.2F, false);
		head.texOffs(71, 7).addBox(2.8342F, -8.1F, -4.3634F, 1.0F, 8.0F, 1.0F, 0.2F, false);
		head.texOffs(34, 26).addBox(-4.0F, -0.74F, -4.0F, 8.0F, 1.0F, 8.0F, -0.8399F, false);
		head.texOffs(5, 17).addBox(-3.0F, -8.0F, -4.09F, 6.0F, 4.0F, 2.0F, -0.01F, false);

		rightPartForehead = new ModelRenderer(this);
		rightPartForehead.setPos(0.0F, -2.01F, -4.29F);
		head.addChild(rightPartForehead);
		setRotationAngle(rightPartForehead, 0.0F, -0.2182F, 0.0F);
		rightPartForehead.texOffs(22, 17).addBox(-0.01F, -5.99F, -0.01F, 1.0F, 6.0F, 1.0F, -0.01F, false);

		leftPartForehead = new ModelRenderer(this);
		leftPartForehead.setPos(0.0F, -2.01F, -4.29F);
		head.addChild(leftPartForehead);
		setRotationAngle(leftPartForehead, 0.0F, 0.2182F, 0.0F);
		leftPartForehead.texOffs(0, 17).addBox(-0.99F, -5.99F, -0.01F, 1.0F, 6.0F, 1.0F, -0.01F, false);

		torso.texOffs(40, 76).addBox(0.499F, -0.6F, -3.15F, 3.0F, 3.0F, 2.0F, -0.6F, false);
		torso.texOffs(51, 76).addBox(-3.501F, -0.6F, -3.15F, 3.0F, 3.0F, 2.0F, -0.6F, false);
		torso.texOffs(28, 73).addBox(-4.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, -0.2F, false);
		torso.texOffs(28, 76).addBox(3.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, -0.2F, false);
		torso.texOffs(51, 65).addBox(-5.0F, -0.5F, -2.5F, 2.0F, 5.0F, 5.0F, 0.1F, false);
		torso.texOffs(61, 67).addBox(-3.5F, 2.55F, 1.4F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		torso.texOffs(36, 65).addBox(3.0F, -0.5F, -2.5F, 2.0F, 5.0F, 5.0F, 0.1F, false);
		torso.texOffs(46, 67).addBox(1.5F, 2.55F, 1.4F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		torso.texOffs(20, 64).addBox(-3.5F, 1.1F, -2.0F, 7.0F, 3.0F, 1.0F, 0.4F, false);
		torso.texOffs(25, 69).addBox(-1.5F, 4.0F, -2.3F, 3.0F, 3.0F, 1.0F, 0.0F, false);
		torso.texOffs(24, 80).addBox(-1.0F, 10.75F, -2.5F, 2.0F, 4.0F, 1.0F, 0.0F, false);
		torso.texOffs(0, 81).addBox(-3.0F, 1.0F, 1.2F, 6.0F, 10.0F, 1.0F, 0.0F, false);
		torso.texOffs(24, 86).addBox(-1.0F, 9.5F, 0.75F, 2.0F, 2.0F, 2.0F, -0.3F, false);

		spine1 = new ModelRenderer(this);
		spine1.setPos(0.0F, -0.6F, 2.6F);
		torso.addChild(spine1);
		setRotationAngle(spine1, -0.7854F, 0.0F, 0.0F);
		spine1.texOffs(24, 73).addBox(-4.5F, -0.65F, -0.5F, 1.0F, 2.0F, 1.0F, -0.2F, false);
		spine1.texOffs(24, 76).addBox(3.5F, -0.65F, -0.5F, 1.0F, 2.0F, 1.0F, -0.2F, false);

		spine2 = new ModelRenderer(this);
		spine2.setPos(0.0F, -0.6F, -2.6F);
		torso.addChild(spine2);
		setRotationAngle(spine2, 0.7854F, 0.0F, 0.0F);
		spine2.texOffs(32, 73).addBox(-4.5F, -0.65F, -0.5F, 1.0F, 2.0F, 1.0F, -0.2F, false);
		spine2.texOffs(32, 76).addBox(3.5F, -0.65F, -0.5F, 1.0F, 2.0F, 1.0F, -0.2F, false);

		leftPartTriangle = new ModelRenderer(this);
		leftPartTriangle.setPos(0.5F, 11.0F, 2.2F);
		torso.addChild(leftPartTriangle);
		setRotationAngle(leftPartTriangle, 0.0F, 0.0F, 0.3272F);
		leftPartTriangle.texOffs(15, 83).addBox(-1.0F, -8.0F, -1.0F, 1.0F, 8.0F, 1.0F, 0.0F, false);

		rightPartTriangle = new ModelRenderer(this);
		rightPartTriangle.setPos(-0.5F, 11.0F, 2.2F);
		torso.addChild(rightPartTriangle);
		setRotationAngle(rightPartTriangle, 0.0F, 0.0F, -0.3272F);
		rightPartTriangle.texOffs(19, 83).addBox(0.0F, -8.0F, -1.0F, 1.0F, 8.0F, 1.0F, 0.0F, false);

		leftArm.texOffs(44, 107).addBox(-1.5F, 2.0F, 0.75F, 3.0F, 3.0F, 2.0F, -0.4F, true);

		leftForeArm.texOffs(32, 96).addBox(1.0F, 2.9F, -1.5F, 2.0F, 3.0F, 3.0F, -0.6F, true);
		leftForeArm.texOffs(42, 97).addBox(1.5F, 5.1F, -2.0F, 1.0F, 1.0F, 4.0F, -0.2F, true);
		leftForeArm.texOffs(56, 117).addBox(1.75F, -0.25F, -1.5F, 1.0F, 3.0F, 3.0F, -0.251F, false);
		leftForeArm.texOffs(48, 117).addBox(-2.75F, -0.25F, -1.5F, 1.0F, 3.0F, 3.0F, -0.251F, false);

		rightArm.texOffs(12, 107).addBox(-1.5F, 2.0F, 0.75F, 3.0F, 3.0F, 2.0F, -0.4F, true);

		rightForeArm.texOffs(0, 96).addBox(-3.0F, 2.9F, -1.5F, 2.0F, 3.0F, 3.0F, -0.6F, false);
		rightForeArm.texOffs(10, 97).addBox(-2.5F, 5.1F, -2.0F, 1.0F, 1.0F, 4.0F, -0.2F, false);
		rightForeArm.texOffs(16, 117).addBox(-2.75F, -0.25F, -1.5F, 1.0F, 3.0F, 3.0F, -0.251F, false);
		rightForeArm.texOffs(24, 117).addBox(1.75F, -0.25F, -1.5F, 1.0F, 3.0F, 3.0F, -0.251F, false);

		head2 = new ModelRenderer(this);
		head2.setPos(0.0F, -12.0F, 0.0F);
		upperPart.addChild(head2);
		head2.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
		head2.texOffs(0, 0).addBox(-3.0F, -4.0F, -4.15F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		head2.texOffs(0, 2).addBox(1.0F, -4.0F, -4.15F, 2.0F, 1.0F, 1.0F, 0.0F, true);
		head2.texOffs(34, 0).addBox(-4.0F, -8.1F, -4.0F, 8.0F, 8.0F, 8.0F, 0.2F, false);
		head2.texOffs(34, 17).addBox(-4.0F, -8.46F, -4.0F, 8.0F, 1.0F, 8.0F, -0.8399F, false);
		head2.texOffs(67, 7).addBox(2.8342F, -8.1F, -4.0F, 1.0F, 8.0F, 1.0F, 0.2F, false);
		head2.texOffs(80, 7).addBox(-3.8363F, -8.1F, -4.3634F, 1.0F, 8.0F, 1.0F, 0.2F, false);
		head2.texOffs(76, 7).addBox(-3.8363F, -8.1F, -4.0F, 1.0F, 8.0F, 1.0F, 0.2F, false);
		head2.texOffs(85, 26).addBox(3.365F, -1.1F, -1.73F, 1.0F, 1.0F, 1.0F, 0.2F, false);
		head2.texOffs(26, 3).addBox(3.0F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		head2.texOffs(85, 28).addBox(-4.365F, -1.1F, -1.73F, 1.0F, 1.0F, 1.0F, 0.2F, false);
		head2.texOffs(67, 29).addBox(-4.0F, -4.1F, -1.727F, 8.0F, 4.0F, 1.0F, 0.2F, false);
		head2.texOffs(67, 26).addBox(-4.0F, -4.195F, 0.73F, 8.0F, 2.0F, 1.0F, 0.2F, false);
		head2.texOffs(67, 22).addBox(-4.0F, -3.195F, 1.0F, 8.0F, 1.0F, 3.0F, 0.2F, false);
		head2.texOffs(26, 1).addBox(-5.0F, -4.0F, -0.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);
		head2.texOffs(71, 7).addBox(2.8342F, -8.1F, -4.3634F, 1.0F, 8.0F, 1.0F, 0.2F, false);
		head2.texOffs(34, 26).addBox(-4.0F, -0.74F, -4.0F, 8.0F, 1.0F, 8.0F, -0.8399F, false);
		head2.texOffs(5, 17).addBox(-3.0F, -8.0F, -4.09F, 6.0F, 4.0F, 2.0F, -0.01F, false);

		rightPartForehead2 = new ModelRenderer(this);
		rightPartForehead2.setPos(0.0F, -2.01F, -4.29F);
		head2.addChild(rightPartForehead2);
		rightPartForehead2.texOffs(22, 17).addBox(-0.01F, -5.99F, -0.01F, 1.0F, 6.0F, 1.0F, -0.01F, false);

		leftPartForehead2 = new ModelRenderer(this);
		leftPartForehead2.setPos(0.0F, -2.01F, -4.29F);
		head2.addChild(leftPartForehead2);
		leftPartForehead2.texOffs(0, 17).addBox(-0.99F, -5.99F, -0.01F, 1.0F, 6.0F, 1.0F, -0.01F, false);

		leftLeg.texOffs(112, 108).addBox(2.0F, -1.25F, -1.5F, 1.0F, 3.0F, 3.0F, 0.0F, true);
		leftLeg.texOffs(108, 118).addBox(-1.0F, 4.5F, -2.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		leftLeg.texOffs(111, 101).addBox(-2.0F, -0.5F, 0.5F, 5.0F, 1.0F, 2.0F, -0.1F, false);
		leftLeg.texOffs(111, 104).addBox(-2.0F, 0.3F, 1.5F, 1.0F, 3.0F, 1.0F, -0.1F, false);
		leftLeg.texOffs(115, 104).addBox(-3.0F, 2.5F, 0.5F, 2.0F, 1.0F, 2.0F, -0.1F, false);
		leftLeg.texOffs(111, 96).addBox(-3.0F, 2.5F, -2.5F, 3.0F, 1.0F, 4.0F, -0.1F, false);
		leftLeg.texOffs(121, 96).addBox(-1.0F, 0.3F, -2.5F, 1.0F, 3.0F, 1.0F, -0.1F, false);
		leftLeg.texOffs(113, 93).addBox(-1.0F, -0.5F, -2.5F, 4.0F, 1.0F, 2.0F, -0.1F, false);

		rightLeg.texOffs(80, 108).addBox(-3.0F, -1.25F, -1.5F, 1.0F, 3.0F, 3.0F, 0.0F, false);
		rightLeg.texOffs(76, 118).addBox(-1.0F, 4.5F, -2.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		rightLeg.texOffs(81, 93).addBox(-3.0F, -0.5F, -2.5F, 4.0F, 1.0F, 2.0F, -0.1F, false);
		rightLeg.texOffs(79, 101).addBox(-3.0F, -0.5F, 0.5F, 5.0F, 1.0F, 2.0F, -0.1F, false);
		rightLeg.texOffs(79, 96).addBox(0.0F, 2.5F, -2.5F, 3.0F, 1.0F, 4.0F, -0.1F, false);
		rightLeg.texOffs(83, 104).addBox(1.0F, 2.5F, 0.5F, 2.0F, 1.0F, 2.0F, -0.1F, false);
		rightLeg.texOffs(89, 96).addBox(0.0F, 0.3F, -2.5F, 1.0F, 3.0F, 1.0F, -0.1F, false);
		rightLeg.texOffs(79, 104).addBox(1.0F, 0.3F, 1.5F, 1.0F, 3.0F, 1.0F, -0.1F, false);
	}

	@Override
	public void prepareMobModel(TheHandEntity entity, float walkAnimPos, float walkAnimSpeed, float partialTick) {
		super.prepareMobModel(entity, walkAnimPos, walkAnimSpeed, partialTick);
		if (head != null) {
			head.visible = entity.hashead();
		}
		if (head2 != null) {
			head2.visible = entity.hashead2();
		}
	}

	@Override
	protected RotationAngle[][] initSummonPoseRotations() {
		return new RotationAngle[][] {
				new RotationAngle[] {
						RotationAngle.fromDegrees(head, -5F, 0, -5F),
						RotationAngle.fromDegrees(body, 0, 0, 0),
						RotationAngle.fromDegrees(upperPart, 0, 0, 0),
						RotationAngle.fromDegrees(leftArm, -5F, 0, 0),
						RotationAngle.fromDegrees(leftForeArm, -90F, 0, 95F),
						RotationAngle.fromDegrees(rightArm, 5F, 0, 0),
						RotationAngle.fromDegrees(rightForeArm, -90F, 0, -95F),
						RotationAngle.fromDegrees(leftLeg, -5F, 0, -10F),
						RotationAngle.fromDegrees(leftLowerLeg, 30F, 0, 0),
						RotationAngle.fromDegrees(rightLeg, -5F, 0, 10F),
						RotationAngle.fromDegrees(rightLowerLeg, 30F, 0, 0)
				},
				new RotationAngle[] {
						RotationAngle.fromDegrees(head, -5F, 0, 5F),
						RotationAngle.fromDegrees(body, 5F, 0, 0),
						RotationAngle.fromDegrees(upperPart, 0, 0, 0),
						RotationAngle.fromDegrees(leftArm, 0, 0, -40F),
						RotationAngle.fromDegrees(leftForeArm, -120F, 0, -40F),
						RotationAngle.fromDegrees(rightArm, -35F, 0, 15F),
						RotationAngle.fromDegrees(rightForeArm, -60F, -20F, 30F),
						RotationAngle.fromDegrees(leftLeg, -5F, 0, -10F),
						RotationAngle.fromDegrees(leftLowerLeg, 30F, 0, 0),
						RotationAngle.fromDegrees(rightLeg, -5F, 0, 10F),
						RotationAngle.fromDegrees(rightLowerLeg, 30F, 0, 0)
				},
				new RotationAngle[] {
						RotationAngle.fromDegrees(head, 0, 0, 0),
						RotationAngle.fromDegrees(body, 10F, -40F, 0),
						RotationAngle.fromDegrees(upperPart, 0, 0, 0),
						RotationAngle.fromDegrees(leftArm, -60F, -40F, -20F),
						RotationAngle.fromDegrees(leftForeArm, 20F, 20F, 110F),
						RotationAngle.fromDegrees(rightArm, -40F, 5F, 80F),
						RotationAngle.fromDegrees(rightForeArm, -10F, 15F, 0),
						RotationAngle.fromDegrees(leftLeg, -20F, 0, -20F),
						RotationAngle.fromDegrees(leftLowerLeg, 20F, 0, 0),
						RotationAngle.fromDegrees(rightLeg, -20F, 0, 20F),
						RotationAngle.fromDegrees(rightLowerLeg, 20F, 0, 0)
				}
		};
	}

	@Override
	protected void initActionPoses() {
		ModelPose<TheHandEntity> kickPose1 = new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(body, -45F, 0F, 0F),
				RotationAngle.fromDegrees(leftArm, 0F, -40F, -30F),
				RotationAngle.fromDegrees(leftForeArm, 0F, 0F, -110F),
				RotationAngle.fromDegrees(rightArm, -40F, 30F, 0F),
				RotationAngle.fromDegrees(rightForeArm, -110F, 0F, 0F),
				RotationAngle.fromDegrees(leftLeg, -60F, -10F, 0F),
				RotationAngle.fromDegrees(leftLowerLeg, 60F, 0F, 0F),
				RotationAngle.fromDegrees(rightLeg, -10F, 15F, 10F),
				RotationAngle.fromDegrees(rightLowerLeg, 10F, 0F, -10F)
		});
		ModelPose<TheHandEntity> kickPose2 = new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(body, -45F, 0F, 0F),
				RotationAngle.fromDegrees(leftLeg, -80F, -10F, 0F),
				RotationAngle.fromDegrees(leftLowerLeg, 80F, 0F, 0F)
		});
		ModelPose<TheHandEntity> kickPose3 = new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(body, -45F, 0F, 0F),
				RotationAngle.fromDegrees(leftLeg, 0F, -10F, 0F),
				RotationAngle.fromDegrees(leftLowerLeg, 0F, 0, 0F)
		});
		ModelPose<TheHandEntity> kickRecoveryBodyFix = new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(body, 0F, 0F, 0F)
		});
		actionAnim.put(StandPose.HEAVY_ATTACK_FINISHER, new PosedActionAnimation.Builder<TheHandEntity>()
				.addPose(StandEntityAction.Phase.WINDUP, new ModelPoseTransitionMultiple.Builder<>(idlePose)
						.addPose(0.5F, kickPose1)
						.addPose(0.75F, kickPose2)
						.build(kickPose3))
				.addPose(StandEntityAction.Phase.RECOVERY, new ModelPoseTransitionMultiple.Builder<>(kickPose3)
						.addPose(0.5F, kickPose3)
                        .addPose(0.9F, kickRecoveryBodyFix)
						.build(idlePose))
				.build(idlePose));

		ModelPose<TheHandEntity> handPose1 = new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(head2, -10F, -10F, 0F),
				RotationAngle.fromDegrees(body, 10F, 10F, 0F),
				RotationAngle.fromDegrees(upperPart, 0F, 0F, 0F),
				RotationAngle.fromDegrees(leftArm, -2.5F, 0F, -5F),
				RotationAngle.fromDegrees(leftForeArm, -20F, 0F, 0F),
				RotationAngle.fromDegrees(rightArm, 45F, 0F, 90F),
				RotationAngle.fromDegrees(rightForeArm, -140F, 35F, -35F),
				RotationAngle.fromDegrees(leftLeg, -5.5F, 0F, -10F),
				RotationAngle.fromDegrees(leftLowerLeg, 25F, 0F, 0F),
				RotationAngle.fromDegrees(rightLeg, -5.5F, 0F, 15F),
				RotationAngle.fromDegrees(rightLowerLeg, 15F, 0F, 0F)
		});
		ModelPose<TheHandEntity> handPose2 = new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(head2, -15F, -5F, 0F),
				RotationAngle.fromDegrees(body, 5F, 5F, 0F),
				RotationAngle.fromDegrees(upperPart, 15F, 0F, 0F),
				RotationAngle.fromDegrees(rightArm, -70F, 70F, 90F),
				RotationAngle.fromDegrees(rightForeArm, -10F, 10F, 10F),
				RotationAngle.fromDegrees(leftLeg, 9.5F, 0F, -10F)
		});
		ModelPose<TheHandEntity> handPose3 = new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(head2, -20F, 0F, 0F),
				RotationAngle.fromDegrees(body, 0F, 0F, 0F),
				RotationAngle.fromDegrees(upperPart, 30F, 0F, 0F),
				RotationAngle.fromDegrees(rightArm, -90F, -40F, 90F),
				RotationAngle.fromDegrees(rightForeArm, 0F, 0F, 0F),
				RotationAngle.fromDegrees(leftLeg, 24.5F, 0F, -10F)
		});
		actionAnim.put(TheHandErase.ERASE_POSE, new PosedActionAnimation.Builder<TheHandEntity>()
				.addPose(StandEntityAction.Phase.WINDUP, new ModelPoseTransition<>(handPose1, handPose2))
				.addPose(StandEntityAction.Phase.PERFORM, new ModelPoseTransition<>(handPose2, handPose3))
				.addPose(StandEntityAction.Phase.RECOVERY, new ModelPoseTransitionMultiple.Builder<>(handPose3)
						.addPose(0.5F, handPose3)
						.build(idlePose))
				.build(idlePose));

		ModelPose.ModelAnim<TheHandEntity> armsRotation = (rotationAmount, entity, ticks, yRotationOffset, xRotation) -> {
			leftArm.xRotSecond = xRotation * MathUtil.DEG_TO_RAD;
			rightArm.xRotSecond = xRotation * MathUtil.DEG_TO_RAD;
		};

		RotationAngle[]erasureRightStart = new RotationAngle[] {
				RotationAngle.fromDegrees(body, 10, -45, 0),
				RotationAngle.fromDegrees(rightArm, -90, 90, 0),
				RotationAngle.fromDegrees(rightForeArm, 0, 0, -20)
		};

		RotationAngle[] erasureRightImpact = new RotationAngle[] {
				RotationAngle.fromDegrees(body, 10, -45, 0),
				RotationAngle.fromDegrees(rightArm, -90, 0, 0),
				RotationAngle.fromDegrees(rightForeArm, 0, 0, -50)
		};

		IModelPose<TheHandEntity> erasureStabStart = new ModelPoseSided<>(
				new ModelPose<TheHandEntity>(mirrorAngles(erasureRightStart)).setAdditionalAnim(armsRotation),
				new ModelPose<TheHandEntity>(erasureRightStart).setAdditionalAnim(armsRotation));

		IModelPose<TheHandEntity> erasureStabImpact = new ModelPoseSided<>(
				new ModelPose<TheHandEntity>(mirrorAngles(erasureRightImpact)).setAdditionalAnim(armsRotation),
				new ModelPose<TheHandEntity>(erasureRightImpact).setAdditionalAnim(armsRotation));

		IModelPose<TheHandEntity> stabLoop = new ModelPoseTransition<TheHandEntity>(erasureStabStart, erasureStabImpact).setEasing(sw -> {
			float halfSwing = sw < 0.4F ? sw * 20 / 8 : sw > 0.6F ? (1 - sw) * 20 / 8 : 1F;
			return halfSwing * halfSwing * halfSwing;
		});

		actionAnim.putIfAbsent(TheHandErasureBarrage.ERASURE_BARRAGE_POSE, new StandOneHandedBarrageAnimation<TheHandEntity>(this,
				stabLoop,
				idlePose,
				Hand.MAIN_HAND));

		super.initActionPoses();
	}

	@Override
	protected ModelPose<TheHandEntity> initIdlePose() {
		return new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(upperPart, 0.0F, 0.0F, 0.0F),
				RotationAngle.fromDegrees(leftArm, -2.5F, 0F, -5F),
				RotationAngle.fromDegrees(leftForeArm, -20F, 0.0F, 0.0F),
				RotationAngle.fromDegrees(rightArm, -22.5F, -35F, 15F),
				RotationAngle.fromDegrees(rightForeArm, -105F, -10F, 0.0F),
				RotationAngle.fromDegrees(leftLeg, -5.5F, 0.0F, -10F),
				RotationAngle.fromDegrees(leftLowerLeg, 25F, 0.0F, 0.0F),
				RotationAngle.fromDegrees(rightLeg, -5.5F, 0.0F, 15F),
				RotationAngle.fromDegrees(rightLowerLeg, 15F, 0.0F, 0.0F)
		});
	}

	@Override
	protected ModelPose<TheHandEntity> initIdlePose2Loop() {
		return new ModelPose<>(new RotationAngle[] {
				RotationAngle.fromDegrees(leftArm, 0.0F, 0.0F, -5F),
				RotationAngle.fromDegrees(leftForeArm, -22.5F, 0.0F, 0.0F),
				RotationAngle.fromDegrees(rightArm, -25F, -35F, 15F),
				RotationAngle.fromDegrees(rightForeArm, -102.5F, -10F, 0.0F)
		});
	}
}