package b100.tputils.asm;

import static b100.tputils.asm.ASMHelper.*;

import org.objectweb.asm.tree.ClassNode;

public abstract class Transformer {
	
	public byte[] preTransform(String className, byte[] bytes) {
		if(!accepts(className)) {
			return bytes;
		}
		
		ClassNode classNode = getClassNode(bytes);
		TexturePackUtilsASM.log("Transforming "+className);
		ClassNode transformedClass = null;
		try{
			transformedClass = transform(className, classNode);
		}catch (Exception e) {
			TexturePackUtilsASM.log("Transformation failed with exception!");
			e.printStackTrace();
		}
		if(transformedClass != null) {
			return getBytes(classNode);
		}else {
			TexturePackUtilsASM.log("Transformation failed!");
			return bytes;
		}
	}
	
	public abstract boolean accepts(String className);
	
	public abstract ClassNode transform(String className, ClassNode classNode);
	
}
