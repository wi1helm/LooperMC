package nub.wi1helm.player;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;

public class LoopPlayer extends Player {


    private Boolean canBreak = false;
    private Double loopers = 0.0;

    public LoopPlayer(PlayerConnection playerConnection, GameProfile gameProfile) {
        super(playerConnection, gameProfile);
    }

    @Override
    public void tick(long time) {
        super.tick(time);

    }

    public Double getLoopers() {
        return loopers;
    }

    public void setLoopers(Double loopers) {
        this.loopers = loopers;
    }

    public Boolean canBreak() {
        return canBreak;
    }
}
