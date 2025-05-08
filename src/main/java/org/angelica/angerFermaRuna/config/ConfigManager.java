package org.angelica.angerFermaRuna.config;

import org.angelica.angerFermaRuna.AngerFermaRuna;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final AngerFermaRuna plugin;
    private FileConfiguration config;
    
    public ConfigManager(AngerFermaRuna plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public String getRunaName() {
        return config.getString("runa.name", "&aРуна Огородника");
    }
    
    public String getRunaMaterial() {
        return config.getString("runa.material", "LIME_DYE");
    }
    
    public boolean isRunaGlowing() {
        return config.getBoolean("runa.glow", true);
    }
    
    public int getRunaCooldown() {
        return config.getInt("runa.cooldown", 10);
    }
    
    public int getRunaRadius() {
        return config.getInt("runa.radius", 5);
    }
    
    public int getMaxGrowthStages() {
        return config.getInt("runa.max-growth-stages", 7);
    }
    
    public List<String> getRunaLore() {
        return config.getStringList("runa.lore");
    }
    
    public Map<String, Boolean> getEnabledCrops() {
        Map<String, Boolean> crops = new HashMap<>();
        crops.put("wheat", config.getBoolean("crops.wheat", true));
        crops.put("carrots", config.getBoolean("crops.carrots", true));
        crops.put("potatoes", config.getBoolean("crops.potatoes", true));
        crops.put("beetroots", config.getBoolean("crops.beetroots", true));
        crops.put("sweet_berries", config.getBoolean("crops.sweet_berries", true));
        return crops;
    }
    
    public boolean isReplantingEnabled() {
        return config.getBoolean("replanting.enabled", true);
    }
    
    public boolean showReplantParticles() {
        return config.getBoolean("replanting.show-particles", true);
    }
    
    public boolean playReplantSound() {
        return config.getBoolean("replanting.play-sound", true);
    }
    
    public boolean isAdminNotificationsEnabled() {
        return config.getBoolean("admin-notifications.enabled", true);
    }
    
    public boolean isAdminTitleEnabled() {
        return config.getBoolean("admin-notifications.title.enabled", true);
    }
    
    public int getTitleFadeIn() {
        return config.getInt("admin-notifications.title.fade-in", 10);
    }
    
    public int getTitleStay() {
        return config.getInt("admin-notifications.title.stay", 70);
    }
    
    public int getTitleFadeOut() {
        return config.getInt("admin-notifications.title.fade-out", 20);
    }
} 