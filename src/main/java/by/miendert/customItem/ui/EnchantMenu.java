package by.miendert.customItem.ui;

import by.miendert.customItem.CustomItem;
import by.miendert.customItem.model.PlayerSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static by.miendert.customItem.util.Tools.getCompatibleItems;
import static by.miendert.customItem.util.Tools.getEnchantName;



public class EnchantMenu extends Menu{

    public EnchantMenu(CustomItem plugin){
        this.plugin=plugin;
        this.size=54;
        this.title="§6Выбор зачарований";
        this.gui = Bukkit.createInventory(null,size,title);
    }

    @Override
    public void open(Player player) {
        PlayerSession session = plugin.getSessionManager().getSession(player);
        Map<Enchantment, Integer> selectedEnchants = session.getItemData().getEnchants();

        fillBackground();
        placeEnchantments(gui, selectedEnchants);

        ItemStack confirmButton = createButton(Material.EMERALD,
                Arrays.asList("§7Выбрано: §e" + selectedEnchants.size() + " зачарований",
                        "§aКликните для возврата"), "§aПодтвердить выбор");

        gui.setItem(53, confirmButton);

        player.openInventory(gui);
    }

    public void placeEnchantments(Inventory gui, Map<Enchantment, Integer> selectedEnchants) {

        List<Enchantment> sortedEnchants = getSortedEnchants();
        Map<Enchantment, Integer> maxEnchantLevels = getMaxEnchantLevels();

        for (int i = 0; i < sortedEnchants.size(); i++) {
            Enchantment enchant = sortedEnchants.get(i);
            ItemStack enchantItem = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = enchantItem.getItemMeta();

            int currentLevel = selectedEnchants.getOrDefault(enchant,0);
            String selectedStatus = currentLevel > 0 ? "§aУровень " + currentLevel : "§cНе выбрано";

            meta.setDisplayName("§e" + getEnchantName(enchant));
            meta.setLore(Arrays.asList(
                    "§7Макс. уровень: §e" + maxEnchantLevels.get(enchant),
                    "§7Совместимость: §e" + getCompatibleItems(enchant),
                    "",
                    selectedStatus,
                    "",
                    "§aЛКМ - выбрать уровень",
                    "§cПКМ - удалить зачарование"
            ));

            enchantItem.setItemMeta(meta);
            gui.setItem(i, enchantItem);

        }
    }

    protected Map<Enchantment, Integer> getMaxEnchantLevels(){
        Map<Enchantment,Integer> maxEnchantLevels = new HashMap<>();
        for (Enchantment enchant : Enchantment.values()) {
            maxEnchantLevels.put(enchant, enchant.getMaxLevel());
        }

        return maxEnchantLevels;
    }

    protected List<Enchantment> getSortedEnchants(){
        return Arrays.stream(Enchantment.values())
                .sorted(Comparator.comparing(e -> getEnchantName(e).toLowerCase()))
                .collect(Collectors.toList());
    }
}
