package hxb.xb_testandroidfunction.test_arithmetic;

import android.util.Log;

public class Arithmetic {
    public static String TAG = "Arithmetic";

    public static int findTheSmallestPositiveInteger(int[] test) {
        /**
         * 求最小的正整数
         * 例子：
         * 输入: [1,2,0]
         * 输出: 3
         *
         * 输入: [3,4,-1,1]
         * 输出: 2
         *
         * 输入: [7,8,9,11,12]
         * 输出: 1
         * */
        int testLength = test.length;
        int testLengthMid = testLength / 2 + testLength % 2;
        boolean is = true;
        int j = 1;//最小正整数的起始值
        while (is) {
            int iFirstHalf = 0;//记录前一半数组 没有最小正整数有多少次
            int jAfterHalf = 0;//记录后一半数组 没有最小正整数有多少次
            for (int i = 0; i < testLengthMid; i++) {//利用二分数组 对比法
                int firstHalf = test[i];
                if (j != firstHalf) {
                    iFirstHalf++;
                }
//                Log.e("hxb", "firstHalf: "+firstHalf);
                if (testLengthMid + i < testLength) {//判断后一半数据
                    int afterHalf = test[testLengthMid + i];
                    if (j != afterHalf) {
                        jAfterHalf++;
                    }
//                    Log.e("hxb", "afterHalf: "+afterHalf );
                }

            }
            //前一半和后一半记录次数相加等于数组长度，则证明该时刻的最小正整数是在该数组中没有的
            if (iFirstHalf + jAfterHalf == testLength) {
                is = false;
            } else {
                j++;
            }
        }

        return j;

    }

    public static int afterTheRain(int[] test) {
        /**
         * 接雨水算法
         * 列如：
         *  [2,1,0,3] = 能接雨水数是：3
         *  1，首先将一个有长度的数组，以x水平轴在该轴上[0]--[n]展开，以y轴垂直于x轴记录数组中值大小，
         *  从而形成柱状图。
         *  2，数组中的元素[1],[2] 是属于该数组的低洼处。而自然界中接雨水，满了一个不规则容器，
         *  就会溢出，而溢出的规则是以最低边缘为上限，所以数组[0]是该数组的最低边缘。
         * */

        int testLength = test.length;
        int rainCumsum = 0;//接雨水的累计数

        int marginLeft = 0;//记录接雨水容器的左边缘，以此为基准

        for (int i = 0; i < testLength; i++) {
            if (i != marginLeft) {
                continue;
            }
            int elementValueI = test[marginLeft];
            int rainCumsumTemporary = 0;//暂时记录低洼处的实体部分的和
            int lowLyingEntityNums = 0;//暂时记录低洼处的实体部分的有几个
            for (int j = marginLeft + 1; j < testLength; j++) {
                int elementValueJ = test[j];
                if (elementValueI > elementValueJ) {
                    rainCumsumTemporary += elementValueJ;
                    lowLyingEntityNums++;
                    /*
                     *这里表示数组中:如elementValueI大过了elementValueI后面所有的数，
                     * 就有这几种可能性:
                     * 1,可能有一个相对小于elementValueI 的一个右边缘值。
                     * 2,可能 该elementValueI 后面中的数组值都一样。
                     * 3,可能 该elementValueI 后面中的数组值成下坡状的走势。
                     * 4,可能 该elementValueI 后面中的数组中有小洼地的状况。
                     * */
                    int maxIndex = testLength - 1;
                    if (j == maxIndex) {//该判断证明了elementValueI大过了elementValueI后面所有的数。
                        rainCumsumTemporary = 0;//把积累的值再次清零
                        /*
                         * 该for循环采用倒叙遍历，前提是elementValueI大过了elementValueI后面所有的数。
                         * k = maxIndex 表示从数组的末尾开始。
                         * k > i 表示数组倒叙遍历的终止位置
                         * */
                        for (int k = maxIndex; k > i; k--) {
                            /*
                             *这里用 <= 的比较符是因为有可能两个以上的值都是最大值，
                             *由于采用倒叙找最大值，其中有一个最大值的脚标 肯定离
                             * 该elementValueI的脚标最近。也符合接雨水的洼地的边缘条件
                             * */
                            int valueK = test[k];
                            if (test[maxIndex] <= valueK) {
                                maxIndex = k;//找出最大值的脚标
                                /*
                                 *只要还有最大值，就将之前的累计值清零，
                                 * 因为洼地的右边缘被重新定义 脚标位置 了。
                                 */
                                rainCumsumTemporary = 0;
                            } else {
                                /*
                                 *当 定义了/重新定义了 洼地的右边缘，就累计洼地左右边缘中的
                                 * 实体部分（数组中小于左右边缘的值，称为实体部分）。
                                 */
                                rainCumsumTemporary += valueK;
                            }
                        }

                        //求两数之间的隔了几个数，所以要减1
                        int indexDifference = maxIndex - i - 1;
                        //elementValueI 与右边缘的值中间有隔数
                        if (indexDifference > 0) {
                            //直接将左边缘的脚标移到当前的右边缘脚标的位置上，也减少了运算。
                            marginLeft = maxIndex;
                            int valueMax = test[maxIndex];
                            if (elementValueI > valueMax) {
                                rainCumsum += valueMax * indexDifference - rainCumsumTemporary;
                            } else {
                                rainCumsum += elementValueI * indexDifference - rainCumsumTemporary;
                            }
                        } else {
                            /*
                             *elementValueI 与右边缘的值相邻了，
                             * 就需要将左边缘的脚标指针移到相邻的位置上。
                             */
                            marginLeft++;
                        }

                    }
                } else {
                    /*
                     * 1，当进入else条件时,说明上述if条件中已找到  数组洼地的右边缘脚标中的值
                     *  大于  左边缘脚标中的值  或是等于。
                     *,2，此时需要将数组的 左边缘脚标 改为 右边缘的脚标；
                     * 目的是:<1>跳过左边边缘和右边缘之间的脚标，减少运算次数。
                     *       <2>减少数组洼地中有小洼地，重复计算。
                     *
                     * */
                    marginLeft = j;
                    //判断低洼处的哪个边缘最低，就以那个为准
                    if (elementValueI > elementValueJ) {
                        rainCumsum += elementValueJ * lowLyingEntityNums - rainCumsumTemporary;
                    } else {
                        rainCumsum += elementValueI * lowLyingEntityNums - rainCumsumTemporary;
                    }
                    break;
                }
            }
        }
        return rainCumsum;

    }



    public static int afterTheRain2(int[] height) {
        int maxLeft=0,maxRight=0,l=0,r=height.length-1;
        int water=0;
        while(l<r){
            if(height[l]<height[r]){
                if(height[l]>maxLeft){
                    maxLeft=height[l];
                }else{
                    water+=maxLeft-height[l];
                }
                l++;
            }else{
                if(height[r]>maxRight){
                    maxRight=height[r];
                }else{
                    water+=maxRight-height[r];
                }
                r--;
            }
        }
        return water;
    }

}
