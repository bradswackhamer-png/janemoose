package com.swack.janemoose.client.model;

import com.swack.janemoose.client.renderer.AttachmentRenderState;
import com.swack.janemoose.item.IAttachment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;

public class AttachmentModel extends EntityModel<AttachmentRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath("janemoose", "attachment"), "main");

    private final ModelPart root;
    private final ModelPart toolbar;

    // Beams by width
    private final ModelPart beamIronLeft;
    private final ModelPart beamIronRight;
    private final ModelPart beamDiamondLeft;
    private final ModelPart beamDiamondRight;

    // Cultivator components
    private final ModelPart cultivatorCenter;
    private final ModelPart cultivatorIron;
    private final ModelPart cultivatorDiamond;

    // Harvester components
    private final ModelPart harvesterCutterCenter;
    private final ModelPart harvesterCutterIron;
    private final ModelPart harvesterCutterDiamond;
    private final ModelPart harvesterReel;
    private final ModelPart harvesterReelIron;
    private final ModelPart harvesterReelDiamond;

    // Planter components
    private final ModelPart planterCenter;
    private final ModelPart planterIron;
    private final ModelPart planterDiamond;

    // Status indicator buttons
    private final ModelPart statusButtonActive;
    private final ModelPart statusButtonInactive;

    public AttachmentModel(ModelPart root) {
        super(root);
        this.root = root;
        this.toolbar = root.getChild("toolbar");

        this.beamIronLeft = this.toolbar.getChild("beam_iron_left");
        this.beamIronRight = this.toolbar.getChild("beam_iron_right");
        this.beamDiamondLeft = this.toolbar.getChild("beam_diamond_left");
        this.beamDiamondRight = this.toolbar.getChild("beam_diamond_right");

        this.cultivatorCenter = this.toolbar.getChild("cultivator_center");
        this.cultivatorIron = this.toolbar.getChild("cultivator_iron");
        this.cultivatorDiamond = this.toolbar.getChild("cultivator_diamond");

        this.harvesterCutterCenter = this.toolbar.getChild("harvester_cutter_center");
        this.harvesterCutterIron = this.toolbar.getChild("harvester_cutter_iron");
        this.harvesterCutterDiamond = this.toolbar.getChild("harvester_cutter_diamond");
        this.harvesterReel = this.toolbar.getChild("harvester_reel");
        this.harvesterReelIron = this.harvesterReel.getChild("harvester_reel_iron");
        this.harvesterReelDiamond = this.harvesterReel.getChild("harvester_reel_diamond");

        this.planterCenter = this.toolbar.getChild("planter_center");
        this.planterIron = this.toolbar.getChild("planter_iron");
        this.planterDiamond = this.toolbar.getChild("planter_diamond");

        this.statusButtonActive = this.toolbar.getChild("status_button_active");
        this.statusButtonInactive = this.toolbar.getChild("status_button_inactive");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. Drawbar tongue reaching forward (-Z) to tractor hitch
        partdefinition.addOrReplaceChild("tongue",
                CubeListBuilder.create()
                        // Drawbar beam
                        .texOffs(0, 0)
                        .addBox(-2.0F, -4.0F, -12.0F, 4.0F, 2.0F, 12.0F)
                        // Clevis hitch ring coupler
                        .texOffs(32, 0)
                        .addBox(-1.5F, -4.5F, -14.0F, 3.0F, 3.0F, 2.0F)
                        // Hydraulic lift cylinder
                        .texOffs(42, 0)
                        .addBox(-1.0F, -7.0F, -6.0F, 2.0F, 3.0F, 6.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 2. Toolbar pivot (tilts up when lifted, down when lowered)
        PartDefinition toolbar = partdefinition.addOrReplaceChild("toolbar",
                CubeListBuilder.create()
                        // Center crossbeam (Width 1, 16px wide)
                        .texOffs(0, 16)
                        .addBox(-8.0F, -4.0F, -2.0F, 16.0F, 3.0F, 4.0F)
                        // Control console box
                        .texOffs(40, 16)
                        .addBox(-2.0F, -7.0F, -1.0F, 4.0F, 3.0F, 2.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // Beam width extensions
        toolbar.addOrReplaceChild("beam_iron_left",
                CubeListBuilder.create().texOffs(0, 23).addBox(8.0F, -4.0F, -2.0F, 8.0F, 3.0F, 4.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("beam_iron_right",
                CubeListBuilder.create().texOffs(0, 23).addBox(-16.0F, -4.0F, -2.0F, 8.0F, 3.0F, 4.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("beam_diamond_left",
                CubeListBuilder.create().texOffs(0, 30).addBox(16.0F, -4.0F, -2.0F, 8.0F, 3.0F, 4.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("beam_diamond_right",
                CubeListBuilder.create().texOffs(0, 30).addBox(-24.0F, -4.0F, -2.0F, 8.0F, 3.0F, 4.0F),
                PartPose.ZERO);

        // Buttons: Active (Green) and Inactive (Red)
        toolbar.addOrReplaceChild("status_button_active",
                CubeListBuilder.create().texOffs(0, 64).addBox(-1.5F, -8.0F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("status_button_inactive",
                CubeListBuilder.create().texOffs(8, 64).addBox(-1.5F, -8.0F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.ZERO);

        // CULTIVATOR
        toolbar.addOrReplaceChild("cultivator_center",
                CubeListBuilder.create()
                        // Center shank
                        .texOffs(0, 37)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        // Center sweep point
                        .texOffs(8, 37)
                        .addBox(-2.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("cultivator_iron",
                CubeListBuilder.create()
                        .texOffs(0, 37).addBox(5.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(8, 37).addBox(3.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F)
                        .texOffs(0, 37).addBox(-7.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(8, 37).addBox(-8.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("cultivator_diamond",
                CubeListBuilder.create()
                        .texOffs(0, 37).addBox(13.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(8, 37).addBox(11.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F)
                        .texOffs(0, 37).addBox(-15.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(8, 37).addBox(-16.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F),
                PartPose.ZERO);

        // HARVESTER
        toolbar.addOrReplaceChild("harvester_cutter_center",
                CubeListBuilder.create()
                        .texOffs(0, 44)
                        .addBox(-8.0F, 2.0F, 0.0F, 16.0F, 2.0F, 4.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("harvester_cutter_iron",
                CubeListBuilder.create()
                        .texOffs(0, 50).addBox(8.0F, 2.0F, 0.0F, 8.0F, 2.0F, 4.0F)
                        .texOffs(0, 50).addBox(-16.0F, 2.0F, 0.0F, 8.0F, 2.0F, 4.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("harvester_cutter_diamond",
                CubeListBuilder.create()
                        .texOffs(0, 56).addBox(16.0F, 2.0F, 0.0F, 8.0F, 2.0F, 4.0F)
                        .texOffs(0, 56).addBox(-24.0F, 2.0F, 0.0F, 8.0F, 2.0F, 4.0F),
                PartPose.ZERO);

        // Harvester Reel (rotating)
        PartDefinition harvesterReel = toolbar.addOrReplaceChild("harvester_reel",
                CubeListBuilder.create()
                        // Central axle
                        .texOffs(64, 0).addBox(-8.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F)
                        // Paddle bats
                        .texOffs(64, 4).addBox(-8.0F, -4.0F, -0.5F, 16.0F, 1.0F, 1.0F)
                        .texOffs(64, 4).addBox(-8.0F, 3.0F, -0.5F, 16.0F, 1.0F, 1.0F)
                        .texOffs(64, 6).addBox(-8.0F, -0.5F, -4.0F, 16.0F, 1.0F, 1.0F)
                        .texOffs(64, 6).addBox(-8.0F, -0.5F, 3.0F, 16.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, -1.0F, 2.0F));

        harvesterReel.addOrReplaceChild("harvester_reel_iron",
                CubeListBuilder.create()
                        .texOffs(64, 8).addBox(8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F)
                        .texOffs(64, 8).addBox(-16.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F)
                        .texOffs(64, 12).addBox(8.0F, -4.0F, -0.5F, 8.0F, 1.0F, 1.0F)
                        .texOffs(64, 12).addBox(-16.0F, -4.0F, -0.5F, 8.0F, 1.0F, 1.0F)
                        .texOffs(64, 12).addBox(8.0F, 3.0F, -0.5F, 8.0F, 1.0F, 1.0F)
                        .texOffs(64, 12).addBox(-16.0F, 3.0F, -0.5F, 8.0F, 1.0F, 1.0F),
                PartPose.ZERO);

        harvesterReel.addOrReplaceChild("harvester_reel_diamond",
                CubeListBuilder.create()
                        .texOffs(64, 14).addBox(16.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F)
                        .texOffs(64, 14).addBox(-24.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F)
                        .texOffs(64, 18).addBox(16.0F, -4.0F, -0.5F, 8.0F, 1.0F, 1.0F)
                        .texOffs(64, 18).addBox(-24.0F, -4.0F, -0.5F, 8.0F, 1.0F, 1.0F),
                PartPose.ZERO);

        // PLANTER
        toolbar.addOrReplaceChild("planter_center",
                CubeListBuilder.create()
                        // Seed hopper tank
                        .texOffs(64, 24).addBox(-4.0F, -10.0F, -3.0F, 8.0F, 6.0F, 6.0F)
                        // Seed hopper lid
                        .texOffs(64, 36).addBox(-4.5F, -11.0F, -3.5F, 9.0F, 1.0F, 7.0F)
                        // Drop tube & furrow opener
                        .texOffs(96, 24).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 7.0F, 2.0F)
                        // Rear press wheel
                        .texOffs(96, 33).addBox(-1.5F, 1.0F, 1.0F, 3.0F, 3.0F, 3.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("planter_iron",
                CubeListBuilder.create()
                        .texOffs(64, 44).addBox(7.0F, -9.0F, -2.5F, 6.0F, 5.0F, 5.0F)
                        .texOffs(64, 44).addBox(-13.0F, -9.0F, -2.5F, 6.0F, 5.0F, 5.0F)
                        .texOffs(96, 24).addBox(9.0F, -4.0F, -1.0F, 2.0F, 7.0F, 2.0F)
                        .texOffs(96, 24).addBox(-11.0F, -4.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("planter_diamond",
                CubeListBuilder.create()
                        .texOffs(64, 44).addBox(16.0F, -9.0F, -2.5F, 6.0F, 5.0F, 5.0F)
                        .texOffs(64, 44).addBox(-22.0F, -9.0F, -2.5F, 6.0F, 5.0F, 5.0F)
                        .texOffs(96, 24).addBox(18.0F, -4.0F, -1.0F, 2.0F, 7.0F, 2.0F)
                        .texOffs(96, 24).addBox(-20.0F, -4.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(AttachmentRenderState state) {
        super.setupAnim(state);

        // Hydraulic lift/lower: Tilt toolbar up when lifted, down when lowered
        if (!state.isLowered) {
            this.toolbar.xRot = -0.55F;
            this.toolbar.y = 16.0F;
        } else {
            this.toolbar.xRot = 0.0F;
            this.toolbar.y = 20.0F;
        }

        // Width wings
        boolean isIron = state.width >= 2;
        boolean isDiamond = state.width >= 3;
        this.beamIronLeft.visible = isIron;
        this.beamIronRight.visible = isIron;
        this.beamDiamondLeft.visible = isDiamond;
        this.beamDiamondRight.visible = isDiamond;

        // Implement type visibility
        boolean isCult = state.type == IAttachment.AttachmentType.CULTIVATOR;
        boolean isHarv = state.type == IAttachment.AttachmentType.HARVESTER;
        boolean isPlant = state.type == IAttachment.AttachmentType.PLANTER;

        // Cultivator
        this.cultivatorCenter.visible = isCult;
        this.cultivatorIron.visible = isCult && isIron;
        this.cultivatorDiamond.visible = isCult && isDiamond;

        // Harvester
        this.harvesterCutterCenter.visible = isHarv;
        this.harvesterCutterIron.visible = isHarv && isIron;
        this.harvesterCutterDiamond.visible = isHarv && isDiamond;
        this.harvesterReel.visible = isHarv;
        this.harvesterReelIron.visible = isHarv && isIron;
        this.harvesterReelDiamond.visible = isHarv && isDiamond;

        // Reel spinning animation when running & lowered & tractor moving
        if (isHarv && state.isActive && state.isLowered && state.isMoving) {
            this.harvesterReel.xRot = state.animationTicks * 0.35F;
        } else {
            this.harvesterReel.xRot = 0.0F;
        }

        // Planter
        this.planterCenter.visible = isPlant;
        this.planterIron.visible = isPlant && isIron;
        this.planterDiamond.visible = isPlant && isDiamond;

        // Status Button
        this.statusButtonActive.visible = state.isActive;
        this.statusButtonInactive.visible = !state.isActive;
    }
}
