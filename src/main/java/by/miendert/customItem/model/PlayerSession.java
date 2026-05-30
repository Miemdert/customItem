package by.miendert.customItem.model;

import by.miendert.customItem.CustomItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class PlayerSession {
    private final CustomItem plugin;
    public enum InputState {none,waiting_for_name,waiting_for_lore,}
    private final CustomItemData itemData;
    private final ItemStack originalItem;
    private InputState inputState = InputState.none;
    private Enchantment currentEnchantSelection = null;
    private ItemStack selectedDye = null;
    private boolean applyingColor = false;

    public PlayerSession(ItemStack item, CustomItem plugin){
        this.plugin = plugin;
        originalItem = item.clone();
        itemData = CustomItemData.fromItemStack(item, plugin.getPluginConfig());

    }

    public ItemStack getItem(){
        return itemData.getItemStack();
    }

    public CustomItemData getItemData() {
        return itemData;
    }

    public Enchantment getCurrentEnchantSelection() {
        return currentEnchantSelection;
    }

    public InputState getInputState() {
        return inputState;
    }

    public ItemStack getOriginalItem() {
        return originalItem;
    }

    public ItemStack getSelectedDye() {
        return selectedDye;
    }

    public void setCurrentEnchantSelection(Enchantment currentEnchantSelection) {
        this.currentEnchantSelection = currentEnchantSelection;
    }

    public void setInputState(InputState inputState) {
        this.inputState = inputState;
    }

    public void setSelectedDye(ItemStack selectedDye) {
        this.selectedDye = selectedDye;
    }

    public void clearSelectedDye(){
        this.selectedDye=null;
    }

    public Boolean isDyeSelected(){
        return !(selectedDye==null);
    }

    public Boolean isInputPending(){
        return !(inputState==InputState.none);
    }

    public void clearInputState(){
        inputState=InputState.none;
    }

    public void setApplyingColor(boolean applyingColor) {
        this.applyingColor = applyingColor;
    }

    public boolean isApplyingColor() {
        return applyingColor;
    }
}
