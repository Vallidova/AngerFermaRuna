package org.angelica.angerFermaRuna.commands;

import org.angelica.angerFermaRuna.AngerFermaRuna;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RunaCommand implements CommandExecutor {

    private final AngerFermaRuna plugin;

    public RunaCommand(AngerFermaRuna plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give":
                return handleGiveCommand(sender, args);
            case "help":
                sendHelpMessage(sender);
                return true;
            default:
                sendUnknownCommandMessage(sender);
                return true;
        }
    }

    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("angerfermaruna.give")) {
            if (sender instanceof Player) {
                plugin.getMessageManager().sendMessage((Player) sender, 
                    plugin.getMessageManager().getCommandNoPermissionMessage());
            } else {
                sender.sendMessage("You don't have permission to use this command");
            }
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command must be used by a player or specify a player name");
                return true;
            }

            Player player = (Player) sender;
            plugin.getRunaManager().giveRuna(player, null);
            return true;
        }

        String targetName = args[1];
        Player targetPlayer = Bukkit.getPlayerExact(targetName);

        if (targetPlayer == null) {
            if (sender instanceof Player) {
                plugin.getMessageManager().sendMessage((Player) sender, 
                    plugin.getMessageManager().getPlayerOfflineMessage(targetName));
            } else {
                sender.sendMessage("Player " + targetName + " is not online");
            }
            return true;
        }

        if (sender instanceof Player) {
            plugin.getRunaManager().giveRuna(targetPlayer, (Player) sender);
        } else {
            plugin.getRunaManager().giveRuna(targetPlayer, null);
            sender.sendMessage("Farmer's Rune given to player " + targetPlayer.getName());
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§a▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
        sender.sendMessage("§a⊰ §2AngerFermaRuna §a- §fРуна Огородника §a⊱");
        sender.sendMessage("§a▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
        sender.sendMessage("§a❘ §f/afr give §8- §7Выдать себе руну");
        sender.sendMessage("§a❘ §f/afr give [игрок] §8- §7Выдать руну игроку");
        sender.sendMessage("§a❘ §f/afr help §8- §7Показать эту справку");
        sender.sendMessage("§a▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃▃");
    }

    private void sendUnknownCommandMessage(CommandSender sender) {
        sender.sendMessage("§c✘ Неизвестная команда. Используйте §f/afr help §cдля списка команд");
    }
} 