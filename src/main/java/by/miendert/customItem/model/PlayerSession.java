package by.miendert.customItem.model;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerSession {
    public enum InputState {none,waiting_for_name,waiting_for_lore,}
    private final CustomItemData itemData;
    private final ItemStack originalItem;
    private InputState inputState = InputState.none;
    private Enchantment currentEnchantSelection = null;
    private ItemStack selectedDye = null;

    public PlayerSession(ItemStack item){
        originalItem = item.clone();
        itemData = CustomItemData.fromItemStack(item);
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
}
