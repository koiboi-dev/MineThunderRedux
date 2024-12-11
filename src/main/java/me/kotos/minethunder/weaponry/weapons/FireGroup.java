package me.kotos.minethunder.weaponry.weapons;

import me.kotos.minethunder.vehicles.settings.ShellSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public record FireGroup(String name, Material material, boolean grouped, int[] weaponIndexes, String[] shells, PlayerInventory groupInv) {

    public FireGroup(String name, Material material, boolean grouped, int[] weaponIndexes, String[] shells) {
        this(name, material, grouped, weaponIndexes, shells, generateGroupInventory(shells));
    }

    private static PlayerInventory generateGroupInventory(String[] shells){
        PlayerInventory inv = (PlayerInventory) Bukkit.createInventory(null, InventoryType.PLAYER);
        ItemStack[] contents = new ItemStack[inv.getContents().length];
        for (String shellId : shells) {
            ShellSettings shell = ShellSettings.getShell(shellId);

        }
        return inv;
    }
}
