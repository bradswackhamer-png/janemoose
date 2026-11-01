package com.swack.janemoose.client.model;

import com.swack.janemoose.client.renderer.BucketTruckRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class BucketTruckModel extends EntityModel<BucketTruckRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath("janemoose", "bucket_truck"), "main");

    private final ModelPart frontLeftWheel;
    private final ModelPart frontRightWheel;
    private final ModelPart backLeftWheel;
    private final ModelPart backRightWheel;
    private final ModelPart outriggerJacks;
    private final ModelPart turret;
    private final ModelPart lowerBoom;
    private final ModelPart upperBoom;
    private final ModelPart flyBoom;
    private final ModelPart bucket;

    public BucketTruckModel(ModelPart root) {
        super(root);
        this.frontLeftWheel = root.getChild("front_left_wheel");
        this.frontRightWheel = root.getChild("front_right_wheel");
        this.backLeftWheel = root.getChild("back_left_wheel");
        this.backRightWheel = root.getChild("back_right_wheel");
        this.outriggerJacks = root.getChild("outrigger_jacks");
        this.turret = root.getChild("turret");
        this.lowerBoom = this.turret.getChild("lower_boom");
        this.upperBoom = this.lowerBoom.getChild("upper_boom");
        this.flyBoom = this.upperBoom.getChild("fly_boom");
        this.bucket = this.flyBoom.getChild("bucket");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. White Cab-Over-Engine (COE) Utility Cab
        partdefinition.addOrReplaceChild("cab",
                CubeListBuilder.create()
                        // Lower cab body & doors (stops cleanly at Z=-9.0F)
                        .texOffs(0, 0)
                        .addBox(-9.5F, -12.0F, -25.0F, 19.0F, 8.5F, 16.0F)
                        // Contoured white cab roof
                        .texOffs(72, 0)
                        .addBox(-9.5F, -21.0F, -24.5F, 19.0F, 2.0F, 15.5F)
                        // Rear cab wall behind seats
                        .texOffs(144, 0)
                        .addBox(-9.5F, -19.0F, -10.5F, 19.0F, 7.0F, 1.5F)
                        // Front left & right A-pillars
                        .texOffs(190, 0)
                        .addBox(8.1F, -19.0F, -24.3F, 1.2F, 7.0F, 1.2F)
                        .texOffs(190, 0)
                        .addBox(-9.3F, -19.0F, -24.3F, 1.2F, 7.0F, 1.2F)
                        // Front tinted windshield glass (inset so no z-fighting with pillars)
                        .texOffs(200, 0)
                        .addBox(-8.0F, -18.9F, -24.0F, 16.0F, 6.8F, 0.8F)
                        // Front bumper with fog lights & grille guard
                        .texOffs(0, 26)
                        .addBox(-10.0F, -4.2F, -26.2F, 20.0F, 3.0F, 1.15F)
                        // Left & Right wing mirrors
                        .texOffs(46, 26)
                        .addBox(9.6F, -18.0F, -23.5F, 1.4F, 4.5F, 2.0F)
                        .texOffs(46, 26)
                        .addBox(-11.0F, -18.0F, -23.5F, 1.4F, 4.5F, 2.0F)
                        // Interior cab bench seat & steering wheel
                        .texOffs(60, 22)
                        .addBox(-7.5F, -11.9F, -16.5F, 15.0F, 2.2F, 5.5F)
                        .texOffs(104, 22)
                        .addBox(2.5F, -14.5F, -21.5F, 4.0F, 1.0F, 4.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 2. Heavy Steel Chassis Frame, Axles & Underbody Control Cabinet
        partdefinition.addOrReplaceChild("chassis",
                CubeListBuilder.create()
                        // Main steel C-channel chassis rails
                        .texOffs(0, 36)
                        .addBox(-6.5F, -3.4F, -23.0F, 13.0F, 2.4F, 45.0F)
                        // Underbody electrical/hydraulic control box with green indicator lights
                        .texOffs(118, 36)
                        .addBox(-9.8F, -3.4F, -5.0F, 19.6F, 4.8F, 9.0F)
                        // Front steer axle
                        .texOffs(178, 36)
                        .addBox(-9.0F, -1.5F, -16.5F, 18.0F, 1.8F, 2.0F)
                        // Heavy rear dual drive axle
                        .texOffs(178, 42)
                        .addBox(-9.5F, -1.8F, 10.5F, 19.0F, 2.2F, 3.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 3. Rear Flatbed Utility Deck with Red/White Reflective Safety Skirts & Side Guardrails
        partdefinition.addOrReplaceChild("flatbed",
                CubeListBuilder.create()
                        // Flatbed platform (starts cleanly at Z=-8.8F behind cab)
                        .texOffs(0, 86)
                        .addBox(-10.5F, -5.3F, -8.8F, 21.0F, 1.85F, 32.0F)
                        // Left & right safety side rails
                        .texOffs(108, 86)
                        .addBox(9.5F, -8.3F, -8.0F, 0.8F, 3.0F, 30.0F)
                        .texOffs(108, 86)
                        .addBox(-10.3F, -8.3F, -8.0F, 0.8F, 3.0F, 30.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 4. Four Hydraulic Outrigger Stabilizer Housings & Warning Beacons
        partdefinition.addOrReplaceChild("outriggers",
                CubeListBuilder.create()
                        // Front Left Outrigger Housing
                        .texOffs(0, 122)
                        .addBox(10.55F, -7.5F, -8.5F, 3.5F, 7.5F, 3.0F)
                        // Front Right Outrigger Housing
                        .texOffs(16, 122)
                        .addBox(-14.05F, -7.5F, -8.5F, 3.5F, 7.5F, 3.0F)
                        // Rear Left Outrigger Housing
                        .texOffs(0, 122)
                        .addBox(10.55F, -7.5F, 19.5F, 3.5F, 7.5F, 3.0F)
                        // Rear Right Outrigger Housing
                        .texOffs(16, 122)
                        .addBox(-14.05F, -7.5F, 19.5F, 3.5F, 7.5F, 3.0F)
                        // Amber Warning Beacons atop all 4 outriggers
                        .texOffs(32, 122)
                        .addBox(11.5F, -9.0F, -7.8F, 1.6F, 1.5F, 1.6F)
                        .texOffs(32, 122)
                        .addBox(-13.1F, -9.0F, -7.8F, 1.6F, 1.5F, 1.6F)
                        .texOffs(32, 122)
                        .addBox(11.5F, -9.0F, 20.2F, 1.6F, 1.5F, 1.6F)
                        .texOffs(32, 122)
                        .addBox(-13.1F, -9.0F, 20.2F, 1.6F, 1.5F, 1.6F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 4b. Telescoping Outrigger Hydraulic Jack Legs & Foot Pads
        partdefinition.addOrReplaceChild("outrigger_jacks",
                CubeListBuilder.create()
                        // 4 black hydraulic jack rams
                        .texOffs(64, 122)
                        .addBox(11.3F, -2.0F, -7.9F, 2.0F, 4.5F, 1.8F)
                        .texOffs(64, 122)
                        .addBox(-13.3F, -2.0F, -7.9F, 2.0F, 4.5F, 1.8F)
                        .texOffs(64, 122)
                        .addBox(11.3F, -2.0F, 20.1F, 2.0F, 4.5F, 1.8F)
                        .texOffs(64, 122)
                        .addBox(-13.3F, -2.0F, 20.1F, 2.0F, 4.5F, 1.8F)
                        // 4 steel stabilizer ground pads
                        .texOffs(76, 122)
                        .addBox(10.6F, 2.5F, -8.5F, 3.4F, 0.6F, 3.0F)
                        .texOffs(76, 122)
                        .addBox(-14.0F, 2.5F, -8.5F, 3.4F, 0.6F, 3.0F)
                        .texOffs(76, 122)
                        .addBox(10.6F, 2.5F, 19.5F, 3.4F, 0.6F, 3.0F)
                        .texOffs(76, 122)
                        .addBox(-14.0F, 2.5F, 19.5F, 3.4F, 0.6F, 3.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 5. Rotating Turntable Pedestal & Turret Operator Seat
        PartDefinition turret = partdefinition.addOrReplaceChild("turret",
                CubeListBuilder.create()
                        // Central white turret pedestal column
                        .texOffs(0, 156)
                        .addBox(-4.5F, -9.0F, -4.5F, 9.0F, 8.7F, 9.0F)
                        // Side operator seat & turret control levers
                        .texOffs(38, 156)
                        .addBox(4.55F, -11.0F, -2.5F, 4.5F, 7.5F, 5.0F),
                PartPose.offset(0.0F, 15.0F, 12.0F));

        // 6. Articulated Lower Boom (child of turret, pivots at Y=-8.0F)
        PartDefinition lowerBoom = turret.addOrReplaceChild("lower_boom",
                CubeListBuilder.create()
                        // White box-section lower boom arm with warning decal
                        .texOffs(60, 156)
                        .addBox(-2.2F, -2.2F, -38.0F, 4.4F, 4.4F, 38.0F)
                        // Lower hydraulic lift cylinder ram
                        .texOffs(148, 156)
                        .addBox(-1.2F, 2.25F, -25.0F, 2.4F, 2.0F, 20.0F),
                PartPose.offset(0.0F, -8.0F, 0.0F));

        // 7. Articulated Upper Boom (child of lower_boom, pivots at knuckle Z=-38.0F)
        PartDefinition upperBoom = lowerBoom.addOrReplaceChild("upper_boom",
                CubeListBuilder.create()
                        // Knuckle hinge linkage & red/white chevron counterweight nose
                        .texOffs(0, 200)
                        .addBox(-2.6F, -3.4F, -3.5F, 5.2F, 5.4F, 6.0F)
                        // Upper boom main white arm with GKZ14 / JANE MOOSE branding
                        .texOffs(26, 200)
                        .addBox(-1.9F, -1.9F, 2.55F, 3.8F, 3.8F, 37.45F),
                PartPose.offset(0.0F, 0.0F, -38.0F));

        // 8. Telescoping Inner Fly-Boom (child of upper_boom, slides out up to 32 pixels)
        PartDefinition flyBoom = upperBoom.addOrReplaceChild("fly_boom",
                CubeListBuilder.create()
                        .texOffs(116, 200)
                        .addBox(-1.4F, -1.4F, 4.0F, 2.8F, 2.8F, 36.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // 9. Self-Leveling Enclosed Aerial Work Bucket / Platform (child of fly_boom at Z=40.0F)
        flyBoom.addOrReplaceChild("bucket",
                CubeListBuilder.create()
                        // Solid non-slip bucket floor
                        .texOffs(180, 90)
                        .addBox(-7.5F, 6.2F, -2.0F, 15.0F, 1.5F, 14.0F)
                        // Front bucket wall
                        .texOffs(180, 108)
                        .addBox(-7.5F, -5.5F, -2.0F, 15.0F, 11.7F, 1.5F)
                        // Back bucket wall with red/white reflector strips
                        .texOffs(214, 108)
                        .addBox(-7.5F, -5.5F, 10.5F, 15.0F, 11.7F, 1.5F)
                        // Left bucket wall
                        .texOffs(180, 124)
                        .addBox(6.0F, -5.5F, -0.5F, 1.5F, 11.7F, 11.0F)
                        // Right bucket wall
                        .texOffs(206, 124)
                        .addBox(-7.5F, -5.5F, -0.5F, 1.5F, 11.7F, 11.0F)
                        // Upper platform joystick control box on front rail
                        .texOffs(232, 124)
                        .addBox(-3.0F, -8.0F, -2.4F, 6.0F, 2.5F, 2.8F),
                PartPose.offset(0.0F, 0.0F, 40.0F));

        // 10. 16-Faceted Round Commercial Truck Tires & Silver Steel Dish Rims
        PartDefinition frontLeftWheel = partdefinition.addOrReplaceChild("front_left_wheel",
                createRoundTruckWheel(), PartPose.offset(9.5F, 19.5F, -15.5F));
        addTruckWheelFacets(frontLeftWheel);

        PartDefinition frontRightWheel = partdefinition.addOrReplaceChild("front_right_wheel",
                createRoundTruckWheel(), PartPose.offset(-9.5F, 19.5F, -15.5F));
        addTruckWheelFacets(frontRightWheel);

        PartDefinition backLeftWheel = partdefinition.addOrReplaceChild("back_left_wheel",
                createRoundTruckWheel(), PartPose.offset(9.8F, 19.5F, 12.0F));
        addTruckWheelFacets(backLeftWheel);

        PartDefinition backRightWheel = partdefinition.addOrReplaceChild("back_right_wheel",
                createRoundTruckWheel(), PartPose.offset(-9.8F, 19.5F, 12.0F));
        addTruckWheelFacets(backRightWheel);

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    private static CubeListBuilder createRoundTruckWheel() {
        return CubeListBuilder.create()
                // Tire tread facet 0
                .texOffs(204, 160).addBox(-2.0F, -4.5F, -0.88F, 4.0F, 9.0F, 1.76F)
                // Silver steel wheel rim & hubcap
                .texOffs(204, 180).addBox(-2.15F, -2.8F, -2.8F, 4.3F, 5.6F, 5.6F);
    }

    private static void addTruckWheelFacets(PartDefinition wheel) {
        float[] angles = new float[]{0.3927F, 0.7854F, 1.1781F, 1.5708F, 1.9635F, 2.3562F, 2.7489F};
        for (int i = 0; i < angles.length; i++) {
            wheel.addOrReplaceChild("wf_" + i,
                    CubeListBuilder.create().texOffs(204, 160).addBox(-2.0F, -4.5F, -0.88F, 4.0F, 9.0F, 1.76F),
                    PartPose.rotation(angles[i], 0.0F, 0.0F));
        }
        wheel.addOrReplaceChild("wrim_diag",
                CubeListBuilder.create().texOffs(204, 180).addBox(-2.12F, -2.8F, -2.8F, 4.24F, 5.6F, 5.6F),
                PartPose.rotation(0.7854F, 0.0F, 0.0F));
    }

    @Override
    public void setupAnim(BucketTruckRenderState state) {
        super.setupAnim(state);
        this.frontLeftWheel.xRot = state.wheelAngle;
        this.frontRightWheel.xRot = state.wheelAngle;
        this.backLeftWheel.xRot = state.wheelAngle;
        this.backRightWheel.xRot = state.wheelAngle;

        float boomExt = Mth.clamp(state.boomExtension, 0.0F, 1.0F);

        // Outrigger hydraulic jacks deploy smoothly to the ground as soon as boom begins lifting
        float jackDeploy = Mth.clamp(boomExt * 5.0F, 0.0F, 1.0F);
        this.outriggerJacks.y = 20.0F + jackDeploy * 0.9F;

        // Turret yaw rotation
        this.turret.yRot = (float) Math.toRadians(state.boomYaw);

        // Articulated 2-stage boom + telescopic fly-boom + self-leveling aerial bucket
        float theta1 = Mth.lerp(boomExt, 0.30F, 1.25F);
        float theta2 = Mth.lerp(boomExt, 0.05F, 1.30F);
        float flyExtend = Mth.lerp(boomExt, 0.0F, 32.0F);

        this.lowerBoom.xRot = -theta1;
        this.upperBoom.xRot = theta1 + theta2;
        this.flyBoom.z = flyExtend;
        // Keep the aerial work bucket 100% horizontal at all boom angles
        this.bucket.xRot = -theta2;
    }
}
