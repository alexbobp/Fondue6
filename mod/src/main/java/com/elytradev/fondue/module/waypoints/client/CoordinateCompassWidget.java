package com.elytradev.fondue.module.waypoints.client;

import com.elytradev.fruitphone.client.render.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CoordinateCompassWidget extends CompassWidget {
    public abstract float getX();
    public abstract float getY();
    public abstract float getZ();
    public abstract int getColor();
    public abstract int getU();
    public abstract int getV();
    public abstract ResourceLocation getTexture();
    public abstract int getTexWidth();
    public abstract int getTexHeight();

    @Override public int getWidth() {return 8;}
    @Override public double getFalloffSize() {return 256;}
    @Override public double getFalloffStart() {return 512;}

    @Override
    public float getYaw() {
        double diffX = getX()-playerX();
        double diffZ = getZ()-playerZ();

        return (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(diffZ, diffX))-90);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render() {
        Rendering.color3(getColor());
        Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture());
        Gui.drawModalRectWithCustomSizedTexture(0, 0, getU()*8, getV()*8, 8, 8, getTexWidth(), getTexHeight());
    }

    @Override
    public double getDistanceSq() {
        return Minecraft.getMinecraft().player.getDistanceSq(getX(), getY(), getZ());
    }
}
