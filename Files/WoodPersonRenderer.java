package com.thewoodlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thewoodlands.TheWoodlands;
import com.thewoodlands.entity.WoodPersonEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoodPersonRenderer extends HumanoidMobRenderer<WoodPersonEntity, HumanoidModel<WoodPersonEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TheWoodlands.MOD_ID, "textures/entity/woodperson/wood_person.png");

    public WoodPersonRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(WoodPersonEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(WoodPersonEntity entity, PoseStack poseStack, float partialTick) {
        // Slightly taller than a player - just enough to be wrong
        poseStack.scale(1.0f, 1.05f, 1.0f);
    }
}
