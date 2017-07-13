package com.elytradev.fondue.module.waypoints.client;

import com.elytradev.fondue.module.spiritgraves.EntityGrave;
import com.elytradev.fondue.module.spiritgraves.ModuleSpiritGraves;
import com.elytradev.fondue.module.spiritgraves.client.RenderGrave;
import com.elytradev.fruitphone.client.render.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityCompassWidget extends CoordinateCompassWidget {
    private final Entity pointsTo;
    private final ResourceLocation tex;
    private final boolean dead; // to distinguish graves from tracked living players

    public EntityCompassWidget(EntityGrave pointsTo) {
        this.pointsTo = pointsTo;
        tex = ModuleSpiritGraves.getTextureForPlayer(pointsTo.getOwner());
        dead = true;
    }

    public EntityCompassWidget(EntityPlayer pointsTo) {
        this.pointsTo = pointsTo;
        tex = ModuleSpiritGraves.getTextureForPlayer(pointsTo.getGameProfile());
        dead = false;
    }

    @Override public float getX() {return (float)pointsTo.posX;}
    @Override public float getY() {return (float)pointsTo.posY;}
    @Override public float getZ() {return (float)pointsTo.posZ;}
    @Override public int getColor() {return -1;}
    @Override public int getU() {return 0;}
    @Override public int getV() {return 0;}
    @Override public ResourceLocation getTexture() {return tex;}
    @Override public int getTexWidth() {return 8;}
    @Override public int getTexHeight() {return 8;}
    @Override public boolean stillAlive() {return pointsTo.isEntityAlive();}

    @Override public void render() {
        Rendering.color3(getColor());
        Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture());
        RenderGrave.drawFuzzyCircle(0.1875f, 0.1875f, 0.0625f, 0.0625f);
    }
}
