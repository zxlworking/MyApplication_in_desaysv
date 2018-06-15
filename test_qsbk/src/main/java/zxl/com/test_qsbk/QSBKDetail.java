package zxl.com.test_qsbk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uidq0955 on 2018/6/14.
 */

public class QSBKDetail {
    public int code = 0;
    public String desc = "";
    public String qsbk_detail_content = "";
    public List<QSBKComment> user_comment_list = new ArrayList<>();

    @Override
    public String toString() {
        return "QSBKDetail{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", qsbk_detail_content='" + qsbk_detail_content + '\'' +
                ", user_comment_list=" + user_comment_list +
                '}';
    }
}
