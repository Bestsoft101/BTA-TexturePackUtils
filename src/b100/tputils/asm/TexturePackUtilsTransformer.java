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
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import b100.asm.AccessTransformer;
import b100.asmloader.ClassTransformer;
import b100.tputils.betterfoliage.asm.BetterFoliageTransformer;

public class TexturePackUtilsTransformer extends ClassTransformer {
	
	public BetterFoliageTransformer betterFoliageTransformer = new BetterFoliageTransformer();
	
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
	
	public static class MinecraftTransformer extends ClassTransformer {
		
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
	
	public static class RenderEngineTransformer extends ClassTransformer {

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
	
	public static class WorldRendererTransformer extends ClassTransformer {

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

}
