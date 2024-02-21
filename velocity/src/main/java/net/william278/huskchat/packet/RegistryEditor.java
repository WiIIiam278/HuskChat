package net.william278.huskchat.packet;

import com.github.retrooper.packetevents.protocol.nbt.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public record RegistryEditor(NBTCompound root) {

    public static ChatType HUSKCHAT = RegistryEditor.ChatType.builder()
            .name(Key.key("huskchat", "format"))
            .id(50)
            .elements(Map.of(
                    RegistryEditor.ChatType.ElementType.CHAT,
                    RegistryEditor.ChatType.ElementData.builder()
                            .translationKey("%s%s")
                            .parameters(List.of(
                                    RegistryEditor.ChatType.ElementData.ElementParameter.SENDER,
                                    RegistryEditor.ChatType.ElementData.ElementParameter.CONTENT
                            ))
                            .build(),
                    RegistryEditor.ChatType.ElementType.NARRATION,
                    RegistryEditor.ChatType.ElementData.builder()
                            .translationKey("chat.type.text.narrate")
                            .parameters(List.of(
                                    RegistryEditor.ChatType.ElementData.ElementParameter.SENDER,
                                    RegistryEditor.ChatType.ElementData.ElementParameter.CONTENT
                            ))
                            .build()
            ))
            .build();

    public void injectTypes(@NotNull List<ChatType> toAdd) {
        System.out.println("Adding chat types");
        System.out.println(root.getTags().keySet());
        NBTList<NBTCompound> values = root
                .getCompoundTagOrThrow("minecraft:chat_type")
                .getCompoundListTagOrThrow("value");
        for (ChatType chatType : toAdd) {
            NBTCompound elements = new NBTCompound();
            for (ChatType.ElementType type : chatType.elements.keySet()) {
                elements.setTag(type.id(), chatType.elements.get(type).toTag());
            }

            NBTCompound chatTypeTag = new NBTCompound();
            chatTypeTag.setTag("name", new NBTString(chatType.name.asString()));
            chatTypeTag.setTag("id", new NBTInt(chatType.id));
            chatTypeTag.setTag("element", elements);
            values.addTag(chatTypeTag);
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ChatType {
        private Key name;
        private int id;
        private Map<ElementType, ElementData> elements;

        enum ElementType {
            CHAT,
            NARRATION;

            @NotNull
            public static ElementType fromString(@NotNull String s) {
                return ElementType.valueOf(s.toUpperCase(Locale.ENGLISH));
            }

            @NotNull
            public String id() {
                return name().toLowerCase(Locale.ENGLISH);
            }
        }

        @Getter
        @Builder
        public static class ElementData {
            private String translationKey;
            private NBTCompound style;
            private List<ElementParameter> parameters;

            @NotNull
            public NBTCompound toTag() {
                NBTCompound compound = new NBTCompound();
                compound.setTag("translation_key", new NBTString(translationKey));
                if (style != null) {
                    compound.setTag("style", style);
                }
                if (parameters != null) {
                    NBTList<NBTString> parameterList = new NBTList<>(NBTType.STRING);
                    for (ElementParameter parameter : parameters) {
                        parameterList.addTag(new NBTString(parameter.id()));
                    }
                    compound.setTag("parameters", parameterList);
                }
                return compound;
            }

            enum ElementParameter {
                TARGET,
                SENDER,
                CONTENT;

                @NotNull
                public static ElementParameter fromString(@NotNull String s) {
                    return ElementParameter.valueOf(s.toUpperCase(Locale.ENGLISH));
                }

                @NotNull
                public String id() {
                    return name().toLowerCase(Locale.ENGLISH);
                }

            }
        }
    }

}
