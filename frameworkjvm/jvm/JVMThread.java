package com.github.netty.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程
 * Created by acer01 on 2018/12/16/016.
 */
public class JVMThread {
    /**
     * 程序计数器 (记录当前字节码的行号)
     */
    private int programCounter = 0;
    /**
     * 虚拟机栈
     */
    private VMStack vmstack;

    public JVMThread(int maxStackSize){
        this.vmstack = new VMStack(maxStackSize);
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    /**
     * 压入1帧
     * @param frame
     */
    void pushFrame(JVMFrame frame){
        vmstack.push(frame);
    }

    /**
     * 弹出1帧
     * @return
     */
    JVMFrame popFrame(){
        return vmstack.pop();
    }

    /**
     * 当前帧
     * @return
     */
    JVMFrame currentFrame(){
        return vmstack.top();
    }

    /**
     * 虚拟机栈
     * (注: JAVA虚拟机规范对栈约束非常宽松, 可以固定大小,也可以动态扩容)
     */
    class VMStack<T extends JVMFrame>{
        private int maxStackSize;
        private List<JVMFrame> list = new ArrayList<>();

        public VMStack(int maxStackSize) {
            this.maxStackSize = maxStackSize;
        }

        public void push(JVMFrame item) {
            if(list.size() > maxStackSize){
                throw new java.lang.StackOverflowError("栈溢出");
            }
            list.add(item);
        }

        public JVMFrame pop() {
            return list.remove(0);
        }

        public JVMFrame top(){
            return list.get(0);
        }
    }
}
