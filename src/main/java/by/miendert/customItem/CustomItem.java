package by.miendert.customItem;

import org.bukkit.plugin.java.JavaPlugin;

String permissionlack;

public final class CustomItem extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger.info("CustomItem is enabled");

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSenser sender, Command cmd, String label, Strin[] args){
        if (cmd.getName().equalsIgnoreCase("item")){
            if (!sender.hasPermissiom("customItem.use")){
                sender.sendMessage(permissionlack);
            }

        }
    }

    public

}
