package org.angelica.angerFermaRuna.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RunaTabCompleter implements TabCompleter {

    private final List<String> subCommands = Arrays.asList("give", "help");
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("afr")) {
            return new ArrayList<>();
        }
        
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String partialCommand = args[0].toLowerCase();
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(partialCommand)) {
                    completions.add(subCommand);
                }
            }
            return completions;
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            String partialPlayerName = args[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialPlayerName))
                    .collect(Collectors.toList());
        }
        
        return completions;
    }
} 