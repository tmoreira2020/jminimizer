package net.java.dev.jminimizer;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.Deprecated;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Signature;
import org.apache.bcel.classfile.SourceFile;
import org.apache.bcel.classfile.StackMap;
import org.apache.bcel.classfile.StackMapEntry;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.classfile.Unknown;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Thiago Leão Moreira
 * @since Jul 8, 2004
 *  
 */
public class ConstantPoolCleanerVisitor implements org.apache.bcel.classfile.Visitor {

    private static final Log log = LogFactory.getLog(ConstantPoolCleanerVisitor.class);
    private ConstantPoolGen newPool;
    private ConstantPoolGen oldPool;
    private boolean deepStripment;
    
    public ConstantPoolCleanerVisitor(boolean deepStripment) {
        newPool= new ConstantPoolGen();
        this.deepStripment= deepStripment;
    }


    /**
     * @see org.apache.bcel.classfile.Visitor#visitCode(org.apache.bcel.classfile.Code)
     */
    public void visitCode(Code obj) {
    	log.trace("Visiting method attribute of type Cope: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitCode(org.apache.bcel.classfile.Code)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitCodeException(org.apache.bcel.classfile.CodeException)
     */
    public void visitCodeException(CodeException obj) {
    	log.trace("Visiting code attribute of type CopeException: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitCodeException(org.apache.bcel.classfile.CodeException)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantClass(org.apache.bcel.classfile.ConstantClass)
     */
    public void visitConstantClass(ConstantClass obj) {
    	log.trace("Visiting Constant of type Class: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantClass(org.apache.bcel.classfile.ConstantClass)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantDouble(org.apache.bcel.classfile.ConstantDouble)
     */
    public void visitConstantDouble(ConstantDouble obj) {
    	log.trace("Visiting Constant of type Double: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantDouble(org.apache.bcel.classfile.ConstantDouble)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantFieldref(org.apache.bcel.classfile.ConstantFieldref)
     */
    public void visitConstantFieldref(ConstantFieldref obj) {
    	log.trace("Visiting Constant of type FieldRef: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantFieldref(org.apache.bcel.classfile.ConstantFieldref)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantFloat(org.apache.bcel.classfile.ConstantFloat)
     */
    public void visitConstantFloat(ConstantFloat obj) {
    	log.trace("Visiting Constant of type Float: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantFloat(org.apache.bcel.classfile.ConstantFloat)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantInteger(org.apache.bcel.classfile.ConstantInteger)
     */
    public void visitConstantInteger(ConstantInteger obj) {
    	log.trace("Visiting Constant of type Integer: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantInteger(org.apache.bcel.classfile.ConstantInteger)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantInterfaceMethodref(org.apache.bcel.classfile.ConstantInterfaceMethodref)
     */
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
    	log.trace("Visiting Constant of type InterfaceMethodref: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantInterfaceMethodref(org.apache.bcel.classfile.ConstantInterfaceMethodref)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantLong(org.apache.bcel.classfile.ConstantLong)
     */
    public void visitConstantLong(ConstantLong obj) {
    	log.trace("Visiting Constant of type Long: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantLong(org.apache.bcel.classfile.ConstantLong)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantMethodref(org.apache.bcel.classfile.ConstantMethodref)
     */
    public void visitConstantMethodref(ConstantMethodref obj) {
    	log.trace("Visiting Constant of type Methodref: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantMethodref(org.apache.bcel.classfile.ConstantMethodref)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantNameAndType(org.apache.bcel.classfile.ConstantNameAndType)
     */
    public void visitConstantNameAndType(ConstantNameAndType obj) {
    	log.trace("Visiting Constant of type NameAndType: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantNameAndType(org.apache.bcel.classfile.ConstantNameAndType)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantPool(org.apache.bcel.classfile.ConstantPool)
     */
    public void visitConstantPool(ConstantPool obj) {
    	log.trace("Visiting ConstantPool: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantPool(org.apache.bcel.classfile.ConstantPool)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantString(org.apache.bcel.classfile.ConstantString)
     */
    public void visitConstantString(ConstantString obj) {
    	log.trace("Visiting Constant of type String: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantString(org.apache.bcel.classfile.ConstantString)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantUtf8(org.apache.bcel.classfile.ConstantUtf8)
     */
    public void visitConstantUtf8(ConstantUtf8 obj) {
    	log.trace("Visiting Constant of type Utf8: nothing done!!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitConstantUtf8(org.apache.bcel.classfile.ConstantUtf8)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantValue(org.apache.bcel.classfile.ConstantValue)
     */
    public void visitConstantValue(ConstantValue obj) {
    	log.debug("Visiting field attribute of type ConstantValue");
        obj.setNameIndex(newPool.addConstant(oldPool.getConstant(obj.getNameIndex()), oldPool));
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitDeprecated(org.apache.bcel.classfile.Deprecated)
     */
    public void visitDeprecated(Deprecated obj) {
    	log.debug("Visiting field/method attribute of type Deprecated");
        obj.setNameIndex(newPool.addConstant(oldPool.getConstant(obj.getNameIndex()), oldPool));
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitExceptionTable(org.apache.bcel.classfile.ExceptionTable)
     */
    public void visitExceptionTable(ExceptionTable obj) {
    	log.debug("Visiting code attribute of type ExceptionTable");
        obj.setNameIndex(newPool.addConstant(oldPool.getConstant(obj.getNameIndex()), oldPool));
        int[] exceptions= obj.getExceptionIndexTable();
        for (int i = 0; i < exceptions.length; i++) {
			exceptions[i]= newPool.addConstant(oldPool.getConstant(exceptions[i]), oldPool);
		}
        obj.setExceptionIndexTable(exceptions);
        obj.setConstantPool(newPool.getConstantPool());
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitField(org.apache.bcel.classfile.Field)
     */
    public void visitField(Field obj) {
    	log.debug("Visiting field");
        obj.setNameIndex(newPool.addConstant(oldPool.getConstant(obj.getNameIndex()), oldPool));
        obj.setSignatureIndex(newPool.addConstant(oldPool.getConstant(obj.getSignatureIndex()), oldPool));
        Attribute[] attributes= obj.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            attributes[i].accept(this);
        }
        obj.setConstantPool(newPool.getConstantPool());
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitInnerClass(org.apache.bcel.classfile.InnerClass)
     */
    public void visitInnerClass(InnerClass obj) {
    	log.debug("Visiting innerClass");
        int index= newPool.addConstant(oldPool.getConstant(obj.getInnerClassIndex()), oldPool);
        obj.setInnerClassIndex(index);
        index= obj.getInnerNameIndex();
        if (index != 0) {
	        index= newPool.addConstant(oldPool.getConstant(index), oldPool);
	        obj.setInnerNameIndex(index);
        }
        index= obj.getOuterClassIndex();
        if (index != 0) {
	        index= newPool.addConstant(oldPool.getConstant(index), oldPool);
	        obj.setOuterClassIndex(index);
        }
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitInnerClasses(org.apache.bcel.classfile.InnerClasses)
     */
    public void visitInnerClasses(InnerClasses obj) {
    	log.debug("Visiting innerClasses");
        obj.setNameIndex(newPool.addUtf8(Constants.ATTRIBUTE_NAMES[Constants.ATTR_INNER_CLASSES]));
        InnerClass[] innerClasss=obj.getInnerClasses();
        for (int i = 0; i < innerClasss.length; i++) {
            innerClasss[i].accept(this);
        }
        obj.setConstantPool(newPool.getConstantPool());
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitJavaClass(org.apache.bcel.classfile.JavaClass)
     */
    public void visitJavaClass(JavaClass obj) {
    	log.trace("Visiting JavaClass: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitJavaClass(org.apache.bcel.classfile.JavaClass)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitLineNumber(org.apache.bcel.classfile.LineNumber)
     */
    public void visitLineNumber(LineNumber obj) {
    	log.trace("Visiting code attribute of type LineNumber: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitLineNumber(org.apache.bcel.classfile.LineNumber)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitLineNumberTable(org.apache.bcel.classfile.LineNumberTable)
     */
    public void visitLineNumberTable(LineNumberTable obj) {
    	log.trace("Visiting code attribute of type LineNumberTable: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitLineNumberTable(org.apache.bcel.classfile.LineNumberTable)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitLocalVariable(org.apache.bcel.classfile.LocalVariable)
     */
    public void visitLocalVariable(LocalVariable obj) {
    	log.trace("Visiting code attribute of type LocalVariable: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitLocalVariable(org.apache.bcel.classfile.LocalVariable)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitLocalVariableTable(org.apache.bcel.classfile.LocalVariableTable)
     */
    public void visitLocalVariableTable(LocalVariableTable obj) {
    	log.trace("Visiting code attribute of type LocalVariableTable: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitLocalVariableTable(org.apache.bcel.classfile.LocalVariableTable)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitMethod(org.apache.bcel.classfile.Method)
     */
    public void visitMethod(Method obj) {
    	log.debug("Visiting method");
        Attribute[] attributes= obj.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
        	if (attributes[i] instanceof Synthetic) {
                attributes[i].accept(this);
			}
        	//only for methods that is native or abstract
        	if (attributes[i] instanceof ExceptionTable && (obj.isAbstract() || obj.isNative())) {
                attributes[i].accept(this);
			}
        	if (attributes[i] instanceof Deprecated) {
                attributes[i].accept(this);
			}
        }
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitSignature(org.apache.bcel.classfile.Signature)
     */
    public void visitSignature(Signature obj) {
    	log.trace("Visiting signature: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitLocalVariableTable(org.apache.bcel.classfile.LocalVariableTable)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitSourceFile(org.apache.bcel.classfile.SourceFile)
     */
    public void visitSourceFile(SourceFile obj) {
        log.debug("Visiting field/method's attribute of type SourceFile: nothing done!!");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitSynthetic(org.apache.bcel.classfile.Synthetic)
     */
    public void visitSynthetic(Synthetic obj) {
        log.debug("Visiting field/method's attribute of type Synthetic");
        obj.setNameIndex(newPool.addUtf8(Constants.ATTRIBUTE_NAMES[Constants.ATTR_SYNTHETIC]));
        obj.setConstantPool(newPool.getConstantPool());
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitUnknown(org.apache.bcel.classfile.Unknown)
     */
    public void visitUnknown(Unknown obj) {
    	log.trace("Visiting Unknown attribute: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitUnknown(org.apache.bcel.classfile.Unknown)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitStackMap(org.apache.bcel.classfile.StackMap)
     */
    public void visitStackMap(StackMap obj) {
    	log.trace("Visiting code's attribute of type StackMap: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitStackMap(org.apache.bcel.classfile.StackMap)");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitStackMapEntry(org.apache.bcel.classfile.StackMapEntry)
     */
    public void visitStackMapEntry(StackMapEntry obj) {
    	log.trace("Visiting code's attribute of type StackMapEntry: nothing done!!");
    	throw new RuntimeException("Not implemented: net.java.dev.jminimizer.ConstantPoolCleanerVisitor.visitStackMapEntry(org.apache.bcel.classfile.StackMapEntry)");
    }
    
    public JavaClass cleanUpClassGen(ClassGen classGen) {
    	oldPool= classGen.getConstantPool();
    	ClassGen newClassGen= new ClassGen(classGen.getClassName(), classGen.getSuperclassName(), classGen.getFileName(), classGen.getAccessFlags(), classGen.getInterfaceNames(), newPool);
    	Attribute[] attributes= classGen.getAttributes();
    	for (int i = 0; i < attributes.length; i++) {
			attributes[i].accept(this);
		}
        Method[] methods= classGen.getMethods();
        for (int i = 0; i < methods.length; i++) {
        	//TODO used because a bug in bcel in method MethodGen.copy(String, ConstantPoolGen)
        	if (methods[i].isAbstract() || methods[i].isNative()) {
            	methods[i].setNameIndex(newPool.addConstant(oldPool.getConstant(methods[i].getNameIndex()), oldPool));
            	methods[i].setSignatureIndex(newPool.addConstant(oldPool.getConstant(methods[i].getSignatureIndex()), oldPool));
        	} else {
	            MethodGen methodGen= new MethodGen(methods[i], classGen.getClassName(), oldPool).copy(classGen.getClassName(), newPool);
	            methodGen.stripAttributes(deepStripment);
	            if (deepStripment) {
					attributes= methodGen.getAttributes();
					for (int j = 0; j < attributes.length; j++) {
						if (attributes[j] instanceof Synthetic || attributes[j] instanceof Deprecated) {
							methodGen.removeAttribute(attributes[j]);
						}
					}
				}
	            methods[i]= methodGen.getMethod();
        	}
            methods[i].accept(this);
            newClassGen.addMethod(methods[i]);
        }
        Field[] fields= classGen.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (deepStripment) {
				attributes= fields[i].getAttributes();
				for (int j = 0; j < attributes.length; j++) {
					if (attributes[j] instanceof Synthetic || attributes[j] instanceof Deprecated) {
						FieldGen fieldGen= new FieldGen(fields[i], oldPool);
						fieldGen.removeAttribute(attributes[j]);
						fields[i]= fieldGen.getField();
					}
				}
			}
			fields[i].accept(this);
			newClassGen.addField(fields[i]);			
		}
        if (deepStripment) {
        	attributes= newClassGen.getAttributes();
        	for (int i = 0; i < attributes.length; i++) {
        		if (attributes[i] instanceof SourceFile ||
        				attributes[i] instanceof Deprecated) {
        			newClassGen.removeAttribute(attributes[i]);
        		}
    		}
		}
        return newClassGen.getJavaClass();
    }
    
}