package com.xg7plugins.boot;

import com.xg7plugins.commands.setup.ICommand;
import com.xg7plugins.data.database.entity.Entity;
import com.xg7plugins.events.Listener;
import com.xg7plugins.events.PacketListener;
import com.xg7plugins.tasks.Task;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginConfigurations {

    String prefix();

    String mainCommandName();
    String[] mainCommandAliases();

    String[] configs() default {};

    String[] onEnableDraw() default {};


    Class<? extends Entity<?,?>>[] entities() default {};

    Class<? extends Listener>[] listeners() default {};

    Class<? extends PacketListener>[] packetListeners() default {};

    Class<? extends ICommand>[] commands() default {};



}
