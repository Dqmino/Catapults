package codes.domino.catapult;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;

public class CatapultItem implements Listener {

    public static final ItemStack CATAPULT_ITEM_STACK;
    private static final NamespacedKey CATAPULT_ITEM_KEY = new NamespacedKey(Catapult.getInstance(), "catapult");
    private static final NamespacedKey EXPLOSIVE_ENTITY_KEY = new NamespacedKey(Catapult.getInstance(), "explosiveentity");

    static {
        CATAPULT_ITEM_STACK = new ItemStack(Material.DISPENSER);
        ItemMeta meta = CATAPULT_ITEM_STACK.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Catapult");
        meta.setLore(List.of(ChatColor.GRAY + "Shoot an explosive by right clicking the Catapult with a fire charge."));
        meta.getPersistentDataContainer().set(CATAPULT_ITEM_KEY, PersistentDataType.BOOLEAN, true);
        meta.setCustomModelData(842024);
        CATAPULT_ITEM_STACK.setItemMeta(meta);
    }

    @EventHandler
    public void onRightClick(BlockPlaceEvent event) {

        PersistentDataContainer container = event.getItemInHand().getItemMeta().getPersistentDataContainer();
        if (!container.has(CATAPULT_ITEM_KEY, PersistentDataType.BOOLEAN)) return;

        if (!event.getBlockPlaced().getRelative(((Directional) event.getBlock().getBlockData()).getFacing()).isEmpty()
                || ((Directional) event.getBlock().getBlockData()).getFacing() == BlockFace.UP) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place the Catapult against the sky or another block.");
            return;
        }

        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), Catapult.getInstance());
        customBlockData.set(CATAPULT_ITEM_KEY, PersistentDataType.BOOLEAN, true);

    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.DISPENSER) return;
        PersistentDataContainer container = new CustomBlockData(event.getClickedBlock(), Catapult.getInstance());
        if (!container.has(CATAPULT_ITEM_KEY, PersistentDataType.BOOLEAN)) return;

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.FIRE_CHARGE) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        event.setCancelled(true);
        event.getItem().setAmount(event.getItem().getAmount() - 1);
        Dispenser dispenser = (Dispenser) event.getClickedBlock().getState();
        Snowball ball = dispenser.getBlockProjectileSource().launchProjectile(Snowball.class);
        ball.getPersistentDataContainer().set(EXPLOSIVE_ENTITY_KEY, PersistentDataType.BOOLEAN, true);
        ball.setVelocity(ball.getVelocity().add(new Vector(0, 0.14733711080883245, 0)));
        ball.setCustomName("Missile"); // for resource packs

    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent event) {
        if (!event.getEntity().getPersistentDataContainer().has(EXPLOSIVE_ENTITY_KEY, PersistentDataType.BOOLEAN))
            return;
        Location hitLocation;
        if (event.getHitEntity() != null) {
            hitLocation = event.getHitEntity().getLocation();
        } else {
            hitLocation = event.getHitBlock().getLocation();
        }
        hitLocation.getWorld().createExplosion(hitLocation, 2f);
        event.getEntity().getPersistentDataContainer().remove(EXPLOSIVE_ENTITY_KEY);
        event.getEntity().remove();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), Catapult.getInstance());
        if (customBlockData.has(CATAPULT_ITEM_KEY, PersistentDataType.BOOLEAN)) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), CATAPULT_ITEM_STACK);
            event.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipe(Catapult.getInstance().recipeKey);
    }

}
