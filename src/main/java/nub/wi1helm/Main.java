package nub.wi1helm;

import net.kyori.adventure.text.Component;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerStartSneakingEvent;
import nub.wi1helm.listeners.GlobalListener;
import nub.wi1helm.player.LoopPlayer;
import nub.wi1helm.sidebar.ServerSidebar;
import nub.wi1helm.tasks.TaskManager;
import nub.wi1helm.tasks.mail.Mailbox;

public class Main {

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init(new Auth.Online());

        MinecraftServer.getConnectionManager().setPlayerProvider(LoopPlayer::new);

        ServerManager manager = ServerManager.getManager();
        GlobalListener global = GlobalListener.getInstance();

        ServerSidebar.init();

        TaskManager.getInstance();

        server.start("0.0.0.0", 25565);

    }
}