package org.inlambda.kiwi.range;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class IntRange {
    private final int atLeast;
    private final int atMost ;

    public boolean isInRange(int i){
        return atLeast <= i && i <= atMost;
    }

    public static IntRange rangeOf(int atLeast,int atMost){
        return new IntRange(atLeast,atMost);
    }
    public static IntRange rangeAtMost(int atMost){
        return new IntRange(Integer.MIN_VALUE,atMost);
    }
    public static IntRange rangeAtLeast(int atLeast){
        return new IntRange(atLeast,Integer.MAX_VALUE);
    }
    private static final IntRange POSITIVE = new IntRange(1,Integer.MAX_VALUE);
    public static IntRange rangePositive(){
        return POSITIVE;
    }
    private static final IntRange NEGATIVE = new IntRange(Integer.MIN_VALUE, -1);
    public static IntRange rangeNegative(){
        return NEGATIVE;
    }
}
