package com.elytradev.fondue.module.waypoints.client;

import com.elytradev.fondue.module.waypoints.WaypointShape;
import com.elytradev.fondue.module.waypoints.WaypointStyle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class WaypointCompassWidget extends BlockCompassWidget {
	private static final ResourceLocation WAYPOINT = new ResourceLocation("fondue", "textures/gui/waypoint.png");
	
	public int color;
	public WaypointShape shape;
	public WaypointStyle style;
	
	public WaypointCompassWidget(int color, BlockPos location, WaypointShape shape, WaypointStyle style) {
		super(location);
		this.color = color;
		this.shape = shape;
		this.style = style;
	}

	@Override public int getColor() {return color;}
	@Override public int getU() {return shape.ordinal();}
	@Override public int getV() {return style.ordinal();}
	@Override public ResourceLocation getTexture() {return WAYPOINT;}
	@Override public int getTexWidth() {return 72;}
	@Override public int getTexHeight() {return 16;}
	@Override public boolean stillAlive() {return true;}
}
