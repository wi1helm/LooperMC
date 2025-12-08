package nub.wi1helm.player.sidebar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.timer.TaskSchedule;
import nub.wi1helm.player.GamePlayer; // Assuming LoopPlayer is now GamePlayer

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SidebarManager {

    // Instance map to hold active sidebars
    private final Map<UUID, Sidebar> sidebars = new HashMap<>();

    private static final String ID_DATE = "date";
    private static final String ID_EMPTY_1 = "empty1";
    private static final String ID_LOOPERS = "loopers";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");

    /**
     * Constructor: Starts the periodic update loop immediately.
     */
    public SidebarManager() {
        startUpdateLoop();
    }

    /**
     * Starts the scheduled task that updates the dynamic lines for all viewers.
     */
    private void startUpdateLoop() {
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                if (!(p instanceof GamePlayer player)) continue;

                Sidebar sidebar = sidebars.get(player.getUuid());
                if (sidebar != null) {
                    // Update the date line
                    sidebar.updateLineContent(ID_DATE, Component.text(DATE_FORMAT.format(new Date())).color(NamedTextColor.GRAY));

                    // Update the loopers line
                    Component loopersContent = Component.text("Loopers: ")
                            .append(Component.text(player.getLoopers() + "\uD83D\uDD01").color(NamedTextColor.GOLD));
                    sidebar.updateLineContent(ID_LOOPERS, loopersContent);
                }
            }
            // Run every 20 ticks (1 second) for consistent updates
            return TaskSchedule.tick(20);
        });
    }

    /**
     * Creates and assigns a sidebar for a new player.
     * @param player The GamePlayer to assign the sidebar to.
     */
    public void createSidebar(GamePlayer player) {
        // Only proceed if the player doesn't already have one
        if (sidebars.containsKey(player.getUuid())) return;

        Sidebar sidebar = new Sidebar(MiniMessage.miniMessage().deserialize("<gold><bold>LooperMC</bold></gold>"));

        // --- Line Creation ---
        // Date (Score 10)
        sidebar.createLine(new Sidebar.ScoreboardLine(ID_DATE, Component.empty(), 10, Sidebar.NumberFormat.blank()));
        // Empty 1 (Score 9)
        sidebar.createLine(new Sidebar.ScoreboardLine(ID_EMPTY_1, Component.empty(), 9, Sidebar.NumberFormat.blank()));
        // Loopers (Score 8)
        sidebar.createLine(new Sidebar.ScoreboardLine(ID_LOOPERS, Component.empty(), 8, Sidebar.NumberFormat.blank()));

        // Attach the sidebar to the player
        sidebar.addViewer(player);

        // Cache the sidebar
        sidebars.put(player.getUuid(), sidebar);

        // Run the update logic once immediately to fill the initial content
        updateInitialContent(player);
    }

    /**
     * Removes a player's sidebar from the cache and hides it.
     * @param player The GamePlayer whose sidebar to remove.
     */
    public void removeSidebar(GamePlayer player) {
        Sidebar sidebar = sidebars.remove(player.getUuid());
        if (sidebar != null) {
            sidebar.removeViewer(player);
        }
    }

    /**
     * Runs the update logic once to set the initial content after creation.
     */
    private void updateInitialContent(GamePlayer player) {
        Sidebar sidebar = sidebars.get(player.getUuid());
        if (sidebar == null) return;

        // Initialize Date
        sidebar.updateLineContent(ID_DATE, Component.text(DATE_FORMAT.format(new Date())).color(NamedTextColor.GRAY));

        // Initialize Empty 1
        sidebar.updateLineContent(ID_EMPTY_1, Component.empty());

        // Initialize Loopers
        Component loopersContent = Component.text("Loopers: ")
                .append(Component.text(player.getLoopers() + "\uD83D\uDD01").color(NamedTextColor.GOLD));
        sidebar.updateLineContent(ID_LOOPERS, loopersContent);
    }

    /**
     * Gets the sidebar instance for external use (e.g., if you want to manually set a temporary line).
     * @param uuid The player's UUID.
     * @return The Sidebar instance or null.
     */
    public Sidebar getSidebar(UUID uuid) {
        return sidebars.get(uuid);
    }
}