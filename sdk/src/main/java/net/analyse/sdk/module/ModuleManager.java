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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

/**
 * A manager that loads, unloads, registers, unregisters and disables platform modules.
 */
public class ModuleManager {
    private final Platform platform;
    private final List<PlatformModule> modules;
    private final File baseDirectory;

    public ModuleManager(Platform platform) {
        this.platform = platform;
        this.modules = new ArrayList<>();
        this.baseDirectory = platform.getDirectory();
    }

    /**
     * Get the loaded modules
     *
     * @return Loaded modules
     */
    public List<PlatformModule> getModules() {
        return modules;
    }

    /**
     * Loads all platform modules.
     *
     * @return The list of loaded modules.
     */
    public List<PlatformModule> load() {
        File moduleDir = new File(platform.getDirectory(), "modules");

        if (!moduleDir.exists()) {
            platform.debug("Creating module directory..");
            if (!moduleDir.mkdirs()) {
                platform.log(Level.WARNING, "Failed to create module directory!");
                return Collections.emptyList();
            }
        }

        if (!moduleDir.isDirectory()) {
            platform.log(Level.WARNING, "Invalid module directory!");
            return Collections.emptyList();
        }

        List<Class<?>> moduleClasses = getClasses();

        if (moduleClasses == null) {
            return Collections.emptyList();
        }

        List<PlatformModule> moduleList = new ArrayList<>();

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

                List<String> dependencies = module.getDependencies();
                boolean allDependenciesEnabled = dependencies.stream().allMatch(platform::isPluginEnabled);
                if (allDependenciesEnabled) {
                    moduleList.add(module);
                } else {
                    String missingDependency = dependencies.stream().filter(dependency -> !platform.isPluginEnabled(dependency)).findFirst().orElse(null);
                    if (missingDependency != null) {
                        disable(module, String.format("Skipped %s module due to a missing dependency: %s", module.getName(), missingDependency));
                    }
                }
            } catch (IllegalAccessException | InstantiationException ignored) {

            }
        }
        return moduleList;
    }

    /**
     * Scans the specified folder for jar files containing classes that implement the given target class.
     *
     * @return The list of classes that implement the target class.
     */
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

    /**
     * Scans the specified jar file for classes that implement the given target class.
     *
     * @param jar         The jar file to scan.
     * @param list        The list of classes found so far.
     * @param classLoader The class loader to use.
     * @return The updated list of classes that implement the target class.
     */
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

    /**
     * Registers a platform module manually.
     *
     * @param module the module to register.
     */
    public void register(PlatformModule module) {
        platform.log("Loaded module: " + module.getName());
        module.onEnable();
    }

    /**
     * Unregisters a platform module manually, removing it from the plugin data.
     *
     * @param module the module to unregister.
     */
    public void unregister(PlatformModule module) {
        platform.log("Unloaded module: " + module.getName());
        module.onDisable();
    }

    /**
     * Disables a platform module, logging a warning message with the given reason and unregistering it from the plugin data.
     *
     * @param module the module to disable.
     * @param reason the reason for disabling the module.
     */
    public void disable(PlatformModule module, String reason) {
        platform.log(Level.WARNING, reason);
        unregister(module);
    }
}
