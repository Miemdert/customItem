package by.miendert.customItem.ui;

import by.miendert.customItem.CustomItem;
import by.miendert.customItem.config.PluginConfig;
import by.miendert.customItem.model.PlayerSession;
import by.miendert.customItem.service.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static by.miendert.customItem.util.Tools.getEnchantName;

public class EnchantmentLevelSelectMenu extends EnchantMenu{

    private final Enchantment  enchantment;

    public EnchantmentLevelSelectMenu(CustomItem plugin, Enchantment enchantment){
        super(plugin);
        this.enchantment = enchantment;
        this.size = 27;
        this.title = "§6Выбор уровня: " + getEnchantName(enchantment);
        this.gui = Bukkit.createInventory(null, size, title);
    }

    @Override
    public void open(Player player) {
        SessionManager sessionManager = plugin.getSessionManager();
        PlayerSession session = sessionManager.getSession(player);
        PluginConfig pluginConfig = plugin.getPluginConfig();
        session.setCurrentEnchantSelection(enchantment);

        addSelectedEnchantmentLevels(gui, enchantment, pluginConfig.getIgnoreLevelRestrictions(), session.getOriginalItem());
        player.sendMessage(enchantment.toString());
        player.openInventory(gui);
}
    private void addSelectedEnchantmentLevels(Inventory gui, Enchantment enchantment, boolean ignoreLevelRestriction, ItemStack targetItem){
        for (int level = 1; level <= getMaxEnchantLevels().get(enchantment); level++) {
            ItemStack levelItem = new ItemStack(Material.PAPER);
            ItemMeta meta = levelItem.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§eУровень " + level);


            boolean compatible = enchantment.canEnchantItem(targetItem) || ignoreLevelRestriction;

            meta.setLore(Arrays.asList(
                    (compatible?"§aСовместимо":"§4Не совместимо"),
                    "§aКликните для выбора"
            ));

            if (!compatible) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                levelItem.setType(Material.BARRIER);
            }

            levelItem.setItemMeta(meta);
            gui.addItem(levelItem);
        }
    }
}
