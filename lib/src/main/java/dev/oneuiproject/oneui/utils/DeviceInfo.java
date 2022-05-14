package dev.oneuiproject.oneui.utils;

import android.content.Context;
import android.os.Build;

import dev.oneuiproject.oneui.utils.internal.ReflectUtils;

public class DeviceInfo {
    private static Integer SEM_INT = (Integer) ReflectUtils.genericGetField(Build.VERSION.class, "SEM_INT");
    private static int mIsSdlDevice = -1;

    public static boolean isSemDevice() {
        return SEM_INT != null;
    }

    public static boolean isSdlDevice() {
        if (mIsSdlDevice == -1) {
            if (Build.VERSION.SDK_INT == 23) {
                mIsSdlDevice = Build.MANUFACTURER.equals("samsung") ? 1 : 0;
            } else {
                try {
                    Class.forName("com.sec.android.secmediarecorder.SecMediaRecorder");
                    mIsSdlDevice = 1;
                } catch (Error | Exception unused) {
                    mIsSdlDevice = 0;
                }
            }
        }
        return mIsSdlDevice == 1;
    }

    public static boolean isTabletDevice(Context context) {
        return WindowManagerSupport.getSmallestDeviceWidthDp(context) >= 685;
    }
}
