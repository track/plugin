package net.analyse.sdk.module;

import net.analyse.sdk.platform.Platform;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

public class ModuleManager {
    private final Platform plugin;
    private List<PlatformModule> modules;
    private File baseDirectory;

    public ModuleManager(Platform plugin) {
        this.plugin = plugin;
        this.modules = new ArrayList<>();
        this.baseDirectory = plugin.getDirectory();
    }

    public List<PlatformModule> getModules() {
        return modules;
    }

    private List<Class<?>> getClasses(String folder, Class<?> targetClass) {
        List<Class<?>> list = new ArrayList<>();

        try {
            File f = new File(this.baseDirectory, folder);
            if (!f.exists()) {
                return list;
            }

            FilenameFilter fileNameFilter = (dir, name) -> name.endsWith(".jar");
            File[] jars = f.listFiles(fileNameFilter);
            if (jars == null) {
                return list;
            }

            ClassLoader classLoader = targetClass.getClassLoader();
            for (File file : jars) {
                list = gather(file.toURI().toURL(), list, classLoader, targetClass);
            }

            return list;
        } catch (Throwable ignored) {

        }

        return null;
    }

    private List<Class<?>> gather(final URL jar, List<Class<?>> list, ClassLoader classLoader, Class<?> targetClass) {
        if (list == null) {
            list = new ArrayList<>();
        }

        try (URLClassLoader cl = new URLClassLoader(new URL[]{jar}, classLoader);
             final JarInputStream jarInputStream = new JarInputStream(jar.openStream())) {

            while (true) {
                JarEntry jarInputStreamNextJarEntry = jarInputStream.getNextJarEntry();
                if (jarInputStreamNextJarEntry == null) {
                    break;
                }

                String name = jarInputStreamNextJarEntry.getName();
                if (name == null || name.isEmpty()) {
                    continue;
                }

                if (name.endsWith(".class")) {
                    name = name.replace("/", ".");
                    String cname = name.substring(0, name.lastIndexOf(".class"));

                    Class<?> c = cl.loadClass(cname);
                    if (targetClass.isAssignableFrom(c)) {
                        list.add(c);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void load() throws Exception {
        File moduleDir = new File(plugin.getDirectory() + File.separator + "modules");

        if(! moduleDir.exists()) {
            plugin.debug("Module folder doesn't exist (creating it!).");
            moduleDir.createNewFile();
        }

        if (! moduleDir.isDirectory()) {
            throw new Exception("Invalid module directory: " + moduleDir.getAbsolutePath());
        }

        List<Class<?>> moduleClasses = getClasses("modules", PlatformModule.class);

        if(moduleClasses == null) {
            return;
        }

        for (Class<?> moduleClass : moduleClasses) {
            try {
                Constructor<?>[] constructors = moduleClass.getConstructors();
                PlatformModule module = null;

                if (constructors.length == 0) {
                    module = (PlatformModule) moduleClass.newInstance();
                } else {
                    for (Constructor<?> constructor : constructors) {
                        if (constructor.getParameterTypes().length == 0) {
                            module = (PlatformModule) moduleClass.newInstance();
                            break;
                        }
                    }
                }

                if(module == null) continue;
                this.modules.add(module);
            } catch (IllegalAccessException | InstantiationException ignored) {

            }
        }
    }

    public void unload() {
        // Unloads all addons
    }

    // A plugin developer runs this on startup of the plugin.
    public void register(PlatformModule module) {
        // Registers anmodule manually.
    }

    // A plugin developer can call this so Analyse stops it, now this doesn't unregister their events, only removes it from the plugin data.
    public void unregister(PlatformModule module) {
        // Unregisters an module manually.
        getModules().remove(module);
    }

    public void disable(PlatformModule module, String reason) {
        plugin.log(Level.WARNING, "Disabling module " + module.getName() + " because: " + reason);
        unregister(module);
    }
}
