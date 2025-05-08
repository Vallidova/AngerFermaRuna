package org.angelica.angerFermaRuna.config;

import org.angelica.angerFermaRuna.AngerFermaRuna;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {

    private final AngerFermaRuna plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;
    private final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
    
    public MessageManager(AngerFermaRuna plugin) {
        this.plugin = plugin;
        createMessagesFile();
    }
    
    private void createMessagesFile() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            
            messagesConfig.setDefaults(defaultConfig);
            
            String configVersion = messagesConfig.getString("version", "1.0");
            String defaultVersion = defaultConfig.getString("version", "1.0");
            
            if (!configVersion.equals(defaultVersion)) {
                try {
                    messagesConfig.save(messagesFile);
                } catch (IOException e) {
                    plugin.getLogger().severe("Не удалось обновить файл messages.yml!");
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            messagesConfig.setDefaults(YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8)));
        }
    }
    
    public String getPrefix() {
        return formatColor(messagesConfig.getString("prefix", "&a[Руна Огородника] &f"));
    }
    
    public String getRunaSuccessMessage() {
        return getPrefix() + formatColor(messagesConfig.getString("messages.runa.success", 
                "&aВы активировали руну! &fРастения вокруг выросли."));
    }
    
    public String getRunaHarvestMessage(int count) {
        String message = messagesConfig.getString("messages.runa.harvest", 
                "&aВы собрали &f{count} &7ед. урожая!");
        message = message.replace("{count}", String.valueOf(count));
        return getPrefix() + formatColor(message);
    }
    
    public String getRunaCooldownMessage(int seconds) {
        String message = messagesConfig.getString("messages.runa.cooldown", 
                "&cРуна еще не восстановилась! &fОсталось &c{time} &fсек.");
        message = message.replace("{time}", String.valueOf(seconds));
        return getPrefix() + formatColor(message);
    }
    
    public String getNoPlantMessage() {
        return getPrefix() + formatColor(messagesConfig.getString("messages.runa.no-plants", 
                "&6Рядом нет подходящих растений для активации руны."));
    }
    
    public String getNoPermissionMessage() {
        return getPrefix() + formatColor(messagesConfig.getString("messages.runa.no-permission", 
                "&cУ вас нет прав на использование руны."));
    }
    
    public String getCommandGiveSelfMessage() {
        return getPrefix() + formatColor(messagesConfig.getString("messages.command.give-self", 
                "&aВы получили Руну Огородника."));
    }
    
    public String getCommandGiveOtherMessage(String playerName) {
        String message = messagesConfig.getString("messages.command.give-other", 
                "&aВы выдали Руну Огородника игроку &f{player}&a.");
        message = message.replace("{player}", playerName);
        return getPrefix() + formatColor(message);
    }
    
    public String getCommandReceivedMessage(String playerName) {
        String message = messagesConfig.getString("messages.command.received", 
                "&aВы получили Руну Огородника от &f{player}&a.");
        message = message.replace("{player}", playerName);
        return getPrefix() + formatColor(message);
    }
    
    public String getCommandNoPermissionMessage() {
        return getPrefix() + formatColor(messagesConfig.getString("messages.command.no-permission", 
                "&cУ вас нет прав на использование этой команды."));
    }
    
    public String getCommandUsageMessage() {
        return getPrefix() + formatColor(messagesConfig.getString("messages.command.usage", 
                "&6Использование: &f/afr give [player]"));
    }
    
    public String getPlayerOfflineMessage(String playerName) {
        String message = messagesConfig.getString("messages.command.player-offline", 
                "&cИгрок &f{player} &cне в сети.");
        message = message.replace("{player}", playerName);
        return getPrefix() + formatColor(message);
    }
    
    public String getAdminTitleLine1() {
        return formatColor(messagesConfig.getString("messages.admin-title.line1", 
                "&cСоздатель плагина Vallidova"));
    }
    
    public String getAdminTitleLine2() {
        return formatColor(messagesConfig.getString("messages.admin-title.line2", 
                "&fСпасибо за использование. spigotmc.ru"));
    }
    
    public String formatColor(String text) {
        if (text == null) return "";
        
        Matcher matcher = hexPattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String color = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.valueOf("WHITE").toString());
            
            try {
                net.md_5.bungee.api.ChatColor.of("#" + color);
                
                StringBuilder hexColor = new StringBuilder();
                hexColor.append(net.md_5.bungee.api.ChatColor.COLOR_CHAR).append("x");
                
                for (char c : color.toCharArray()) {
                    hexColor.append(net.md_5.bungee.api.ChatColor.COLOR_CHAR).append(c);
                }
                
                matcher.appendReplacement(buffer, hexColor.toString());
            } catch (Exception ignored) {
            }
        }
        
        matcher.appendTail(buffer);
        
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
    
    public void sendMessage(Player player, String message) {
        if (player != null && message != null && !message.isEmpty()) {
            player.sendMessage(message);
        }
    }
} 