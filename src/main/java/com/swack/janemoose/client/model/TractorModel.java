package com.swack.janemoose.client.model;

import com.swack.janemoose.client.renderer.TractorRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;

public class TractorModel extends EntityModel<TractorRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath("janemoose", "tractor"), "main");

    private final ModelPart frontLeftWheel;
    private final ModelPart frontRightWheel;
    private final ModelPart backLeftWheel;
    private final ModelPart backRightWheel;

    public TractorModel(ModelPart root) {
        super(root);
        this.frontLeftWheel = root.getChild("front_left_wheel");
        this.frontRightWheel = root.getChild("front_right_wheel");
        this.backLeftWheel = root.getChild("back_left_wheel");
        this.backRightWheel = root.getChild("back_right_wheel");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. Heavy Industrial Steel Chassis, Axles & Front Weights
        partdefinition.addOrReplaceChild("chassis",
                CubeListBuilder.create()
                        // Main steel frame rails
                        .texOffs(0, 30)
                        .addBox(-7.0F, -5.0F, -12.0F, 14.0F, 3.0F, 22.0F)
                        // Front cast-iron axle beam & steering tie rod
                        .texOffs(76, 30)
                        .addBox(-8.0F, -4.0F, -11.0F, 16.0F, 2.0F, 2.0F)
                        // Rear heavy axle & differential housing
                        .texOffs(76, 36)
                        .addBox(-10.0F, -5.0F, 7.0F, 20.0F, 3.0F, 3.0F)
                        // Front weight bracket
                        .texOffs(76, 44)
                        .addBox(-5.0F, -4.0F, -19.0F, 10.0F, 3.0F, 2.0F)
                        // Front suitcase counterweights (stacked Jane Moose cast iron weights)
                        .texOffs(102, 44)
                        .addBox(-4.5F, -6.0F, -21.0F, 9.0F, 5.0F, 2.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 2. Engine Hood & Dashboard Console
        partdefinition.addOrReplaceChild("hood",
                CubeListBuilder.create()
                        // Main Jane Moose green engine hood with yellow body stripe & decals
                        .texOffs(0, 0)
                        .addBox(-5.0F, -15.0F, -17.0F, 10.0F, 9.0F, 13.0F)
                        // Dashboard instrument console
                        .texOffs(74, 0)
                        .addBox(-4.0F, -14.0F, -4.0F, 8.0F, 8.0F, 2.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 3. Honeycomb Radiator Grille, Jane Moose Emblem & Dual Headlights
        partdefinition.addOrReplaceChild("grill",
                CubeListBuilder.create()
                        // Honeycomb mesh grille with yellow perimeter surround, leaping moose emblem & headlights
                        .texOffs(96, 0)
                        .addBox(-4.8F, -14.8F, -18.0F, 9.6F, 8.8F, 1.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 4. Vertical Exhaust Smokestack & Cyclone Air Cleaner Dome
        partdefinition.addOrReplaceChild("exhaust",
                CubeListBuilder.create()
                        // Graphite cast-iron vertical exhaust pipe
                        .texOffs(138, 0)
                        .addBox(3.0F, -23.0F, -13.0F, 1.5F, 9.0F, 1.5F)
                        // Rain flapper cap
                        .texOffs(146, 0)
                        .addBox(2.5F, -23.8F, -13.5F, 2.5F, 0.8F, 2.5F)
                        // Cyclone pre-cleaner air intake dome on hood left side
                        .texOffs(158, 0)
                        .addBox(-4.5F, -18.0F, -12.5F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 5. Deluxe Operator Suspension Seat
        partdefinition.addOrReplaceChild("seat",
                CubeListBuilder.create()
                        // Black vinyl cushion with diamond pleat stitching
                        .texOffs(0, 60)
                        .addBox(-3.5F, -8.0F, 1.0F, 7.0F, 2.5F, 6.0F)
                        // Contoured padded backrest with yellow Jane Moose embroidery
                        .texOffs(28, 60)
                        .addBox(-4.0F, -15.0F, 6.0F, 8.0F, 7.0F, 2.0F)
                        // Left & right armrests
                        .texOffs(50, 60)
                        .addBox(-4.5F, -11.0F, 2.0F, 1.0F, 2.0F, 5.0F)
                        .texOffs(50, 60)
                        .addBox(3.5F, -11.0F, 2.0F, 1.0F, 2.0F, 5.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 6. Steering Wheel, Floorboard Pedals & Diamond Plate Platform
        partdefinition.addOrReplaceChild("steering",
                CubeListBuilder.create()
                        // Angled steering column
                        .texOffs(64, 60)
                        .addBox(-0.5F, -13.0F, -3.0F, 1.0F, 5.0F, 1.0F)
                        // 3-spoke steering wheel with center emblem
                        .texOffs(64, 60)
                        .addBox(-2.5F, -14.0F, -5.0F, 5.0F, 1.0F, 5.0F)
                        // Diamond-plate non-slip platform floor
                        .texOffs(86, 60)
                        .addBox(-7.0F, -5.5F, -4.0F, 14.0F, 1.0F, 12.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 7. Jane Moose Yellow Clamshell Fenders with Lights
        partdefinition.addOrReplaceChild("fenders",
                CubeListBuilder.create()
                        // Left fender with amber blinker & red taillight
                        .texOffs(0, 90)
                        .addBox(7.0F, -15.0F, 0.0F, 4.0F, 7.0F, 15.0F)
                        // Right fender with amber blinker & red taillight
                        .texOffs(40, 90)
                        .addBox(-11.0F, -15.0F, 0.0F, 4.0F, 7.0F, 15.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 8. ROPS Tubular Roll-Bar & Yellow Sunshade Canopy Roof
        partdefinition.addOrReplaceChild("canopy",
                CubeListBuilder.create()
                        // Left roll bar upright
                        .texOffs(80, 90)
                        .addBox(6.0F, -23.0F, 6.0F, 1.5F, 15.0F, 1.5F)
                        // Right roll bar upright
                        .texOffs(80, 90)
                        .addBox(-7.5F, -23.0F, 6.0F, 1.5F, 15.0F, 1.5F)
                        // Crossbar
                        .texOffs(80, 90)
                        .addBox(-7.5F, -23.5F, 6.0F, 15.0F, 1.5F, 1.5F)
                        // Fiberglass sunshade canopy roof
                        .texOffs(96, 90)
                        .addBox(-8.0F, -24.5F, -2.0F, 16.0F, 1.5F, 14.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 9. Rear 3-Point Hitch, Top Link & PTO Spline
        partdefinition.addOrReplaceChild("hitch",
                CubeListBuilder.create()
                        // Draft arms & drawbar (offset down and back to prevent coplanar z-fighting with chassis)
                        .texOffs(160, 90)
                        .addBox(-3.0F, -4.5F, 10.02F, 6.0F, 1.8F, 5.0F)
                        // PTO stub shaft
                        .texOffs(184, 90)
                        .addBox(-1.0F, -6.5F, 10.02F, 2.0F, 1.8F, 2.0F)
                        // Tow pin
                        .texOffs(184, 90)
                        .addBox(-0.5F, -6.0F, 13.5F, 1.0F, 3.8F, 1.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // 10. Front Steering Wheels (Ultra-Round 16-Faceted Narrow Ribbed Tires)
        PartDefinition frontLeftWheel = partdefinition.addOrReplaceChild("front_left_wheel",
                createRoundFrontWheel(), PartPose.offset(9.0F, 20.0F, -10.0F));
        addFrontWheelFacets(frontLeftWheel);

        PartDefinition frontRightWheel = partdefinition.addOrReplaceChild("front_right_wheel",
                createRoundFrontWheel(), PartPose.offset(-9.0F, 20.0F, -10.0F));
        addFrontWheelFacets(frontRightWheel);

        // 11. Massive Rear Drive Wheels (Ultra-Round 16-Faceted Agricultural Tires with Chevron Lugs)
        PartDefinition backLeftWheel = partdefinition.addOrReplaceChild("back_left_wheel",
                createRoundRearWheel(), PartPose.offset(11.0F, 17.0F, 8.0F));
        addRearWheelFacets(backLeftWheel);

        PartDefinition backRightWheel = partdefinition.addOrReplaceChild("back_right_wheel",
                createRoundRearWheel(), PartPose.offset(-11.0F, 17.0F, 8.0F));
        addRearWheelFacets(backRightWheel);

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    private static CubeListBuilder createRoundFrontWheel() {
        return CubeListBuilder.create()
                // Facet 0 (0 degrees)
                .texOffs(0, 130).addBox(-1.6F, -4.0F, -0.78F, 3.2F, 8.0F, 1.56F)
                // Yellow Rim & Chrome Cap
                .texOffs(50, 130).addBox(-1.7F, -2.5F, -2.5F, 3.4F, 5.0F, 5.0F);
    }

    private static void addFrontWheelFacets(PartDefinition wheel) {
        float[] angles = new float[]{0.3927F, 0.7854F, 1.1781F, 1.5708F, 1.9635F, 2.3562F, 2.7489F};
        for (int i = 0; i < angles.length; i++) {
            wheel.addOrReplaceChild("f_" + i,
                    CubeListBuilder.create().texOffs(0, 130).addBox(-1.6F, -4.0F, -0.78F, 3.2F, 8.0F, 1.56F),
                    PartPose.rotation(angles[i], 0.0F, 0.0F));
        }
        // Diagonal Rim reinforcement (inset by 0.02F to completely prevent coplanar z-fighting)
        wheel.addOrReplaceChild("rim_diag",
                CubeListBuilder.create().texOffs(50, 130).addBox(-1.68F, -2.5F, -2.5F, 3.36F, 5.0F, 5.0F),
                PartPose.rotation(0.7854F, 0.0F, 0.0F));
    }

    private static CubeListBuilder createRoundRearWheel() {
        return CubeListBuilder.create()
                // Facet 0 (0 degrees)
                .texOffs(0, 160).addBox(-3.0F, -7.0F, -1.375F, 6.0F, 14.0F, 2.75F)
                // Massive Yellow Steel Dish Rim & Cast Iron Wheel Weight Center
                .texOffs(85, 160).addBox(-3.2F, -4.5F, -4.5F, 6.4F, 9.0F, 9.0F);
    }

    private static void addRearWheelFacets(PartDefinition wheel) {
        float[] angles = new float[]{0.3927F, 0.7854F, 1.1781F, 1.5708F, 1.9635F, 2.3562F, 2.7489F};
        for (int i = 0; i < angles.length; i++) {
            wheel.addOrReplaceChild("rf_" + i,
                    CubeListBuilder.create().texOffs(0, 160).addBox(-3.0F, -7.0F, -1.375F, 6.0F, 14.0F, 2.75F),
                    PartPose.rotation(angles[i], 0.0F, 0.0F));
        }
        // Diagonal Rim reinforcement (inset by 0.02F to completely prevent coplanar z-fighting)
        wheel.addOrReplaceChild("rrim_diag",
                CubeListBuilder.create().texOffs(85, 160).addBox(-3.18F, -4.5F, -4.5F, 6.36F, 9.0F, 9.0F),
                PartPose.rotation(0.7854F, 0.0F, 0.0F));
    }

    @Override
    public void setupAnim(TractorRenderState state) {
        super.setupAnim(state);
        this.frontLeftWheel.xRot = state.wheelAngle;
        this.frontRightWheel.xRot = state.wheelAngle;
        this.backLeftWheel.xRot = state.wheelAngle;
        this.backRightWheel.xRot = state.wheelAngle;
    }
}
