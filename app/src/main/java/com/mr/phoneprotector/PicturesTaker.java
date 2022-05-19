package com.mr.phoneprotector;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class PicturesTaker {
    //https://stackoverflow.com/questions/43031207/take-photo-in-service-using-camera2-api

    private static final String TAG = "AmogusPic";
    private static final int CAMERA_CALIBRATION_DELAY = 500;
    private static long cameraCaptureStartTime;
    private CameraDevice cameraDevice;
    private CameraCaptureSession session;
    private ImageReader imageReader;
    private String fileString;
    public static final int CAMERACHOICE_B = CameraCharacteristics.LENS_FACING_BACK;
    public static final int CAMERACHOICE_F = CameraCharacteristics.LENS_FACING_FRONT;
    private Context context;
    private int camChoice;
    private boolean done;

    public PicturesTaker(Context context) {
        this.context = context;
    }



    private CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            Log.d(TAG, "CameraDevice.StateCallback onOpened");
            cameraDevice = camera;
            actOnReadyCameraDevice();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            Log.w(TAG, "CameraDevice.StateCallback onDisconnected");
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            Log.e(TAG, "CameraDevice.StateCallback onError " + error);
        }
    };

    public void actOnReadyCameraDevice()
    {
        try {
            cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), sessionStateCallback, null);
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }
    }

    private CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onReady(CameraCaptureSession session) {
            Log.e(TAG, "onReady in");
            if (!done) {
                PicturesTaker.this.session = session;
                try {
                    session.setRepeatingRequest(createCaptureRequest(), null, null);
                    cameraCaptureStartTime = System.currentTimeMillis();
                } catch (CameraAccessException e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                try {
                    PicturesOut();
                    Log.e(TAG, "Pictures out yes)");
                } catch (Exception E) {
                    Log.e(TAG, "Pictures out no(");
                    return;
                }
            }
        }

        @Override
        public void onConfigured(CameraCaptureSession session) {

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        }
    };

    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Log.d(TAG, "onImageAvailable");
            Image img = reader.acquireLatestImage();
            if (img != null) {
                if (System.currentTimeMillis () > cameraCaptureStartTime + CAMERA_CALIBRATION_DELAY) {
                    processImage(img);
                    reader.close();
                    done = true;

                }
                img.close();

            }
        }
    };

    public void readyCamera(int choice) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String pickedCamera = null;
            if (choice == CAMERACHOICE_B) {
                camChoice = CAMERACHOICE_B;
                pickedCamera = getCameraB(manager);
            } else if (choice == CAMERACHOICE_F) {
                camChoice = CAMERACHOICE_F;
                pickedCamera = getCameraF(manager);
            }

            manager.openCamera(pickedCamera, cameraStateCallback, null);
            imageReader = ImageReader.newInstance(1920, 1088, ImageFormat.JPEG, 2 /* images buffered */);
            imageReader.setOnImageAvailableListener(onImageAvailableListener, null);
            Log.d(TAG, "imageReader created");
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        } catch (SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }

    private String getCameraB(CameraManager manager){
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CAMERACHOICE_B) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    private String getCameraF(CameraManager manager){
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CAMERACHOICE_F) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    private CaptureRequest createCaptureRequest() {
        try {
            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            builder.addTarget(imageReader.getSurface());
            return builder.build();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private void processImage(Image image) {
        //Process image data
        ByteBuffer buffer;
        byte[] bytes;
        boolean success = false;
        getFile(this.camChoice);
        File file = new File(fileString);
        FileOutputStream output = null;

        if (image.getFormat() == ImageFormat.JPEG) {
            buffer = image.getPlanes()[0].getBuffer();
            bytes = new byte[buffer.remaining()]; // makes byte array large enough to hold image
            buffer.get(bytes); // copies image from buffer to byte array
            try {
                output = new FileOutputStream(file);
                output.write(bytes);    // write the byte array to file
                success = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                image.close(); // close this to free up buffer for other images
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private void getFile(int choice) {
        File fileName = new File(Environment.getExternalStorageDirectory().getPath(),
                "/Phone Protector/");
        String date = new SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).format(new Date());
        if(!fileName.exists()){
            fileName.mkdirs();
        }
        if (choice == CAMERACHOICE_B) {
            this.fileString = fileName.getAbsolutePath()+"/"+"_"+date+"_CameraB.jpg";
        } else if (choice == CAMERACHOICE_F) {
            this.fileString = fileName.getAbsolutePath()+"/"+"_"+date+"_CameraF.jpg";
        }

    }

    public void PicturesOut () {
        try {
            this.session.abortCaptures();
        } catch (CameraAccessException e){
            Log.e(TAG, e.getMessage());
        }


        this.session.close();
        this.cameraDevice.close();
    }
}
