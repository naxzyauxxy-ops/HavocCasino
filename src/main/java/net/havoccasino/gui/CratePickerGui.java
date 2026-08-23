package net.havoccasino.gui;

import net.havoccasino.HavocCasino;
import net.havoccasino.game.Crate;
import net.havoccasino.util.Msg;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lists configured crates; clicking one opens it (charging its cost).
 */
public final class CratePickerGui implements Clickable {

    private static final int SIZE = 27;
    private static final int BACK = 22;
    private static final int[] BUTTONS = {10, 11, 12, 13, 14, 15, 16};

    private final HavocCasino plugin;
    private final Player player;
    private final Inventory inventory;
    private final Map<Integer, String> crateIds = new HashMap<>();

    public CratePickerGui(HavocCasino plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        MenuHolder holder = new MenuHolder();
        this.inventory = Bukkit.createInventory(holder, SIZE, plugin.messages().line(player, "menu.crates-title"));
        holder.setInventory(inventory);
        holder.setClickable(this);
        render();
    }

    public void open() {
        player.openInventory(inventory);
    }

    private void render() {
        ItemStack filler = icon(Material.BLACK_STAINED_GLASS_PANE, "<gray>", null);
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }
        int i = 0;
        for (Crate crate : plugin.crateManager().all()) {
            if (i >= BUTTONS.length) {
                break;
            }
            crateIds.put(BUTTONS[i], crate.id());
            inventory.setItem(BUTTONS[i], icon(crate.icon(), crate.display(),
                    List.of("<gray>Cost: <yellow>" + plugin.currencyService().format(crate.cost()),
                            "<gray>Click to open.")));
            i++;
        }
        inventory.setItem(BACK, icon(Material.BARRIER, "<red>Back", List.of("<gray>Return to the menu.")));
    }

    @Override
    public void handleClick(int slot) {
        if (slot == BACK) {
            new CasinoMenuGui(plugin, player).open();
            return;
        }
        String id = crateIds.get(slot);
        if (id == null) {
            return;
        }
        Crate crate = plugin.crateManager().get(id);
        if (crate == null) {
            return;
        }
        player.closeInventory();
        plugin.games().openCrate(player, crate);
    }

    private ItemStack icon(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Msg.item(name));
            if (lore != null) {
                List<Component> lines = new ArrayList<>();
                for (String line : lore) {
                    lines.add(Msg.item(line));
                }
                meta.lore(lines);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
