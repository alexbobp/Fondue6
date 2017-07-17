package com.elytradev.fondue.module.waypoints.client;

import com.elytradev.fondue.module.spiritgraves.EntityGrave;
import com.elytradev.fondue.module.spiritgraves.ModuleSpiritGraves;
import com.elytradev.fruitphone.client.render.Rendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

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
    @Override public int getColor() {return dead?0x903030:-1;}
    @Override public int getU() {return 0;}
    @Override public int getV() {return 0;}
    @Override public ResourceLocation getTexture() {return tex;}
    @Override public int getTexWidth() {return 8;}
    @Override public int getTexHeight() {return 8;}
    @Override public boolean stillAlive() {return pointsTo.isEntityAlive();}

    @Override public void render() {
        Rendering.color3(getColor());
        Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture());
        GL11.glPushMatrix(); {
            if (dead) {
                GL11.glTranslatef(4, 4, 0); // grave owner face upside down
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glTranslatef(-4, -4, 0);
            }
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 8, 8, 8, 8, 64, 64);
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 40, 8, 8, 8, 64, 64);
        } GL11.glPopMatrix();
    }
}
