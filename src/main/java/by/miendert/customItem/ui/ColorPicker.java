package by.miendert.customItem.ui;

import by.miendert.customItem.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ColorPicker extends Menu{
    public ColorPicker(CustomItem plugin){
        this.plugin = plugin;
        this.size = 54;
        this.title = "§6Выбор цвета предмета";
        this.gui = Bukkit.createInventory(null, size, title);
    }
    
    @Override
    public void open(Player player) {
        
        ItemStack white = createButton(Material.WHITE_DYE, "§fБелый", 10);
        ItemStack orange = createButton(Material.ORANGE_DYE, "§6Оранжевый", 11);
        ItemStack magenta = createButton(Material.MAGENTA_DYE, "§dПурпурный", 12);
        ItemStack lightBlue = createButton(Material.LIGHT_BLUE_DYE, "§9Голубой", 13);
        ItemStack yellow = createButton(Material.YELLOW_DYE, "§eЖёлтый", 14);
        ItemStack lime = createButton(Material.LIME_DYE, "§aЛаймовый", 15);
        ItemStack pink = createButton(Material.PINK_DYE, "§cРозовый", 16);
        
        ItemStack gray = createButton(Material.GRAY_DYE, "§8Серый", 19);
        ItemStack lightGray = createButton(Material.LIGHT_GRAY_DYE, "§7Светло-серый", 20);
        ItemStack cyan = createButton(Material.CYAN_DYE, "§3Бирюзовый", 21);
        ItemStack purple = createButton(Material.PURPLE_DYE, "§5Фиолетовый", 22);
        ItemStack blue = createButton(Material.BLUE_DYE, "§1Синий", 23);
        ItemStack brown = createButton(Material.BROWN_DYE, "§4Коричневый", 24);
        ItemStack green = createButton(Material.GREEN_DYE, "§2Зелёный", 25);
        ItemStack red = createButton(Material.RED_DYE, "§4Красный", 28);
        ItemStack black = createButton(Material.BLACK_DYE, "§0Чёрный", 29);
    
        ItemStack resetButton = createButton(Material.BARRIER, 
                List.of("Кликните, чтобы сбросить текущий цвет"), "§cСбросить цвет");
        
        fillBackground();

        gui.setItem(45, backButton);
        gui.setItem(10, white);
        gui.setItem(11, orange);
        gui.setItem(12, magenta);
        gui.setItem(13, lightBlue);
        gui.setItem(14, yellow);
        gui.setItem(15, lime);
        gui.setItem(16, pink);
        gui.setItem(19, gray);
        gui.setItem(20, lightGray);
        gui.setItem(21, cyan);
        gui.setItem(22, purple);
        gui.setItem(23, blue);
        gui.setItem(24, brown);
        gui.setItem(25, green);
        gui.setItem(28, red);
        gui.setItem(29, black);

        player.openInventory(gui);
    }
}
