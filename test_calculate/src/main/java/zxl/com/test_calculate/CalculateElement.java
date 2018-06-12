package zxl.com.test_calculate;

/**
 * Created by uidq0955 on 2018/6/11.
 */

public class CalculateElement {
    public long _id = 0;
    public int calculate_arg1 = 0;
    public int calculate_arg2 = 0;

    public int calculate_operator_arg = 0;
    public String calculate_operator_str = "";

    public String calculate_result = "";
    public String input_calculate_result = "";
    public int is_input_calculate_result_right = 1;
    public long calculate_date = 0;

    @Override
    public String toString() {
        return "CalculateElement{" +
                "_id=" + _id +
                ", calculate_arg1=" + calculate_arg1 +
                ", calculate_arg2=" + calculate_arg2 +
                ", calculate_operator_arg=" + calculate_operator_arg +
                ", calculate_operator_str='" + calculate_operator_str + '\'' +
                ", calculate_result='" + calculate_result + '\'' +
                ", input_calculate_result='" + input_calculate_result + '\'' +
                ", is_input_calculate_result_right=" + is_input_calculate_result_right +
                ", calculate_date=" + calculate_date +
                '}';
    }
}
