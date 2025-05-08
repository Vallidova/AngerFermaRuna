package org.angelica.angerFermaRuna;

import org.angelica.angerFermaRuna.commands.RunaCommand;
import org.angelica.angerFermaRuna.commands.RunaTabCompleter;
import org.angelica.angerFermaRuna.config.ConfigManager;
import org.angelica.angerFermaRuna.config.MessageManager;
import org.angelica.angerFermaRuna.listeners.RunaListener;
import org.angelica.angerFermaRuna.managers.CooldownManager;
import org.angelica.angerFermaRuna.managers.RunaManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AngerFermaRuna extends JavaPlugin {
    
    private static AngerFermaRuna instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private RunaManager runaManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.runaManager = new RunaManager(this);
        this.cooldownManager = new CooldownManager(this);
        
        RunaCommand runaCommand = new RunaCommand(this);
        getCommand("afr").setExecutor(runaCommand);
        getCommand("afr").setTabCompleter(new RunaTabCompleter());
        
        Bukkit.getPluginManager().registerEvents(new RunaListener(this), this);
        
        getLogger().info("Плагин успешно запущен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин успешно выключен!");
    }
    
    public static AngerFermaRuna getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
    
    public RunaManager getRunaManager() {
        return runaManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
