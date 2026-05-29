package by.miendert.customItem.tools;

import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;
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
}
