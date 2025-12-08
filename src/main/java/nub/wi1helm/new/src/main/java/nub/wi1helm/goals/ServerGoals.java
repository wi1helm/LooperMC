package nub.wi1helm.goals;

import net.kyori.adventure.text.Component;

public enum ServerGoals {
    // NPC interaction based
    DELIVER_MAIL(Component.text("Deliver Mail"), Component.text("Deliver letters given by the Matt the Mailman NPC"), 200),
    BARTENDER_SUPPLY(Component.text("Bartender Supply"), Component.text("Restock the Tavern NPC with barrels of 'Suspicious Stew'"), 50),
    TRICKSTER_PRANKS(Component.text("Trickster Pranks"), Component.text("Give 'Slime Ball Pies' to the Trickster NPC"), 30),
    MAYOR_VOTES(Component.text("Mayor Votes"), Component.text("Collect fake votes for the Mayor NPC’s re-election"), 100),

    HIDE_AND_SEEK(Component.text("Hide & Seek"), Component.text("Find Lily NPC whereever she is hiding"), 50),

    // Special block/item interactions
    MAGIC_WELL_WISHES(Component.text("Magic Well Wishes"), Component.text("Throw custom 'Wish Coins' into the Magic Well block"), 300),
    VOID_JUMPERS(Component.text("Void Jumpers"), Component.text("Jump into the town’s Void Pit as a sacrifice"), 2),
    MUSEUM_EXHIBITS(Component.text("Museum Exhibits"), Component.text("Donate custom relic items to the Town Museum"), 25),
    CANDLE_FESTIVAL(Component.text("Candle Festival"), Component.text("Light town candles during the Night Festival event"), 200),

    // Cooperative tasks
    TOWN_CHORES(Component.text("Town Chores"), Component.text("Complete random chores given by various NPCs"), 500),
    STREET_PERFORMERS(Component.text("Street Performers"), Component.text("Play custom music discs on the town stage jukebox"), 100),
    BAKERY_OVERLOAD(Component.text("Bakery Overload"), Component.text("Bake and deliver cakes to the Bakery NPC"), 150),
    MARKET_MADNESS(Component.text("Market Madness"), Component.text("Trade with market stall NPCs"), 500),

    // Chaotic/funny tasks
    PIGEON_FEEDERS(Component.text("Pigeon Feeders"), Component.text("Feed custom 'Town Pigeons' at the park"), 250),
    FOUNTAIN_FISH(Component.text("Fountain Fish"), Component.text("Throw raw fish into the central fountain"), 100),
    DRUNKEN_DUELS(Component.text("Drunken Duels"), Component.text("Lose tavern duels (intentionally or not!)"), 50),
    STATUE_POLISH(Component.text("Statue Polish"), Component.text("Polish the Town Hero Statue with 'Shiny Rag' items"), 75);

    private final Component name;
    private final Component description;
    private final int target;

    ServerGoals(Component name, Component description, int target) {
        this.name = name;
        this.description = description;
        this.target = target;
    }

    public Component displayName() {
        return name;
    }

    public Component description() {
        return description;
    }

    public int target() {
        return target;
    }

    public ServerGoal goal() {
        return new ServerGoal(name, description, target, this);
    }
}
