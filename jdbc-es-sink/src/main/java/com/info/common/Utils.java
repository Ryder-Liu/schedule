package com.asiainfo.common;

import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ProjectName: flumesinkes
 * @Package: com.ai.renzq.flume.sink.es
 * @ClassName: ${TYPE_NAME}
 * @Description:
 * @Author: Renzq
 * @CreateDate: 2018/4/11 0:07
 * @UpdateUser: Renzq
 * @UpdateDate: 2018/4/11 0:07
 * @UpdateRemark:
 * @Version: 1.0
 * Copyright: Copyright (c) 2018
 **/
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);


    public enum UnitEnum {
        SECOND("s"),
        MINUTE("m"),
        MILI_SECOND("M"),
        UNKNOWN("unknown");

        private String unit;

        UnitEnum(String unit) {
            this.unit = unit;
        }

        @Override
        public String toString() {
            return unit;
        }

        public static UnitEnum fromString(String unit) {
            for (UnitEnum unitEnum : UnitEnum.values()) {
                if (unitEnum.unit.equals(unit)) {
                    return unitEnum;
                }
            }
            return UNKNOWN;
        }
    }

    public static TimeValue getTimeValue(String interval, String defaultValue) {
        TimeValue timeValue = null;
        String timeInterval = interval != null ? interval : defaultValue;
        if (timeInterval != null) {
            Integer time = Integer.valueOf(timeInterval.substring(0, timeInterval.length() - 1));
            String unit = timeInterval.substring(timeInterval.length() - 1);
            UnitEnum unitEnum = UnitEnum.fromString(unit);
            switch (unitEnum) {
                case MINUTE:
                    timeValue = TimeValue.timeValueMinutes(time);
                    break;
                case SECOND:
                    timeValue = TimeValue.timeValueSeconds(time);
                    break;
                case MILI_SECOND:
                    timeValue = TimeValue.timeValueMillis(time);
                    break;
                default:
                    logger.error("Unit is incorrect, please check the Time Value unit: " + unit);
            }
        }
        return timeValue;
    }
}
