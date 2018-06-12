package zxl.com.test_calculate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uidq0955 on 2018/6/11.
 */

public class CalculateElementList {
    public int current_page = 0;
    public int page_count = 0;
    public int total_page = 0;
    public int code = 0;
    public String desc = "";
    public List<CalculateElement> result = new ArrayList<>();

    @Override
    public String toString() {
        return "CalculateElementList{" +
                "current_page=" + current_page +
                ", page_count=" + page_count +
                ", total_page=" + total_page +
                ", code=" + code +
                ", desc='" + desc + '\'' +
                ", result=" + result +
                '}';
    }
}
