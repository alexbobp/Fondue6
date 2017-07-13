package com.elytradev.fondue.module.spiritgraves;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.elytradev.fondue.Fondue;
import com.elytradev.fondue.Goal;
import com.elytradev.fondue.module.Module;
import com.elytradev.fondue.module.waypoints.client.ModuleWaypointsClient;
import com.google.common.collect.ImmutableSet;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class ModuleSpiritGraves extends Module {

	@Override
	public String getName() {
		return "Spirit Graves";
	}

	@Override
	public String getDescription() {
		return "Spawns balls of smoke when you die which don't despawn, and can be broken to get your items back (in the same slots they were in originally!)";
	}

	@Override
	public Set<Goal> getGoals() {
		return ImmutableSet.of(Goal.IMPROVE_VANILLA, Goal.REDUCE_FRICTION, Goal.BE_UNIQUE);
	}
	
	public static SoundEvent SPIRIT;
	public static SoundEvent DISPEL;

	public static Item GRAVE;

	static SpiritGravesConfig cfg;
	
	@Override
	public void onPreInit(FMLPreInitializationEvent e) {
		EntityRegistry.registerModEntity(new ResourceLocation("fondue", "spirit_grave"), EntityGrave.class, "spirit_grave", Fondue.nextEntityId++, Fondue.inst, 64, 2, true);
		GameRegistry.register(SPIRIT = new SoundEvent(new ResourceLocation("fondue", "spirit")).setRegistryName("spirit"));
		GameRegistry.register(DISPEL = new SoundEvent(new ResourceLocation("fondue", "dispel")).setRegistryName("dispel"));
		GRAVE = new ItemSpiritBottle().setUnlocalizedName("fondue.spiritBottle").setRegistryName("spirit_bottle").setMaxStackSize(3);
		GameRegistry.register(GRAVE);
		GameRegistry.addRecipe(new ShapedOreRecipe(GRAVE,
				"%^%",
				"#@#",
				" # ",
				'^', Items.DIAMOND,
				'%', Blocks.CHEST,
				'@', Items.ENDER_PEARL,
				'#', "blockGlass"
		));
		MinecraftForge.EVENT_BUS.register(this);
		Fondue.inst.network.register(GraveDispelMessage.class);
		cfg = new SpiritGravesConfig(new File(e.getModConfigurationDirectory(), "spiritgraves.cfg"));
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	@SideOnly(CLIENT)
	public void onJoinWorld(EntityJoinWorldEvent e) {
		Entity ent = e.getEntity();
		if (e.getWorld().isRemote && ent != Minecraft.getMinecraft().player &&
				(ent instanceof EntityGrave || ent instanceof EntityPlayer)) {
			Fondue.getModule(ModuleWaypointsClient.class).addTrackedEntity(ent);
		}
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onDeath(LivingDeathEvent e) {
		if (e.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e.getEntity();
			EntityGrave grave = new EntityGrave(e.getEntity().world, player.posX, player.posY, player.posZ, player);

			if (grave.foundGrave && !grave.isEmpty()) {
				grave.clear(player);
				player.world.spawnEntity(grave);
				if (grave.ejectBottle)
					player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ,
							new ItemStack(Items.GLASS_BOTTLE)));
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onDrops(PlayerDropsEvent e) {
		EntityGrave eg = (EntityGrave)e.getEntityPlayer().world.findNearestEntityWithinAABB(EntityGrave.class,
				e.getEntityPlayer().getEntityBoundingBox().expand(0.5, 0.5, 0.5),
				e.getEntityPlayer());
		if (eg != null) {
			for (EntityItem ei : e.getDrops()) {
				eg.addExtra(ei.getEntityItem());
				ei.setDead();
			}
			e.getDrops().clear();
		}
	}

	@SideOnly(Side.CLIENT)
	public static ResourceLocation getTextureForPlayer(GameProfile profile) {
		if (profile != null) {
			SkinManager mgr = Minecraft.getMinecraft().getSkinManager();
			Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = mgr.loadSkinFromCache(profile);

			if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
				return mgr.loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
			} else {
				return DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(profile));
			}
		}
		return DefaultPlayerSkin.getDefaultSkinLegacy();
	}
}
