package test.zxl.com.test_messenger.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by uidq0955 on 2018/5/17.
 */

public class TestChildObject implements Parcelable {
    public int testInt;
    public String testStr = "";

    public TestChildObject() {
    }

    public TestChildObject(Parcel in) {
        testInt = in.readInt();
        testStr = in.readString();
    }

    public static final Creator<TestChildObject> CREATOR = new Creator<TestChildObject>() {
        @Override
        public TestChildObject createFromParcel(Parcel in) {
            return new TestChildObject(in);
        }

        @Override
        public TestChildObject[] newArray(int size) {
            return new TestChildObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(testInt);
        dest.writeString(testStr);
    }

    @Override
    public String toString() {
        return "TestChildObject{" +
                "testInt=" + testInt +
                ", testStr='" + testStr + '\'' +
                '}';
    }
}
