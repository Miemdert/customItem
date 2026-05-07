package by.miendert.customItem.model;

import by.miendert.customItem.CustomItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemData {
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private Map<Enchantment, Integer> enchants = new HashMap<>();
    private Material material;

    public static CustomItemData fromItemStack (ItemStack item){
        CustomItemData data = new CustomItemData();
        if (item == null || item.getType().equals(Material.AIR)){
            return data;
        }

        data.setMaterial(item.getType());

        ItemMeta meta = item.getItemMeta();

        if(meta == null){
            return data;
        }

        if (!meta.hasDisplayName()) {
            data.setDisplayName(item.getType().name().toLowerCase().replace("_", " "));
        } else {
            data.setDisplayName(meta.getDisplayName());
        }

        if(meta.hasLore()){
            data.setLore(meta.getLore());
        }

        if(meta.hasEnchants()){
            data.setEnchants(meta.getEnchants());
        }

        return data;
    }

    public Boolean hasLore(){
        return !lore.isEmpty();
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setMaterial(Material material){
        this.material = material;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void addLore(String lore){
        this.lore.add(lore);
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public void setEnchants(Map<Enchantment, Integer> enchants) {
        this.enchants = enchants;
    }
}
