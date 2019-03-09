package de.pauhull.bansystem.spigot.v1_8_R3;

import de.pauhull.bansystem.common.util.Messages;
import de.pauhull.bansystem.spigot.abstraction.IReportInventory;
import de.pauhull.bansystem.spigot.abstraction.util.ItemBuilder;
import de.pauhull.uuidfetcher.common.communication.message.RunCommandMessage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul
 * on 09.03.2019
 *
 * @author pauhull
 */
public class ReportInventory implements Listener, IReportInventory {

    private static final String TITLE = "§cReport";
    private static final List<String> LORE = Arrays.asList("§8§m                              ", " ", "§8● §7Klicke zum auswählen §8●", " ", "§8§m                              ");
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(" ").build();
    private static final ItemStack WHITE_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack REPORT = new ItemBuilder(Material.BARRIER).setDisplayName("§d§lCandyCraft.de§8 | §eReport").build();
    private static final ItemStack SEND = new ItemBuilder(Material.INK_SACK, (short) 10).setDisplayName("§8» §aBestätigen").build();

    private JavaPlugin plugin;
    private Map<String, String> reasons;
    private Map<String, String> reporting;

    public ReportInventory(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reasons = new HashMap<>();
        this.reporting = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void show(Player player, String reporting) {

        this.reporting.put(player.getName(), reporting);

        Inventory inventory = Bukkit.createInventory(null, 45, TITLE);

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, WHITE_GLASS);
        }

        for (int x = 1; x < 8; x++) {
            for (int y = 1; y < 4; y++) {
                int i = x + y * 9;
                inventory.setItem(i, BLACK_GLASS);
            }
        }

        inventory.setItem(39, BLACK_GLASS);
        inventory.setItem(41, BLACK_GLASS);

        inventory.setItem(4, REPORT);

        placeAllItems(player, inventory);

        inventory.setItem(40, SEND);

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    private void placeAllItems(Player player, Inventory inventory) {
        placeInInventory(player, inventory, 11, Items.HACKING);
        placeInInventory(player, inventory, 12, Items.CHAT);
        placeInInventory(player, inventory, 13, Items.TEAMING);
        placeInInventory(player, inventory, 14, Items.SPAWNTRAPPING);
        placeInInventory(player, inventory, 15, Items.BUGUSING);
        placeInInventory(player, inventory, 23, Items.NAME);
        placeInInventory(player, inventory, 22, Items.MISC);
        placeInInventory(player, inventory, 21, Items.SKIN);
    }

    private void placeInInventory(Player player, Inventory inventory, int slot, Items item) {
        if (reasons.containsKey(player.getName()) && reasons.get(player.getName()).equals(item.name())) {
            inventory.setItem(slot, item.getSelected());
        } else {
            inventory.setItem(slot, item.getItem());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {

            if (stack.equals(SEND)) {
                if (reasons.containsKey(player.getName())) {
                    if (this.reporting.containsKey(player.getName())) {
                        String reporting = this.reporting.get(player.getName());
                        this.reporting.remove(player.getName());
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1, 1);
                        new RunCommandMessage(player.getName(), "reportbungee player " + reporting + " " + reasons.get(player.getName())).sendToProxy("Proxy");
                        reasons.remove(player.getName());
                    }
                } else {
                    player.sendMessage(Messages.BAN_PREFIX + "Du hast §ckeinen §7Grund ausgewählt!");
                }

                return;
            }

            for (Items item : Items.values()) {
                if (item.getItem().equals(stack)) {
                    reasons.put(player.getName(), item.name());
                    placeAllItems(player, inventory);
                    player.updateInventory();
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    return;
                }
            }
        }
    }

    private enum Items {

        HACKING(new ItemBuilder(Material.IRON_AXE).setLore(LORE).setDisplayName("§8» §eHacking").build()),
        CHAT(new ItemBuilder(Material.BOOK_AND_QUILL).setLore(LORE).setDisplayName("§8» §eChatverhalten").build()),
        TEAMING(new ItemBuilder(Material.BED).setLore(LORE).setDisplayName("§8» §eTeaming").build()),
        SPAWNTRAPPING(new ItemBuilder(Material.WEB).setLore(LORE).setDisplayName("§8» §eSpawntrapping").build()),
        BUGUSING(new ItemBuilder(Material.LAVA_BUCKET).setLore(LORE).setDisplayName("§8» §eBugusing").build()),
        NAME(new ItemBuilder(Material.NAME_TAG).setLore(LORE).setDisplayName("§8» §eName").build()),
        MISC(new ItemBuilder(Material.REDSTONE_COMPARATOR).setLore(LORE).setDisplayName("§8» §eSonstiges").build()),
        SKIN(new ItemBuilder(Material.SKULL_ITEM, (short) 3).setLore(LORE).setDisplayName("§8» §eSkin").build());

        @Getter
        private ItemStack item;

        @Getter
        private ItemStack selected;

        Items(ItemStack item) {
            this.item = item;

            selected = item.clone();
            ItemMeta meta = selected.getItemMeta();
            meta.setLore(Arrays.asList("§8§m                              ", " ", "§8● §a§l[AUSGEWÄHLT] §8●", " ", "§8§m                              "));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 0, true);
            selected.setItemMeta(meta);
        }

    }

}
