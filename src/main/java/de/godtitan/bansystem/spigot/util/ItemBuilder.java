package de.godtitan.bansystem.spigot.util;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ItemBuilder {

    private ItemStack stack;

    public ItemBuilder(Material mat) {
        this.stack = new ItemStack(mat);
    }

    public ItemBuilder(Material mat, short sh) {
        this.stack = new ItemStack(mat, 1, sh);
    }

    public ItemMeta getItemMeta() {
        return this.stack.getItemMeta();
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        this.stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) this.stack.getItemMeta();
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setBannerColor(DyeColor color) {
        BannerMeta meta = (BannerMeta) this.stack.getItemMeta();
        meta.setBaseColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) this.stack.getItemMeta();
        meta.setOwner(owner);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(displayname);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return this.stack;
    }
}
