package cy.jdkdigital.trophymanager.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.trophymanager.TrophyManager;
import cy.jdkdigital.trophymanager.common.item.TrophyItem;
import cy.jdkdigital.trophymanager.init.ModBlocks;
import cy.jdkdigital.trophymanager.init.ModItems;
import cy.jdkdigital.trophymanager.init.ModRecipeTypes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TrophyMinecartRecipe implements ICraftingRecipe
{
    public final ResourceLocation id;

    public TrophyMinecartRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        if (stacks.size() != 2) {
            return false;
        }

        boolean hasMinecart = false;
        boolean hasTrophy = false;
        for(ItemStack itemstack: stacks) {
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(Items.MINECART)) {
                    if (hasMinecart) {
                        return false;
                    }
                    hasMinecart = true;
                }

                if (itemstack.getItem().equals(ModBlocks.TROPHY.get().asItem())) {
                    if (hasTrophy) {
                        return false;
                    }
                    hasTrophy = true;
                }
            }
        }

        return hasMinecart && hasTrophy;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInventory inv) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        if (stacks.size() == 2) {
            ItemStack outStack = new ItemStack(ModItems.TROPHY_MINECART.get());

            for(ItemStack itemstack: stacks) {
                if (itemstack.getItem() instanceof TrophyItem) {
                    outStack.setTag(itemstack.getTag());
                }
            }

            return outStack;
        }
        return ItemStack.EMPTY;
    }

    private List<ItemStack> getItemsInInventory(CraftingInventory inv) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                stacks.add(itemstack);
            }
        }
        return stacks;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return new ItemStack(ModItems.TROPHY_MINECART.get());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(getResultItem()));
        return list;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.TROPHY_MINECART.get();
    }

    public static class Serializer<T extends TrophyMinecartRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final TrophyMinecartRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(TrophyMinecartRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            return this.factory.create(id);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            return this.factory.create(id);
        }

        public void toNetwork(@Nonnull PacketBuffer buffer, T recipe) {
        }

        public interface IRecipeFactory<T extends TrophyMinecartRecipe>
        {
            T create(ResourceLocation id);
        }
    }
}