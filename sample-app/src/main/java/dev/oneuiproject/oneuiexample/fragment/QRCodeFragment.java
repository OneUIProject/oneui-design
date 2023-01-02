package dev.oneuiproject.oneuiexample.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sec.sesl.tester.R;

import dev.oneuiproject.oneui.qr.QREncoder;
import dev.oneuiproject.oneuiexample.base.BaseFragment;

public class QRCodeFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView qr_image_1 = view.findViewById(R.id.qr_image_1);
        ImageView qr_image_2 = view.findViewById(R.id.qr_image_2);
        ImageView qr_image_3 = view.findViewById(R.id.qr_image_3);
        ImageView qr_image_4 = view.findViewById(R.id.qr_image_4);

        qr_image_1.setImageBitmap(new QREncoder(mContext, "The One UI Sample app has been made to showcase the components from both our oneui-core libraries and oneui-design module.")
                .setIcon(R.mipmap.ic_launcher)
                .generate());

        qr_image_2.setImageBitmap(new QREncoder(mContext, "https://github.com/OneUIProject/oneui-design/raw/main/sample-app/release/sample-app-release.apk")
                .setIcon(R.drawable.ic_oui_file_type_apk)
                .setFGColor(Color.parseColor("#ff6ebe64"), true, true)
                .generate());

        qr_image_3.setImageBitmap(new QREncoder(mContext, "custom colors and size")
                .setIcon(R.drawable.ic_oui_file_type_txt)
                .setSize(350)
                .setBGColor(Color.BLACK)
                .setFGColor(Color.RED, true, true)
                .generate());

        qr_image_4.setImageBitmap(new QREncoder(mContext, "without frame and icon")
                .setFrame(false)
                .generate());

    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_qr_code;
    }

    @Override
    public int getIconResId() {
        return R.drawable.ic_oui_qr_code;
    }

    @Override
    public CharSequence getTitle() {
        return "QRCode";
    }

}
