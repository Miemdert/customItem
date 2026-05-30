package by.miendert.customItem.ui;

import by.miendert.customItem.CustomItem;
import by.miendert.customItem.model.PlayerSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ApplyColorMenu extends Menu{

    public ApplyColorMenu(CustomItem plugin){
        this.plugin = plugin;
        this.size = 27;
        this.title = "§6Применить к";
        this.gui = Bukkit.createInventory(null, size, title);
    }

    @Override
    public void open(Player player) {
        fillBackground();

        PlayerSession session = plugin.getSessionManager().getSession(player);

        ItemStack nameButton = createButton(Material.NAME_TAG,
                List.of(session.isApplyingColor()?"§aКликните, чтобы применить цвет к названию":"§aКликните, чтобы очистить цвет названиия"), true?"§3Применить к названию":"§3Очистить цвет названия");
        ItemStack loreButton = createButton(Material.BOOK,
                List.of(session.isApplyingColor()?"§aКликните, чтобы применить цвет к лору":"§aКликните, чтобы очистить цвет лора"), true?"§eПрименить к лору":"§eОчистить цвет лора");

        gui.setItem(10, nameButton);
        gui.setItem(16, loreButton);
        gui.setItem(22, backButton);

        player.openInventory(gui);
    }
}
