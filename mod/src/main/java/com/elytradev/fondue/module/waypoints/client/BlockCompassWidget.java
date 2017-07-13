package com.elytradev.fondue.module.waypoints.client;

import net.minecraft.util.math.BlockPos;

public abstract class BlockCompassWidget extends CoordinateCompassWidget {
	public BlockPos location;
	public BlockCompassWidget(BlockPos location) {
		this.location = location;
	}

	@Override public float getX() {return location.getX()+.5f;}
	@Override public float getY() {return location.getY()+.5f;}
	@Override public float getZ() {return location.getZ()+.5f;}
}
