package by.miendert.customItem.config;


import by.miendert.customItem.CustomItem;

public class PluginConfig {
    private final CustomItem plugin;
    private String permissionlack;
    private Boolean ignoreLevelRestrictions;

    public PluginConfig(CustomItem plugin) {
        this.plugin=plugin;
    }

    public void load(){
        permissionlack = plugin.getConfig().getString("permissionlack", "§cУ вас нет прав на использование этой команды!");
        ignoreLevelRestrictions = plugin.getConfig().getBoolean("ignoreLevelRestrictions", false);
        plugin.saveConfig();
    }

    public String getPermissionlack() {
        return permissionlack;
    }

    public Boolean getIgnoreLevelRestrictions() {
        return ignoreLevelRestrictions;
    }

    public void setIgnoreLevelRestrictions(Boolean value){
        this.ignoreLevelRestrictions=value;
        plugin.saveConfig();
    }
}
