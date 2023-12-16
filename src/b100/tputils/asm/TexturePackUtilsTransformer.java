package b100.tputils.asm;

import static b100.tputils.asm.ASMHelper.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import b100.asm.AccessTransformer;
import b100.asmloader.ClassTransformer;

public class TexturePackUtilsTransformer extends ClassTransformer {
	
	public BetterFoliageRenderBlocksTransformer betterFoliageTransformer = new BetterFoliageRenderBlocksTransformer();
	
	public List<Transformer> transformers = new ArrayList<>();
	public Map<String, String> accessTransform = new HashMap<>();
	
	public TexturePackUtilsTransformer() {
		loadAccessTransformerData();
	}
	
	public void loadAccessTransformerData() {
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = TexturePackUtilsTransformer.class.getResourceAsStream("/access.cfg");
			br = new BufferedReader(new InputStreamReader(in));
			
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				if(line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				
				String[] keys = line.split(" ");
				String className = keys[1];
				
				accessTransform.put(className, line);
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}finally {
			try{
				in.close();
			}catch (Exception e) {}
			try{
				br.close();
			}catch (Exception e) {}
		}
	}

	@Override
	public boolean accepts(String className) {
		return accessTransform.containsKey(className);
	}

	@Override
	public void transform(String className, ClassNode classNode) {
		String transform = this.accessTransform.get(className);
		if(transform != null) {
			AccessTransformer.transform(classNode, transform);
		}
	}
	
	class MinecraftTransformer extends ClassTransformer {
		
		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/Minecraft");
		}

		@Override
		public void transform(String className, ClassNode classNode) {
			// Add startup listener
			for(MethodNode method : classNode.methods) {
				if(method.name.equals("startGame")) {
					InsnList insnList = new InsnList();
					insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
					insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/asm/TexturePackUtilsASM", "onStartup", "(Lnet/minecraft/client/Minecraft;)V"));
					method.instructions.insertBefore(method.instructions.getFirst(), insnList);
				}
			}
		}

	}
	
	class RenderEngineTransformer extends ClassTransformer {

		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/RenderEngine");
		}
		
		@Override
		public void transform(String className, ClassNode classNode) {
			// Add texture refresh listener
			for(MethodNode method : classNode.methods) {
				if(method.name.equals("refreshTextures")) {
					InsnList insnList = new InsnList();
					insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/asm/TexturePackUtilsASM", "onRefreshTextures", "()V"));
					injectBeforeEnd(method, insnList);
				}
			}
		}
		
	}
	
	class WorldRendererTransformer extends ClassTransformer {

		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/WorldRenderer");
		}

		@Override
		public void transform(String className, ClassNode classNode) {
			// Add listener before world render
			for(MethodNode method : classNode.methods) {
				if(method.name.equals("renderWorld")) {
					injectAtStart(method, new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/asm/TexturePackUtilsASM", "beginRenderWorld", "()V"));
				}
			}
		}
		
	}
	
	class BetterFoliageRenderBlocksTransformer extends ClassTransformer {

		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/RenderBlocks");
		}

		@Override
		public void transform(String className, ClassNode classNode) {
			MethodNode method = ASMHelper.findMethod(classNode, "renderStandardBlock", "(Lnet/minecraft/core/block/Block;IIIFFF)Z");
			InsnList instructions = method.instructions;
			
			AbstractInsnNode returnNode = ASMHelper.findInstruction(instructions.getLast(), true, (n) -> n.getOpcode() == Opcodes.IRETURN);

			InsnList addList = new InsnList();
			
			addList.add(new VarInsnNode(Opcodes.ALOAD, 0));
			addList.add(new VarInsnNode(Opcodes.ALOAD, 1));
			
			addList.add(new VarInsnNode(Opcodes.ILOAD, 2));
			addList.add(new VarInsnNode(Opcodes.ILOAD, 3));
			addList.add(new VarInsnNode(Opcodes.ILOAD, 4));
			
			addList.add(new VarInsnNode(Opcodes.FLOAD, 5));
			addList.add(new VarInsnNode(Opcodes.FLOAD, 6));
			addList.add(new VarInsnNode(Opcodes.FLOAD, 7));
			
			addList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/asm/TexturePackUtilsASM", "onRenderBlock", "(Lnet/minecraft/client/render/RenderBlocks;Lnet/minecraft/core/block/Block;IIIFFF)V"));

			instructions.insertBefore(returnNode, addList);
		}
	}
	
	class SeasonalColormapsRenderGlobalTransformer extends ClassTransformer {
		
		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/RenderGlobal");
		}
		
		@Override
		public void transform(String className, ClassNode classNode) {
			MethodNode drawSkyMethod = ASMHelper.findMethod(classNode, "drawSky", "(F)V");
			
			if(drawSkyMethod == null) {
				TexturePackUtilsASM.log("drawSky method not found in RenderGlobal, custom sky color won't work!");
				return;
			}
			
			if(!replaceSkyColorMethodCall(drawSkyMethod)) {
				TexturePackUtilsASM.log("getSkyColor method call not found in RenderGlobal.drawSky(), custom sky color won't work!");
				return;
			}
		}
		
	}
	
	class SeasonalColormapsFogManagerTransformer extends ClassTransformer {

		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/FogManager");
		}

		@Override
		public void transform(String className, ClassNode classNode) {
			MethodNode updateFogColorMethod = ASMHelper.findMethod(classNode, "updateFogColor", "(F)V");
			
			if(updateFogColorMethod == null) {
				TexturePackUtilsASM.log("updateFogColor method not found in FogManager, custom fog color won't work!");
				return;
			}

			if(!replaceSkyColorMethodCall(updateFogColorMethod)) {
				TexturePackUtilsASM.log("getSkyColor method call not found in FogManager.updateFogColor(), custom fog color won't work!");
			}
			if(!replaceFogColorMethodCall(updateFogColorMethod)) {
				TexturePackUtilsASM.log("getFogColor method call not found in FogManager.updateFogColor(), custom fog color won't work!");
			}
		}
		
	}
	
	private static boolean replaceSkyColorMethodCall(MethodNode method) {
		AbstractInsnNode first = method.instructions.getFirst();
		
		AbstractInsnNode oldNode = ASMHelper.findInstruction(first, false, (n) -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && FindInstruction.methodInsn(n, "net/minecraft/core/world/World", "getSkyColor", "(Lnet/minecraft/client/render/camera/ICamera;F)Lnet/minecraft/core/util/phys/Vec3d;"));
		if(oldNode == null) {
			return false;
		}
		
		AbstractInsnNode newNode = new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/asm/TexturePackUtilsASM", "getSkyColor", "(Lnet/minecraft/core/world/World;Lnet/minecraft/client/render/camera/ICamera;F)Lnet/minecraft/core/util/phys/Vec3d;");
		replaceInstruction(method, oldNode, newNode);
		
		return true;
	}
	
	private static boolean replaceFogColorMethodCall(MethodNode method) {
		AbstractInsnNode first = method.instructions.getFirst();
		
		AbstractInsnNode oldNode = ASMHelper.findInstruction(first, false, (n) -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && FindInstruction.methodInsn(n, "net/minecraft/core/world/World", "getFogColor", "(F)Lnet/minecraft/core/util/phys/Vec3d;"));
		if(oldNode == null) {
			return false;
		}
		
		AbstractInsnNode newNode = new MethodInsnNode(Opcodes.INVOKESTATIC, "b100/tputils/asm/TexturePackUtilsASM", "getFogColor", "(Lnet/minecraft/core/world/World;F)Lnet/minecraft/core/util/phys/Vec3d;");
		replaceInstruction(method, oldNode, newNode);
		
		return true;
	}
	
	private static void replaceInstruction(MethodNode method, AbstractInsnNode oldIns, AbstractInsnNode newIns) {
		AbstractInsnNode prev = oldIns.getPrevious();
		method.instructions.remove(oldIns);
		method.instructions.insert(prev, newIns);
	}


}
