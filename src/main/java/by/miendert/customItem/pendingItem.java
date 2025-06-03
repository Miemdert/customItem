package by.miendert.customItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class pendingItem {
    String name;
    Material material;
    String lore;
    List<Enchantment> enchantmentList;

    public pendingItem(){
        this.name = "";
        this.material = Material.AIR;
        this.lore = "";
        this.enchantmentList = new ArrayList<>();
    }

    public void addName(String name){
        this.name = name;
    }

    public void addMaterial(Material material){
        this.material = material;
    }

    public void addLore(String lore){
        this.lore = lore;
    }
    public void addEnchantment(Enchantment enchantment){
        this.enchantmentList.add(enchantment);
    }
}



