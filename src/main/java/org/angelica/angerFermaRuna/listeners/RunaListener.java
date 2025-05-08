package org.angelica.angerFermaRuna.listeners;

import org.angelica.angerFermaRuna.AngerFermaRuna;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RunaListener implements Listener {

    private final AngerFermaRuna plugin;
    private final Random random = new Random();

    private final Map<Material, Material> cropToSeedMap = new HashMap<>();
    
    private final Map<Material, Material> seedToSoilMap = new HashMap<>();

    public RunaListener(AngerFermaRuna plugin) {
        this.plugin = plugin;
        
        cropToSeedMap.put(Material.WHEAT, Material.WHEAT_SEEDS);
        cropToSeedMap.put(Material.CARROTS, Material.CARROT);
        cropToSeedMap.put(Material.POTATOES, Material.POTATO);
        cropToSeedMap.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
        cropToSeedMap.put(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES);
        
        seedToSoilMap.put(Material.WHEAT_SEEDS, Material.FARMLAND);
        seedToSoilMap.put(Material.CARROT, Material.FARMLAND);
        seedToSoilMap.put(Material.POTATO, Material.FARMLAND);
        seedToSoilMap.put(Material.BEETROOT_SEEDS, Material.FARMLAND);
        seedToSoilMap.put(Material.SWEET_BERRIES, Material.GRASS_BLOCK);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (!event.isSneaking()) {
            return;
        }
        
        if (!player.hasPermission("angerfermaruna.use")) {
            return;
        }
        
        ItemStack handItem = player.getInventory().getItemInMainHand();
        
        if (!plugin.getRunaManager().isRunaItem(handItem)) {
            return;
        }
        
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId()) && 
            !player.hasPermission("angerfermaruna.bypass")) {
            int remainingTime = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId());
            plugin.getMessageManager().sendMessage(player, 
                plugin.getMessageManager().getRunaCooldownMessage(remainingTime));
            return;
        }
        
        int radius = plugin.getConfigManager().getRunaRadius();
        
        Map<String, Boolean> enabledCrops = plugin.getConfigManager().getEnabledCrops();
        
        int growthCount = 0;
        
        Location playerLocation = player.getLocation();
        int playerX = playerLocation.getBlockX();
        int playerY = playerLocation.getBlockY();
        int playerZ = playerLocation.getBlockZ();
        
        for (int x = playerX - radius; x <= playerX + radius; x++) {
            for (int y = playerY - radius; y <= playerY + radius; y++) {
                for (int z = playerZ - radius; z <= playerZ + radius; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    BlockData blockData = block.getBlockData();
                    
                    if (!(blockData instanceof Ageable)) {
                        continue;
                    }
                    
                    boolean shouldProcess = false;
                    
                    Material blockType = block.getType();
                    if (blockType == Material.WHEAT && enabledCrops.getOrDefault("wheat", true)) {
                        shouldProcess = true;
                    } else if (blockType == Material.CARROTS && enabledCrops.getOrDefault("carrots", true)) {
                        shouldProcess = true;
                    } else if (blockType == Material.POTATOES && enabledCrops.getOrDefault("potatoes", true)) {
                        shouldProcess = true;
                    } else if (blockType == Material.BEETROOTS && enabledCrops.getOrDefault("beetroots", true)) {
                        shouldProcess = true;
                    } else if (blockType == Material.SWEET_BERRY_BUSH && enabledCrops.getOrDefault("sweet_berries", true)) {
                        shouldProcess = true;
                    }
                    
                    if (shouldProcess) {
                        Ageable ageable = (Ageable) blockData;
                        
                        if (ageable.getAge() < ageable.getMaximumAge()) {
                            growthCount++;
                            ageable.setAge(ageable.getMaximumAge());
                            block.setBlockData(ageable);
                        }
                    }
                }
            }
        }
        
        if (growthCount > 0) {
            plugin.getCooldownManager().setCooldown(player.getUniqueId());
            
            plugin.getMessageManager().sendMessage(player, plugin.getMessageManager().getRunaSuccessMessage());
        } else {
            plugin.getMessageManager().sendMessage(player, plugin.getMessageManager().getNoPlantMessage());
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockData blockData = block.getBlockData();
        
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (!plugin.getRunaManager().isRunaItem(handItem)) {
            return;
        }
        
        if (!(blockData instanceof Ageable)) {
            return;
        }
        
        Material blockType = block.getType();
        boolean isCrop = blockType == Material.WHEAT || 
                          blockType == Material.CARROTS || 
                          blockType == Material.POTATOES || 
                          blockType == Material.BEETROOTS || 
                          blockType == Material.SWEET_BERRY_BUSH;
        
        if (!isCrop) {
            return;
        }
        
        if (!plugin.getConfigManager().isReplantingEnabled()) {
            return;
        }
        
        Material seedType = cropToSeedMap.get(blockType);
        
        if (seedType != null) {
            Block soilBlock = block.getWorld().getBlockAt(block.getX(), block.getY() - 1, block.getZ());
            Material requiredSoil = seedToSoilMap.get(seedType);
            
            if (soilBlock.getType() == requiredSoil) {
                event.setDropItems(false);
                
                Ageable ageable = (Ageable) blockData;
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    dropCropItems(block, player);
                }
                
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    block.setType(blockType);
                    Ageable newAgeable = (Ageable) block.getBlockData();
                    newAgeable.setAge(0);
                    block.setBlockData(newAgeable);
                    
                    if (plugin.getConfigManager().showReplantParticles()) {
                        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);
                        block.getWorld().spawnParticle(Particle.COMPOSTER, blockLocation, 10, 0.3, 0.3, 0.3, 0.1);
                    }
                    
                    if (plugin.getConfigManager().playReplantSound()) {
                        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);
                        block.getWorld().playSound(blockLocation, Sound.ITEM_CROP_PLANT, 1.0f, 1.0f);
                    }
                });
            }
        } else if (blockType == Material.SWEET_BERRY_BUSH) {
            event.setDropItems(false);
            
            Ageable ageable = (Ageable) blockData;
            if (ageable.getAge() >= 2) {
                Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);
                int berryCount = 1 + random.nextInt(3);
                player.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.SWEET_BERRIES, berryCount));
            }
            
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                block.setType(blockType);
                Ageable newAgeable = (Ageable) block.getBlockData();
                newAgeable.setAge(1);
                block.setBlockData(newAgeable);
                
                if (plugin.getConfigManager().showReplantParticles()) {
                    Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);
                    block.getWorld().spawnParticle(Particle.COMPOSTER, blockLocation, 10, 0.3, 0.3, 0.3, 0.1);
                }
                
                if (plugin.getConfigManager().playReplantSound()) {
                    Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);
                    block.getWorld().playSound(blockLocation, Sound.ITEM_CROP_PLANT, 1.0f, 1.0f);
                }
            });
        }
    }
    
    private void dropCropItems(Block block, Player player) {
        Material blockType = block.getType();
        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5);
        
        switch (blockType) {
            case WHEAT:
                player.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.WHEAT, 1));
                int seedCount = random.nextInt(4);
                if (seedCount > 0) {
                    player.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.WHEAT_SEEDS, seedCount));
                }
                break;
                
            case CARROTS:
                int carrotCount = 1 + random.nextInt(4);
                player.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.CARROT, carrotCount));
                break;
                
            case POTATOES:
                int potatoCount = 1 + random.nextInt(4);
                player.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.POTATO, potatoCount));
                
                if (random.nextInt(100) < 2) {
                    player.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.POISONOUS_POTATO, 1));
                }
                break;
                
            case BEETROOTS:
                player.getWorld().dropItemNaturally(blockLocation, new ItemStack(Material.BEETROOT, 1));
                int beetrootSeedCount = random.nextInt(4);
                if (beetrootSeedCount > 0) {
                    player.getWorld().dropItemNaturally(blockLocation, 
                            new ItemStack(Material.BEETROOT_SEEDS, beetrootSeedCount));
                }
                break;
                
            default:
                break;
        }
    }
} 