// noinspection all

var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');

function initializeCoreMod() {
    return {
        'fillUpdates': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.entity.animal.horse.SkeletonTrapGoal',
                'methodName': 'lambda$tick$0',
                'methodDesc': '(Lnet/minecraft/server/level/ServerLevel;)V'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                instructions.set(
                    getInstruction(instructions, Opcodes.INVOKEVIRTUAL, 22),
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "net/cjsah/mod/carpet/utils/RandomTools",
                        "nextGauBian",
                        "(Ljava/util/Random;)D",
                        false
                    )
                )
                instructions.set(
                    getInstruction(instructions, Opcodes.INVOKEVIRTUAL, 20),
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "net/cjsah/mod/carpet/utils/RandomTools",
                        "nextGauBian",
                        "(Ljava/util/Random;)D",
                        false
                    )
                )
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
