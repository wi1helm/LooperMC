package nub.wi1helm.module.modules.chores.laundry;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.npc.NPC;
import nub.wi1helm.goals.GoalManager;
import nub.wi1helm.goals.ServerGoals;
import org.jetbrains.annotations.NotNull;

public class AlexNPC extends NPC {

    private static final PlayerSkin SKIN_YES_CLOTHES = new PlayerSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc1NjQwMTAwMjYyNCwKICAicHJvZmlsZUlkIiA6ICJmMzNlZGMyNTRmNDk0NWY2YTg5ZjFjM2JhZmNkZjIwNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJGVV9CYWJ5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E2NTA4N2NlM2ViNzIxMmZlMzMwZDEyNjNkNzJjMjI0OTU5N2ZhMTZkMWY5M2MxZmQ2OWRjNjIyYmZhNmRhMDAiCiAgICB9CiAgfQp9",
            "Fvxg6+b5GpEz8Z+e2a5D/b/TIx/yOeNeOkoW2rIz65JyfLUgPdnLgyUAtbHFZAwXipHBdKbbaofla+BhSeFIYCLMhFUVIXOHHj02JOpcID5a/PLVxJkVQfKoXzVe7au7+NoDJ+XB2pfNFbvs4rJAE5b4+trBzM1iHsKW5s78w6uuwtOyamYUMzYvyLJCprZn6fFJU9MruxxoQWt4VCVnsek+htzq03TSmsN0/mtPzG0yxNf3rVmVU1j0WVMDe+Ok7LbJN8z+KyHC35fslZ+UhSIEcxU/hQfcU1q8W0bB9U802kzNLa8MUvfM4Kv+5FgKPnYAkjka5XSXjxj028rw578EQFTn1joghr7M5jin0ntdQqIHJqYYBQlq3NwGjvzLNiyGXuKaKrjHbtAZT1c5vPSQXPPNylggxW8Cc1lo/7AlZeU9IjYPYJHdCLk+ePn6lH3zol8O4pR5TOgen5dh+RvAVgKS68G5guhpXrRNojFi41sZ/q5CcE0a1LJu8Y24UZAfbgHgsARBygrQ6LlW447qT4of7NwA0qQsORaKtiuAnwD1E/epWJFSQBwnb63PxL6/1HROF/jobePTj1oW1AUGTbthzcjIj+Q6WiKJtFbNdcegZblORZwD0wo+3rA6PmyqeUm9jCEnjD3MzpjLtHLPUdjkBj/O4dIZA/hG58g=");
    private static final PlayerSkin SKIN_NO_CLOTHES = new PlayerSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc2MTY2NTI2NTIzOSwKICAicHJvZmlsZUlkIiA6ICIyYjcyZWYyYWUzMmQ0Zjc1OGEyMThlMDI4MTViYmNjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ2b2xrb2RhZl82MyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mM2U4MGQxNGY5ZGVhZjA2ODY5ODE0ZWI4MGI5NWNlYWJkOWI5MjY4NWFlYzMzYmJjMTcyMzhhYzg2Mjk3ZTkxIgogICAgfQogIH0KfQ==",
            "dhfN7QCPkRXVSNdIo7cOvUzuGpHXZPR8gl6jNWLNoiWdLrV+scXNdcHn4lPDHyYaLLocw2595fifHbnSNfanC3Q1R6+J9NRNoohkVuUWlXOS9pytluFfOGr/Zwm2QiSvriXX1133GOA7QOKU5XEthDUGDIQqzdlU5y5VyruyyAv0MZ/W0DtkNI7HnI1uyZ8wwEzlpKMYdCg/RpXjh046lBufcyumog3HQbO8MSDWwk2lSjHSkCsJalzNiE6pWRq2gvfX6omYLRbc5MxrU00MeyEVtMVwj3cife1jz6KsY1jASKDXMciPZeCsnmpFh+SU3xsnRrwBmH1KhYz3De5pab8WgCoXe6j4vYdMyKb1rla0vpOkzAYuoMXKkw3QtJoMtx+SVhkU9ZDhqLLRj/J9uTVKruqWjmX7NcuN/o8tucnmpFh+SU3xsnRrwBmH1KhYz3De5pab8WgCoXe6j4vYdMyKb1rla0vpOkzAYuoMXKkw3QtJoMtx+SVhkU9ZDhqLLRj/J9uTVKruqWjmX7NcuN/o8tucnTruqWjmX7NcuN/o8tucnROqGjGXUh8ts39Yhv/2n+ejv6xx3WTLhsiaaUsPDZayMBpWPiX0l0q2ZKVn9Ue3VXlLrFhB6bJMk5NmETuKBdjIDoYXQn9fdl1yykMvlo3N/zLtAsuckdVPUf5pFLjvLf2GJprZXZTASYkq+0NfveQ7xuuZSuQqfT+k2SSdRDeFye6Xb5CKYpXobzX156f2HFAsHO7/6Zsk2zJ7SH5Upo0YZecA2eSocRi9ViCFeqEb5TE4QmrSY0="
    );

    private final GoalManager goalManager;

    public AlexNPC(GoalManager goalManager) {

        super(Component.text("Alex"));
        this.goalManager = goalManager;

        if (goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES)) {
            setSkin(SKIN_NO_CLOTHES);
        } else {
            setSkin(SKIN_YES_CLOTHES);
        }

    }

    @Override
    public void onPlayerInteract(PlayerEntityInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getHand() != PlayerHand.MAIN) return;

        if (!goalManager.hasActiveGoal(ServerGoals.TOWN_CHORES)) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<white>How are you today?</white>"));
            player.playSound(Sound.sound().type(SoundEvent.ENTITY_PILLAGER_AMBIENT).source(Sound.Source.NEUTRAL).build());
            return;
        }

        if (goalManager.isGoalCompleted(ServerGoals.TOWN_CHORES)) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<white>Thank you for helping me</white>"));
            player.playSound(Sound.sound().type(SoundEvent.ENTITY_VILLAGER_AMBIENT).source(Sound.Source.NEUTRAL).build());
            }
        else {
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<white>Could you help me dry my clothes</white>"));
            player.playSound(Sound.sound().type(SoundEvent.ENTITY_VILLAGER_YES).source(Sound.Source.NEUTRAL).build());
        }
    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {

    }

    @Override
    public void spawn(@NotNull EntityManager entityManager, @NotNull Instance instance, @NotNull Pos position) {
        super.spawn(entityManager, instance, position);
        enableLookAtPlayers(getMinestomEntities(), position.yaw(), position.pitch(), 3F);
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:laundry:npc:alex");
    }
}
