package nub.wi1helm.player;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.network.packet.server.play.DeathCombatEventPacket;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import nub.wi1helm.goals.GoalManager;

public class GamePlayer extends Player {

    private final GoalManager goalManager;

    private boolean canBreak = false;
    private double loopers = 10;
    private int foodRegen = 1;

    private int tickCounter = 0; // track ticks for food regen

    public GamePlayer(PlayerConnection playerConnection, GameProfile gameProfile, GoalManager goalManager) {
        super(playerConnection, gameProfile);
        this.goalManager = goalManager;

        eventNode().addListener(PlayerBlockBreakEvent.class, this::onBlockBreak);
        eventNode().addListener(PlayerBlockPlaceEvent.class, this::onBlockPlace);
    }

    /**
     * Tries to use X food points.
     * @return true if the player had enough, false if not.
     */
    public boolean useFood(int amount) {
        int current = getFood();
        if (current < amount) return false;
        setFood(current - amount);
        return true;
    }

    /**
     * Adds food, but never above 20.
     */
    public void addFood(int amount) {
        setFood(Math.min(20, Math.max(0, getFood() + amount)));
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        foodTick();


    }

    private void foodTick() {
        if (getFood() == 20) return;
        tickCounter++;
        if (tickCounter >= 30) {
            addFood(foodRegen);
            tickCounter = 0; // reset counter
        }
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

