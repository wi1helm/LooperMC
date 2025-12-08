package nub.wi1helm.player;

import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import nub.wi1helm.goals.GoalManager;

public class GamePlayer extends Player {

    private final GoalManager goalManager;

    private boolean canBreak = false;
    private double loopers = 10;


    public GamePlayer(PlayerConnection playerConnection, GameProfile gameProfile, GoalManager goalManager) {
        super(playerConnection, gameProfile);
        this.goalManager = goalManager;

        eventNode().addListener(PlayerBlockBreakEvent.class, this::onBlockBreak);
        eventNode().addListener(PlayerBlockPlaceEvent.class, this::onBlockPlace);
    }

    public double getLoopers() {
        return loopers;
    }

    public void setLoopers(double loopers) {
        this.loopers = loopers;
    }

    private void onBlockBreak(PlayerBlockBreakEvent event) {
        event.setCancelled(!this.canBreak);
    }
    private void onBlockPlace(PlayerBlockPlaceEvent event) {
        event.setCancelled(true);
    }
}
