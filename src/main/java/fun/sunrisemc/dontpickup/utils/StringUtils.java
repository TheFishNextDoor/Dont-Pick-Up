package fun.sunrisemc.dontpickup.utils;

import java.util.Optional;

import org.bukkit.Material;

import org.jetbrains.annotations.NotNull;

public class StringUtils {

    // Parsing

    public static Optional<Material> parseMaterial(@NotNull String str) {
        String materialBName = normalize(str);
        for (Material material : Material.values()) {
            if (normalize(material.name()).equals(materialBName)) {
                return Optional.of(material);
            }
        }
        return Optional.empty();
    }

    // Formatting

    @NotNull
    public static String formatMaterial(@NotNull Material material) {
        return titleCase(material.name());
    }

    // Case

    @NotNull
    private static String titleCase(@NotNull String str) {
        str = str.replace("_", " ");
        String[] words = str.split(" ");
        String titleCase = "";
        for (String word : words) {
            titleCase += word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
        }
        return titleCase.trim();
    }

    // Normalization

    @NotNull
    private static String normalize(@NotNull String str) {
        return str.trim().toLowerCase().replace(" ", "").replace("_", "").replace("-", "");
    }
}