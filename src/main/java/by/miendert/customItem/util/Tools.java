package by.miendert.customItem.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Tools {

    public static String getEnchantName(Enchantment enchant){
        return switch (enchant.getKey().getKey()) {
            // Броня
            case "protection" -> "Защита";
            case "fire_protection" -> "Огнеупорность";
            case "feather_falling" -> "Невесомость";
            case "blast_protection" -> "Взрывоустойчивость";
            case "projectile_protection" -> "Защита от снарядов";
            case "respiration" -> "Подводное дыхание";
            case "aqua_affinity" -> "Подводник";
            case "thorns" -> "Шипы";
            case "depth_strider" -> "Глубинный шаг";
            case "frost_walker" -> "Ледяная поступь";
            case "binding_curse" -> "Проклятие несъемности";
            case "soul_speed" -> "Скорость душ";
            case "swift_sneak" -> "Тихий шаг";

            // Оружие
            case "sharpness" -> "Острота";
            case "smite" -> "Небесная кара";
            case "bane_of_arthropods" -> "Гибель членистоногих";
            case "knockback" -> "Отбрасывание";
            case "fire_aspect" -> "Огненный аспект";
            case "looting" -> "Грабеж";
            case "sweeping" -> "Разящий клинок";
            case "impaling" -> "Пронзание";
            case "loyalty" -> "Верность";
            case "riptide" -> "Прибой";
            case "channeling" -> "Громовержец";

            // Инструменты
            case "efficiency" -> "Эффективность";
            case "silk_touch" -> "Шелковое касание";
            case "unbreaking" -> "Прочность";
            case "fortune" -> "Удача";
            case "power" -> "Мощь";
            case "punch" -> "Отдача";
            case "flame" -> "Пламя";
            case "infinity" -> "Бесконечность";
            case "luck_of_the_sea" -> "Морская удача";
            case "lure" -> "Приманка";
            case "mending" -> "Починка";
            case "vanishing_curse" -> "Проклятие исчезновения";
            case "multishot" -> "Залп";
            case "piercing" -> "Пробивание";
            case "quick_charge" -> "Быстрая перезарядка";

            // Другие
            case "arrow_damage" -> "Мощь";
            case "arrow_knockback" -> "Отдача";
            case "arrow_fire" -> "Пламя";
            case "arrow_infinite" -> "Бесконечность";
            default -> {
                String key = enchant.getKey().getKey();
                yield Arrays.stream(key.split("_"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                        .collect(Collectors.joining(" "));
            }
        };
    }
    public static String toRoman(int number) {
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return number > 0 && number <= roman.length ? roman[number-1] : "" + number;
    }

    public static String getCompatibleItems(Enchantment enchant) {
        List<String> compatible = new ArrayList<>();

        if (canEnchantType(enchant, Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD,
                Material.STONE_SWORD, Material.WOODEN_SWORD, Material.NETHERITE_SWORD)) {
            compatible.add("мечи");
        }

        if (canEnchantType(enchant, Material.DIAMOND_AXE, Material.IRON_AXE, Material.GOLDEN_AXE,
                Material.STONE_AXE, Material.WOODEN_AXE, Material.NETHERITE_AXE)) {
            compatible.add("топоры");
        }

        if (canEnchantType(enchant, Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE,
                Material.STONE_PICKAXE, Material.WOODEN_PICKAXE, Material.NETHERITE_PICKAXE)) {
            compatible.add("кирки");
        }

        if (canEnchantType(enchant, Material.DIAMOND_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL,
                Material.STONE_SHOVEL, Material.WOODEN_SHOVEL, Material.NETHERITE_SHOVEL)) {
            compatible.add("лопаты");
        }

        if (canEnchantType(enchant, Material.DIAMOND_HOE, Material.IRON_HOE, Material.GOLDEN_HOE,
                Material.STONE_HOE, Material.WOODEN_HOE, Material.NETHERITE_HOE)) {
            compatible.add("мотыги");
        }

        if (canEnchantType(enchant, Material.DIAMOND_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET,
                Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.NETHERITE_HELMET,
                Material.TURTLE_HELMET)) {
            compatible.add("шлемы");
        }

        if (canEnchantType(enchant, Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE,
                Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.NETHERITE_CHESTPLATE)) {
            compatible.add("нагрудники");
        }

        if (canEnchantType(enchant, Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS,
                Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.NETHERITE_LEGGINGS)) {
            compatible.add("поножи");
        }

        if (canEnchantType(enchant, Material.DIAMOND_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS,
                Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.NETHERITE_BOOTS)) {
            compatible.add("ботинки");
        }

        if (canEnchantType(enchant, Material.BOW)) {
            compatible.add("луки");
        }

        if (canEnchantType(enchant, Material.CROSSBOW)) {
            compatible.add("арбалеты");
        }

        if (canEnchantType(enchant, Material.TRIDENT)) {
            compatible.add("трезубцы");
        }

        if (canEnchantType(enchant, Material.FISHING_ROD)) {
            compatible.add("удочки");
        }

        if (canEnchantType(enchant, Material.SHEARS)) {
            compatible.add("ножницы");
        }

        if (canEnchantType(enchant, Material.FLINT_AND_STEEL)) {
            compatible.add("огнива");
        }

        if (canEnchantType(enchant, Material.CARROT_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK)) {
            compatible.add("удочки с морковью/грибом");
        }

        if (canEnchantType(enchant, Material.ELYTRA)) {
            compatible.add("элитры");
        }

        if (canEnchantType(enchant, Material.SHIELD)) {
            compatible.add("щиты");
        }

        if (enchant.equals(Enchantment.MENDING) || enchant.equals(Enchantment.VANISHING_CURSE)) {
            compatible.add("все инструменты, оружие и броня");
        }

        if (enchant.equals(Enchantment.BINDING_CURSE)) {
            compatible.add("броня");
        }

        if (compatible.isEmpty()) {
            return "специальные предметы";
        }

        return String.join(", ", compatible);
    }


    public static boolean canEnchantType(Enchantment enchant, Material... materials) {
        for (Material material : materials) {
            if (enchant.canEnchantItem(new ItemStack(material))) {
                return true;
            }
        }
        return false;
    }

    public static Enchantment getEnchantFromDisplayName(String displayName) {
        String enchantName = displayName.substring(2);
        for (Enchantment enchant : Enchantment.values()) {
            if (getEnchantName(enchant).equals(enchantName)) {
                return enchant;
            }
        }
        return null;
    }
}