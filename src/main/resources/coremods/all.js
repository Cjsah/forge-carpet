// noinspection all

var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
var IincInsnNode = Java.type('org.objectweb.asm.tree.IincInsnNode');
var LookupSwitchInsnNode = Java.type('org.objectweb.asm.tree.LookupSwitchInsnNode');
var TableSwitchInsnNode = Java.type('org.objectweb.asm.tree.TableSwitchInsnNode');
var InvokeDynamicInsnNode = Java.type('org.objectweb.asm.tree.InvokeDynamicInsnNode');
var MultiANewArrayInsnNode = Java.type('org.objectweb.asm.tree.MultiANewArrayInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var IntInsnNode = Java.type('org.objectweb.asm.tree.IntInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');


function initializeCoreMod() {
    return {
        'fillUpdates': {
            'target': {
                'type': 'METHOD',
                'class': '<class>',
                'methodName': '<method>',
                'methodDesc': '<desc>'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                instructions.insertBefore(
                    // <node>
                    // <list[ASM.listOf({nodes})]/node>
                );
                return methodNode;
            }
        }
    }
}

function getFirstInsn(/*org.objectweb.asm.tree.InsnList*/ insnList) {
    for (var i = 0; i < insnList.size(); i++) {
        var ain = insnList.get(i);
        if (ain.getOpcode() != -1) {
            return ain;
        }
    }
    return null;
}

function getInsn(/*org.objectweb.asm.tree.InsnList*/ insnList, /*int*/ opCode, /*int*/ index) {
    var i = 0;
    for (var j = 0; j < insnList.size(); j++) {
        var ain = insnList.get(j);
        if (ain.getOpcode() == opCode && i++ == index) {
            return ain;
        }
    }
    return null;
}

function getInsn(/*org.objectweb.asm.tree.InsnList*/ insnList, /*int*/ opCode) {
    for (var j = 0; j < insnList.size(); j++) {
        var ain = insnList.get(j);
        if (ain.getOpcode() == opCode) {
            return ain;
        }
    }
    return null;
}
