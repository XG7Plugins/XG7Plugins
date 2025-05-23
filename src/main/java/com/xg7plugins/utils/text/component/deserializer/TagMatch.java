package com.xg7plugins.utils.text.component.deserializer;

import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.deserializer.tags.TagType;
import com.xg7plugins.utils.text.component.deserializer.tags.TextTag;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

@Getter
public class TagMatch {

    private final TagType type;
    private final TextTag tag;
    private final String name;
    private final String args;
    private final String content;
    private final int start;
    private final int end;

    TagMatch(TagType type, Matcher matcher) {
        this.type = type;
        this.name = matcher.group(1).toLowerCase();
        this.args = matcher.groupCount() == 1 ? null : matcher.group(2);
        this.content = matcher.groupCount() == 1 || matcher.groupCount() == 2 ? null : matcher.group(3);
        this.start = matcher.start();
        this.end = matcher.end();

        if (!ComponentDeserializer.containsTag(name)) throw new IllegalArgumentException(String.format("Unknown tag '%s'", name));

        this.tag = ComponentDeserializer.getTag(name);

        if (tag == null || !tag.getType().equals(type)) throw new IllegalArgumentException(String.format("Tag '%s' is null or is not a %s tag", name, type));
    }

    public List<String> getArgs() {
        if (args == null) return new ArrayList<>();
        return Arrays.asList(args.split(":"));
    }

    public void resolve(Component component) {
        tag.resolve(component, getArgs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, args, content, start, end, tag);
    }

}
