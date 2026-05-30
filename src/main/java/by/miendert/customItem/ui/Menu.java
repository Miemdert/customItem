package by.miendert.customItem.ui;

import by.miendert.customItem.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public abstract class Menu {
    protected  CustomItem plugin;
    protected  Inventory gui;
    protected  String title;
    protected  int size;

    protected final ItemStack backButton = createButton(Material.ARROW,
            List.of("Кликните, чтобы вернуться назад"), "§eНазад");

    protected Menu(){}

    protected Menu(CustomItem plugin, Inventory gui, String title, int size){
        this.plugin = plugin;
        this.gui = gui;
        this.title = title;
        this.size=size;
    }

    protected void fillBackground(){
        ItemStack filler = createFiller();
        for(int i = 0; i < size; i++) {
        gui.setItem(i, filler);
        }

    }

    protected ItemStack createFiller(){
        ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        assert fillerMeta != null;
        fillerMeta.setDisplayName(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH +  "|||||");
        fillerItem.setItemMeta(fillerMeta);
        return fillerItem;
    }

    protected ItemStack createButton(Material material, List<String> Lore, String name){
        ItemStack button = new ItemStack(material);
        ItemMeta buttonMeta = button.getItemMeta();
        assert buttonMeta != null;
        buttonMeta.setDisplayName(name);
        buttonMeta.setLore(Lore);
        button.setItemMeta(buttonMeta);
        return button;
    }

    protected ItemStack createButton(Material dye, String name, int customModelData){
        ItemStack item = new ItemStack(dye);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setCustomModelData(customModelData);
        meta.setLore(Collections.singletonList("§aКликните для выбора"));
        item.setItemMeta(meta);
        return item;
    }

    public abstract void open(Player player);

}
