package com.nxtdelivery.tabulous.util;

import cc.polyfrost.oneconfig.utils.Multithreading;
import cc.polyfrost.oneconfig.utils.NetworkUtils;
import com.google.gson.JsonObject;
import com.nxtdelivery.tabulous.Tabulous;
import com.nxtdelivery.tabulous.config.TabulousConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static cc.polyfrost.oneconfig.libs.universal.UDesktop.open;

public class Updater {
    public static String updateUrl = "";
    public static String latestTag;
    public static boolean shouldUpdate = false;

    public static void update() {
        Multithreading.runAsync(() -> {
            try {
                JsonObject latestRelease = NetworkUtils.getJsonElement("https://api.github.com/repos/W-OVERFLOW/" + Tabulous.ID + "/releases/latest").getAsJsonObject();
                latestTag = latestRelease.get("tag_name").getAsString();
                DefaultArtifactVersion currentVersion = new DefaultArtifactVersion(StringUtils.substringBefore(Tabulous.VER, "-"));
                DefaultArtifactVersion latestVersion = new DefaultArtifactVersion(StringUtils.substringBefore(StringUtils.substringAfter(latestTag, "v"), "-"));
                if (currentVersion.compareTo(latestVersion) >= 0) {
                    return;
                }
                updateUrl = latestRelease.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
                if (!updateUrl.isEmpty()) {
                    if (TabulousConfig.showUpdate) {
                        //TODO EssentialAPI.getNotifications().push(Tabulous.NAME, Tabulous.NAME + " has a new update (" + latestTag + ")! Click here to download it automatically!", Updater::openUpdateGui);
                    }
                    shouldUpdate = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean download(String url, File file) {
        if (file.exists()) return true;
        url = url.replace(" ", "%20");
        try {
            NetworkUtils.downloadFile(url, file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return file.exists();
    }


    public static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Opening Deleter task...");
            try {
                String runtime = getJavaRuntime();
                if (Minecraft.isRunningOnMac) {
                    open(Tabulous.jarFile.getParentFile());
                }
                File file = new File(Tabulous.modDir.getParentFile(), "Deleter-1.2.jar");
                Runtime.getRuntime()
                        .exec("\"" + runtime + "\" -jar \"" + file.getAbsolutePath() + "\" \"" + Tabulous.jarFile.getAbsolutePath() + "\"");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.currentThread().interrupt();
        }));
    }

    /**
     * Gets the current Java runtime being used.
     *
     * @link <a href="https://stackoverflow.com/a/47925649">...</a>
     */
    public static String getJavaRuntime() throws IOException {
        String os = System.getProperty("os.name");
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator +
                (os != null && os.toLowerCase(Locale.ENGLISH).startsWith("windows") ? "java.exe" : "java");
        if (!new File(java).isFile()) {
            throw new IOException("Unable to find suitable java runtime at " + java);
        }
        return java;
    }
}