package org.angelica.angerFermaRuna.managers;

import org.angelica.angerFermaRuna.AngerFermaRuna;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunaManager {

    private final AngerFermaRuna plugin;
    private final NamespacedKey runaKey;
    private final Map<Player, Boolean> receivedRuna = new HashMap<>();

    public RunaManager(AngerFermaRuna plugin) {
        this.plugin = plugin;
        this.runaKey = new NamespacedKey(plugin, "ferma_runa");
    }

    public ItemStack createRunaItem() {
        String materialName = plugin.getConfigManager().getRunaMaterial();
        Material material;
        
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Неверный материал для руны: " + materialName + ". Используется LIME_DYE.");
            material = Material.LIME_DYE;
        }
        
        ItemStack runaItem = new ItemStack(material, 1);
        ItemMeta meta = runaItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(plugin.getMessageManager().formatColor(plugin.getConfigManager().getRunaName()));
            
            List<String> formattedLore = new ArrayList<>();
            for (String loreLine : plugin.getConfigManager().getRunaLore()) {
                formattedLore.add(plugin.getMessageManager().formatColor(loreLine));
            }
            meta.setLore(formattedLore);
            
            if (plugin.getConfigManager().isRunaGlowing()) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(runaKey, PersistentDataType.BYTE, (byte) 1);
            
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            
            runaItem.setItemMeta(meta);
        }
        
        return runaItem;
    }
    
    public boolean isRunaItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(runaKey, PersistentDataType.BYTE);
    }
    
    public boolean giveRuna(Player player, Player fromPlayer) {
        if (player == null) {
            return false;
        }
        
        ItemStack runaItem = createRunaItem();
        
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), runaItem);
        } else {
            player.getInventory().addItem(runaItem);
        }
        
        if (fromPlayer != null) {
            plugin.getMessageManager().sendMessage(fromPlayer, 
                    plugin.getMessageManager().getCommandGiveOtherMessage(player.getName()));
            plugin.getMessageManager().sendMessage(player, 
                    plugin.getMessageManager().getCommandReceivedMessage(fromPlayer.getName()));
        } else {
            plugin.getMessageManager().sendMessage(player, 
                    plugin.getMessageManager().getCommandGiveSelfMessage());
        }
        
        if (plugin.getConfigManager().isAdminNotificationsEnabled() && 
            plugin.getConfigManager().isAdminTitleEnabled() &&
            !hasReceivedRuna(player) &&
            (player.isOp() || player.hasPermission("*"))) {
            
            int fadeIn = plugin.getConfigManager().getTitleFadeIn();
            int stay = plugin.getConfigManager().getTitleStay();
            int fadeOut = plugin.getConfigManager().getTitleFadeOut();
            
            String line1 = plugin.getMessageManager().getAdminTitleLine1();
            String line2 = plugin.getMessageManager().getAdminTitleLine2();
            
            player.sendTitle(line1, line2, fadeIn, stay, fadeOut);
            
            setReceivedRuna(player);
        }
        
        return true;
    }
    
    public boolean hasReceivedRuna(Player player) {
        return receivedRuna.getOrDefault(player, false);
    }
    
    public void setReceivedRuna(Player player) {
        receivedRuna.put(player, true);
    }
} 