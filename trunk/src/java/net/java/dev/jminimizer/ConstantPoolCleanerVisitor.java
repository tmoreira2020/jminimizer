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
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
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
    
    public ConstantPoolCleanerVisitor() {
        newPool= new ConstantPoolGen();
    }


    /**
     * @see org.apache.bcel.classfile.Visitor#visitCode(org.apache.bcel.classfile.Code)
     */
    public void visitCode(Code obj) {
        //System.out.println("Visitor.visitCode");
        obj.setNameIndex(newPool.addUtf8(Constants.ATTRIBUTE_NAMES[Constants.ATTR_CODE]));
        InstructionList list= new InstructionList(obj.getCode());
        CodeException[] codeExceptions= obj.getExceptionTable();
        InstructionHandle[] startIH= new InstructionHandle[codeExceptions.length];
        InstructionHandle[] endIH= new InstructionHandle[codeExceptions.length];
        InstructionHandle[] handlerIH= new InstructionHandle[codeExceptions.length];
        for (int i = 0; i < codeExceptions.length; i++) {
            codeExceptions[i].accept(this);
            startIH[i]= list.findHandle(codeExceptions[i].getStartPC());
            endIH[i]= list.findHandle(codeExceptions[i].getEndPC());
            handlerIH[i]= list.findHandle(codeExceptions[i].getHandlerPC());
        }
        list.replaceConstantPool(oldPool, newPool);
        list.setPositions(true);
        for (InstructionHandle ih= list.getStart(); ih != null ; ih= ih.getNext()) {
            for (int i = 0; i < startIH.length; i++) {
                if (ih == startIH[i]) {
                    codeExceptions[i].setStartPC(ih.getPosition());
                }
                if (ih == endIH[i]) {
                    codeExceptions[i].setEndPC(ih.getPosition());
                }
                if (ih == handlerIH[i]) {
                    codeExceptions[i].setHandlerPC(ih.getPosition());
                }
            }
        }
        obj.setCode(list.getByteCode());
        Attribute[] attributes= obj.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            attributes[i].accept(this);
        }
        obj.setConstantPool(newPool.getConstantPool());
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitCodeException(org.apache.bcel.classfile.CodeException)
     */
    public void visitCodeException(CodeException obj) {
        //System.out.println("Visitor.visitCodeException");
        //System.out.println(obj);
        obj.setCatchType(newPool.addConstant(oldPool.getConstant(obj.getCatchType()), oldPool));
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantClass(org.apache.bcel.classfile.ConstantClass)
     */
    public void visitConstantClass(ConstantClass obj) {
        System.out.println("Visitor.visitConstantClass");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantDouble(org.apache.bcel.classfile.ConstantDouble)
     */
    public void visitConstantDouble(ConstantDouble obj) {
        System.out.println("Visitor.visitConstantDouble");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantFieldref(org.apache.bcel.classfile.ConstantFieldref)
     */
    public void visitConstantFieldref(ConstantFieldref obj) {
        System.out.println("Visitor.visitConstantFieldref");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantFloat(org.apache.bcel.classfile.ConstantFloat)
     */
    public void visitConstantFloat(ConstantFloat obj) {
        System.out.println("Visitor.visitConstantFloat");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantInteger(org.apache.bcel.classfile.ConstantInteger)
     */
    public void visitConstantInteger(ConstantInteger obj) {
        System.out.println("Visitor.visitConstantInteger");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantInterfaceMethodref(org.apache.bcel.classfile.ConstantInterfaceMethodref)
     */
    public void visitConstantInterfaceMethodref(ConstantInterfaceMethodref obj) {
        System.out.println("Visitor.visitConstantInterfaceMethodref");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantLong(org.apache.bcel.classfile.ConstantLong)
     */
    public void visitConstantLong(ConstantLong obj) {
        System.out.println("Visitor.visitConstantLong");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantMethodref(org.apache.bcel.classfile.ConstantMethodref)
     */
    public void visitConstantMethodref(ConstantMethodref obj) {
        System.out.println("Visitor.visitConstantMethodref");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantNameAndType(org.apache.bcel.classfile.ConstantNameAndType)
     */
    public void visitConstantNameAndType(ConstantNameAndType obj) {
        System.out.println("Visitor.visitConstantNameAndType");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantPool(org.apache.bcel.classfile.ConstantPool)
     */
    public void visitConstantPool(ConstantPool obj) {
        System.out.println("Visitor.visitConstantPool");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantString(org.apache.bcel.classfile.ConstantString)
     */
    public void visitConstantString(ConstantString obj) {
        System.out.println("Visitor.visitConstantString");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantUtf8(org.apache.bcel.classfile.ConstantUtf8)
     */
    public void visitConstantUtf8(ConstantUtf8 obj) {
        System.out.println("Visitor.visitConstantUtf8");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitConstantValue(org.apache.bcel.classfile.ConstantValue)
     */
    public void visitConstantValue(ConstantValue obj) {
        System.out.println("Visitor.visitConstantValue");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitDeprecated(org.apache.bcel.classfile.Deprecated)
     */
    public void visitDeprecated(Deprecated obj) {
        System.out.println("Visitor.visitDeprecated");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitExceptionTable(org.apache.bcel.classfile.ExceptionTable)
     */
    public void visitExceptionTable(ExceptionTable obj) {
        //System.out.println("Visitor.visitExceptionTable");
        //System.out.println(obj);
        obj.setNameIndex(newPool.addUtf8(Constants.ATTRIBUTE_NAMES[Constants.ATTR_EXCEPTIONS]));
        int[] exceptions=obj.getExceptionIndexTable();
        for (int i = 0; i < exceptions.length; i++) {
            exceptions[i]= newPool.addConstant(oldPool.getConstant(exceptions[i]), oldPool);
        }
        obj.setExceptionIndexTable(exceptions);
        obj.setConstantPool(newPool.getConstantPool());
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitField(org.apache.bcel.classfile.Field)
     */
    public void visitField(Field obj) {
        //System.out.println("Visitor.visitField");
        //System.out.println(obj);
        obj.setNameIndex(newPool.addConstant(oldPool.getConstant(obj.getNameIndex()), oldPool));
        obj.setSignatureIndex(newPool.addConstant(oldPool.getConstant(obj.getSignatureIndex()), oldPool));
        Attribute[] attributes= obj.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            attributes[i].accept(this);
        }
        obj.setConstantPool(newPool.getConstantPool());
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitInnerClass(org.apache.bcel.classfile.InnerClass)
     */
    public void visitInnerClass(InnerClass obj) {
        //System.out.println("Visitor.visitInnerClass");
        //System.out.println(obj);
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
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitInnerClasses(org.apache.bcel.classfile.InnerClasses)
     */
    public void visitInnerClasses(InnerClasses obj) {
        //System.out.println("Visitor.visitInnerClasses");
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
        //System.out.println("Visitor.visitJavaClass");
        oldPool= new ConstantPoolGen(obj.getConstantPool());
        obj.setClassNameIndex(newPool.addClass(obj.getClassName()));
        obj.setSuperclassNameIndex(newPool.addClass(obj.getSuperclassName()));
        Attribute[] attributes= obj.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            attributes[i].accept(this);
        }
        Field[] fields= obj.getFields();
        for (int i = 0; i < fields.length; i++) {
           fields[i].accept(this);
        }
        Method[] methods= obj.getMethods();
        for (int i = 0; i < methods.length; i++) {
           methods[i].accept(this);
        }
        //System.out.println(newPool.getFinalConstantPool());
        obj.setConstantPool(newPool.getFinalConstantPool());
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitLineNumber(org.apache.bcel.classfile.LineNumber)
     */
    public void visitLineNumber(LineNumber obj) {
        //System.out.println("Visitor.visitLineNumber");
        //System.out.println(obj);
        //Não é preciso fazer nada
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitLineNumberTable(org.apache.bcel.classfile.LineNumberTable)
     */
    public void visitLineNumberTable(LineNumberTable obj) {
        //System.out.println("Visitor.visitLineNumberTable");
        //System.out.println(obj);
        obj.setNameIndex(newPool.addUtf8(Constants.ATTRIBUTE_NAMES[Constants.ATTR_LINE_NUMBER_TABLE]));
        LineNumber[] lineNumbers= obj.getLineNumberTable();
        for (int i = 0; i < lineNumbers.length; i++) {
            lineNumbers[i].accept(this);
        }
        obj.setConstantPool(newPool.getConstantPool());
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitLocalVariable(org.apache.bcel.classfile.LocalVariable)
     */
    public void visitLocalVariable(LocalVariable obj) {
        //System.out.println("Visitor.visitLocalVariable");
        //System.out.println(obj);
        obj.setNameIndex(newPool.addConstant(oldPool.getConstant(obj.getNameIndex()), oldPool));
        obj.setSignatureIndex(newPool.addConstant(oldPool.getConstant(obj.getSignatureIndex()), oldPool));
        obj.setConstantPool(newPool.getConstantPool());
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitLocalVariableTable(org.apache.bcel.classfile.LocalVariableTable)
     */
    public void visitLocalVariableTable(LocalVariableTable obj) {
        //System.out.println("Visitor.visitLocalVariableTable");
        //System.out.println(obj);
        obj.setNameIndex(newPool.addUtf8(Constants.ATTRIBUTE_NAMES[Constants.ATTR_LOCAL_VARIABLE_TABLE]));
        LocalVariable[] localVariables=obj.getLocalVariableTable();
        for (int i = 0; i < localVariables.length; i++) {
            localVariables[i].accept(this);
        }
        obj.setConstantPool(newPool.getConstantPool());
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitMethod(org.apache.bcel.classfile.Method)
     */
    public void visitMethod(Method obj) {
        //System.out.println("Visitor.visitMethod");
        //sSystem.out.println(obj);
        obj.setNameIndex(newPool.addConstant(oldPool.getConstant(obj.getNameIndex()), oldPool));
        obj.setSignatureIndex(newPool.addConstant(oldPool.getConstant(obj.getSignatureIndex()), oldPool));
        Attribute[] attributes= obj.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            attributes[i].accept(this);
        }
        obj.setConstantPool(newPool.getConstantPool());
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitSignature(org.apache.bcel.classfile.Signature)
     */
    public void visitSignature(Signature obj) {
        System.out.println("Visitor.visitSignature");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitSourceFile(org.apache.bcel.classfile.SourceFile)
     */
    public void visitSourceFile(SourceFile obj) {
        //System.out.println("Visitor.visitSourceFile");
        //System.out.println(obj);
        obj.setNameIndex(newPool.addUtf8(Constants.ATTRIBUTE_NAMES[Constants.ATTR_SOURCE_FILE]));
        obj.setSourceFileIndex(newPool.addUtf8(obj.getSourceFileName()));
        obj.setConstantPool(newPool.getConstantPool());
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitSynthetic(org.apache.bcel.classfile.Synthetic)
     */
    public void visitSynthetic(Synthetic obj) {
        //System.out.println("Visitor.visitSynthetic");
        //System.out.println(obj);
        obj.setNameIndex(newPool.addUtf8(Constants.ATTRIBUTE_NAMES[Constants.ATTR_SYNTHETIC]));
        obj.setConstantPool(newPool.getConstantPool());
        //System.out.println(obj);
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitUnknown(org.apache.bcel.classfile.Unknown)
     */
    public void visitUnknown(Unknown obj) {
        System.out.println("Visitor.visitUnknown");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitStackMap(org.apache.bcel.classfile.StackMap)
     */
    public void visitStackMap(StackMap obj) {
        System.out.println("Visitor.visitStackMap");
    }

    /**
     * @see org.apache.bcel.classfile.Visitor#visitStackMapEntry(org.apache.bcel.classfile.StackMapEntry)
     */
    public void visitStackMapEntry(StackMapEntry obj) {
        System.out.println("Visitor.visitStackMapEntry");
    }

}