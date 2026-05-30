package by.miendert.customItem.ui;

import by.miendert.customItem.CustomItem;
import by.miendert.customItem.model.CustomItemData;
import by.miendert.customItem.model.PlayerSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static by.miendert.customItem.util.Tools.getEnchantName;
import static by.miendert.customItem.util.Tools.toRoman;


public class MainMenu extends Menu {

    public MainMenu(CustomItem plugin){
        this.size = 27;
        this.plugin = plugin;
        this.title =  "§6Создание предмета";
        this.gui = Bukkit.createInventory(null,size,title);
    }


    @Override
    public void open(Player player) {
        by.miendert.customItem.service.SessionManager sessionManager = plugin.getSessionManager();
        PlayerSession session = sessionManager.getSession(player);

        CustomItemData data = session.getItemData();

        fillBackground();

        List<String> Lore = new ArrayList<>();
        Lore.add("§7Кликните, чтобы добавить описание");
        Lore.add("§6Текущий лор:");

        if (!data.hasLore()) {
            Lore.add("§4Нет");
        } else {
            Lore.addAll(data.getLore());
        }

        ItemStack loreButton = createButton(Material.BOOK, Lore, "§aДобавить лор");
        ItemStack colorButton = createButton(Material.GRAY_DYE, List.of("§7Кликните, чтобы выбрать цвет"), "§aВыбрать цвет");
        ItemStack nameButton = createButton(Material.NAME_TAG, Arrays.asList("§7Кликните, чтобы ввести имя предмета",
                "§6Текущее имя: " + data.getDisplayName()), "§aВыбрать название предмета");
        ItemStack createButton = createButton(Material.ANVIL, List.of("§7Кликните, чтобы получить предмет"),"§eСоздать предмет");
        ItemStack exitButton = createButton(Material.BARRIER, List.of("§7Кликните, чтобы выйти"), "§4Выход");

        Lore.clear();
        if (!data.hasEnchants()){
            Lore.add("§7Нет выбранных зачарований");
        }else{
            Map<Enchantment, Integer> enchants = data.getEnchants();
            Lore.add("§7Выбрано: §e" + enchants.size() + " зачарований");
            enchants.forEach((e,l) ->
                    Lore.add("§7- " + getEnchantName(e) + " §e" + toRoman(l)));
            Lore.add("");
            Lore.add("§aКликните для изменения");
        }

        ItemStack enchantButton = createButton(Material.ENCHANTED_BOOK, Lore, "§bТекущие зачарования");

        gui.setItem(1, loreButton);
        gui.setItem(3, colorButton);
        gui.setItem(5, nameButton);
        gui.setItem(7, createButton);
        gui.setItem(21, enchantButton);
        gui.setItem(23,exitButton);

        player.openInventory(gui);
    }


}
