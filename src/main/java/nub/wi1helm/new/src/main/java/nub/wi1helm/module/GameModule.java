package nub.wi1helm.module;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

public interface GameModule {

    @NotNull Tag<?> getModuleTag();

    void registerListeners(EventNode<@NotNull Event> node);
}
