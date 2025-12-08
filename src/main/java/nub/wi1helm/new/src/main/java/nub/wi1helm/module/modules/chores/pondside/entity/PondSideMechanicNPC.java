package nub.wi1helm.module.modules.chores.pondside.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import nub.wi1helm.entity.EntityManager;
import nub.wi1helm.entity.npc.NPC;
import org.jetbrains.annotations.NotNull;

public class PondSideMechanicNPC extends NPC {

    private static PlayerSkin skin = new PlayerSkin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc1NzYyMzQ5MDUxNSwKICAicHJvZmlsZUlkIiA6ICIxOTc0ZWI5YzU5NjY0MTc3OTMxNzQ1ZDFmMmIwYTUyNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJhY3Rpb24zNiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jNWNiMDRhZDdkOGM3ODIzMTY4YTFkYWY5YjRmNzg3NTIwMTEwZTc4ZGFiZWZkMTY4OTYzNTJlZTc5YTc2ZWY1IgogICAgfQogIH0KfQ==",
            "XYigC/JeDP7iSoqT0ATQv7JEkGY+DLjSxXu3dTRv2PNP2Jch1HxIjuRE9xqj/XiJRc97V/Lr2zdjSRrqvriDIm2NnAkQYpvE9TAlgR6ccSwzLVxhATdJK9hXt7pn78o+CU34LRsr7A9W5W+axOOBpjy1oOkIh4wVqIYGbEnED9sCuCIXlEdECg8IPAmIEEZt0VSA8dcy6ADbic5nsS3GFWu3RyYKMIKwseyuv0skV5PPR0RyNWxRj9qmlWFfUSieBLQDwpCIuEdnLk7a5SoZE9NN9VQSFdGBnY/e0N1LdOTW/EkFdrG2k027IcKepND0RoONo7XIFuFixC+iVvads9J1n2Gqf1ZT/PttCKecxGqkWh16/1EwYynrE2mbTvcpKUwj21YCQSMKmQuNdsdAswMES3ImCFLptxc/MsI2pKddQWbiBYS1K23aMFwbQgYR4rVYAI17M2YbROfXfzT18ztARCqqoM4uEi8fu27zHfJM1cd6YsOfRP9zqNwgRydAo1jcyb198RmglcOIQgC/OykRj1FlGlhJhKJGsGd610/YXqBSe90Ht/sli4DbUd9/xW5w6CnW4DICYwViXpkXbGNzb9tKrZPKFtvZAiRQpApp5wM7Qaxy9yEQjujjOC5oUtYoVMZTh4zoZxCMm2nAPqYRwo3f6TMSrjheSwm8hoA="
    );

    public PondSideMechanicNPC() {
        super(skin, Component.text("Mr Pondside the mechanic"));
    }

    @Override
    public void setupInteractions() {

    }

    @Override
    public void spawn(EntityManager manager, Instance instance) {
        super.spawn(manager, instance, new Pos(16.5,-47.0,3.5, -95, 4));
        enableLookAtPlayers(getMinestomEntities(), -95, 4, 3F);
    }

    @Override
    public @NotNull Tag<@NotNull String> getEntityTag() {
        return Tag.String("module:pondside:entity:pondsidemechanic");
    }
}
