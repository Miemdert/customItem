package by.miendert.customItem;

import by.miendert.customItem.config.PluginConfig;
import by.miendert.customItem.model.CustomItemData;
import by.miendert.customItem.model.PlayerSession;
import by.miendert.customItem.service.SessionManager;
import by.miendert.customItem.ui.*;
import by.miendert.customItem.util.Tools;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static by.miendert.customItem.util.Tools.getEnchantFromDisplayName;
import static by.miendert.customItem.util.Tools.getEnchantName;

public class CustomItem extends JavaPlugin implements CommandExecutor, Listener {


    private final Map<UUID, Map<Enchantment, Integer>> selectedEnchants = new HashMap<>();
    private final Map<UUID, Enchantment> currentEnchantSelection = new HashMap<>();

    PluginConfig pluginConfig = new PluginConfig(this);
    SessionManager sessionManager = new SessionManager();
    Menu menu;

    @Override
    public void onEnable() {
        getLogger().info("CustomItem is enabled");
        getServer().getPluginManager().registerEvents(this, this);
        pluginConfig.load();
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomItem is disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        if (!player.hasPermission("customItem.use")) {
            player.sendMessage(pluginConfig.getPermissionlack());
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage("§cВозьмите предмет в руку для редактирования!");
            return true;
        }

        sessionManager.startEditing(player, itemInHand, this);
        MainMenu mainMenu = new MainMenu(this);
        mainMenu.open(player);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Создание предмета")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        PlayerSession session = sessionManager.getSession(player);
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        switch (clickedItem.getType()) {
            case GRAY_DYE:
                if (event.getSlot() == 3 ){
                    player.closeInventory();
                    Bukkit.getScheduler().runTask(this, () -> {
                        menu = new ColorPicker(this);
                        menu.open(player);
                    });
                }
                break;
            case NAME_TAG:
                player.closeInventory();
                player.sendMessage("§aВведите имя предмета в чат:");
                session.setInputState(PlayerSession.InputState.waiting_for_name);
                break;

            case BOOK:
                player.closeInventory();
                player.sendMessage("§aВведите текст лора в чат:");
                session.setInputState(PlayerSession.InputState.waiting_for_lore);
                break;

            case ENCHANTED_BOOK:
                if (event.getSlot() == 21) {
                    player.closeInventory();
                    Bukkit.getScheduler().runTask(this, () -> {
                        menu = new EnchantMenu(this);
                        menu.open(player);
                    });
                }
                break;

            case ANVIL:
                ItemStack item = sessionManager.getSession(player).getItem();
                if (item == null) {
                    player.sendMessage("§cОшибка: предмет не найден!");
                    return;
                }
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), item);
                player.sendMessage("§aПредмет изменен!");
                sessionManager.removeSession(player);
                player.closeInventory();
                break;

            case BARRIER:
                player.closeInventory();
                sessionManager.removeSession(player);
                player.sendMessage("§cРедактирование отменено");
                break;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerSession session = sessionManager.getSession(player);
        UUID uuid = player.getUniqueId();

        if (!session.isInputPending()) {
            return;
        }

        event.setCancelled(true);
        String text = event.getMessage();
        CustomItemData data = session.getItemData();

        if (data == null) {
            player.sendMessage("§cОшибка: предмет не найден!");
            session.clearInputState();
            return;
        }


        switch (session.getInputState()) {
            case waiting_for_name:
                data.setDisplayName(text);
                break;

            case waiting_for_lore:
                data.addLore(text);
                break;
        }

        Bukkit.getScheduler().runTask(this, () -> {
            menu = new MainMenu(this);
            menu.open(player);
        });

        session.clearInputState();
    }

    @EventHandler
    public void onColorMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Выбор цвета предмета")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        PlayerSession session = sessionManager.getSession(player);
        CustomItemData data = session.getItemData();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.ARROW && event.getSlot() == 45) {
            menu = new MainMenu(this);
            menu.open(player);
            return;
        }

        if (clicked.getType() == Material.BARRIER && event.getSlot() == 53) {
            //Очистка цвета
            session.setApplyingColor(false);
            menu = new ApplyColorMenu(this);
            menu.open(player);
            return;
        }

        if (clicked.getType().toString().endsWith("_DYE")) {
            //Применение цвета
            session.setSelectedDye(clicked);
            session.setApplyingColor(true);
            menu = new ApplyColorMenu(this);
            menu.open(player);
        }
    }

    private void applyColorToItem(Player player, boolean applyToLore) {
        PlayerSession session = sessionManager.getSession(player);
        CustomItemData data = session.getItemData();
        if (data == null) return;


        ItemStack dye = session.getSelectedDye();
        if (dye == null || !dye.getItemMeta().hasDisplayName()) return;

        String colorCode = dye.getItemMeta().getDisplayName().substring(0, 2);

        if (applyToLore) {
            if (data.hasLore()) {
                List<String> coloredLore = data.getLore().stream()
                        .map(line -> colorCode + ChatColor.stripColor(line))
                        .collect(Collectors.toList());
                data.setLore(coloredLore);
            } else {
                player.sendMessage("§cУ предмета нет лора для окрашивания");
            }
        } else {

            String currentName = data.getDisplayName();
            String strippedName = ChatColor.stripColor(currentName);
            data.setDisplayName(colorCode + strippedName);
        }

    }



    @EventHandler
    public void onClickApplyMenu(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Применить к")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        PlayerSession session = sessionManager.getSession(player);
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.ARROW && event.getSlot() == 22) {
            session.clearSelectedDye();
            menu = new ColorPicker(this);
            menu.open(player);
            return;
        }

        if (clicked.getType() == Material.NAME_TAG && event.getSlot() == 10) {
            if (session.isDyeSelected()) {
                applyColorToItem(player, false);
                session.clearSelectedDye();
            } else {
                resetItemColor(player, false);
            }
            menu = new MainMenu(this);
            menu.open(player);
            return;
        }

        if (clicked.getType() == Material.BOOK && event.getSlot() == 16) {
            if(session.isDyeSelected()){
                applyColorToItem(player, true);
                session.clearSelectedDye();
            }else {
                resetItemColor(player, true);
            }

            menu = new MainMenu(this);
            menu.open(player);
            return;
        }
    }

    private void resetItemColor(Player player, Boolean resetLore) {
        PlayerSession session = sessionManager.getSession(player);
        CustomItemData data = session.getItemData();
        if (data == null) return;


        if(!resetLore){
            data.setDisplayName(ChatColor.stripColor(data.getDisplayName()));
        }


        if (resetLore){
            if (data.hasLore()) {
                List<String> strippedLore = data.getLore().stream()
                        .map(ChatColor::stripColor)
                        .collect(Collectors.toList());
                data.setLore(strippedLore);
            }
        }
    }

    @EventHandler
    public void onEnchantMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("§6Выбор зачарований")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;


        if (clicked.getType() == Material.EMERALD && event.getSlot() == 53) {
            applyEnchantsToItem(player);
            menu=new MainMenu(this);
            menu.open(player);
            return;
        }


        if (clicked.getType() == Material.ENCHANTED_BOOK) {
            Enchantment enchant = getEnchantFromDisplayName(clicked.getItemMeta().getDisplayName());
            if (enchant == null) return;

            if (event.isLeftClick()) {

                menu = new EnchantmentLevelSelectMenu(this, enchant);
            } else if (event.isRightClick()) {

                selectedEnchants.get(player.getUniqueId()).remove(enchant);
                updateEnchantMenu(player);
            }
        }
    }

    @EventHandler
    public void onLevelSelect(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("§6Выбор уровня:")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null) return;


        if (clicked.getType() == Material.ARROW && event.getSlot() == 18) {
            menu = new EnchantMenu(this);
            menu.open(player);
            return;
        }


        if (clicked.getType() == Material.PAPER || clicked.getType() == Material.BARRIER) {
            try {
                int level = Integer.parseInt(clicked.getItemMeta().getDisplayName().replace("§eУровень ", ""));
                Enchantment enchant = currentEnchantSelection.get(player.getUniqueId());


                ItemStack targetItem = sessionManager.getSession(player).getItem();
                if (!enchant.canEnchantItem(targetItem)) {
                    if (!pluginConfig.getIgnoreLevelRestrictions()) {
                        player.sendMessage("§cЭто зачарование несовместимо с вашим предметом!");
                        return;
                    }
                }


                selectedEnchants.get(player.getUniqueId()).put(enchant, level);
                menu = new EnchantMenu(this);
                menu.open(player);
            } catch (NumberFormatException e) {
                player.sendMessage("§cОшибка выбора уровня!");
            }
        }
    }


    private void applyEnchantsToItem(Player player) {
        UUID uuid = player.getUniqueId();
        ItemStack item = sessionManager.getSession(player).getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;


        meta.getEnchants().keySet().forEach(meta::removeEnchant);


        Map<Enchantment, Integer> enchants = selectedEnchants.get(uuid);
        if (enchants != null) {
            enchants.forEach((enchant, level) -> {
                try {
                    meta.addEnchant(enchant, level, pluginConfig.getIgnoreLevelRestrictions());
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cНе удалось добавить " + getEnchantName(enchant) + " (несовместимо)");
                }
            });
        }

        item.setItemMeta(meta);
    }

    private void updateEnchantMenu(Player player) {
        Bukkit.getScheduler().runTask(this, () -> {
            if (player.getOpenInventory().getTitle().startsWith("§6Выбор зачарований")) {
                menu = new EnchantMenu(this);
                menu.open(player);
            }
        });
    }

    public SessionManager getSessionManager(){return sessionManager;}
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }
}
