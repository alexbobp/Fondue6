package com.elytradev.fondue.module.spiritgraves;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class ItemSpiritBottle extends Item {
	@Override
	@SideOnly(CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		if (ModuleSpiritGraves.cfg.requiresBottle)
			tooltip.add(I18n.format("item.fondue.spiritBottleRequired.hint"));
		else
			tooltip.add(I18n.format("item.fondue.spiritBottleUseless.hint"));
	}
}
