package tech.quantit.northstar.strategy.api.indicator.complex;

import tech.quantit.northstar.common.model.TimeSeriesValue;
import tech.quantit.northstar.strategy.api.indicator.TimeSeriesUnaryOperator;

import static tech.quantit.northstar.strategy.api.indicator.function.AverageFunctions.*;
import static tech.quantit.northstar.strategy.api.indicator.function.StatsFunctions.*;

/**
 * MID:MA(CLOSE,N);//求N个周期的收盘价均线，称为布林通道中轨
 * TMP2:=STD(CLOSE,M);//求M个周期内的收盘价的标准差
 * TOP:MID+P*TMP2;//布林通道上轨
 * BOTTOM:MID-P*TMP2;//布林通道下轨
 * @author KevinHuangwl
 *
 */
public final class BOLL {

    private int x;
    private int n;

    /**
     * 创建一个布林线生成器
     * @param n		代表均线周期
     * @param x		代表标准差倍数
     */
    public BOLL(int n, int x) {
        this.x = x;
        this.n = n;
    }

    /**
     * 创建一个布林线生成器
     * @param n		代表均线周期
     * @param x		代表标准差倍数
     * @return
     */
    public static BOLL of(int n, int x){
        return new BOLL(n, x);
    }

    /**
     * 获取上轨线计算函数
     * MID:MA(CLOSE,N);//求N个周期的收盘价均线，称为布林通道中轨
	 * TMP2:=STD(CLOSE,M);//求M个周期内的收盘价的标准差
	 * TOP:MID+P*TMP2;//布林通道上轨
     * @return
     */
    public TimeSeriesUnaryOperator upper(){
    	final TimeSeriesUnaryOperator ma = MA(n);
    	final TimeSeriesUnaryOperator std = STD(n);
        return tv -> {
            TimeSeriesValue v = ma.apply(tv);
            TimeSeriesValue v0 = std.apply(tv);
            double val = v.getValue() + x * v0.getValue();
            return new TimeSeriesValue(val, tv.getTimestamp());
        };
    }

    /**
     * 获取下轨线计算函数
     * MID:MA(CLOSE,N);//求N个周期的收盘价均线，称为布林通道中轨
	 * TMP2:=STD(CLOSE,M);//求M个周期内的收盘价的标准差
	 * BOTTOM:MID-P*TMP2;//布林通道下轨
     * @return
     */
    public TimeSeriesUnaryOperator lower(){
    	final TimeSeriesUnaryOperator ma = MA(n);
    	final TimeSeriesUnaryOperator std = STD(n);
        return tv -> {
            TimeSeriesValue v = ma.apply(tv);
            TimeSeriesValue v0 = std.apply(tv);
            double val = v.getValue() - x * v0.getValue();
            return new TimeSeriesValue(val, tv.getTimestamp());
        };
    }

    /**
     * 获取中轨线计算函数
     * MID:MA(CLOSE,N);//求N个周期的收盘价均线，称为布林通道中轨
     * @return
     */
    public TimeSeriesUnaryOperator mid(){
        return MA(n);
    }

}