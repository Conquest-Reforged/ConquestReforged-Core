package com.conquestrefabricated.core.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class BlockStats {

    public final int vanillaBlocks;
    public final int vanillaStates;
    public final int conquestBlocks;
    public final int conquestStates;
    public final int totalBlocks;
    public final int totalStates;

    public BlockStats() {
        int vb = 0, vs = 0, cb = 0, cs = 0, tb = 0, ts = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            tb++;
            ts += block.getStateDefinition().getPossibleStates().size();

            Identifier name = BuiltInRegistries.BLOCK.getKey(block);
            if (name == null) {
                continue;
            }


            if (name.getNamespace().equals("minecraft")) {
                vb++;
                vs += block.getStateDefinition().getPossibleStates().size();
                continue;
            }
            if (name.getNamespace().equals("conquest")) {
                cb++;
                cs += block.getStateDefinition().getPossibleStates().size();
            }
        }
        this.vanillaBlocks = vb;
        this.vanillaStates = vs;
        this.conquestBlocks = cb;
        this.conquestStates = cs;
        this.totalBlocks = tb;
        this.totalStates = ts;
    }
}
