package net.analyse.sdk.module;

import net.analyse.sdk.platform.Platform;
import net.analyse.sdk.platform.PlatformModule;

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

public class ModuleManager {
    private final Platform platform;
    private final List<PlatformModule> modules;
    private final File baseDirectory;

    public ModuleManager(Platform platform) {
        this.platform = platform;
        this.modules = new ArrayList<>();
        this.baseDirectory = platform.getDirectory();
    }

    public List<PlatformModule> getModules() {
        return modules;
    }

    private List<Class<?>> getClasses() {
        List<Class<?>> list = new ArrayList<>();

        try {
            File f = new File(this.baseDirectory, "modules");
            if (!f.exists()) {
                return list;
            }

            FilenameFilter fileNameFilter = (dir, name) -> name.endsWith(".jar");
            File[] jars = f.listFiles(fileNameFilter);
            if (jars == null) {
                return list;
            }

            ClassLoader classLoader = PlatformModule.class.getClassLoader();
            for (File file : jars) {
                list = gather(file.toURI().toURL(), list, classLoader);
            }

            return list;
        } catch (Throwable ignored) {

        }

        return null;
    }

    private List<Class<?>> gather(final URL jar, List<Class<?>> list, ClassLoader classLoader) {
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
                if (name.isEmpty()) {
                    continue;
                }

                if (name.endsWith(".class")) {
                    name = name.replace("/", ".");
                    String cname = name.substring(0, name.lastIndexOf(".class"));

                    Class<?> c = cl.loadClass(cname);
                    if (PlatformModule.class.isAssignableFrom(c)) {
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
        File moduleDir = new File(platform.getDirectory(), "modules");

        if (!moduleDir.exists()) {
            boolean created = moduleDir.mkdirs();

            // Check if the directory was created successfully
            if (!created) {
                throw new RuntimeException("Unable to create the Module directory");
            }
        } else if (!moduleDir.isDirectory()) {
            throw new IllegalArgumentException("The Module directory path points to a file, not a directory");
        }

        if (!moduleDir.isDirectory()) {
            throw new Exception("Invalid module directory: " + moduleDir.getAbsolutePath());
        }

        List<Class<?>> moduleClasses = getClasses();

        if (moduleClasses == null) {
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

                if (module == null) continue;
                this.modules.add(module);
            } catch (IllegalAccessException | InstantiationException ignored) {

            }
        }
    }

    public void unload() {
        // Unloads all addons
        for (PlatformModule module : getModules()) {
            platform.debug("Disabling " + module.getName() + "..");
            module.onDisable();
            getModules().remove(module);
        }
    }

    // A plugin developer runs this on startup of the plugin.
    public void register(PlatformModule module) {
        // Registers anmodule manually.
    }

    // A plugin developer can call this so Analyse stops it, now this doesn't unregister their events, only removes it from the plugin data.
    public void unregister(PlatformModule module) {
        // Unregisters an module manually.

    }

    public void disable(PlatformModule module, String reason) {
        platform.debug("Disabling module " + module.getName() + " because: " + reason);
        unregister(module);
    }
}
