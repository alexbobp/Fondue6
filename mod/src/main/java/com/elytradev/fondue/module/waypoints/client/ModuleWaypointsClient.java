package com.elytradev.fondue.module.waypoints.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elytradev.fondue.Goal;
import com.elytradev.fondue.module.ModuleClient;
import com.elytradev.fondue.module.spiritgraves.EntityGrave;
import com.elytradev.fondue.module.waypoints.ModuleWaypoints;
import com.elytradev.fondue.module.waypoints.TileEntityWaypoint;
import com.elytradev.fondue.module.waypoints.WaypointData;
import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.Gravity;
import com.elytradev.fruitphone.client.render.Rendering;
import com.elytradev.fruitphone.item.ItemFruitPassive;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

@SideOnly(CLIENT)
public class ModuleWaypointsClient extends ModuleClient {
	@Override
	public String getName() {
		return "Waypoints (Client)";
	}

	@Override
	public String getDescription() {
		return "Registers item models and renders the compass.";
	}

	@Override
	public Set<Goal> getGoals() {
		return ImmutableSet.of();
	}
	
	private List<CompassWidget> baseWidgets = Lists.newArrayList(
			CompassWidget.NORTH, CompassWidget.NORTH_EAST,
			CompassWidget.EAST, CompassWidget.SOUTH_EAST,
			CompassWidget.SOUTH, CompassWidget.SOUTH_WEST,
			CompassWidget.WEST, CompassWidget.NORTH_WEST);
	private Map<BlockPos, WaypointCompassWidget> lookup = Maps.newHashMap();
	private List<CompassWidget> widgets = Lists.newArrayList();

	@Override
	@SideOnly(CLIENT)
	public void onPreInit(FMLPreInitializationEvent e) {
		Item waypoint = Item.getItemFromBlock(ModuleWaypoints.WAYPOINT);
		ModelLoader.setCustomModelResourceLocation(waypoint, 0, new ModelResourceLocation(waypoint.getRegistryName(), "inventory"));
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaypoint.class, new RenderWaypoint());
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	@SideOnly(CLIENT)
	public void onStitch(TextureStitchEvent.Pre e) {
		e.getMap().registerSprite(new ResourceLocation("fondue", "blocks/waypoint_pole_glowmap"));
		e.getMap().registerSprite(new ResourceLocation("fondue", "blocks/waypoint_side_glowmap"));
	}
	
	@SubscribeEvent
	@SideOnly(CLIENT)
	public void onPostRenderOverlay(RenderGameOverlayEvent.Post e) {
		widgets.removeIf(widget -> !widget.stillAlive());
		if (widgets.isEmpty()) return;

		if (e.getType() == ElementType.ALL) {
			ItemStack glasses = null;
			if (Minecraft.getMinecraft().player.hasCapability(FruitPhone.CAPABILITY_EQUIPMENT, null))
				glasses = Minecraft.getMinecraft().player.getCapability(FruitPhone.CAPABILITY_EQUIPMENT, null).glasses;

			{
				int color = -1;
				if (glasses != null) {
					if (glasses.getItem() instanceof ItemFruitPassive) {
						ItemFruitPassive item = (ItemFruitPassive)glasses.getItem();
						color = item.getColor(glasses);
					}
				}
				
				float scale = FruitPhone.inst.glassesScale;
				
				int width = 400;
				int height = 7;
				
				int objWidth = (int)(width*scale)+4;
				int objHeight = (int)(height*scale)+4;
				
				float fov = Minecraft.getMinecraft().gameSettings.fovSetting*1.75f;
				
				fov *= (objWidth / (float)e.getResolution().getScaledWidth());
				
				Gravity g = Gravity.NORTH;
				
				int x = g.resolveX(0, e.getResolution().getScaledWidth(), objWidth);
				int y = g.resolveY(2, e.getResolution().getScaledHeight(), objHeight);
				
				String headsUp = null;
				int headsUpColor = -1;
				
				Rendering.color3(color);
				GlStateManager.pushMatrix(); {
					GlStateManager.translate(x, y, 0);
					
					Gui.drawRect(0, 0, objWidth, objHeight, color);
					Gui.drawRect(1, 1, objWidth-1, objHeight-1, 0xFF0C1935);
					GlStateManager.pushMatrix(); {
						GlStateManager.translate(2f, 2f, 40f);
						GlStateManager.scale(scale, scale, 1);
						
						float playerYaw = MathHelper.wrapDegrees(Minecraft.getMinecraft().getRenderViewEntity().rotationYaw);
						
						float max = fov+45;
						float sc = (width)/fov;
						
						
						GlStateManager.colorMask(false, false, false, false);
						GlStateManager.depthMask(true);
						
						Gui.drawRect(0, 0, width, height, 0xFF000000);
						
						GlStateManager.enableDepth();
						GlStateManager.depthFunc(GL11.GL_EQUAL);
						
						GlStateManager.colorMask(true, true, true, true);
						GlStateManager.depthMask(false);
						
						GlStateManager.translate(width/2f, 0, 0);
						
						for (CompassWidget cw : Iterables.concat(baseWidgets, widgets)) {
							float yaw = MathHelper.wrapDegrees(cw.getYaw());
							float diff = MathHelper.wrapDegrees(yaw-playerYaw);
							
							if (Math.abs(diff) < max) {
								double distSq = cw.getDistanceSq();
								float ws = 1;
								if (distSq != Double.POSITIVE_INFINITY) {
									double start = cw.getFalloffStart();
									double size = cw.getFalloffSize();
									double end = start+size;
									if (distSq > end*end) continue;
									if (distSq > start*start) {
										double dist = Math.sqrt(distSq);
										double d = (size-(dist-start))/size;
										ws = (float)d;
									}
									if (Math.abs(diff) < 2) {
										headsUp = ((int)Math.sqrt(distSq))+"m";
										if (cw instanceof WaypointCompassWidget) {
											headsUpColor = ((WaypointCompassWidget) cw).color;
										}
									}
								}
								GlStateManager.pushMatrix();
								GlStateManager.translate((diff*sc), 4, 0);
								GlStateManager.scale(ws, ws, 1);
								GlStateManager.translate(-(cw.getWidth()/4f), -4, 0);
								cw.render();
								GlStateManager.popMatrix();
							}
						}
						
						GlStateManager.depthFunc(GL11.GL_LEQUAL);
						GlStateManager.enableDepth();
						GlStateManager.depthMask(true);
						
						if (headsUp != null) {
							Minecraft.getMinecraft().fontRenderer.drawString(headsUp, -Minecraft.getMinecraft().fontRenderer.getStringWidth(headsUp)/2, objHeight+2, headsUpColor);
						}
						
					} GlStateManager.popMatrix();
					Rendering.drawRect(objWidth/2f+1, 0, objWidth/2f+2, 2, color);
				} GlStateManager.popMatrix();
			}
		}
	}

	public void setWaypoints(List<WaypointData> waypoints) {
		widgets.clear();
		for (WaypointData wd : waypoints) {
			addWaypoint(wd);
		}
	}

	public void addWaypoint(WaypointData wd) {
		WaypointCompassWidget w = new WaypointCompassWidget(wd.color, wd.pos, wd.shape, wd.style);
		widgets.add(w);
		lookup.put(wd.pos, w);
	}

	public void addTrackedEntity(Entity e) {
		if (e instanceof EntityGrave)
			widgets.add(new EntityCompassWidget((EntityGrave)e));
		if (e instanceof EntityPlayer)
			widgets.add(new EntityCompassWidget((EntityPlayer)e));
	}

	public void updateWaypoint(WaypointData data) {
		if (lookup.containsKey(data.pos)) {
			WaypointCompassWidget wcw = lookup.get(data.pos);
			wcw.color = data.color;
			wcw.shape = data.shape;
			wcw.style = data.style;
		} else {
			addWaypoint(data);
		}
	}
	
	public void removeWaypoint(BlockPos pos) {
		widgets.remove(lookup.remove(pos));
	}

	public void onObeliskLoad(World world, BlockPos pos) {
		ObeliskCompassWidget w = new ObeliskCompassWidget(pos);
		baseWidgets.add(w);
		widgets.add(w);
	}

}
