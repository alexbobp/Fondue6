package com.elytradev.fondue.module.waypoints.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class CompassWidget {

	public static final CardinalCompassWidget NORTH = new CardinalCompassWidget(180, "N", false);
	public static final CardinalCompassWidget EAST = new CardinalCompassWidget(-90, "E", false);
	public static final CardinalCompassWidget SOUTH = new CardinalCompassWidget(0, "S", false);
	public static final CardinalCompassWidget WEST = new CardinalCompassWidget(90, "W", false);
	
	public static final CardinalCompassWidget NORTH_WEST = new CardinalCompassWidget(135, "NW", true);
	public static final CardinalCompassWidget NORTH_EAST = new CardinalCompassWidget(-135, "NE", true);
	public static final CardinalCompassWidget SOUTH_WEST = new CardinalCompassWidget(45, "SW", true);
	public static final CardinalCompassWidget SOUTH_EAST = new CardinalCompassWidget(-45, "SE", true);

	static EntityPlayer player() { return Minecraft.getMinecraft().player; }
	static double playerX() {
		EntityPlayer player = player();
		return player.lastTickPosX + (player.posX - player.lastTickPosX) * Minecraft.getMinecraft().getRenderPartialTicks();
	}
	static double playerZ() {
		EntityPlayer player = player();
		return player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * Minecraft.getMinecraft().getRenderPartialTicks();
	}
	public abstract float getYaw();
	public abstract double getDistanceSq();
	
	public abstract void render();
	public abstract int getWidth();
	
	public double getDistance() {
		return Math.sqrt(getDistanceSq());
	}
	
	public abstract double getFalloffStart();
	public abstract double getFalloffSize();

	public abstract boolean stillAlive();
}
