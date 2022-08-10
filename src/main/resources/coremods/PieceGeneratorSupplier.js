// noinspection all

var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

function initializeCoreMod() {
    return {
        'plop': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier',
                'methodName': 'lambda$simple$0',
                'methodDesc': '(Ljava/util/function/Predicate;Ljava/util/Optional;Lnet/minecraft/world/level/levelgen/structure/pieces/PieceGeneratorSupplier$Context;)Ljava/util/Optional;'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                var label = new LabelNode();
                instructions.insertBefore(
                    getInstruction(instructions, Opcodes.ALOAD, 0),
                    ASM.listOf(
                        new FieldInsnNode(
                            Opcodes.GETSTATIC,
                            'net/cjsah/mod/carpet/CarpetSettings',
                            'skipGenerationChecks',
                            'Ljava/lang/ThreadLocal;'
                        ),
                        new MethodInsnNode(
                            Opcodes.INVOKEVIRTUAL,
                            "java/lang/ThreadLocal",
                            "get",
                            "()Ljava/lang/Object;",
                            false
                        ),
                        new TypeInsnNode(
                            Opcodes.CHECKCAST,
                            "java/lang/Boolean"
                        ),
                        new MethodInsnNode(
                            Opcodes.INVOKEVIRTUAL,
                            "java/lang/Boolean",
                            "booleanValue",
                            "()Z",
                            false
                        ),
                        new JumpInsnNode(
                            Opcodes.IFNE,
                            label
                        )
                    )
                );
                instructions.insertBefore(
                    getInstruction(instructions, Opcodes.ALOAD, 2),
                    label
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
