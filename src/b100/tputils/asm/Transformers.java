package b100.tputils.asm;

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
import b100.tputils.asm.utils.ASMHelper;
import b100.tputils.asm.utils.FindInstruction;

public class Transformers extends ClassTransformer {
	
	private static final String listenerClass = "b100/tputils/asm/Listeners";
	
	public List<Transformer> transformers = new ArrayList<>();
	public Map<String, String> accessTransform = new HashMap<>();
	
	public Transformers() {
		loadAccessTransformerData();
	}
	
	public void loadAccessTransformerData() {
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = Transformers.class.getResourceAsStream("/access.cfg");
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
			MethodNode startGame = ASMHelper.findMethod(classNode, "startGame");
			
			InsnList insnList = new InsnList();
			insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
			insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, listenerClass, "onStartup", "(Lnet/minecraft/client/Minecraft;)V"));
			startGame.instructions.insertBefore(startGame.instructions.getFirst(), insnList);
		}

	}
	
	class RenderEngineTransformer extends ClassTransformer {

		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/RenderEngine");
		}
		
		@Override
		public void transform(String className, ClassNode classNode) {
			MethodNode refreshTextures = ASMHelper.findMethod(classNode, "refreshTextures");
			
			InsnList insert = new InsnList();
			insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, listenerClass, "beforeRefreshTextures", "()V"));
			ASMHelper.insertAtStart(refreshTextures, insert);
			
			insert = new InsnList();
			insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, listenerClass, "onRefreshTextures", "()V"));
			
			ASMHelper.insertBeforeLastReturn(refreshTextures, insert);
		}
	}
	
	class WorldRendererTransformer extends ClassTransformer {

		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/WorldRenderer");
		}

		@Override
		public void transform(String className, ClassNode classNode) {
			MethodNode renderWorld = ASMHelper.findMethod(classNode, "renderWorld");
			InsnList insert = new InsnList();
			insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, listenerClass, "beginRenderWorld", "()V"));
			ASMHelper.insertAtStart(renderWorld, insert);
		}
		
	}
	
	class ChunkRendererTransformer extends ClassTransformer {

		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/ChunkRenderer");
		}

		@Override
		public void transform(String className, ClassNode classNode) {
			MethodNode updateRenderer = ASMHelper.findMethod(classNode, "updateRenderer");
			
			AbstractInsnNode render = ASMHelper.findInstruction(updateRenderer, false, (n) -> FindInstruction.methodInsn(n, "render"));
			
			InsnList insert = new InsnList();
			insert.add(new VarInsnNode(Opcodes.ILOAD, 17));
			insert.add(new VarInsnNode(Opcodes.ILOAD, 15));
			insert.add(new VarInsnNode(Opcodes.ILOAD, 16));
			insert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, listenerClass, "renderBlock", "(III)V"));
			updateRenderer.instructions.insert(render, insert);
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
				Listeners.log("drawSky method not found in RenderGlobal, custom sky color won't work!");
				return;
			}
			
			if(!replaceSkyColorMethodCall(drawSkyMethod)) {
				Listeners.log("getSkyColor method call not found in RenderGlobal.drawSky(), custom sky color won't work!");
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
				Listeners.log("updateFogColor method not found in FogManager, custom fog color won't work!");
				return;
			}

			if(!replaceSkyColorMethodCall(updateFogColorMethod)) {
				Listeners.log("getSkyColor method call not found in FogManager.updateFogColor(), custom fog color won't work!");
			}
			if(!replaceFogColorMethodCall(updateFogColorMethod)) {
				Listeners.log("getFogColor method call not found in FogManager.updateFogColor(), custom fog color won't work!");
			}
		}
	}
	
	class AtlasStitcherTransformer extends ClassTransformer {

		@Override
		public boolean accepts(String className) {
			return className.equals("net/minecraft/client/render/stitcher/AtlasStitcher");
		}

		@Override
		public void transform(String className, ClassNode classNode) {
			MethodNode generateAtlas = ASMHelper.findMethod(classNode, "generateAtlas");
			
			AbstractInsnNode getResourceAsStreamNode = ASMHelper.findInstruction(generateAtlas, false, (n) -> FindInstruction.methodInsn(n, "getResourceAsStream"));
			AbstractInsnNode readImageNode = ASMHelper.findInstruction(generateAtlas, false, (n) -> FindInstruction.methodInsn(n, "readImageUnhandled"));
			
			ASMHelper.replaceInstruction(generateAtlas, getResourceAsStreamNode, new MethodInsnNode(Opcodes.INVOKESTATIC, listenerClass, "getTextureOverride", "(Lnet/minecraft/client/render/texturepack/TexturePackList;Ljava/lang/String;)Ljava/awt/image/BufferedImage;"));
			generateAtlas.instructions.remove(readImageNode);
		}
	}
	
	private static boolean replaceSkyColorMethodCall(MethodNode method) {
		AbstractInsnNode first = method.instructions.getFirst();
		
		AbstractInsnNode oldNode = ASMHelper.findInstruction(first, false, (n) -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && FindInstruction.methodInsn(n, "net/minecraft/core/world/World", "getSkyColor", "(Lnet/minecraft/client/render/camera/ICamera;F)Lnet/minecraft/core/util/phys/Vec3d;"));
		if(oldNode == null) {
			return false;
		}
		
		AbstractInsnNode newNode = new MethodInsnNode(Opcodes.INVOKESTATIC, listenerClass, "getSkyColor", "(Lnet/minecraft/core/world/World;Lnet/minecraft/client/render/camera/ICamera;F)Lnet/minecraft/core/util/phys/Vec3d;");
		replaceInstruction(method, oldNode, newNode);
		
		return true;
	}
	
	private static boolean replaceFogColorMethodCall(MethodNode method) {
		AbstractInsnNode first = method.instructions.getFirst();
		
		AbstractInsnNode oldNode = ASMHelper.findInstruction(first, false, (n) -> n.getOpcode() == Opcodes.INVOKEVIRTUAL && FindInstruction.methodInsn(n, "net/minecraft/core/world/World", "getFogColor", "(F)Lnet/minecraft/core/util/phys/Vec3d;"));
		if(oldNode == null) {
			return false;
		}
		
		AbstractInsnNode newNode = new MethodInsnNode(Opcodes.INVOKESTATIC, listenerClass, "getFogColor", "(Lnet/minecraft/core/world/World;F)Lnet/minecraft/core/util/phys/Vec3d;");
		replaceInstruction(method, oldNode, newNode);
		
		return true;
	}
	
	private static void replaceInstruction(MethodNode method, AbstractInsnNode oldIns, AbstractInsnNode newIns) {
		AbstractInsnNode prev = oldIns.getPrevious();
		method.instructions.remove(oldIns);
		method.instructions.insert(prev, newIns);
	}
}
