package com.elytradev.fondue.module.spiritgraves;

import java.util.Set;

import com.elytradev.fondue.Fondue;
import com.elytradev.fondue.Goal;
import com.elytradev.fondue.module.Module;
import com.elytradev.fondue.module.stoned.ShapedOreRecipeHighPriority;
import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

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
	
	@Override
	public void onPreInit(FMLPreInitializationEvent e) {
		EntityRegistry.registerModEntity(new ResourceLocation("fondue", "spirit_grave"), EntityGrave.class, "spirit_grave", Fondue.nextEntityId++, Fondue.inst, 64, 2, true);
		GameRegistry.register(SPIRIT = new SoundEvent(new ResourceLocation("fondue", "spirit")).setRegistryName("spirit"));
		GameRegistry.register(DISPEL = new SoundEvent(new ResourceLocation("fondue", "dispel")).setRegistryName("dispel"));
		GRAVE = new ItemSpiritBottle().setUnlocalizedName("fondue.spiritBottle").setRegistryName("spirit_bottle").setMaxStackSize(3);
		GameRegistry.register(GRAVE);
		GameRegistry.addRecipe(new ShapedOreRecipeHighPriority(GRAVE,
				"%^%",
				"#@#",
				" # ",
				'^', Items.DIAMOND,
				'%', Blocks.CHEST,
				'@', Items.ENDER_PEARL,
				'#', "blockGlassRed"
		));
		MinecraftForge.EVENT_BUS.register(this);
		Fondue.inst.network.register(GraveDispelMessage.class);
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onDeath(LivingDeathEvent e) {
		if (e.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e.getEntity();
			EntityGrave grave = new EntityGrave(e.getEntity().world);
			grave.setPosition(player.posX, player.posY, player.posZ);
			grave.motionY = 0.85;
			grave.populateFrom(player);
			if (!grave.isEmpty() && grave.foundGrave) {
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
		EntityGrave eg = (EntityGrave)e.getEntityPlayer().world.findNearestEntityWithinAABB(EntityGrave.class, e.getEntityPlayer().getEntityBoundingBox().expand(0.5, 0.5, 0.5), e.getEntityPlayer());
		if (eg != null) {
			for (EntityItem ei : e.getDrops()) {
				eg.addExtra(ei.getEntityItem());
				ei.setDead();
			}
			e.getDrops().clear();
		}
	}

}
