package com.tlbail.ptuts3androidapp.Controller.Fragment;

import static android.content.Context.CAMERA_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.tlbail.ptuts3androidapp.Controller.PhotoActivity;
import com.tlbail.ptuts3androidapp.R;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFragment extends Fragment implements TextureView.SurfaceTextureListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Photo.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFragment newInstance(String param1, String param2) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private CameraCaptureSession myCameraCaptureSession;
    private String myCameraID;
    private CameraManager myCameraManager;
    private CameraDevice myCameraDevice;
    private TextureView myTextrureView;
    private CaptureRequest.Builder myCaptureRequestBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_photo, container, false);


        myTextrureView = result.findViewById(R.id.photoPreviewTextureView);

        myTextrureView.setSurfaceTextureListener(this);

        myCameraManager = (CameraManager) getActivity().getSystemService(CAMERA_SERVICE);

        fitBestParams();

        return result;
    }

    private void fitBestParams(){

        float cameraAspectRatio = (float) 0.75;

        //Preparation
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int finalWidth = screenWidth;
        int finalHeight = screenHeight;
        int widthDifference = 0;
        int heightDifference = 0;
        float screenAspectRatio = (float) screenWidth / screenHeight;

        //Determines whether we crop width or crop height
        if (screenAspectRatio > cameraAspectRatio) { //Keep width crop height
            finalHeight = (int) (screenWidth / cameraAspectRatio);
            heightDifference = finalHeight - screenHeight;
        } else { //Keep height crop width
            finalWidth = (int) (screenHeight * cameraAspectRatio);
            widthDifference = finalWidth - screenWidth;
        }

        //Apply the result to the Preview
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) myTextrureView.getLayoutParams();
        lp.width = finalWidth;
        lp.height = finalHeight;
        //Below 2 lines are to center the preview, since cropping default occurs at the right and bottom
        lp.leftMargin = - (widthDifference / 2);
        lp.topMargin = - (heightDifference / 2);
        myTextrureView.setLayoutParams(lp);
    }

    private CameraDevice.StateCallback myStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            myCameraDevice = camera;
            cameraPreview(myTextrureView);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            myCameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            myCameraDevice.close();
            myCameraDevice = null;

        }
    };

    private void openCamera() {
        try {
            myCameraID = myCameraManager.getCameraIdList()[0];

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                openPopUPRestartActivity();
                return;
            }
            myCameraManager.openCamera(myCameraID, myStateCallBack, null);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openPopUPRestartActivity() {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        PopupWindow pw = new PopupWindow(layoutInflater.inflate(R.layout.popupwindowlayout, null, false), getView().getWidth(), getView().getHeight(), true);
        pw.showAtLocation(getActivity().findViewById(R.id.mainFragmentPhoto), Gravity.CENTER, 0, 0);

        Button button = pw.getContentView().findViewById(R.id.buttonrestartPopUpWindow);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityIntent = new Intent(getActivity(), PhotoActivity.class);
                getActivity().startActivity(activityIntent);
            }
        });



    }


    public void cameraPreview(View view){

        SurfaceTexture mySurfaceTexture = myTextrureView.getSurfaceTexture();
        Surface mySurface = new Surface(mySurfaceTexture);

        try {
            myCaptureRequestBuilder = myCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            myCaptureRequestBuilder.addTarget(mySurface);
            myCameraDevice.createCaptureSession(Arrays.asList(mySurface), new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            myCameraCaptureSession = session;
                            myCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                            try {
                                myCameraCaptureSession.setRepeatingRequest(myCaptureRequestBuilder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        myCameraDevice.close();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }
}