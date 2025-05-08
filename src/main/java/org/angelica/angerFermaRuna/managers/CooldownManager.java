package org.angelica.angerFermaRuna.managers;

import org.angelica.angerFermaRuna.AngerFermaRuna;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    
    private final AngerFermaRuna plugin;
    private final Map<UUID, Long> cooldowns;
    
    public CooldownManager(AngerFermaRuna plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }
    
    public boolean isOnCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return false;
        }
        
        long lastUsage = cooldowns.get(playerUUID);
        long currentTime = System.currentTimeMillis();
        int cooldownTime = plugin.getConfigManager().getRunaCooldown() * 1000;
        
        return (currentTime - lastUsage) < cooldownTime;
    }
    
    public void setCooldown(UUID playerUUID) {
        cooldowns.put(playerUUID, System.currentTimeMillis());
    }
    
    public int getRemainingCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return 0;
        }
        
        long lastUsage = cooldowns.get(playerUUID);
        long currentTime = System.currentTimeMillis();
        int cooldownTime = plugin.getConfigManager().getRunaCooldown() * 1000;
        
        long timeElapsed = currentTime - lastUsage;
        
        if (timeElapsed >= cooldownTime) {
            return 0;
        }
        
        return (int) Math.ceil((cooldownTime - timeElapsed) / 1000.0);
    }
    
    public void removeCooldown(UUID playerUUID) {
        cooldowns.remove(playerUUID);
    }
    
    public void clearAllCooldowns() {
        cooldowns.clear();
    }
} 