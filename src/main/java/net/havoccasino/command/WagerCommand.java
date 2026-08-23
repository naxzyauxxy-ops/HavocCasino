package net.havoccasino.command;

import net.havoccasino.HavocCasino;
import net.havoccasino.gui.CasinoMenuGui;
import net.havoccasino.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * {@code /wager} opens the casino menu; {@code /wager messages [on|off]} toggles
 * this player's HavocCasino alerts. Used by the server settings dialog
 * ("Wager Alerts" -> command "wager messages").
 */
public final class WagerCommand implements CommandExecutor {

    private final HavocCasino plugin;

    public WagerCommand(HavocCasino plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Msg.send(sender, "<red>Only players can use this.");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("messages")) {
            if (!player.hasPermission("havoccasino.messages")) {
                Msg.force(player, "<red>You don't have permission to use this.");
                return true;
            }
            if (args.length >= 2) {
                String value = args[1].toLowerCase();
                if (value.equals("on") || value.equals("enable")) {
                    plugin.playerSettings().setMessagesEnabled(player.getUniqueId(), true);
                    plugin.playerSettings().save();
                    plugin.messages().force(player, "settings.enabled");
                    return true;
                }
                if (value.equals("off") || value.equals("disable")) {
                    plugin.playerSettings().setMessagesEnabled(player.getUniqueId(), false);
                    plugin.playerSettings().save();
                    plugin.messages().force(player, "settings.disabled");
                    return true;
                }
            }
            boolean nowEnabled = plugin.playerSettings().toggleMessages(player.getUniqueId());
            plugin.playerSettings().save();
            plugin.messages().force(player, nowEnabled ? "settings.enabled" : "settings.disabled");
            return true;
        }

        if (!player.hasPermission("havoccasino.menu")) {
            Msg.send(player, "<gray>Usage: <white>/wager messages");
            return true;
        }
        new CasinoMenuGui(plugin, player).open();
        return true;
    }
}
