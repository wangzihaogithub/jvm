package com.github.netty.protocol;

/**
 * 字节码读取器
 * Created by acer01 on 2018/12/16/016.
 */
public class JVMByteCodeReader {
    /**
     * 字节码
     */
    private byte[] codes;
    /**
     * 当前字节码读取到哪个字节
     */
    private int index;

    public void reset(byte[] codes,int index){
        this.codes = codes;
        this.index = index;
    }

    public byte readByte(){
        int value = codes[index];
        index = index + 1;
        return (byte) value;
    }

    public int readInt32(){
        int value = (codes[index]     & 0xff) << 24 |
                (codes[index + 1] & 0xff) << 16 |
                (codes[index + 2] & 0xff) <<  8 |
                codes[index + 3] & 0xff;
        index = index + 4;
        return value;
    }

    public long readInt64(){
        long value = ((long) codes[index]     & 0xff) << 56 |
                ((long) codes[index + 1] & 0xff) << 48 |
                ((long) codes[index + 2] & 0xff) << 40 |
                ((long) codes[index + 3] & 0xff) << 32 |
                ((long) codes[index + 4] & 0xff) << 24 |
                ((long) codes[index + 5] & 0xff) << 16 |
                ((long) codes[index + 6] & 0xff) <<  8 |
                (long) codes[index + 7] & 0xff;
        index = index + 8;
        return value;
    }
}
