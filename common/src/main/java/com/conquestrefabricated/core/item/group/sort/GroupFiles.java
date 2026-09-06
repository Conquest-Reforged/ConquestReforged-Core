package com.conquestrefabricated.core.item.group.sort;

import com.conquestrefabricated.core.Namespaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.world.item.ItemStack;

/**
 * Loads the {@code assets/<namespace>/groups/<label>.txt} ordering files that back the creative
 * tabs.
 * <p>
 * Every registered namespace gets a chance to contribute to each tab, and every copy of a given
 * file on the classpath is read rather than just the first one the class loader happens to find.
 * That's what lets a third party addon drop its own blocks into our tabs: it ships its ordering
 * under its own namespace and the entries are appended after ours.
 */
public class GroupFiles {

    private static final String PATH_FORMAT = "assets/%s/groups/%s.txt";

    private GroupFiles() {
    }

    /**
     * @param label the tab label, eg {@code rr_utility}
     * @return a sorter over every registered namespace's ordering file for that tab
     */
    public static Sorter<ItemStack> loadSorter(String label) {
        List<String> lines = new ArrayList<>();
        StringBuilder sources = new StringBuilder();

        for (String namespace : Namespaces.all()) {
            String path = String.format(PATH_FORMAT, namespace, label);
            for (String line : readAll(path)) {
                lines.add(line);
            }
            if (sources.length() > 0) {
                sources.append(", ");
            }
            sources.append(path);
        }

        if (lines.isEmpty()) {
            return Sorter.none();
        }
        return ItemList.read(lines, sources.toString());
    }

    private static List<String> readAll(String path) {
        // both loaders usually see the same jars, so dedupe by url before reading
        Set<URL> urls = new LinkedHashSet<>();
        for (ClassLoader loader : loaders()) {
            if (loader == null) {
                continue;
            }
            try {
                Enumeration<URL> found = loader.getResources(path);
                while (found.hasMoreElements()) {
                    urls.add(found.nextElement());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> lines = new ArrayList<>();
        for (URL url : urls) {
            read(url, lines);
        }
        return lines;
    }

    private static void read(URL url, List<String> lines) {
        try (InputStream in = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reader.lines().forEach(line -> {
                if (!line.isEmpty() && !lines.contains(line)) {
                    lines.add(line);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ClassLoader[] loaders() {
        return new ClassLoader[]{
                Thread.currentThread().getContextClassLoader(),
                GroupFiles.class.getClassLoader()
        };
    }
}
