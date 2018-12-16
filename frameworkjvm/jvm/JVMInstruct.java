package com.github.netty.protocol;

import java.lang.ref.Reference;

/**
 * 虚拟机指令 [
 *      1.常量(constants)指令, 2.加载(loads)指令, 3.存储(stores)指令, 4.操作数栈(stack)指令, 5.数学(math)指令,
 *      6.转换(conversions)指令, 7.比较(comparisons)指令, 8.控制(control)指令, 9.引用(references)指令, 10.扩展(extended)指令,
 *      11.保留(reserved)指令
 *  ]
 *  注: 其中保留指令有3条,1条是留给调试器用做断点, 助记符breadkpoint. 其余2条留给虚拟机内部用,助记符impdep1,impdep2.
 *      这3条保留指令不允许出现在class文件中
 *
 * Created by acer01 on 2018/12/16/016.
 */
public class JVMInstruct {

    public void iload(JVMFrame frame,int index){
        int value = frame.getLocalVars().getInt(index);
        frame.getOperandStack().pushInt(value);
    }

    public void istore(JVMFrame frame,int index){
        int value = frame.getOperandStack().popInt();
        frame.getLocalVars().setInt(index,value);
    }

    public void pushSlot(JVMFrame.OperandStack operandStack,JVMFrame.Slot slot){
        operandStack.pushSlot(slot);
    }

    public JVMFrame.Slot pushSlot(JVMFrame.OperandStack operandStack){
        return operandStack.popSlot();
    }

    public void iadd(JVMFrame frame){
        int value1 = frame.getOperandStack().popInt();
        int value2 = frame.getOperandStack().popInt();
        int value3 = value1 + value2;
        frame.getOperandStack().pushInt(value3);
    }

    public void isub(JVMFrame frame){
        int value1 = frame.getOperandStack().popInt();
        int value2 = frame.getOperandStack().popInt();
        int value3 = value1 - value2;
        frame.getOperandStack().pushInt(value3);
    }

    public void iinc(JVMFrame frame,int index,int addNumConst){
        int value1 = frame.getLocalVars().getInt(index);
        int value2 = value1 + addNumConst;
        frame.getLocalVars().setInt(index,value2);
    }

    public void aconst_null(JVMFrame frame){
        frame.getOperandStack().pushReference(null);
    }

    public void iconst_0(JVMFrame frame){
        frame.getOperandStack().pushInt(0);
    }

    public void bipush(JVMFrame frame,byte value){
        frame.getOperandStack().pushInt(value);
    }

    public void goto_(JVMFrame frame){
//        frame.branch();
    }

    public int ireturn(JVMFrame frame){
        return frame.getOperandStack().popInt();
    }

    public Reference areturn(JVMFrame frame){
        return frame.getOperandStack().popReference();
    }

    public Object return_(){
        return JVM.NULL;
    }

}
