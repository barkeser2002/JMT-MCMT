package org.jmt.mcmt.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class MCMTClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        if (transformedName.equals("net.minecraft.world.WorldServer")) {
            return transformWorldServer(basicClass);
        } else if (transformedName.equals("net.minecraft.world.World")) {
            return transformWorld(basicClass);
        } else if (transformedName.equals("net.minecraft.world.gen.ChunkProviderServer")) {
            return transformChunkProviderServer(basicClass);
        }

        return basicClass;
    }

    private byte[] transformWorldServer(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if ((method.name.equals("tick") || method.name.equals("func_72835_b")) && method.desc.equals("()V")) {
                InsnList il = new InsnList();
                LabelNode skipLabel = new LabelNode();

                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/jmt/mcmt/asmdest/ASMHookTerminator", "callTick", "(Lnet/minecraft/world/WorldServer;)Z", false));
                il.add(new JumpInsnNode(Opcodes.IFEQ, skipLabel));
                il.add(new InsnNode(Opcodes.RETURN));
                il.add(skipLabel);

                method.instructions.insert(method.instructions.getFirst(), il);
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private byte[] transformWorld(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if ((method.name.equals("updateEntities") || method.name.equals("func_72939_s")) && method.desc.equals("()V")) {
                InsnList il = new InsnList();
                LabelNode skipLabel = new LabelNode();

                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/jmt/mcmt/asmdest/ASMHookTerminator", "callEntityTick", "(Lnet/minecraft/world/World;)Z", false));
                il.add(new JumpInsnNode(Opcodes.IFEQ, skipLabel));
                il.add(new InsnNode(Opcodes.RETURN));
                il.add(skipLabel);

                method.instructions.insert(method.instructions.getFirst(), il);
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private byte[] transformChunkProviderServer(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if ((method.name.equals("tick") || method.name.equals("func_73156_b")) && method.desc.equals("()Z")) {
                InsnList il = new InsnList();
                LabelNode skipLabel = new LabelNode();

                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/jmt/mcmt/asmdest/ASMHookTerminator", "callChunkProviderTick", "(Lnet/minecraft/world/gen/ChunkProviderServer;)Z", false));
                il.add(new JumpInsnNode(Opcodes.IFEQ, skipLabel));
                il.add(new InsnNode(Opcodes.ICONST_1));
                il.add(new InsnNode(Opcodes.IRETURN));
                il.add(skipLabel);

                method.instructions.insert(method.instructions.getFirst(), il);
            }
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
