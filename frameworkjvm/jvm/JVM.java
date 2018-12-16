package com.github.netty.protocol;

/**
 * Created by acer01 on 2018/12/16/016.
 */
public class JVM {

    public static void main(String[] args) {
        JVMClassLoader loader = new JVMClassLoader();
        byte[] byteCodes = loader.load("/src/Test.java");

        int maxStackSize = 200;
        JVMThread mainThread = new JVMThread(maxStackSize);

        executeByteCode(mainThread,byteCodes);
    }

    static Object executeByteCode(JVMThread thread, byte[] byteCodes){
        JVMByteCodeReader byteCodeReader = new JVMByteCodeReader();
        JVMFrame frame = thread.popFrame();
        JVMInstruct instruct = new JVMInstruct();
        for(;;){
            int pc = frame.getNextProgramCounter();
            thread.setProgramCounter(pc);
            byteCodeReader.reset(byteCodes,pc);
            int opcode = byteCodeReader.readInt32();
            switch (opcode){
                case 0x01:{
                    instruct.aconst_null(frame);
                    break;
                }
                case 0x10:{
                    byte b = byteCodeReader.readByte();
                    instruct.bipush(frame,b);
                    break;
                }
                case 0x60:{
                    instruct.iadd(frame);
                    break;
                }
                case 0x09:{
                    instruct.iconst_0(frame);
                    break;
                }
                case 0x15:{
                    int index = byteCodeReader.readInt32();
                    instruct.iload(frame,index);
                    break;
                }
                case 0x36:{
                    int index = byteCodeReader.readInt32();
                    instruct.istore(frame,index);
                    break;
                }
                case 0x84:{
                    int index = byteCodeReader.readInt32();
                    int num = byteCodeReader.readInt32();
                    instruct.iinc(frame,index,num);
                    break;
                }
                case 0xa7:{
                    instruct.goto_(frame);
                    break;
                }
                //退出操作 ret
                case 0xa9:{
                    return instruct.return_();
                }
                //退出操作 ret一个int
                case 0xac:{
                    return instruct.ireturn(frame);
                }
                //退出操作 ret一个引用
                case 0xb0:{
                    return instruct.areturn(frame);
                }
            }

        }
    }

    public static final Object NULL = new Object();
}
