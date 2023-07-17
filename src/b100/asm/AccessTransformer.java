package b100.asm;

import java.lang.reflect.Modifier;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class AccessTransformer {
	
	public static void transform(ClassNode classNode, String config) {
		String[] keys = config.split(" ");
		String newAccess = keys[0];
		String type = keys[2];
		
		if(type.equals("method")) {
			String methodName = keys[3];
			String methodDesc = keys.length > 4 ? keys[4] : null;
			
			MethodNode method = getMethod(classNode, methodName, methodDesc);
			log("Transform method "+classNode.name+" "+method.name+" "+method.desc);
			method.access = modifyAccess(method.access, newAccess);
		}else if(type.equals("field")) {
			String fieldName = keys[3];
			
			FieldNode field = getField(classNode, fieldName);
			log("Transform field "+classNode.name+" "+field.name);
			field.access = modifyAccess(field.access, newAccess);
		}else {
			throw new RuntimeException("Invalid transform type: '"+type+"'");
		}
	}
	
	private static int modifyAccess(int access, String newAccess) {
		if(newAccess.equals("public")) {
			access &= ~Modifier.PRIVATE;
			access &= ~Modifier.PROTECTED;
			access |= Modifier.PUBLIC;
			return access;
		}else if(newAccess.equals("unfinal")) {
			access &= ~Modifier.FINAL;
			return access;
		}else {
			throw new RuntimeException("Invalid access modifier: '"+newAccess+"'!"); 
		}
	}
	
	private static MethodNode getMethod(ClassNode classNode, String methodName, String methodDesc) {
		MethodNode method = null;
		for(MethodNode methodNode : classNode.methods) {
			if(methodNode.name.equals(methodName) && (methodDesc == null || methodNode.desc.equals(methodDesc))) {
				if(method != null) {
					throw new RuntimeException("Found multiple matches for method '"+methodName+"' in class '"+classNode.name+"'!");
				}
				method = methodNode;
			}
		}
		
		if(method == null) {
			throw new NullPointerException("Could not find method '"+methodName+"' in class '"+classNode.name+"'!");
		}
		return method;
	}
	
	private static FieldNode getField(ClassNode classNode, String fieldName) {
		for(FieldNode fieldNode : classNode.fields) {
			if(fieldNode.name.equals(fieldName)) {
				return fieldNode;
			}
		}
		throw new NullPointerException("Could not find field '"+fieldName+"' in class '"+classNode.name+"'!");
	}
	
	public static void log(String string) {
		System.out.print("[AT] "+string+"\n");
	}

}
