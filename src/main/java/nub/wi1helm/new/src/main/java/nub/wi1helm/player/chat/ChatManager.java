package nub.wi1helm.player.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import nub.wi1helm.core.GameService;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ChatManager implements GameService {

    private final EventNode<@NotNull Event> node = EventNode.all("chatmanager");

    public ChatManager() {
        onJoin();
        onDisconnect();
        onChat();
    }

    private void onJoin() {
        node.addListener(PlayerSpawnEvent.class, event -> {
           if (!event.isFirstSpawn()) return;

           Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
            players.forEach(player -> {

                Component joinMessage = MiniMessage.miniMessage().deserialize("<green>[+] " + event.getPlayer().getUsername() + "</green>");

                player.sendMessage(joinMessage);
            });

        });
    }

    private void onDisconnect() {
        node.addListener(PlayerDisconnectEvent.class, event -> {
            Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
            players.forEach(player -> {

                Component joinMessage = MiniMessage.miniMessage().deserialize("<red>[-] " + event.getPlayer().getUsername() + "</red>");

                player.sendMessage(joinMessage);
            });

        });
    }

    private void onChat() {
        node.addListener(PlayerChatEvent.class, event -> {
           event.setCancelled(true);
           event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<gray>Chatting is disabled on this server!</gray>"));
        });
    }

    @Override
    public void registerListeners() {
        MinecraftServer.getGlobalEventHandler().addChild(this.node);
    }
}
