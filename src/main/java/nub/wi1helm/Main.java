package nub.wi1helm;

import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import nub.wi1helm.content.ContentManager;
import nub.wi1helm.eventnodes.GoalEventNode;
import nub.wi1helm.eventnodes.PlayerEventNode;
import nub.wi1helm.eventnodes.listeners.EntityInteractionListener;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.instances.LoopInstance;
import nub.wi1helm.player.LoopPlayer;
import nub.wi1helm.sidebar.ServerSidebar;

public class Main {

    static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init(new Auth.Online());

        MinecraftServer.getConnectionManager().setPlayerProvider(LoopPlayer::new);

        MinecraftServer.getPacketListenerManager().setPlayListener(ClientInteractEntityPacket.class, EntityInteractionListener::entityInteractionListener);

        LoopInstance instance = LoopInstance.get();
        GoalManager goalManager = GoalManager.get();

        GoalEventNode.init(goalManager);
        PlayerEventNode.init();


        ContentManager contentManager = ContentManager.init(instance, goalManager);



        ServerSidebar.init();

        server.start("0.0.0.0", 25565);

    }
}