package b100.tputils.betterfoliage.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import b100.asmloader.ClassTransformer;

public class BetterFoliageTransformer extends ClassTransformer {

	@Override
	public boolean accepts(String className) {
		return className.equals("net/minecraft/client/render/RenderBlocks");
	}

	@Override
	public void transform(String className, ClassNode classNode) {
		for(MethodNode method : classNode.methods) {
			if(method.name.equals("renderStandardBlock")) {
				InsnList instructions = method.instructions;
				for(int i=0; i < instructions.size(); i++) {
					AbstractInsnNode instruction = instructions.get(i);
					if(instruction.getOpcode() == Opcodes.IRETURN) {
						InsnList addList = new InsnList();
						
						addList.add(new VarInsnNode(Opcodes.ALOAD, 0));
						addList.add(new VarInsnNode(Opcodes.ALOAD, 1));
						
						addList.add(new VarInsnNode(Opcodes.ILOAD, 2));
						addList.add(new VarInsnNode(Opcodes.ILOAD, 3));
						addList.add(new VarInsnNode(Opcodes.ILOAD, 4));
						
						addList.add(new VarInsnNode(Opcodes.FLOAD, 6));
						addList.add(new VarInsnNode(Opcodes.FLOAD, 7));
						addList.add(new VarInsnNode(Opcodes.FLOAD, 8));
						
						addList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/betterfoliage/asm/BetterFoliageASM", "onRenderBlock", "(Lnet/minecraft/client/render/RenderBlocks;Lnet/minecraft/core/block/Block;IIIFFF)V"));

						i += addList.size();
						instructions.insertBefore(instruction, addList);
					}
				}
			}
		}
	}

}
