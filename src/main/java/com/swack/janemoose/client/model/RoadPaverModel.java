package com.swack.janemoose.client.model;

import com.swack.janemoose.client.renderer.RoadPaverRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RoadPaverModel extends EntityModel<RoadPaverRenderState> {
    public static final ModelLayerLocation ROAD_PAVER_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath("janemoose", "road_paver"), "main");
    public static final ModelLayerLocation DIESEL_ROAD_PAVER_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath("janemoose", "diesel_road_paver"), "main");

    private final ModelPart root;
    private final ModelPart leftTreads;
    private final ModelPart rightTreads;
    private final ModelPart screed;

    public RoadPaverModel(ModelPart root) {
        super(root);
        this.root = root;
        this.leftTreads = root.getChild("left_treads");
        this.rightTreads = root.getChild("right_treads");
        this.screed = root.getChild("screed");
    }

    public static LayerDefinition createBodyLayer(boolean wideScreed) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // 1. LEFT CRAWLER TREADS & WHEELS
        root.addOrReplaceChild(
                "left_treads",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-21.0F, -10.0F, -20.0F, 6.0F, 10.0F, 40.0F)
                        .texOffs(52, 0)
                        .addBox(-21.5F, -9.0F, -22.0F, 7.0F, 8.0F, 4.0F)
                        .texOffs(52, 12)
                        .addBox(-21.5F, -9.0F, 18.0F, 7.0F, 8.0F, 4.0F)
                        .texOffs(74, 0)
                        .addBox(-20.5F, -3.0F, -16.0F, 5.0F, 3.0F, 32.0F),
                PartPose.ZERO
        );

        // 2. RIGHT CRAWLER TREADS & WHEELS
        root.addOrReplaceChild(
                "right_treads",
                CubeListBuilder.create()
                        .texOffs(0, 50)
                        .addBox(15.0F, -10.0F, -20.0F, 6.0F, 10.0F, 40.0F)
                        .texOffs(52, 0)
                        .addBox(14.5F, -9.0F, -22.0F, 7.0F, 8.0F, 4.0F)
                        .texOffs(52, 12)
                        .addBox(14.5F, -9.0F, 18.0F, 7.0F, 8.0F, 4.0F)
                        .texOffs(74, 0)
                        .addBox(15.5F, -3.0F, -16.0F, 5.0F, 3.0F, 32.0F),
                PartPose.ZERO
        );

        // 3. MAIN HEAVY CHASSIS BELLY
        root.addOrReplaceChild(
                "chassis_belly",
                CubeListBuilder.create().texOffs(0, 100)
                        .addBox(-15.0F, -14.0F, -20.0F, 30.0F, 10.0F, 40.0F),
                PartPose.ZERO
        );

        // 4. FRONT RECEIVING HOPPER
        root.addOrReplaceChild(
                "front_hopper",
                CubeListBuilder.create()
                        .texOffs(100, 50)
                        .addBox(-16.0F, -6.0F, -24.0F, 32.0F, 4.0F, 18.0F)
                        .texOffs(100, 72)
                        .addBox(-20.0F, -16.0F, -24.0F, 4.0F, 11.0F, 18.0F)
                        .texOffs(100, 72)
                        .addBox(16.0F, -16.0F, -24.0F, 4.0F, 11.0F, 18.0F)
                        .texOffs(100, 101)
                        .addBox(-19.0F, -7.0F, -26.0F, 38.0F, 4.0F, 3.0F),
                PartPose.ZERO
        );

        // 5. ENGINE HOUSING & HOOD
        root.addOrReplaceChild(
                "engine_hood",
                CubeListBuilder.create()
                        .texOffs(144, 0)
                        .addBox(-14.0F, -22.0F, -6.0F, 28.0F, 12.0F, 16.0F)
                        .texOffs(144, 28)
                        .addBox(-13.0F, -18.0F, -10.0F, 26.0F, 8.0F, 4.0F)
                        .texOffs(144, 40)
                        .addBox(-11.0F, -17.0F, -11.0F, 3.0F, 3.0F, 2.0F)
                        .addBox(8.0F, -17.0F, -11.0F, 3.0F, 3.0F, 2.0F)
                        .texOffs(154, 40)
                        .addBox(-6.0F, -34.0F, 0.0F, 3.0F, 13.0F, 3.0F)
                        .texOffs(166, 40)
                        .addBox(-6.5F, -35.0F, -0.5F, 4.0F, 1.0F, 4.0F),
                PartPose.ZERO
        );

        // 6. OPERATOR DECK & CONTROL STATIONS
        root.addOrReplaceChild(
                "operator_deck",
                CubeListBuilder.create()
                        .texOffs(0, 150)
                        .addBox(-16.0F, -16.0F, 8.0F, 32.0F, 2.0F, 14.0F)
                        .texOffs(92, 150)
                        .addBox(-5.0F, -24.0F, 10.0F, 10.0F, 8.0F, 4.0F)
                        .texOffs(120, 150)
                        .addBox(-13.0F, -20.0F, 12.0F, 6.0F, 4.0F, 6.0F)
                        .addBox(-13.0F, -27.0F, 16.0F, 6.0F, 7.0F, 2.0F)
                        .texOffs(120, 150)
                        .addBox(7.0F, -20.0F, 12.0F, 6.0F, 4.0F, 6.0F)
                        .addBox(7.0F, -27.0F, 16.0F, 6.0F, 7.0F, 2.0F)
                        .texOffs(144, 150)
                        .addBox(-16.0F, -24.0F, 8.0F, 1.0F, 8.0F, 14.0F)
                        .addBox(15.0F, -24.0F, 8.0F, 1.0F, 8.0F, 14.0F)
                        .addBox(-16.0F, -24.0F, 21.0F, 32.0F, 8.0F, 1.0F),
                PartPose.ZERO
        );

        // 7. REAR LEVELING SCREED
        if (wideScreed) {
            // 5-block wide screed (76 units wide = 4.75 blocks)
            root.addOrReplaceChild(
                    "screed",
                    CubeListBuilder.create()
                            .texOffs(0, 166)
                            .addBox(-38.0F, -6.0F, 20.0F, 76.0F, 6.0F, 6.0F)
                            .texOffs(100, 166)
                            .addBox(-39.0F, -8.0F, 18.0F, 2.0F, 8.0F, 9.0F)
                            .texOffs(100, 166)
                            .addBox(37.0F, -8.0F, 18.0F, 2.0F, 8.0F, 9.0F)
                            .texOffs(122, 166)
                            .addBox(-24.0F, -8.0F, 2.0F, 2.0F, 2.0F, 18.0F)
                            .addBox(22.0F, -8.0F, 2.0F, 2.0F, 2.0F, 18.0F)
                            .texOffs(0, 178)
                            .addBox(-36.0F, -7.0F, 26.0F, 72.0F, 1.0F, 5.0F),
                    PartPose.ZERO
            );
        } else {
            // 3-block wide screed (44 units wide = 2.75 blocks)
            root.addOrReplaceChild(
                    "screed",
                    CubeListBuilder.create()
                            .texOffs(0, 166)
                            .addBox(-22.0F, -6.0F, 20.0F, 44.0F, 6.0F, 6.0F)
                            .texOffs(100, 166)
                            .addBox(-23.0F, -8.0F, 18.0F, 2.0F, 8.0F, 9.0F)
                            .texOffs(100, 166)
                            .addBox(21.0F, -8.0F, 18.0F, 2.0F, 8.0F, 9.0F)
                            .texOffs(122, 166)
                            .addBox(-18.0F, -8.0F, 2.0F, 2.0F, 2.0F, 18.0F)
                            .addBox(16.0F, -8.0F, 2.0F, 2.0F, 2.0F, 18.0F)
                            .texOffs(0, 178)
                            .addBox(-20.0F, -7.0F, 26.0F, 40.0F, 1.0F, 5.0F),
                    PartPose.ZERO
            );
        }

        return LayerDefinition.create(mesh, 256, 256);
    }
}
