package com.xg7plugins.utils.text.component.serializer;

import com.xg7plugins.utils.text.component.Component;
import com.xg7plugins.utils.text.component.serializer.tags.ClickTag;
import com.xg7plugins.utils.text.component.serializer.tags.GradientTag;
import com.xg7plugins.utils.text.component.serializer.tags.HexTag;
import com.xg7plugins.utils.text.component.serializer.tags.HoverTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComponentDeserializer {

    private static final Pattern TAG_PATTERN = Pattern.compile("<(\\w+)(?::([^>]*))?>(.*?)</\\1(?::([^>]*))?>");

    private static final HashMap<String, Tag> tags = new HashMap<>();

    static {
        tags.put("click", new ClickTag());
        tags.put("hex", new HexTag());
        tags.put("gradient", new GradientTag());
        tags.put("hover", new HoverTag());
    }

    public static Component deserialize(String text) {
        try {
            Matcher test = TAG_PATTERN.matcher(text);

            if (!test.find()) {
                return Component.text(text).build();
            }

            List<Component> components = new ArrayList<>();
            int lastIndex = 0;
            Matcher matcher = TAG_PATTERN.matcher(text);

            while (matcher.find()) {
                // Adiciona texto plano antes da tag como um componente separado
                if (matcher.start() > lastIndex) {
                    components.add(Component.text(text.substring(lastIndex, matcher.start())).build());
                }

                String tagName = matcher.group(1);
                String[] openArgs = matcher.group(2) != null ? matcher.group(2).split(":") : new String[0];
                String content = matcher.group(3);
                String[] closeArgs = matcher.group(4) != null ? matcher.group(4).split(":") : new String[0];

                // Processa o conteúdo interno da tag recursivamente
                Component innerComponent = deserialize(content);

                Tag tag = tags.get(tagName);
                if (tag == null) throw new IllegalArgumentException("Tag desconhecida: " + tagName);

                // Aplica os efeitos da tag apenas ao componente interno
                tag.resolve(innerComponent, Arrays.asList(openArgs), Arrays.asList(closeArgs));

                components.add(innerComponent);
                lastIndex = matcher.end();
            }

            // Adiciona qualquer texto restante após a última tag
            if (lastIndex < text.length()) {
                components.add(Component.text(text.substring(lastIndex)).build());
            }

            // Cria um componente pai para conter todos os componentes
            Component parent = Component.text("").build();
            for (Component comp : components) {
                parent.addComponent(comp);
            }

            return parent;
        } catch (Exception e) {
            e.printStackTrace();
            return Component.text(text).build();
        }
    }


}
