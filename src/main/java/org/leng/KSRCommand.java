package org.leng;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class KSRCommand implements CommandExecutor {

    private final KillSoupRewards plugin;

    public KSRCommand(KillSoupRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ksr")) {
            if (args.length == 0) {
                // 显示帮助信息
                showHelp(sender);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                // 重载配置文件
                if (sender.hasPermission("ksr.reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage(plugin.translateColors("§a配置文件已成功重载！"));
                } else {
                    sender.sendMessage(plugin.translateColors("§c你没有权限执行此命令！"));
                }
            } else {
                // 无效参数
                sender.sendMessage(plugin.translateColors("§c无效的参数！"));
            }
            return true;
        }
        return false;
    }

    private void showHelp(CommandSender sender) {
        String version = plugin.getDescription().getVersion();
        String author = plugin.getDescription().getAuthors().get(0);
        sender.sendMessage(plugin.translateColors("§aKillSoupRewards 插件帮助信息"));
        sender.sendMessage(plugin.translateColors("§e作者: §b" + author));
        sender.sendMessage(plugin.translateColors("§e版本: §b" + version));
        sender.sendMessage(plugin.translateColors("§eStarryLands Network @ §b" + author));
        sender.sendMessage(plugin.translateColors("§e命令列表:"));
        sender.sendMessage(plugin.translateColors("§b/ksr &e- 显示帮助信息"));
        sender.sendMessage(plugin.translateColors("§b/ksr reload &e- 重载配置文件"));
    }
}