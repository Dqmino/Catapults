package codes.domino.catapult;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class Catapult extends JavaPlugin {

    private static Catapult instance;
    public NamespacedKey recipeKey = new NamespacedKey(this, "catapultrecipe");
    // no getter for the recipe key variable because it simply doesn't matter
    public static Catapult getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new CatapultItem(), this);

        // Only 1 recipe, should be okay to register it on enable :p
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, CatapultItem.CATAPULT_ITEM_STACK);
        recipe.shape("III", "DRD", "INI");

        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('I', Material.IRON_BLOCK);
        recipe.setIngredient('R', Material.REDSTONE_BLOCK);
        recipe.setIngredient('N', Material.NETHERITE_BLOCK);
        Bukkit.addRecipe(recipe);

    }

}
