package b100.tputils.coloredtextures.asm;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import b100.asmloader.ClassTransformer;
import b100.tputils.asm.TexturePackUtilsASM;

public class TextureOverrideTransformer extends ClassTransformer {
	
	public final Set<String> classesToOverride = new HashSet<>();
	
	public TextureOverrideTransformer() {
		addOverrides();
	}
	
	public void addOverrides() {
		classesToOverride.add("net/minecraft/core/block/BlockPlanksPainted");
		classesToOverride.add("net/minecraft/core/block/BlockFencePainted");
		classesToOverride.add("net/minecraft/core/block/BlockFenceGatePainted");
		classesToOverride.add("net/minecraft/core/block/BlockSlabPainted");
		classesToOverride.add("net/minecraft/core/block/BlockStairsPainted");
		classesToOverride.add("net/minecraft/core/block/BlockChestPainted");
		classesToOverride.add("net/minecraft/core/block/BlockLamp");
	}

	@Override
	public boolean accepts(String className) {
		return classesToOverride.contains(className);
	}

	@Override
	public void transform(String className, ClassNode classNode) {
		TexturePackUtilsASM.log("Adding texture override: " + className);
		
		// These methods need to be overwritten
		MethodNode method_getBlockTextureFromSideAndMetadata = null;
		MethodNode method_getBlockTexture = null;
		
		// Check for existing methods
		for(MethodNode method : classNode.methods) {
			if(method.name.equals("getBlockTexture")) method_getBlockTexture = method;
			if(method.name.equals("getBlockTextureFromSideAndMetadata")) method_getBlockTextureFromSideAndMetadata = method;
		}
		
		// Create methods if they dont exist
		if(method_getBlockTexture == null) {
			TexturePackUtilsASM.debug("getBlockTexture not found!");
			method_getBlockTexture = new MethodNode(0, "getBlockTexture", "(Lnet/minecraft/core/world/WorldSource;IIILnet/minecraft/core/util/helper/Side;)I", null, null);
			classNode.methods.add(method_getBlockTexture);
		}else {
			TexturePackUtilsASM.debug("getBlockTexture exists!");
			
		}
		if(method_getBlockTextureFromSideAndMetadata == null) {
			TexturePackUtilsASM.debug("getBlockTextureFromSideAndMetadata not found!");
			method_getBlockTextureFromSideAndMetadata = new MethodNode(0, "getBlockTextureFromSideAndMetadata", "(Lnet/minecraft/core/util/helper/Side;I)I", null, null);
			classNode.methods.add(method_getBlockTextureFromSideAndMetadata);
		}else {
			TexturePackUtilsASM.debug("getBlockTextureFromSideAndMetadata exists!");
		}
		
		// Add or replace code
		InsnList add = new InsnList();
		add.add(new VarInsnNode(Opcodes.ALOAD, 0));
		add.add(new VarInsnNode(Opcodes.ALOAD, 1));
		add.add(new VarInsnNode(Opcodes.ILOAD, 2));
		add.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/coloredtextures/asm/ColoredTexturesASM", "getTexture", "(Lnet/minecraft/core/block/Block;Lnet/minecraft/core/util/helper/Side;I)I"));
		add.add(new InsnNode(Opcodes.IRETURN));
		method_getBlockTextureFromSideAndMetadata.instructions = add;
		
		add = new InsnList();
		add.add(new VarInsnNode(Opcodes.ALOAD, 0));
		add.add(new VarInsnNode(Opcodes.ALOAD, 1));
		add.add(new VarInsnNode(Opcodes.ILOAD, 2));
		add.add(new VarInsnNode(Opcodes.ILOAD, 3));
		add.add(new VarInsnNode(Opcodes.ILOAD, 4));
		add.add(new VarInsnNode(Opcodes.ALOAD, 5));
		add.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/coloredtextures/asm/ColoredTexturesASM", "getTexture", "(Lnet/minecraft/core/block/Block;Lnet/minecraft/core/world/WorldSource;IIILnet/minecraft/core/util/helper/Side;)I"));
		add.add(new InsnNode(Opcodes.IRETURN));
		method_getBlockTexture.instructions = add;
	}

}
