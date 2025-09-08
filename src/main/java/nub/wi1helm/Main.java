package nub.wi1helm;

import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import nub.wi1helm.listeners.GlobalListener;
import nub.wi1helm.player.LoopPlayer;
import nub.wi1helm.sidebar.ServerSidebar;

public class Main {

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init(new Auth.Online());

        MinecraftServer.getConnectionManager().setPlayerProvider(LoopPlayer::new);

        ServerManager manager = ServerManager.getManager();
        GlobalListener global = GlobalListener.getInstance();

        ServerSidebar.init();



        server.start("0.0.0.0", 25565);

    }
}