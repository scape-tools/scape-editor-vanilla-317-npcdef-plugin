package plugin;

import scape.editor.fs.io.RSBuffer;
import scape.editor.gui.plugin.PluginDescriptor;
import scape.editor.gui.plugin.extension.config.NpcDefinitionExtension;

@PluginDescriptor(name="Vanilla 317 Npc Definition Plugin", authors = "Nshusa", version = "1.1.0")
public class Plugin extends NpcDefinitionExtension {

    @Override
    public String applicationIcon() {
        return "icons/icon.png";
    }

    @Override
    public String fxml() {
        return "scene.fxml";
    }

    @Override
    public String[] stylesheets() {
        return new String[]{
                "css/style.css"
        };
    }

    @Override
    public String getFileName() {
        return "npc";
    }

    @Override
    protected void decode(int currentIndex, RSBuffer buffer) {
        id = currentIndex;
        while(true) {
            int opcode = buffer.readUByte();

            if (opcode == 0) {
                break;
            }

            if (opcode == 1) {
                int count = buffer.readUByte();
                modelIds = new int[count];
                for (int i = 0; i < count; i++) {
                    modelIds[i] = buffer.readUShort();
                }
            } else if (opcode == 2) {
                name = buffer.readString10();
            } else if (opcode == 3) {
                description = buffer.readString10();
            } else if (opcode == 12) {
                size = buffer.readByte();
            } else if (opcode == 13) {
                idleAnimation = buffer.readUShort();
            } else if (opcode == 14) {
                walkingAnimation = buffer.readUShort();
            } else if (opcode == 17) {
                walkingAnimation = buffer.readUShort();
                halfTurnAnimation = buffer.readUShort();
                rotateClockwiseAnimation = buffer.readUShort();
                rotateAntiClockwiseAnimation = buffer.readUShort();
            } else if (opcode >= 30 && opcode < 40) {
                if (actions == null) {
                    actions = new String[5];
                }
                actions[opcode - 30] = buffer.readString10();
                if (actions[opcode - 30].equalsIgnoreCase("hidden")) {
                    actions[opcode - 30] = null;
                }
            } else if (opcode == 40) {
                int count = buffer.readUByte();
                originalColours = new int[count];
                replacementColours = new int[count];
                for (int i = 0; i < count; i++) {
                    originalColours[i] = buffer.readUShort();
                    replacementColours[i] = buffer.readUShort();
                }

            } else if (opcode == 60) {
                int count = buffer.readUByte();
                additionalModels = new int[count];

                for (int i = 0; i < count; i++) {
                    additionalModels[i] = buffer.readUShort();
                }
            } else if (opcode == 90) {
                buffer.readUShort();
            } else if (opcode == 91) {
                buffer.readUShort();
            } else if (opcode == 92) {
                buffer.readUShort();
            } else if (opcode == 93) {
                drawMinimapDot = false;
            } else if (opcode == 95) {
                combat = buffer.readUShort();
            } else if (opcode == 97) {
                scaleXY = buffer.readUShort();
            } else if (opcode == 98) {
                scaleZ = buffer.readUShort();
            } else if (opcode == 99) {
                priorityRender = true;
            } else if (opcode == 100) {
                lightModifier = buffer.readByte();
            } else if (opcode == 101) {
                shadowModifier = buffer.readByte() * 5;
            } else if (opcode == 102) {
                headIcon = buffer.readUShort();
            } else if (opcode == 103) {
                rotation = buffer.readUShort();
            } else if (opcode == 106) {
                varbit = buffer.readUShort();
                if (varbit == 65535) {
                    varbit = -1;
                }
                varp = buffer.readUShort();
                if (varp == 65535) {
                    varp = -1;
                }

                int count = buffer.readUByte();
                morphisms = new int[count + 1];

                for (int i = 0; i <= count; i++) {
                    morphisms[i] = buffer.readUShort();
                    if (morphisms[i] == 65535) {
                        morphisms[i] = -1;
                    }
                }

            } else if (opcode == 107) {
                clickable = false;
            } else {
                System.out.println("Unrecognised opcode=" + opcode);
            }
        }
    }

    @Override
    protected void encode(RSBuffer buffer) {
        if (modelIds != null) {
            buffer.writeByte(1);
            buffer.writeByte(modelIds.length);

            for (int i = 0; i < modelIds.length; i++) {
                buffer.writeShort(modelIds[i]);
            }
        }

        if (name != null) {
            buffer.writeByte(2);
            buffer.writeString10(name);
        }

        if (description != null) {
            buffer.writeByte(3);
            buffer.writeString10(description);
        }

        if (size != 1) {
            buffer.writeByte(12);
            buffer.writeByte(size);
        }

        if (idleAnimation != -1) {
            buffer.writeByte(13);
            buffer.writeShort(idleAnimation);
        }

        if (walkingAnimation != -1) {
            buffer.writeByte(14);
            buffer.writeShort(walkingAnimation);
        }

        if (idleAnimation != -1 || walkingAnimation != -1 || halfTurnAnimation != -1 || rotateClockwiseAnimation != -1 || rotateAntiClockwiseAnimation != -1) {
            buffer.writeByte(17);
            buffer.writeShort(walkingAnimation);
            buffer.writeShort(halfTurnAnimation);
            buffer.writeShort(rotateClockwiseAnimation);
            buffer.writeShort(rotateAntiClockwiseAnimation);
        }

        if (actions != null) {
            for (int i = 0; i < actions.length; i++) {
                if (actions[i] == null) {
                    continue;
                }

                buffer.writeByte(30 + i);
                buffer.writeString10(actions[i]);
            }
        }

        if (originalColours != null && replacementColours != null) {
            buffer.writeByte(40);
            buffer.writeByte(originalColours.length);

            for (int i = 0; i < originalColours.length; i++) {
                buffer.writeShort(originalColours[i]);
                buffer.writeShort(replacementColours[i]);
            }
        }

        if (additionalModels != null) {
            buffer.writeByte(60);
            buffer.writeByte(additionalModels.length);
            for (int i = 0; i < additionalModels.length; i++) {
                buffer.writeShort(additionalModels[i]);
            }
        }

        if (!drawMinimapDot) {
            buffer.writeByte(93);
        }

        if (combat != -1) {
            buffer.writeByte(95);
            buffer.writeShort(combat);
        }

        if (scaleXY != 128) {
            buffer.writeByte(97);
            buffer.writeShort(scaleXY);
        }

        if (scaleZ != 128) {
            buffer.writeByte(98);
            buffer.writeShort(scaleZ);
        }

        if (priorityRender) {
            buffer.writeByte(99);
        }

        if (lightModifier != 0) {
            buffer.writeByte(100);
            buffer.writeByte(lightModifier);
        }

        if (shadowModifier != 0) {
            buffer.writeByte(101);
            buffer.writeByte(shadowModifier / 5);
        }

        if (headIcon != -1) {
            buffer.writeByte(102);
            buffer.writeShort(headIcon);
        }

        if (rotation != 32) {
            buffer.writeByte(103);
            buffer.writeShort(rotation);
        }

        if ((varbit != -1 || varp != -1) && morphisms != null) {
            buffer.writeByte(106);
            buffer.writeShort(varbit);
            buffer.writeShort(varp);

            buffer.writeByte(morphisms.length - 1);

            for (int i = 0; i <= morphisms.length - 1; i++) {
                buffer.writeShort(morphisms[i]);
            }
        }

        if (!clickable) {
            buffer.writeByte(107);
        }

        buffer.writeByte(0);
    }

    private int[] additionalModels;
    private boolean clickable = true;
    private int combat = -1;
    private String description;
    private boolean drawMinimapDot = true;
    private int halfTurnAnimation = -1;
    private int headIcon = -1;
    private long id = -1;
    private int idleAnimation = -1;
    private String[] actions;
    private int lightModifier;
    private int[] modelIds;
    private int[] morphisms;
    private int varbit = -1;
    private int varp = -1;
    private String name;
    private int[] originalColours;
    private boolean priorityRender = false;
    private int[] replacementColours;
    private int rotateAntiClockwiseAnimation = -1;
    private int rotateClockwiseAnimation = -1;
    private int rotation = 32;
    private int scaleXY = 128;
    private int scaleZ = 128;
    private int shadowModifier;
    private byte size = 1;
    private int walkingAnimation = -1;
}
