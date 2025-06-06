package by.miendert.customItem;

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

public class CustomItem extends JavaPlugin implements CommandExecutor, Listener {

    private String permissionlack;
    private final Map<UUID, String> pendingInputs = new HashMap<>();
    private final Map<UUID, ItemStack> pendingItems = new HashMap<>();
    private Boolean ignoreLevelRestrictions;
    private final Map<UUID, List<String>> pendingLore = new HashMap<>();
    private final Map<Enchantment, Integer> maxEnchantLevels = new HashMap<>();
    private final Map<UUID, Map<Enchantment, Integer>> selectedEnchants = new HashMap<>();
    private final Map<UUID, Enchantment> currentEnchantSelection = new HashMap<>();
    private Boolean isLoreSet;
    private Map<UUID, ItemStack> pendinColor = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("CustomItem is enabled");
        getServer().getPluginManager().registerEvents(this, this);
        for (Enchantment enchant : Enchantment.values()) {
            maxEnchantLevels.put(enchant, enchant.getMaxLevel());
        }
        loadConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomItem is disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("customItem.use")) {
            player.sendMessage(permissionlack);
            return true;
        }



        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage("§cВозьмите предмет в руку для редактирования!");
            return true;
        }

        ItemMeta itemMeta = itemInHand.getItemMeta();
        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        pendingLore.put(player.getUniqueId(), lore);


        Map<Enchantment, Integer> enchantsCopy = new HashMap<>();
        if (itemMeta.hasEnchants()) {
            enchantsCopy.putAll(itemMeta.getEnchants());
        }
        selectedEnchants.put(player.getUniqueId(), enchantsCopy);


        pendingItems.put(player.getUniqueId(), itemInHand.clone());
        openCustomItemMenu(player);

        return true;
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        permissionlack = getConfig().getString("permissionlack", "§cУ вас нет прав на использование этой команды!");
        ignoreLevelRestrictions = getConfig().getBoolean("ignoreLevelRestrictions", false);
    }

    private void openCustomItemMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6Создание предмета");

        ItemStack item = pendingItems.get(player.getUniqueId());
        ItemMeta itemMeta = item.getItemMeta();


        if (itemMeta.getLore() != null) {
            isLoreSet = true;
        } else isLoreSet = false;

        List<String> Lore = new ArrayList<>();
        Lore.add("§7Кликните, чтобы добавить описание");
        Lore.add("§6Текущий лор:");

        if (pendingLore.isEmpty()) {
            Lore.add("§4Нет");
        } else {
            Lore.addAll(pendingLore.get(player.getUniqueId()));
        }

        ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        fillerMeta.setDisplayName(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH +  "|||||");
        fillerItem.setItemMeta(fillerMeta);
        int i = 0;
        while(i != 27){
            if (i == 1 || i == 3 || i == 5 || i == 7 || i == 23 || i == 21){
                i++;
            }
            gui.setItem(i, fillerItem);
            i++;
        }

        ItemStack loreButton = new ItemStack(Material.BOOK);
        ItemMeta loreMeta = loreButton.getItemMeta();
        loreMeta.setDisplayName("§aДобавить лор");
        loreMeta.setLore(Lore);
        loreButton.setItemMeta(loreMeta);
        gui.setItem(1, loreButton);

        ItemStack colorButton = new ItemStack(Material.GRAY_DYE);
        ItemMeta colorMeta = colorButton.getItemMeta();
        colorMeta.setDisplayName("§aВыбрать цвет");
        colorMeta.setLore(Arrays.asList("§7Кликните, чтобы выбрать цвет"));
        colorButton.setItemMeta(colorMeta);
        gui.setItem(3, colorButton);

        String name;
        if (!itemMeta.hasDisplayName()) {
            name = item.getType().name().toLowerCase().replace("_", " ");
        } else {
            name = itemMeta.getDisplayName();
        }

        ItemStack itemname = new ItemStack(Material.NAME_TAG);
        ItemMeta itemnameMeta = itemname.getItemMeta();
        itemnameMeta.setDisplayName("§aВыбрать название предмета");
        itemnameMeta.setLore(Arrays.asList("§7Кликните, чтобы ввести имя предмета",
                "§6Текущее имя: " + name));
        itemname.setItemMeta(itemnameMeta);
        gui.setItem(5, itemname);

        ItemStack createButton = new ItemStack(Material.ANVIL);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName("§eСоздать предмет");
        createMeta.setLore(Arrays.asList("§7Кликните, чтобы получить предмет"));
        createButton.setItemMeta(createMeta);
        gui.setItem(7, createButton);

        ItemStack exit = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.setDisplayName("§4Выход");
        exitMeta.setLore(Arrays.asList("§7Кликните, чтобы выйти"));
        exit.setItemMeta(exitMeta);
        gui.setItem(23, exit);

        ItemStack enchantsInfo = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta enchantsMeta = enchantsInfo.getItemMeta();
        enchantsMeta.setDisplayName("§bТекущие зачарования");

        Map<Enchantment, Integer> enchants = selectedEnchants.getOrDefault(player.getUniqueId(), new HashMap<>());
        List<String> lore = new ArrayList<>();

        if (enchants.isEmpty()) {
            lore.add("§7Нет выбранных зачарований");
        } else {
            lore.add("§7Выбрано: §e" + enchants.size() + " зачарований");
            enchants.forEach((e, l) ->
                    lore.add("§7- " + getEnchantName(e) + " §e" + toRoman(l)));
        }

        lore.add("");
        lore.add("§aКликните для изменения");
        enchantsMeta.setLore(lore);
        enchantsInfo.setItemMeta(enchantsMeta);
        gui.setItem(21, enchantsInfo);

        player.openInventory(gui);
    }

    private String toRoman(int number) {
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return number > 0 && number <= roman.length ? roman[number-1] : "" + number;
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
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        switch (clickedItem.getType()) {
            case GRAY_DYE:
            if (event.getSlot() == 3 ){
                player.closeInventory();
                Bukkit.getScheduler().runTask(this, () -> {
                    openColorMenu(player);
                });
            }
                break;
            case NAME_TAG:
                player.closeInventory();
                player.sendMessage("§aВведите имя предмета в чат:");
                pendingInputs.put(player.getUniqueId(), "waiting_for_name");
                break;

            case BOOK:
                player.closeInventory();
                player.sendMessage("§aВведите текст лора в чат:");
                pendingInputs.put(player.getUniqueId(), "waiting_for_lore");
                break;

            case ENCHANTED_BOOK:
                if (event.getSlot() == 21) {
                    player.closeInventory();
                    Bukkit.getScheduler().runTask(this, () -> {
                        openEnchantMenu(player);
                    });
                }
                break;

            case ANVIL:
                ItemStack item = pendingItems.get(player.getUniqueId());
                if (item == null) {
                    player.sendMessage("§cОшибка: предмет не найден!");
                    return;
                }
                player.getInventory().setItem(player.getInventory().getHeldItemSlot(), item);
                player.sendMessage("§aПредмет изменен!");
                pendingItems.remove(player.getUniqueId());
                selectedEnchants.clear();
                player.closeInventory();
                break;

            case BARRIER:
                player.closeInventory();
                pendingItems.remove(player.getUniqueId());
                player.sendMessage("§cРедактирование отменено");
                break;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!pendingInputs.containsKey(uuid)) {
            return;
        }

        event.setCancelled(true);
        String text = event.getMessage();
        String inputType = pendingInputs.get(uuid);
        ItemStack item = pendingItems.get(uuid);

        if (item == null) {
            player.sendMessage("§cОшибка: предмет не найден!");
            pendingInputs.remove(uuid);
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage("§cОшибка: не удалось получить ItemMeta!");
            pendingInputs.remove(uuid);
            return;
        }

        switch (inputType) {
            case "waiting_for_name":
                meta.setDisplayName(text);
                break;

            case "waiting_for_lore":
                List<String> lore;
                if(pendingLore.containsKey(uuid)) {
                    lore = pendingLore.get(uuid);
                } else  lore = new ArrayList<>();
                lore.add(text);
                pendingLore.remove(uuid);
                pendingLore.put(player.getUniqueId(), lore);
                meta.setLore(lore);
                break;
        }

        item.setItemMeta(meta);
        pendingItems.put(uuid, item);
        Bukkit.getScheduler().runTask(this, () -> {
            openCustomItemMenu(player);
        });

        pendingInputs.remove(uuid);
    }

    private void openColorMenu(Player player) {
        Inventory colorMenu = Bukkit.createInventory(null, 54, "§6Выбор цвета предмета");


        ItemStack white = createColorItem(Material.WHITE_DYE, "§fБелый", 10);
        ItemStack orange = createColorItem(Material.ORANGE_DYE, "§6Оранжевый", 11);
        ItemStack magenta = createColorItem(Material.MAGENTA_DYE, "§dПурпурный", 12);
        ItemStack lightBlue = createColorItem(Material.LIGHT_BLUE_DYE, "§9Голубой", 13);
        ItemStack yellow = createColorItem(Material.YELLOW_DYE, "§eЖёлтый", 14);
        ItemStack lime = createColorItem(Material.LIME_DYE, "§aЛаймовый", 15);
        ItemStack pink = createColorItem(Material.PINK_DYE, "§cРозовый", 16);


        ItemStack gray = createColorItem(Material.GRAY_DYE, "§8Серый", 19);
        ItemStack lightGray = createColorItem(Material.LIGHT_GRAY_DYE, "§7Светло-серый", 20);
        ItemStack cyan = createColorItem(Material.CYAN_DYE, "§3Бирюзовый", 21);
        ItemStack purple = createColorItem(Material.PURPLE_DYE, "§5Фиолетовый", 22);
        ItemStack blue = createColorItem(Material.BLUE_DYE, "§1Синий", 23);
        ItemStack brown = createColorItem(Material.BROWN_DYE, "§4Коричневый", 24);
        ItemStack green = createColorItem(Material.GREEN_DYE, "§2Зелёный", 25);
        ItemStack red = createColorItem(Material.RED_DYE, "§4Красный", 28);
        ItemStack black = createColorItem(Material.BLACK_DYE, "§0Чёрный", 29);


        ItemStack reset = new ItemStack(Material.BARRIER);
        ItemMeta resetMeta = reset.getItemMeta();
        resetMeta.setDisplayName("§cСбросить цвет");
        reset.setItemMeta(resetMeta);
        colorMenu.setItem(53, reset);


        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§eНазад");
        back.setItemMeta(backMeta);
        colorMenu.setItem(45, back);


        colorMenu.setItem(10, white);
        colorMenu.setItem(11, orange);
        colorMenu.setItem(12, magenta);
        colorMenu.setItem(13, lightBlue);
        colorMenu.setItem(14, yellow);
        colorMenu.setItem(15, lime);
        colorMenu.setItem(16, pink);
        colorMenu.setItem(19, gray);
        colorMenu.setItem(20, lightGray);
        colorMenu.setItem(21, cyan);
        colorMenu.setItem(22, purple);
        colorMenu.setItem(23, blue);
        colorMenu.setItem(24, brown);
        colorMenu.setItem(25, green);
        colorMenu.setItem(28, red);
        colorMenu.setItem(29, black);

        int[] usedSlots = {10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,53,45};

        ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        fillerMeta.setDisplayName(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH +  "|||||");
        fillerItem.setItemMeta(fillerMeta);

        int i = 0;
        while (i != 53) {
            int currentI = i;
            if (Arrays.stream(usedSlots).anyMatch(s -> s == currentI)) {
                i++;
            } else {
                colorMenu.setItem(i, fillerItem);
                i++;
            }
        }
        player.openInventory(colorMenu);
    }

    private ItemStack createColorItem(Material dye, String name, int customModelData) {
        ItemStack item = new ItemStack(dye);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setCustomModelData(customModelData);
        meta.setLore(Collections.singletonList("§aКликните для выбора"));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onColorMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Выбор цвета предмета")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.ARROW && event.getSlot() == 45) {
            openCustomItemMenu(player);
            return;
        }

        if (clicked.getType() == Material.BARRIER && event.getSlot() == 53) {
            applyColorMenu(player, false);
            return;
        }

        if (clicked.getType().toString().endsWith("_DYE")) {
            pendinColor.put(player.getUniqueId(), clicked);
            applyColorMenu(player, true);
        }
    }

    private void applyColorToItem(Player player, boolean applyToLore) {
        ItemStack item = pendingItems.get(player.getUniqueId());
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        ItemStack dye = pendinColor.get(player.getUniqueId());
        if (dye == null || !dye.getItemMeta().hasDisplayName()) return;

        String colorCode = dye.getItemMeta().getDisplayName().substring(0, 2);

        if (applyToLore) {
            if (meta.hasLore()) {
                List<String> coloredLore = meta.getLore().stream()
                        .map(line -> colorCode + ChatColor.stripColor(line))
                        .collect(Collectors.toList());
                meta.setLore(coloredLore);
            } else {
                player.sendMessage("§cУ предмета нет лора для окрашивания");
            }
        } else {
            if (meta.hasDisplayName()) {
                String currentName = meta.getDisplayName();
                String strippedName = ChatColor.stripColor(currentName);
                meta.setDisplayName(colorCode + strippedName);
            } else {
                player.sendMessage("§cУ предмета нет названия для окрашивания");
            }
        }

        item.setItemMeta(meta);
        pendingItems.put(player.getUniqueId(), item);
    }

    private void applyColorMenu(Player player, boolean isApplying) {
        Inventory applyMenu = Bukkit.createInventory(null, 27, "§6Применить к");

        // BOOK - для названия (слот 10)
        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        nameMeta.setDisplayName((isApplying?"§3Применить к названию":"§3Очистить цвет названия"));
        nameMeta.setLore(Arrays.asList((isApplying?"§aКликните, чтобы применить цвет к названию":"§aКликните, чтобы очистить цвет названиия")));
        nameItem.setItemMeta(nameMeta);
        applyMenu.setItem(10, nameItem);

        // NAME_TAG - для лора (слот 16)
        ItemStack loreItem = new ItemStack(Material.BOOK);
        ItemMeta loreMeta = loreItem.getItemMeta();
        loreMeta.setDisplayName((isApplying?"§eПрименить к лору":"§eОчистить цвет лора"));
        loreMeta.setLore(Arrays.asList((isApplying?"§aКликните, чтобы применить цвет к лору":"§aКликните, чтобы очистить цвет лора")));
        loreItem.setItemMeta(loreMeta);
        applyMenu.setItem(16, loreItem);

        // Кнопка назад (слот 22)
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§eНазад");
        back.setItemMeta(backMeta);
        applyMenu.setItem(22, back);

        ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        fillerMeta.setDisplayName(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH +  "|||||");
        fillerItem.setItemMeta(fillerMeta);

        int i = 0;

        while(i != 27){
            if (i == 22 || i == 16 || i ==10){
                i++;
            }
            applyMenu.setItem(i, fillerItem);
            i++;
        }

        player.openInventory(applyMenu);
    }

    @EventHandler
    public void onClickApplyMenu(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Применить к")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.ARROW && event.getSlot() == 22) {
            pendinColor.remove(player.getUniqueId());
            openColorMenu(player);
            return;
        }

        if (clicked.getType() == Material.NAME_TAG && event.getSlot() == 10) {
            if (pendinColor.containsKey(player.getUniqueId())) {
                applyColorToItem(player, false);
                pendinColor.remove(player.getUniqueId());
            } else {
                resetItemColor(player, false);
            }
            openCustomItemMenu(player);
            return;
        }

        if (clicked.getType() == Material.BOOK && event.getSlot() == 16) {
            if(pendinColor.containsKey(player.getUniqueId())){
                applyColorToItem(player, true);
                pendinColor.remove(player.getUniqueId());
            }else {
                resetItemColor(player, true);
            }

            openCustomItemMenu(player);
            return;
        }
    }

    private void resetItemColor(Player player, Boolean resetLore) {
        ItemStack item = pendingItems.get(player.getUniqueId());
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;


        if(!resetLore){
            if (meta.hasDisplayName()) {
                meta.setDisplayName(ChatColor.stripColor(meta.getDisplayName()));
            }
        }


        if (resetLore){
            if (meta.hasLore()) {
                List<String> strippedLore = meta.getLore().stream()
                        .map(ChatColor::stripColor)
                        .collect(Collectors.toList());
                meta.setLore(strippedLore);
            }
        }

        item.setItemMeta(meta);
        pendingItems.put(player.getUniqueId(), item);
    }
    private void openEnchantMenu(Player player) {
        Inventory enchantGUI = Bukkit.createInventory(null, 54, "§6Выбор зачарований");


        selectedEnchants.putIfAbsent(player.getUniqueId(), new HashMap<>());

        List<Enchantment> sortedEnchants = Arrays.stream(Enchantment.values())
                .sorted(Comparator.comparing(e -> getEnchantName(e).toLowerCase()))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedEnchants.size(); i++) {
            Enchantment enchant = sortedEnchants.get(i);
            ItemStack enchantItem = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = enchantItem.getItemMeta();

            int currentLevel = selectedEnchants.get(player.getUniqueId()).getOrDefault(enchant, 0);
            String selectedStatus = currentLevel > 0 ? "§a✔ Уровень " + currentLevel : "§c✖ Не выбрано";

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
            enchantGUI.setItem(i, enchantItem);
        }

        ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        fillerMeta.setDisplayName(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH +  "|||||");
        fillerItem.setItemMeta(fillerMeta);

        int i = sortedEnchants.size();

        while (i != 53 ){
            enchantGUI.setItem(i, fillerItem);
            i++;
        }

        ItemStack confirm = new ItemStack(Material.EMERALD);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aПодтвердить выбор");
        confirmMeta.setLore(Arrays.asList(
                "§7Выбрано: §e" + selectedEnchants.get(player.getUniqueId()).size() + " зачарований",
                "§aКликните для возврата"
        ));
        confirm.setItemMeta(confirmMeta);
        enchantGUI.setItem(53, confirm);

        player.openInventory(enchantGUI);
    }



    private String getCompatibleItems(Enchantment enchant) {
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


    private boolean canEnchantType(Enchantment enchant, Material... materials) {
        for (Material material : materials) {
            if (enchant.canEnchantItem(new ItemStack(material))) {
                return true;
            }
        }
        return false;
    }
    private String getEnchantName(Enchantment enchant) {
        switch (enchant.getKey().getKey()) {
            // Броня
            case "protection": return "Защита";
            case "fire_protection": return "Огнеупорность";
            case "feather_falling": return "Невесомость";
            case "blast_protection": return "Взрывоустойчивость";
            case "projectile_protection": return "Защита от снарядов";
            case "respiration": return "Подводное дыхание";
            case "aqua_affinity": return "Подводник";
            case "thorns": return "Шипы";
            case "depth_strider": return "Глубинный шаг";
            case "frost_walker": return "Ледяная поступь";
            case "binding_curse": return "Проклятие несъемности";
            case "soul_speed": return "Скорость душ";
            case "swift_sneak": return "Тихий шаг";

            // Оружие
            case "sharpness": return "Острота";
            case "smite": return "Небесная кара";
            case "bane_of_arthropods": return "Гибель членистоногих";
            case "knockback": return "Отбрасывание";
            case "fire_aspect": return "Огненный аспект";
            case "looting": return "Грабеж";
            case "sweeping": return "Разящий клинок";
            case "impaling": return "Пронзание";
            case "loyalty": return "Верность";
            case "riptide": return "Прибой";
            case "channeling": return "Громовержец";

            // Инструменты
            case "efficiency": return "Эффективность";
            case "silk_touch": return "Шелковое касание";
            case "unbreaking": return "Прочность";
            case "fortune": return "Удача";
            case "power": return "Мощь";
            case "punch": return "Отдача";
            case "flame": return "Пламя";
            case "infinity": return "Бесконечность";
            case "luck_of_the_sea": return "Морская удача";
            case "lure": return "Приманка";
            case "mending": return "Починка";
            case "vanishing_curse": return "Проклятие исчезновения";
            case "multishot": return "Залп";
            case "piercing": return "Пробивание";
            case "quick_charge": return "Быстрая перезарядка";

            // Другие
            case "arrow_damage": return "Мощь";
            case "arrow_knockback": return "Отдача";
            case "arrow_fire": return "Пламя";
            case "arrow_infinite": return "Бесконечность";


            default:
                String key = enchant.getKey().getKey();
                return Arrays.stream(key.split("_"))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                        .collect(Collectors.joining(" "));
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
            openCustomItemMenu(player);
            return;
        }


        if (clicked.getType() == Material.ENCHANTED_BOOK) {
            Enchantment enchant = getEnchantFromDisplayName(clicked.getItemMeta().getDisplayName());
            if (enchant == null) return;

            if (event.isLeftClick()) {

                openLevelSelectMenu(player, enchant);
            } else if (event.isRightClick()) {

                selectedEnchants.get(player.getUniqueId()).remove(enchant);
                updateEnchantMenu(player);
            }
        }
    }

    private void openLevelSelectMenu(Player player, Enchantment enchant) {
        Inventory levelGUI = Bukkit.createInventory(null, 27, "§6Выбор уровня: " + getEnchantName(enchant));


        currentEnchantSelection.put(player.getUniqueId(), enchant);

        int lastspot = 0;

        for (int level = 1; level <= maxEnchantLevels.get(enchant); level++) {
            ItemStack levelItem = new ItemStack(Material.PAPER);
            ItemMeta meta = levelItem.getItemMeta();
            meta.setDisplayName("§eУровень " + level);

            lastspot = level;

            ItemStack targetItem = pendingItems.get(player.getUniqueId());
            boolean compatible = enchant.canEnchantItem(targetItem) || ignoreLevelRestrictions;

            meta.setLore(Arrays.asList(
                    (compatible?"§aСовместимо":"§4Не совместимо"),
                    "§aКликните для выбора"
            ));

            if (!compatible) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                levelItem.setType(Material.BARRIER);
            }

            levelItem.setItemMeta(meta);
            levelGUI.addItem(levelItem);
        }


        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName("§cНазад");
        back.setItemMeta(backMeta);
        levelGUI.setItem(18, back);

        ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        fillerMeta.setDisplayName(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH +  "|||||");
        fillerItem.setItemMeta(fillerMeta);

        while(lastspot != levelGUI.getSize()) {
            if (lastspot == 18) lastspot++;
            levelGUI.setItem(lastspot, fillerItem);
            lastspot++;
        }

        player.openInventory(levelGUI);
    }
    @EventHandler
    public void onLevelSelect(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("§6Выбор уровня:")) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null) return;


        if (clicked.getType() == Material.ARROW && event.getSlot() == 18) {
            openEnchantMenu(player);
            return;
        }


        if (clicked.getType() == Material.PAPER || clicked.getType() == Material.BARRIER) {
            try {
                int level = Integer.parseInt(clicked.getItemMeta().getDisplayName().replace("§eУровень ", ""));
                Enchantment enchant = currentEnchantSelection.get(player.getUniqueId());


                ItemStack targetItem = pendingItems.get(player.getUniqueId());
                if (!enchant.canEnchantItem(targetItem)) {
                    if (!ignoreLevelRestrictions) {
                        player.sendMessage("§cЭто зачарование несовместимо с вашим предметом!");
                        return;
                    }
                }


                selectedEnchants.get(player.getUniqueId()).put(enchant, level);
                openEnchantMenu(player);
            } catch (NumberFormatException e) {
                player.sendMessage("§cОшибка выбора уровня!");
            }
        }
    }


    private void applyEnchantsToItem(Player player) {
        UUID uuid = player.getUniqueId();
        ItemStack item = pendingItems.get(uuid);
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;


        meta.getEnchants().keySet().forEach(meta::removeEnchant);


        Map<Enchantment, Integer> enchants = selectedEnchants.get(uuid);
        if (enchants != null) {
            enchants.forEach((enchant, level) -> {
                try {
                    meta.addEnchant(enchant, level, ignoreLevelRestrictions);
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cНе удалось добавить " + getEnchantName(enchant) + " (несовместимо)");
                }
            });
        }

        item.setItemMeta(meta);
        pendingItems.put(uuid, item);
    }


    private void updateEnchantMenu(Player player) {
        Bukkit.getScheduler().runTask(this, () -> {
            if (player.getOpenInventory().getTitle().startsWith("§6Выбор зачарований")) {
                openEnchantMenu(player);
            }
        });
    }


    private Enchantment getEnchantFromDisplayName(String displayName) {
        String enchantName = displayName.substring(2);
        for (Enchantment enchant : Enchantment.values()) {
            if (getEnchantName(enchant).equals(enchantName)) {
                return enchant;
            }
        }
        return null;
    }
}
