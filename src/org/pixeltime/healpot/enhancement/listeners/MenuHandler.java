package org.pixeltime.healpot.enhancement.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.pixeltime.healpot.enhancement.events.blacksmith.SecretBook;
import org.pixeltime.healpot.enhancement.events.blackspirit.Enhance;
import org.pixeltime.healpot.enhancement.events.menu.Menu;
import org.pixeltime.healpot.enhancement.manager.SettingsManager;
import org.pixeltime.healpot.enhancement.util.Util;

public class MenuHandler implements Listener {
    private static Map<String, ItemStack> itemOnEnhancingSlot =
        new HashMap<String, ItemStack>();


    public static void updateItem(Player player, ItemStack item) {
        player.getInventory().removeItem(itemOnEnhancingSlot.get(player
            .getDisplayName()));
        itemOnEnhancingSlot.put(player.getDisplayName(), item);
        player.getInventory().addItem(item);

    }


    /**
     * Handles Gui.
     * 
     * @param e
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getSlot() < 0) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        if (e.getCurrentItem().getType() == (Material.AIR)) {
            return;
        }
        Player player = (Player)e.getWhoClicked();
        if (Menu.getScreen() != null) {
            if (e.getInventory().getName().equalsIgnoreCase(Menu.getScreen()
                .getName())) {
                e.setCancelled(true);
                if (e.getCurrentItem().hasItemMeta()) {
                    if (Enhance.getValidationOfItem(player, e.getCurrentItem())
                        && !itemOnEnhancingSlot.containsKey(player
                            .getDisplayName())) {
                        itemOnEnhancingSlot.put(player.getDisplayName(), e
                            .getCurrentItem());
                        Menu.updateInv(e.getCurrentItem(), player,
                            itemOnEnhancingSlot.containsKey(player
                                .getDisplayName()), true, itemOnEnhancingSlot
                                    .get(player.getDisplayName()));
                    }
                    if (Util.isPluginItem(e.getCurrentItem(),
                        SettingsManager.lang.getString("Menu.gui.enhance"))
                        && itemOnEnhancingSlot.containsKey(player
                            .getDisplayName())) {
                        Enhance.diceToEnhancement(itemOnEnhancingSlot.get(player
                            .getDisplayName()), player);
                        Menu.updateInv(e.getCurrentItem(), player,
                            itemOnEnhancingSlot.containsKey(player
                                .getDisplayName()), false, itemOnEnhancingSlot
                                    .get(player.getDisplayName()));
                        return;
                    }
                    if (Util.isPluginItem(e.getCurrentItem(),
                        SettingsManager.lang.getString("Menu.gui.remove"))
                        && itemOnEnhancingSlot.containsKey(player
                            .getDisplayName())) {
                        itemOnEnhancingSlot.remove(player.getDisplayName());
                        Menu.createMenu(player);
                        return;
                    }
                    if (Util.isPluginItem(e.getCurrentItem(),
                        SettingsManager.lang.getString("Menu.gui.force"))
                        && itemOnEnhancingSlot.containsKey(player
                            .getDisplayName())) {
                        Enhance.forceToEnhancement(itemOnEnhancingSlot.get(
                            player.getDisplayName()), player);
                        Menu.updateInv(e.getCurrentItem(), player,
                            itemOnEnhancingSlot.containsKey(player
                                .getDisplayName()), false, itemOnEnhancingSlot
                                    .get(player.getDisplayName()));
                        return;
                    }

                    if (Util.isPluginItem(e.getCurrentItem(),
                        SettingsManager.lang.getString("Menu.gui.store"))) {
                        SecretBook.addFailstackToStorage(player);
                        if (itemOnEnhancingSlot.get(player
                            .getDisplayName()) == null) {
                            Menu.createMenu(player);
                        }
                        else {
                            Menu.updateFailstack(itemOnEnhancingSlot.get(player
                                .getDisplayName()), player);
                        }
                        return;
                    }
                }
                else if (Enhance.getValidationOfItem(player, e.getCurrentItem())
                    && !itemOnEnhancingSlot.containsKey(player
                        .getDisplayName())) {
                    itemOnEnhancingSlot.put(player.getDisplayName(), e
                        .getCurrentItem());
                    Menu.updateInv(e.getCurrentItem(), player,
                        itemOnEnhancingSlot.containsKey(player
                            .getDisplayName()), true, itemOnEnhancingSlot.get(
                                player.getDisplayName()));
                }
            }
        }
        if (!itemOnEnhancingSlot.containsKey(player.getDisplayName())) {
            List<String> loreList = new ArrayList<String>();
            if ((e.getInventory().getType() != InventoryType.CRAFTING) && (e
                .getInventory().getType() != InventoryType.PLAYER)) {
                if ((e.getClick().equals(ClickType.NUMBER_KEY)) && (e
                    .getWhoClicked().getInventory().getItem(e
                        .getHotbarButton()) != null)) {
                    ItemStack itemMoved = e.getWhoClicked().getInventory()
                        .getItem(e.getHotbarButton());
                    if ((itemMoved.hasItemMeta()) && (itemMoved.getItemMeta()
                        .hasLore())) {
                        int loreSize = itemMoved.getItemMeta().getLore().size();
                        for (int i = 0; i < loreSize; i++) {
                            loreList.add((String)itemMoved.getItemMeta()
                                .getLore().get(i));
                        }
                        if (loreList.contains(ChatColor
                            .translateAlternateColorCodes('&',
                                SettingsManager.lang.getString(
                                    "Lore.UntradeableLore")))) {
                            e.setCancelled(true);
                            Util.sendMessage(SettingsManager.lang.getString(
                                "Messages.NoStorage"), e.getWhoClicked());
                        }
                    }
                }
                if (e.getCurrentItem() != null) {
                    if ((e.getCurrentItem().hasItemMeta()) && (e
                        .getCurrentItem().getItemMeta().hasLore())) {
                        int loreSize = e.getCurrentItem().getItemMeta()
                            .getLore().size();
                        for (int i = 0; i < loreSize; i++) {
                            loreList.add((String)e.getCurrentItem()
                                .getItemMeta().getLore().get(i));
                        }
                        if (loreList.contains(ChatColor
                            .translateAlternateColorCodes('&',
                                SettingsManager.lang.getString(
                                    "Lore.UntradeableLore")))) {
                            e.setCancelled(true);
                            Util.sendMessage(SettingsManager.lang.getString(
                                "Messages.NoStorage"), e.getWhoClicked());
                        }
                    }
                }
            }
        }
    }


    /**
     * Removes current item placed on the enhancing slot.
     * 
     * @param e
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player)e.getPlayer();
        if (itemOnEnhancingSlot.containsKey(player.getDisplayName())) {
            itemOnEnhancingSlot.remove(player.getDisplayName());
        }
    }
}
