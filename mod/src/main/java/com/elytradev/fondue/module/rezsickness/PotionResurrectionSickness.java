package com.elytradev.fondue.module.rezsickness;

import com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.concrete.reflect.accessor.Accessors;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;

public class PotionResurrectionSickness extends Potion {

	private static final ResourceLocation TEX = new ResourceLocation("fondue", "textures/effect/resurrection_sickness.png");
	
	protected PotionResurrectionSickness() {
		super(true, 0x220011);
	}
	
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}

	private final Accessor<Float> saturationLevel = Accessors.findField(FoodStats.class, "foodSaturationLevel", "field_75125_b", "b");

	@Override
	public void performEffect(EntityLivingBase elb, int amp) {
		if (elb instanceof EntityPlayer) {
			// death isn't a food source, just a sneaky saturation source
			FoodStats foods = ((EntityPlayer)elb).getFoodStats();
			float sat = foods.getSaturationLevel();
			if (sat >= 1 && sat < 12) saturationLevel.set(foods, sat + 0.03f);
		}
	}
	
	@Override
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
		mc.renderEngine.bindTexture(TEX);
		GlStateManager.color(1, 1, 1, alpha);
		Gui.drawModalRectWithCustomSizedTexture(x+3, y+3, 0, 0, 18, 18, 18, 18);
	}
	
	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		mc.renderEngine.bindTexture(TEX);
		Gui.drawModalRectWithCustomSizedTexture(x+6, y+7, 0, 0, 18, 18, 18, 18);
	}
	

}
