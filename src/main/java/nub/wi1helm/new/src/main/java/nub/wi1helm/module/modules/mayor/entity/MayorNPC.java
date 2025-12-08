package nub.wi1helm.module.modules.mayor.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.npc.NPC;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.item.ItemManager;
import nub.wi1helm.module.modules.mayor.interactions.MayorNPCInteraction;
import org.jetbrains.annotations.NotNull;

/**
 * Represents Mayor Goodwin.
 * This class handles the NPC's identity and spawning, while
 * {@link MayorNPCInteraction} manages the complex dialogue and shop logic.
 */
public class MayorNPC extends NPC {

    private static final PlayerSkin SKIN = new PlayerSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTYyMTkyNjMzOTIxNywKICAicHJvZmlsZUlkIiA6ICJjNjc3MGJjZWMzZjE0ODA3ODc4MTU0NWRhMGFmMDI1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IzYjNhMzM2YTc3Y2FiOGI3NDQ2ODY4Y2VhMjkyMTkwZTc3Mjk4ZjJiNjBlODM1YjkwNTY4NjI1MDJkNjU0NjAiCiAgICB9CiAgfQp9",
            "gbIH16YWg05ykuKXq1Whju1CN68gDa3C3TeCr8aNZ+hHsl6aw+sGUJC9a1brV1IzOFhXKyjxe6AcjVQM6/7wltkGCZ0HNh0BiOglWXK2pbv5xZF6v5pM3YPaDNQ37gxEDsnxw8XJkVGHFNKaFz6dNIn/sjUU0uPiwlv1772zX2EN0FcyWB+YgpKDjZ4x0xs+DytxrFQat3drEIHnEWzq4bGGoDomN8yQccPhqB1rtDGp3TuXvzSfjwDvzzo7aVuFk6qfILW6DctAP+w6bfFE2OpgQoZrAX5/lcgjPGcMbdDi9eWHHh4BFuTJ68ZYzXyXK4j1NHRxtkCPajgdfTKmMzKhyZ5iRcASREaU7wAqErSY2ZBLx99kjfilI8qJ/wDxlnrFrYEbT0iRbLWLjThtO7X0e8+KSAe/1TKp4EdmhQ9YBd3OYTmW7tmDyRU15ukeKHUOA2njIXXDg58Rq6n2+6drb0mPJ6e69+ibu2Gg+Ou1tOxsS5XhFpnquAHCipSbqOBkyZcttR0KCxt23KENVG+66jhCjWTseXEh/yJhUhMvL18kkDQeO3OED0JRh/IG8M/lQEk1V8qT4FTTX3tATsLPbgGQtU5RlvKE4pTusN5v4ZFq+wVEXuZyNiwKgxNt4UUYsifRJ27wcwg/0YH8O7EAnJgUnGWAlurMEaFALow="
    );

    private final MayorNPCInteraction interaction;

    public MayorNPC(GoalManager goalManager, ItemManager itemManager) {
        super(SKIN, Component.text("Mayor Goodwin"));

        // Delegate interaction setup to the dedicated handler class
        this.interaction = new MayorNPCInteraction(this, goalManager, itemManager);
    }

    /**
     * Overridden to prevent setup here, as it's handled by the interaction class.
     */
    @Override
    public void setupInteractions() {
        // Handled by MayorNPCInteraction
        this.interaction.setup();
    }

    @Override
    public void spawn(@NotNull EntityManager entityManager, @NotNull Instance instance, @NotNull Pos position) {
        super.spawn(entityManager, instance, position);
        enableLookAtPlayers(getMinestomEntities(), position.yaw(), position.pitch(), 5F);
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {
        // Implementation from old code - left empty
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:mayor:npc:mayor");
    }
}