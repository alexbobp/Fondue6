package com.elytradev.fondue.module.spiritgraves.client;

import com.elytradev.fondue.module.spiritgraves.EntityGrave;
import com.elytradev.fondue.module.spiritgraves.ModuleSpiritGraves;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderGrave extends Render<EntityGrave> {

	public RenderGrave(RenderManager rm) {
		super(rm);
		this.shadowSize = 0;
	}
	
	@Override
	public void doRender(EntityGrave entity, double x, double y, double z, float entityYaw, float partialTicks) {
		double scale = 0.25;
		if (scale <= 0) return;
		GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.enableRescaleNormal();
			GlStateManager.disableCull();
			GlStateManager.disableAlpha();
			GlStateManager.disableDepth();
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.disableLighting();
			GlStateManager.translate(0, 0.25, 0);
			GlStateManager.scale(scale, scale, scale);
			
			GlStateManager.rotate(180 - renderManager.playerViewY, 0, 1, 0);
			GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1: 1) * -renderManager.playerViewX, 1, 0, 0);
			
			GlStateManager.pushMatrix();
				GlStateManager.rotate((entity.ticksExisted+partialTicks)%360f, 0, 0, 1);
				
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				
				if (renderOutlines) {
					GlStateManager.enableColorMaterial();
					GlStateManager.enableOutlineMode(getTeamColor(entity));
				}
				
				bindEntityTexture(entity);
		
				drawFuzzyCircle(0.1875f, 0.1875f, 0.0625f, 0.0625f);
				drawFuzzyCircle(0.6875f, 0.1875f, 0.0625f, 0.0625f);
			GlStateManager.popMatrix();
			
			if (renderOutlines) {
				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();
			}
	
			GlStateManager.shadeModel(GL11.GL_FLAT);
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.enableDepth();
			GlStateManager.enableCull();
			GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public static void drawFuzzyCircle(float midU, float midV, float uRadius, float vRadius) {
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer vb = tess.getBuffer();
		vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_COLOR);
		vb.pos(0, 0, 0).tex(midU, midV).color(1, 1, 1, 0.99f).endVertex();
		int sides = 32;
		for (int i = 0; i <= sides; i++) {
			float theta = (float)((i/(float)sides)*(Math.PI*2));
			float vX = MathHelper.sin(theta);
			float vY = MathHelper.cos(theta);
			vb.pos(vX, vY, 0).tex(midU+(vX*uRadius), midV+(vY*vRadius)).color(0, 0, 0, 0.99f).endVertex();
		}
		tess.draw();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityGrave entity) {
		return ModuleSpiritGraves.getTextureForPlayer(entity.getOwner());
	}
}
