package com.elytradev.fondue.module.spiritgraves;

import net.minecraftforge.common.config.Configuration;

import java.io.File;


public class SpiritGravesConfig {
    public boolean requiresBottle;
    public SpiritGravesConfig(Configuration c) {
        requiresBottle = c.getBoolean("require bottle", "spiritgraves", true,
        "Require and consume craftable spirit bottle in inventory to spawn spirit grave");
    }
    public SpiritGravesConfig(File f) {
        this(new Configuration(f));
    }
}