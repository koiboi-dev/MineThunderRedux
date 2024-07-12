package me.kotos.minethunder;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class EventHandler implements Listener {
    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        new BukkitRunnable(){
            @Override
            public void run() {
                event.getPlayer().sendMessage(ChatColor.GREEN+
                        "========================================\n" +
                        "Installing resource pack in 10 seconds, if you do not get it please use /mt resource\n" +
                        "========================================");
            }
        }.runTaskLater(MineThunder.getInstance(), 5);

        /*new BukkitRunnable(){
            @Override
            public void run() {
                //MineThunder.debugLog("Loaded Player: "+ Objects.requireNonNull(MineThunder.getInstance().getConfig().getString("resource-pack")));
                event.getPlayer().setResourcePack(Objects.requireNonNull(MineThunder.getInstance().getConfig().getString("resource-pack")));
            }
        }.runTaskLater(MineThunder.getInstance(), 200);*/
    }
}
