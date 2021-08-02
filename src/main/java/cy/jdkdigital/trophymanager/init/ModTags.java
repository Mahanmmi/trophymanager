package cy.jdkdigital.trophymanager.init;

import cy.jdkdigital.trophymanager.TrophyManager;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class ModTags
{
    public static final ITag<Block> TROPHY_BASE = BlockTags.createOptional(new ResourceLocation(TrophyManager.MODID, "trophy_base"));
}
