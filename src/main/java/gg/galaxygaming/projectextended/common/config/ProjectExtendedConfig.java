package gg.galaxygaming.projectextended.common.config;


import gg.galaxygaming.projectextended.ProjectExtended;
import moze_intel.projecte.config.PEModConfig;

public class ProjectExtendedConfig {

    public static final ServerConfig server = new ServerConfig();

    public static void register() {
        PEModConfig peModConfig = new PEModConfig(ProjectExtended.MOD_CONTAINER, server);
        if (server.addToContainer()) {
            ProjectExtended.MOD_CONTAINER.addConfig(peModConfig);
        }
    }
}