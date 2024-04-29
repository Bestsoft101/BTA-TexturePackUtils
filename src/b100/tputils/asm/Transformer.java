package b100.tputils.asm;

import org.objectweb.asm.tree.ClassNode;

import b100.tputils.asm.utils.ASMHelper;

public abstract class Transformer {
	
	public byte[] preTransform(String className, byte[] bytes) {
		if(!accepts(className)) {
			return bytes;
		}
		
		ClassNode classNode = ASMHelper.getClassNode(bytes);
		Listeners.log("Transforming "+className);
		ClassNode transformedClass = null;
		try{
			transformedClass = transform(className, classNode);
		}catch (Exception e) {
			Listeners.log("Transformation failed with exception!");
			e.printStackTrace();
		}
		if(transformedClass != null) {
			return ASMHelper.getBytes(classNode);
		}else {
			Listeners.log("Transformation failed!");
			return bytes;
		}
	}
	
	public abstract boolean accepts(String className);
	
	public abstract ClassNode transform(String className, ClassNode classNode);
	
}
