package com.xg7plugins.commands.node;

import com.xg7plugins.commands.utils.CommandState;
import com.xg7plugins.commands.setup.Command;
import com.xg7plugins.commands.utils.CommandArgs;
import com.xg7plugins.utils.reflection.ReflectionMethod;
import lombok.Data;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Represents a node in the command hierarchy.
 * Each node corresponds to a specific command or subcommand.
 */
@Data
public class CommandNode {

    private final Command command;
    private final String name;

    private CommandNode parent;
    private final List<CommandNode> children = new ArrayList<>();
    private final Map<String, CommandNode> mappedChildren = new HashMap<>();

    private ReflectionMethod commandMethod;

    public void mapChild(String key, CommandNode child) {
        this.mappedChildren.put(key, child);
    }

    public void addChild(CommandNode child) {
        child.setParent(this);
        this.children.add(child);
        this.mappedChildren.put(child.getName(), child);
    }

    public CommandNode getChild(String name) {
        return mappedChildren.get(name);
    }

    public CommandState execute(CommandSender sender, CommandArgs args) {

        if (commandMethod == null) return CommandState.SYNTAX_ERROR;

        if (commandMethod.getParamsTypes().size() == 2) {
            return commandMethod.invoke(sender, args);
        }

        if (commandMethod.getParamsTypes().get(0).isAssignableFrom(CommandSender.class)) {
            return commandMethod.invoke(sender);
        }

        if (commandMethod.getParamsTypes().get(0).isAssignableFrom(CommandArgs.class)) {
            return commandMethod.invoke(args);
        }

        return CommandState.ERROR;

    }

    @Override
    public String toString() {
        return "CommandNode{" +
                "\ncommand=" + command +
                ", \nname='" + name + '\'' +
                ", \nparent=" + (parent == null ? "" : parent.getName()) +
                ", \nchildren=" + children +
                ", \ncommandMethod=" + (command.getClass() + " -> " + commandMethod.getMethod().getName() + "()") +
                "\n}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CommandNode that = (CommandNode) o;
        return Objects.equals(command, that.command) && Objects.equals(name, that.name) && Objects.equals(children, that.children) && Objects.equals(mappedChildren, that.mappedChildren) && Objects.equals(commandMethod, that.commandMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, name, children, mappedChildren, commandMethod);
    }
}
