package org.leng;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class KillSoupRewards extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private File configFile;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        // 加载或创建配置文件
        loadConfig();

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(this, this);

        // 注册命令
        getCommand("ksr").setExecutor(new KSRCommand(this));

        // 打印启用信息
        getLogger().info(translateColors("§aKillSoupRewards 已启用！"));
        getLogger().info(translateColors("§eStarryLands Network @ §bshazi_awa"));

    }

    @Override
    public void onDisable() {
        getLogger().info(translateColors("§cKillSoupRewards 已禁用"));
        getLogger().info(translateColors("§eStarryLands Network @ §bshazi_awa"));
    }

    private void loadConfig() {
        configFile = new File(getDataFolder(), "config.yml");

        // 如果配置文件不存在，则创建默认配置
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        // 检查是否有击杀者且不是自杀
        if (killer == null || killer == victim) {
            return;
        }

        UUID killerId = killer.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldownTime = config.getLong("cooldown", 10) * 1000; // 默认10秒冷却

        // 检查冷却时间
        if (cooldowns.containsKey(killerId)) {
            long lastKillTime = cooldowns.get(killerId);

            if (currentTime - lastKillTime < cooldownTime) {
                long remaining = (cooldownTime - (currentTime - lastKillTime)) / 1000;
                String cooldownMsg = config.getString("messages.cooldown", "&c请等待 &e%time%秒 &c后再获取奖励")
                        .replace("%time%", String.valueOf(remaining));
                killer.sendMessage(translateColors(cooldownMsg));
                return;
            }
        }

        // 更新冷却时间
        cooldowns.put(killerId, currentTime);

        // 获取配置中的奖励物品
        String rewardType = config.getString("reward.type", "MUSHROOM_SOUP");
        int rewardAmount = config.getInt("reward.amount", 1);

        try {
            Material material = Material.valueOf(rewardType.toUpperCase());
            ItemStack reward = new ItemStack(material, 1); // 蘑菇汤不能堆叠，所以数量为1

            // 如果奖励数量大于1，则创建多个蘑菇汤
            for (int i = 0; i < rewardAmount; i++) {
                killer.getInventory().addItem(reward.clone());
            }

            // 发送消息
            String rewardMsg = config.getString("messages.reward", "&a你获得了击杀奖励: &e%item% x%amount%!")
                    .replace("%item%", material.name().toLowerCase().replace("_", " "))
                    .replace("%amount%", String.valueOf(rewardAmount));

            killer.sendMessage(translateColors(rewardMsg));

        } catch (IllegalArgumentException e) {
            getLogger().warning(translateColors("§c配置错误: 无效的物品类型 '" + rewardType + "'"));
        }
    }

    /**
     * 将 & 替换为 §，以解析颜色代码
     * @param input 输入字符串
     * @return 替换后的字符串
     */
    public String translateColors(String input) {
        return input.replace("&", "§");
    }
}