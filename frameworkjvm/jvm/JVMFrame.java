package com.github.netty.protocol;

import java.lang.ref.Reference;

/**
 * 帧
 * Created by acer01 on 2018/12/16/016.
 */
public class JVMFrame {
    /**
     * 局部变量表
     */
    private LocalVars localVars;
    /**
     * 操作数栈
     */
    private OperandStack operandStack;

    /**
     * 用作线程切换
     */
    private JVMThread thread;
    /**
     * 用作线程切换跳转
     */
    private int nextProgramCounter;

    public JVMFrame(JVMThread thread,int maxLocalVarSize, int maxOperandStackSize) {
        this.thread = thread;
        this.localVars = new LocalVars(maxLocalVarSize);
        this.operandStack = new OperandStack(maxOperandStackSize);
    }

    public LocalVars getLocalVars() {
        return localVars;
    }

    public OperandStack getOperandStack() {
        return operandStack;
    }

    public int getNextProgramCounter() {
        return nextProgramCounter;
    }

    /**
     * 切换帧 (线程切换)
     * @param frame
     * @param offset
     */
    public void branch(JVMFrame frame,int offset){
        //当前帧的计数器 + 跳跃至线程帧的计数器 + 偏移量
        frame.nextProgramCounter = thread.getProgramCounter() + frame.thread.getProgramCounter() + offset;
    }

    /**
     * 局部变量表
     */
    class LocalVars{
        private int size = 0;
        private Slot[] slots;

        public LocalVars(int maxLocalVarSize) {
            this.slots = new Slot[maxLocalVarSize];
        }

        void setInt(int index,int value){
            slots[index].int32 = value;
        }

        int getInt(int index){
            return  slots[index].int32;
        }

        void pushLong(long value){
            slots[size].int32 = (int)value >>> 8;
            slots[size + 1].int32 = (int)value;
            size = size + 2;
        }

        long popLong(){
            size = size - 2;
            return ((long) slots[size].int32 & 0xff) <<  8 |
                    (long) slots[size + 1].int32 & 0xff;
        }

        void pushReference(Reference reference){
            slots[size].reference = reference;
            size = size + 1;
        }
        Reference popReference(){
            size = size - 1;
            Reference reference = slots[size].reference;
            slots[size].reference = null;
            return reference;
        }
    }

    /**
     * 操作数栈
     */
    class OperandStack{
        private int size = 0;
        private Slot[] slots;

        /**
         * 因为操作数栈大小是编译器已经确定的
         * @param maxOperandStackSize
         */
        public OperandStack(int maxOperandStackSize) {
            this.slots = new Slot[maxOperandStackSize];
        }

        public void pushSlot(Slot slot){
            slots[size] = slot;
            size++;
        }

        public Slot popSlot(){
            size--;
            Slot slot = slots[size];
            slots[size] = null;
            return slot;
        }

        /**
         * 一次占2个槽
         * @param value
         */
        void pushLong(long value){
            slots[size].int32 = (int)value >>> 8;
            slots[size + 1].int32 = (int)value;
            size = size + 2;
        }

        long popLong(){
            size = size - 2;
            return ((long) slots[size].int32 & 0xff) <<  8 |
                    (long) slots[size + 1].int32 & 0xff;
        }

        void pushInt(int value){
            slots[size].int32 = value;
            size = size + 1;
        }

        int popInt(){
            size = size - 1;
            return  slots[size].int32;
        }

        void pushReference(Reference reference){
            slots[size].reference = reference;
            size = size + 1;
        }
        Reference popReference(){
            size = size - 1;
            Reference reference = slots[size].reference;
            slots[size].reference = null;
            return reference;
        }
    }



    /**
     * 槽
     */
    class Slot{
        /**
         * 等于C语言中的 int32
         */
        private int int32;
        /**
         * 因为java没有指针, 用Reference代替, 相当于C语言中的 *ref
         */
        private Reference reference;
    }
}
