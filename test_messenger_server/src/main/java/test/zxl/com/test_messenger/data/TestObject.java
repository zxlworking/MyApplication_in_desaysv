package test.zxl.com.test_messenger.data;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

/**
 * Created by uidq0955 on 2018/5/17.
 */

public class TestObject implements Parcelable {
    public int testInt;
    public String testStr = "";
    public TestChildObject mTestChildObject = null;

    public TestObject() {
    }

    protected TestObject(Parcel in) {
        testInt = in.readInt();
        testStr = in.readString();
        mTestChildObject = in.readParcelable(TestChildObject.class.getClassLoader());
    }

    public static final Creator<TestObject> CREATOR = new Creator<TestObject>() {
        @Override
        public TestObject createFromParcel(Parcel in) {
            return new TestObject(in);
        }

        @Override
        public TestObject[] newArray(int size) {
            return new TestObject[size];
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
        dest.writeParcelable(mTestChildObject,flags);
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "testInt=" + testInt +
                ", testStr='" + testStr + '\'' +
                ", mTestChildObject=" + mTestChildObject +
                '}';
    }
}
