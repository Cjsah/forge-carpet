// noinspection all

var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var IntInsnNode = Java.type('org.objectweb.asm.tree.IntInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');

var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

function initializeCoreMod() {
    return {
        'customSticky': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.level.block.piston.PistonStructureResolver',
                'methodName': 'addBlockLine',
                'methodDesc': '(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                var label1 = new LabelNode();
                var label2 = new LabelNode();
                var label3 = new LabelNode();
                instructions.insertBefore(
                    getInstruction(instructions, Opcodes.GETFIELD, 19),
                    ASM.listOf(
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            0
                        ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/world/level/block/piston/PistonStructureResolver",
                            "level",
                            "Lnet/minecraft/world/level/Level;"
                        ),
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            11
                        ),
                        new MethodInsnNode(
                            Opcodes.INVOKEVIRTUAL,
                            "net/minecraft/world/level/Level",
                            "getBlockState",
                            "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                            false
                        ),
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            11
                        ),
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            0
                        ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/world/level/block/piston/PistonStructureResolver",
                            "pushDirection",
                            "Lnet/minecraft/core/Direction;"
                        ),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/cjsah/mod/carpet/asm/PistonStructureResolverUtil",
                            "redirectIsStickyBlock",
                            "(Lnet/minecraft/world/level/block/piston/PistonStructureResolver;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z",
                            false
                        ),
                        new JumpInsnNode(
                            Opcodes.IFEQ,
                            label1
                        ),
                        new InsnNode(
                            Opcodes.ICONST_0
                        ),
                        new InsnNode(
                            Opcodes.IRETURN
                        ),
                        label1,
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            0
                        )
                    )
                )

                instructions.insertBefore(
                    getInstruction(instructions, Opcodes.GETFIELD, 18),
                    ASM.listOf(
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            0
                        ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/world/level/block/piston/PistonStructureResolver",
                            "level",
                            "Lnet/minecraft/world/level/Level;"
                        ),
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            8
                        ),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/cjsah/mod/carpet/asm/PistonStructureResolverUtil",
                            "stickToStickySide",
                            "(Lnet/minecraft/world/level/block/piston/PistonStructureResolver;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z",
                            false
                        ),
                        new JumpInsnNode(
                            Opcodes.IFEQ,
                            label2
                        ),
                        new InsnNode(
                            Opcodes.ICONST_0
                        ),
                        new InsnNode(
                            Opcodes.IRETURN
                        ),
                        label2,
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            0
                        )
                    )
                )

                var node = getInstruction(instructions, Opcodes.INVOKEVIRTUAL, 8);
                instructions.insertBefore(
                    node,
                    ASM.listOf(
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            0
                        ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/world/level/block/piston/PistonStructureResolver",
                            "level",
                            "Lnet/minecraft/world/level/Level;"
                        ),
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            0
                        ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/world/level/block/piston/PistonStructureResolver",
                            "pushDirection",
                            "Lnet/minecraft/core/Direction;"
                        ),
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            6
                        ),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/cjsah/mod/carpet/asm/PistonStructureResolverUtil",
                            "isDraggingPreviousBlockBehind",
                            "(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z",
                            false
                        )
                    )
                )

                instructions.remove(node);

                instructions.insertBefore(
                    getInstruction(instructions, Opcodes.ALOAD, 15),
                    label3
                )

                instructions.insertBefore(
                    getInstruction(instructions, Opcodes.INVOKEVIRTUAL, 3),
                    ASM.listOf(
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            0
                        ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/world/level/block/piston/PistonStructureResolver",
                            "pushDirection",
                            "Lnet/minecraft/core/Direction;"
                        ),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/cjsah/mod/carpet/asm/PistonStructureResolverUtil",
                            "blockCanBePulled",
                            "(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
                            false
                        ),
                        new JumpInsnNode(
                            Opcodes.IFNE,
                            label3
                        ),
                        new VarInsnNode(
                            Opcodes.ALOAD,
                            3
                        )
                    )
                );
                return methodNode;
            }
        }
    }
}

function getInstruction(/*org.objectweb.asm.tree.InsnList*/ insnList, /*int*/ opCode, /*int*/ index) {
    var i = 0;
    for (var j = 0; j < insnList.size(); j++) {
        var ain = insnList.get(j);
        if (ain.getOpcode() == opCode && i++ == index) {
            return ain;
        }
    }
    return null;
}
