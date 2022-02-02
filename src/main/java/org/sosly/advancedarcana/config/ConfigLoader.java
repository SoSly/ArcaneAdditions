package org.sosly.advancedarcana.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ConfigLoader {
    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig data = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        data.load();
        spec.setConfig(data);
    }
}