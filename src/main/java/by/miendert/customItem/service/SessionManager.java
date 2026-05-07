package by.miendert.customItem.service;

import by.miendert.customItem.model.PlayerSession;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    private final Map<UUID, PlayerSession> sessions = new HashMap<>();

    public void startEditing(Player player, ItemStack item){
        PlayerSession session = new PlayerSession(item);
        sessions.put(player.getUniqueId(), session);
    }

    public PlayerSession getSession(Player player){
        return sessions.get(player.getUniqueId());
    }

    public void removeSession(Player player){
        sessions.remove(player.getUniqueId());
    }

    public Boolean isEditing(Player player){
        if(sessions.containsKey(player.getUniqueId())){
            return true;
        } else {
            return false;
        }
    }


}
