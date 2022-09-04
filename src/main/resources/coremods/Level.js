// noinspection all

var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var IntInsnNode = Java.type('org.objectweb.asm.tree.IntInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');

function getFirstInsn(/*org.objectweb.asm.tree.InsnList*/ insnList) {
    for (var i = 0; i < insnList.size(); i++) {
        var ain = insnList.get(i);
        if (ain.getOpcode() != -1) {
            return ain;
        }
    }
    return null;
}

function initializeCoreMod() {
    return {
        'fillUpdates': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.level.Level',
                'methodName': 'setBlock',
                'methodDesc': '(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                var label = new LabelNode();
                instructions.insertBefore(
                    getFirstInsn(instructions),
                    ASM.listOf(
                        new VarInsnNode(
                            Opcodes.ILOAD,
                            3
                        ),
                        new IntInsnNode(
                            Opcodes.BIPUSH,
                            16
                        ),
                        new JumpInsnNode(
                            Opcodes.IF_ICMPNE,
                            label
                        ),
                        new FieldInsnNode(
                            Opcodes.GETSTATIC,
                            "net/cjsah/mod/carpet/CarpetSettings",
                            "impendingFillSkipUpdates",
                            "Ljava/lang/ThreadLocal;"
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
                            Opcodes.IFEQ,
                            label
                        ),
                        new InsnNode(
                            Opcodes.ICONST_M1
                        ),
                        new VarInsnNode(
                            Opcodes.ISTORE,
                            3
                        ),
                        label
                    )
                );
                return methodNode;
            }
        }
    }
}
