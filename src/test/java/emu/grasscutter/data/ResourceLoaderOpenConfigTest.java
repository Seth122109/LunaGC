package emu.grasscutter.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ResourceLoaderOpenConfigTest {
    @TempDir Path tempDirectory;

    @Test
    void keepsLoadedAvatarTalentsWhenOptionalDirectoryIsMissing() throws Exception {
        Path avatarTalents = Files.createDirectory(tempDirectory.resolve("AvatarTalents"));
        Files.writeString(
                avatarTalents.resolve("ConfigTalent_Venti.json"),
                """
                {
                  "Venti_Hexenzirkel_1": [
                    {
                      "$type": "AddAbility",
                      "abilityName": "Avatar_Venti_HexenzirkelSkill"
                    }
                  ]
                }
                """);

        var entries = ResourceLoader.loadOpenConfigDirectories(
                List.of(avatarTalents, tempDirectory.resolve("RelicTalents")), ignored -> {});

        assertTrue(entries.containsKey("Venti_Hexenzirkel_1"));
        assertArrayEquals(
                new String[] {"Avatar_Venti_HexenzirkelSkill"},
                entries.get("Venti_Hexenzirkel_1").getAddAbilities());
    }
}
