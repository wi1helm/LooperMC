package nub.wi1helm.sidebar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.player.LoopPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerSidebar {
    private static final Map<UUID, Sidebar> sidebarCache = new HashMap<>();

    public static void init() {
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                if (!(p instanceof LoopPlayer player)) continue;

                // Get or create the sidebar for the player
                Sidebar sidebar = sidebarCache.get(player.getUuid());
                if (sidebar == null) {
                    // Create new sidebar if it doesn't exist
                    sidebar = new Sidebar(MiniMessage.miniMessage().deserialize("<gold><bold>LooperMC</bold></gold>"));

                    sidebar.createLine(new Sidebar.ScoreboardLine("date", Component.empty(), 10, Sidebar.NumberFormat.blank()));
                    sidebar.createLine(new Sidebar.ScoreboardLine("empty1", Component.empty(), 9, Sidebar.NumberFormat.blank()));
                    sidebar.createLine(new Sidebar.ScoreboardLine("loopers", Component.empty(), 8, Sidebar.NumberFormat.blank()));
                    // Attach the sidebar to the player
                    sidebar.addViewer(player);

                    // Cache the sidebar for future updates#ff75aa
                    sidebarCache.put(player.getUuid(), sidebar);
                } else {
                    sidebar.updateLineContent("date", Component.text(new SimpleDateFormat("MM/dd/yy").format(new Date())).color(NamedTextColor.GRAY));
                    sidebar.updateLineContent("loopers", Component.text("Loopers: ").append(Component.text(player.getLoopers()).color(NamedTextColor.AQUA)));
                }
            }
            return TaskSchedule.tick(1); // Run every tick for smoother animation
        });
    }
    public static Map<UUID, Sidebar> getSidebarCache() {
        return sidebarCache;
    }
}