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

        // 1. Heavy Drawbar Tongue reaching forward (-Z) to tractor 3-point hitch
        partdefinition.addOrReplaceChild("tongue",
                CubeListBuilder.create()
                        // Solid steel drawbar beam
                        .texOffs(0, 0)
                        .addBox(-2.0F, -4.0F, -12.0F, 4.0F, 2.0F, 12.0F)
                        // Clevis hitch ring coupler
                        .texOffs(36, 0)
                        .addBox(-1.5F, -4.5F, -14.0F, 3.0F, 3.0F, 2.0F)
                        // Hydraulic lift cylinder & chrome ram
                        .texOffs(48, 0)
                        .addBox(-1.0F, -7.0F, -6.0F, 2.0F, 3.0F, 6.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 2. Toolbar Pivot Crossbeam (Tilts up when lifted, down when lowered)
        PartDefinition toolbar = partdefinition.addOrReplaceChild("toolbar",
                CubeListBuilder.create()
                        // Center crossbeam (Width 1, 16px wide)
                        .texOffs(0, 16)
                        .addBox(-8.0F, -4.0F, -2.0F, 16.0F, 3.0F, 4.0F)
                        // Control console box
                        .texOffs(54, 16)
                        .addBox(-2.0F, -7.0F, -1.0F, 4.0F, 3.0F, 2.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // Beam width extensions
        toolbar.addOrReplaceChild("beam_iron_left",
                CubeListBuilder.create().texOffs(0, 24).addBox(8.0F, -4.0F, -2.0F, 8.0F, 3.0F, 4.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("beam_iron_right",
                CubeListBuilder.create().texOffs(26, 24).addBox(-16.0F, -4.0F, -2.0F, 8.0F, 3.0F, 4.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("beam_diamond_left",
                CubeListBuilder.create().texOffs(0, 32).addBox(16.0F, -4.0F, -2.0F, 8.0F, 3.0F, 4.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("beam_diamond_right",
                CubeListBuilder.create().texOffs(26, 32).addBox(-24.0F, -4.0F, -2.0F, 8.0F, 3.0F, 4.0F),
                PartPose.ZERO);

        // Indicator Buttons: Active (Green) and Inactive (Red)
        toolbar.addOrReplaceChild("status_button_active",
                CubeListBuilder.create().texOffs(70, 16).addBox(-1.5F, -8.0F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.ZERO);
        toolbar.addOrReplaceChild("status_button_inactive",
                CubeListBuilder.create().texOffs(78, 16).addBox(-1.5F, -8.0F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.ZERO);

        // ================= CULTIVATOR (TILLER) =================
        toolbar.addOrReplaceChild("cultivator_center",
                CubeListBuilder.create()
                        // Center forged spring shank
                        .texOffs(0, 40)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        // Hardened duckfoot sweep plow point
                        .texOffs(16, 40)
                        .addBox(-2.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("cultivator_iron",
                CubeListBuilder.create()
                        .texOffs(0, 40).addBox(5.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(16, 40).addBox(3.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F)
                        .texOffs(0, 40).addBox(-7.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(16, 40).addBox(-8.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("cultivator_diamond",
                CubeListBuilder.create()
                        .texOffs(0, 40).addBox(13.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(16, 40).addBox(11.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F)
                        .texOffs(0, 40).addBox(-15.0F, -1.0F, -1.0F, 2.0F, 5.0F, 2.0F)
                        .texOffs(16, 40).addBox(-16.5F, 3.5F, -3.0F, 5.0F, 1.0F, 4.0F),
                PartPose.ZERO);

        // ================= REAL-LIFE HARVESTER (COMBINE GRAIN HEADER) =================
        toolbar.addOrReplaceChild("harvester_cutter_center",
                CubeListBuilder.create()
                        // 1. Sheet metal trough floor & grain pan
                        .texOffs(0, 100).addBox(-8.0F, 1.0F, 0.0F, 16.0F, 2.5F, 8.0F)
                        // 2. Rear back sheet wall
                        .texOffs(50, 100).addBox(-8.0F, -5.0F, 7.0F, 16.0F, 6.0F, 1.5F)
                        // 3. Double-knife sickle cutterbar with triangular rock guards
                        .texOffs(88, 100).addBox(-8.0F, 2.0F, -1.0F, 16.0F, 0.8F, 2.0F)
                        // 4. Outer pointed crop divider snouts (torpedo gatherers)
                        .texOffs(126, 100).addBox(7.2F, 0.0F, -4.0F, 1.5F, 3.5F, 12.0F)
                        .texOffs(126, 100).addBox(-8.7F, 0.0F, -4.0F, 1.5F, 3.5F, 12.0F)
                        // 5. Cross-auger (gathering feed auger drum with spiral flighting)
                        .texOffs(160, 100).addBox(-7.5F, -1.0F, 3.5F, 15.0F, 3.0F, 3.0F)
                        // 6. Feeder house & bulk grain tank with inspection window
                        .texOffs(0, 140).addBox(-4.0F, -9.0F, 5.0F, 8.0F, 5.0F, 5.0F)
                        // Tank lid with rubber weatherseal
                        .texOffs(30, 140).addBox(-4.5F, -10.0F, 4.5F, 9.0F, 1.2F, 6.0F)
                        // Unloading auger tube
                        .texOffs(62, 140).addBox(4.5F, -7.0F, 5.5F, 2.0F, 2.0F, 7.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("harvester_cutter_iron",
                CubeListBuilder.create()
                        // Left & Right trough wings
                        .texOffs(0, 100).addBox(8.0F, 1.0F, 0.0F, 8.0F, 2.5F, 8.0F)
                        .texOffs(0, 100).addBox(-16.0F, 1.0F, 0.0F, 8.0F, 2.5F, 8.0F)
                        // Extended back sheet
                        .texOffs(50, 100).addBox(8.0F, -5.0F, 7.0F, 8.0F, 6.0F, 1.5F)
                        .texOffs(50, 100).addBox(-16.0F, -5.0F, 7.0F, 8.0F, 6.0F, 1.5F)
                        // Extended sickle cutterbar
                        .texOffs(88, 100).addBox(8.0F, 2.0F, -1.0F, 8.0F, 0.8F, 2.0F)
                        .texOffs(88, 100).addBox(-16.0F, 2.0F, -1.0F, 8.0F, 0.8F, 2.0F)
                        // Extended auger drum
                        .texOffs(160, 100).addBox(7.5F, -1.0F, 3.5F, 8.0F, 3.0F, 3.0F)
                        .texOffs(160, 100).addBox(-15.5F, -1.0F, 3.5F, 8.0F, 3.0F, 3.0F)
                        // Moved outer divider snouts to wings
                        .texOffs(126, 100).addBox(15.2F, 0.0F, -4.0F, 1.5F, 3.5F, 12.0F)
                        .texOffs(126, 100).addBox(-16.7F, 0.0F, -4.0F, 1.5F, 3.5F, 12.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("harvester_cutter_diamond",
                CubeListBuilder.create()
                        // Outer trough wings
                        .texOffs(0, 100).addBox(16.0F, 1.0F, 0.0F, 8.0F, 2.5F, 8.0F)
                        .texOffs(0, 100).addBox(-24.0F, 1.0F, 0.0F, 8.0F, 2.5F, 8.0F)
                        .texOffs(50, 100).addBox(16.0F, -5.0F, 7.0F, 8.0F, 6.0F, 1.5F)
                        .texOffs(50, 100).addBox(-24.0F, -5.0F, 7.0F, 8.0F, 6.0F, 1.5F)
                        .texOffs(88, 100).addBox(16.0F, 2.0F, -1.0F, 8.0F, 0.8F, 2.0F)
                        .texOffs(88, 100).addBox(-24.0F, 2.0F, -1.0F, 8.0F, 0.8F, 2.0F)
                        .texOffs(160, 100).addBox(15.5F, -1.0F, 3.5F, 8.0F, 3.0F, 3.0F)
                        .texOffs(160, 100).addBox(-23.5F, -1.0F, 3.5F, 8.0F, 3.0F, 3.0F)
                        .texOffs(126, 100).addBox(23.2F, 0.0F, -4.0F, 1.5F, 3.5F, 12.0F)
                        .texOffs(126, 100).addBox(-24.7F, 0.0F, -4.0F, 1.5F, 3.5F, 12.0F),
                PartPose.ZERO);

        // Harvester Rotating Pickup Bat Reel (4-bat rotating reel with spring tines)
        PartDefinition harvesterReel = toolbar.addOrReplaceChild("harvester_reel",
                CubeListBuilder.create()
                        // Central reel drive shaft
                        .texOffs(85, 140).addBox(-8.0F, -0.6F, -0.6F, 16.0F, 1.2F, 1.2F)
                        // 4 Eccentric Reel Bats with spring pickup fingers
                        .texOffs(120, 140).addBox(-8.0F, -3.5F, -0.5F, 16.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(-8.0F, 2.7F, -0.5F, 16.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(-8.0F, -0.4F, -3.5F, 16.0F, 1.0F, 0.8F)
                        .texOffs(120, 140).addBox(-8.0F, -0.4F, 2.7F, 16.0F, 1.0F, 0.8F)
                        // Reel spiders (end star brackets)
                        .texOffs(156, 140).addBox(7.6F, -3.5F, -3.5F, 0.6F, 7.0F, 7.0F)
                        .texOffs(156, 140).addBox(-8.2F, -3.5F, -3.5F, 0.6F, 7.0F, 7.0F),
                PartPose.offset(0.0F, -2.0F, 1.5F));

        harvesterReel.addOrReplaceChild("harvester_reel_iron",
                CubeListBuilder.create()
                        .texOffs(85, 140).addBox(8.0F, -0.6F, -0.6F, 8.0F, 1.2F, 1.2F)
                        .texOffs(85, 140).addBox(-16.0F, -0.6F, -0.6F, 8.0F, 1.2F, 1.2F)
                        .texOffs(120, 140).addBox(8.0F, -3.5F, -0.5F, 8.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(-16.0F, -3.5F, -0.5F, 8.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(8.0F, 2.7F, -0.5F, 8.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(-16.0F, 2.7F, -0.5F, 8.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(8.0F, -0.4F, -3.5F, 8.0F, 1.0F, 0.8F)
                        .texOffs(120, 140).addBox(-16.0F, -0.4F, -3.5F, 8.0F, 1.0F, 0.8F)
                        .texOffs(120, 140).addBox(8.0F, -0.4F, 2.7F, 8.0F, 1.0F, 0.8F)
                        .texOffs(120, 140).addBox(-16.0F, -0.4F, 2.7F, 8.0F, 1.0F, 0.8F),
                PartPose.ZERO);

        harvesterReel.addOrReplaceChild("harvester_reel_diamond",
                CubeListBuilder.create()
                        .texOffs(85, 140).addBox(16.0F, -0.6F, -0.6F, 8.0F, 1.2F, 1.2F)
                        .texOffs(85, 140).addBox(-24.0F, -0.6F, -0.6F, 8.0F, 1.2F, 1.2F)
                        .texOffs(120, 140).addBox(16.0F, -3.5F, -0.5F, 8.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(-24.0F, -3.5F, -0.5F, 8.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(16.0F, 2.7F, -0.5F, 8.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(-24.0F, 2.7F, -0.5F, 8.0F, 0.8F, 1.0F)
                        .texOffs(120, 140).addBox(16.0F, -0.4F, -3.5F, 8.0F, 1.0F, 0.8F)
                        .texOffs(120, 140).addBox(-24.0F, -0.4F, -3.5F, 8.0F, 1.0F, 0.8F)
                        .texOffs(120, 140).addBox(16.0F, -0.4F, 2.7F, 8.0F, 1.0F, 0.8F)
                        .texOffs(120, 140).addBox(-24.0F, -0.4F, 2.7F, 8.0F, 1.0F, 0.8F),
                PartPose.ZERO);

        // ================= REAL-LIFE PLANTER (ROW-CROP PLANTER WITH ROW UNITS) =================
        toolbar.addOrReplaceChild("planter_center",
                CubeListBuilder.create()
                        // Center Row Unit Hopper
                        .texOffs(0, 60).addBox(-3.5F, -11.0F, 3.0F, 7.0F, 6.0F, 6.0F)
                        // Sloped lower funnel
                        .texOffs(28, 60).addBox(-2.5F, -5.0F, 4.0F, 5.0F, 2.0F, 4.0F)
                        // Hopper Lid with molded latch
                        .texOffs(48, 60).addBox(-4.0F, -12.0F, 2.5F, 8.0F, 1.2F, 7.0F)
                        // Parallel Linkage Mount Arms
                        .texOffs(80, 60).addBox(-1.5F, -3.5F, 0.0F, 3.0F, 2.0F, 4.0F)
                        // Double-Disk Furrow Opener (soil coulter blades)
                        .texOffs(96, 60).addBox(-1.0F, -2.0F, 4.5F, 2.0F, 4.5F, 4.5F)
                        // Left & Right Depth Gauge Wheels
                        .texOffs(112, 60).addBox(2.2F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(-3.7F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        // Seed Drop Meter Tube
                        .texOffs(126, 60).addBox(-0.5F, -3.0F, 5.5F, 1.0F, 5.0F, 1.0F)
                        // V-Closing Press Wheels (Soil Firming Pack Wheels)
                        .texOffs(132, 60).addBox(0.6F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        .texOffs(132, 60).addBox(-2.0F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        // Rear Catwalk Walkway Platform
                        .texOffs(144, 60).addBox(-6.0F, -3.0F, 10.5F, 12.0F, 1.0F, 3.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("planter_iron",
                CubeListBuilder.create()
                        // Left Row Unit ($X = -9$)
                        .texOffs(0, 60).addBox(-12.5F, -11.0F, 3.0F, 7.0F, 6.0F, 6.0F)
                        .texOffs(28, 60).addBox(-11.5F, -5.0F, 4.0F, 5.0F, 2.0F, 4.0F)
                        .texOffs(48, 60).addBox(-13.0F, -12.0F, 2.5F, 8.0F, 1.2F, 7.0F)
                        .texOffs(80, 60).addBox(-10.5F, -3.5F, 0.0F, 3.0F, 2.0F, 4.0F)
                        .texOffs(96, 60).addBox(-10.0F, -2.0F, 4.5F, 2.0F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(-6.8F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(-12.7F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(132, 60).addBox(-8.4F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        .texOffs(132, 60).addBox(-11.0F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        // Right Row Unit ($X = +9$)
                        .texOffs(0, 60).addBox(5.5F, -11.0F, 3.0F, 7.0F, 6.0F, 6.0F)
                        .texOffs(28, 60).addBox(6.5F, -5.0F, 4.0F, 5.0F, 2.0F, 4.0F)
                        .texOffs(48, 60).addBox(5.0F, -12.0F, 2.5F, 8.0F, 1.2F, 7.0F)
                        .texOffs(80, 60).addBox(7.5F, -3.5F, 0.0F, 3.0F, 2.0F, 4.0F)
                        .texOffs(96, 60).addBox(8.0F, -2.0F, 4.5F, 2.0F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(11.2F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(5.3F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(132, 60).addBox(9.6F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        .texOffs(132, 60).addBox(7.0F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        // Extended Walkway Catwalk
                        .texOffs(144, 60).addBox(-16.0F, -3.0F, 10.5F, 10.0F, 1.0F, 3.0F)
                        .texOffs(144, 60).addBox(6.0F, -3.0F, 10.5F, 10.0F, 1.0F, 3.0F),
                PartPose.ZERO);

        toolbar.addOrReplaceChild("planter_diamond",
                CubeListBuilder.create()
                        // Outer Left Row Unit ($X = -18$)
                        .texOffs(0, 60).addBox(-21.5F, -11.0F, 3.0F, 7.0F, 6.0F, 6.0F)
                        .texOffs(28, 60).addBox(-20.5F, -5.0F, 4.0F, 5.0F, 2.0F, 4.0F)
                        .texOffs(48, 60).addBox(-22.0F, -12.0F, 2.5F, 8.0F, 1.2F, 7.0F)
                        .texOffs(80, 60).addBox(-19.5F, -3.5F, 0.0F, 3.0F, 2.0F, 4.0F)
                        .texOffs(96, 60).addBox(-19.0F, -2.0F, 4.5F, 2.0F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(-15.8F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(-21.7F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(132, 60).addBox(-17.4F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        .texOffs(132, 60).addBox(-20.0F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        // Outer Right Row Unit ($X = +18$)
                        .texOffs(0, 60).addBox(14.5F, -11.0F, 3.0F, 7.0F, 6.0F, 6.0F)
                        .texOffs(28, 60).addBox(15.5F, -5.0F, 4.0F, 5.0F, 2.0F, 4.0F)
                        .texOffs(48, 60).addBox(14.0F, -12.0F, 2.5F, 8.0F, 1.2F, 7.0F)
                        .texOffs(80, 60).addBox(16.5F, -3.5F, 0.0F, 3.0F, 2.0F, 4.0F)
                        .texOffs(96, 60).addBox(17.0F, -2.0F, 4.5F, 2.0F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(20.2F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(112, 60).addBox(14.3F, -1.0F, 4.0F, 1.5F, 4.5F, 4.5F)
                        .texOffs(132, 60).addBox(18.6F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        .texOffs(132, 60).addBox(16.0F, 0.5F, 9.5F, 1.4F, 3.0F, 3.0F)
                        // Folding Row Marker Arms
                        .texOffs(80, 60).addBox(-25.0F, -8.0F, -1.0F, 1.5F, 8.0F, 1.5F)
                        .texOffs(80, 60).addBox(23.5F, -8.0F, -1.0F, 1.5F, 8.0F, 1.5F)
                        // Extended Walkway Catwalk
                        .texOffs(144, 60).addBox(-24.0F, -3.0F, 10.5F, 8.0F, 1.0F, 3.0F)
                        .texOffs(144, 60).addBox(16.0F, -3.0F, 10.5F, 8.0F, 1.0F, 3.0F),
                PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 256, 256);
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
