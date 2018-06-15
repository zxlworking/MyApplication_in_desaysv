package zxl.com.test_qsbk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uidq0955 on 2018/6/14.
 */

public class QSBKElementList {
    public int current_page = 0;
    public int code = 0;
    public String desc = "";
    public List<QSBKElement> result = new ArrayList<>();

    @Override
    public String toString() {
        return "QSBKElementList{" +
                "current_page=" + current_page +
                ", code=" + code +
                ", desc='" + desc + '\'' +
                ", result=" + result +
                '}';
    }
}
