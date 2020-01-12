package com.example.healtherar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.view.View;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import android.os.Handler;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;



public class Augmented_Faces extends AppCompatActivity {
    private ArFragment fragment;
    private Node node;
    private Session arSession;
    private ViewRenderable modelFuture;
    private String easyPuzzle;
    private Consultor checkProfile;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.easyPuzzle = intent.getExtras().getString("User");
        Log.i("INFORMATION", easyPuzzle);
        checkProfile = new Consultor(easyPuzzle,getApplicationContext());
        checkProfile.sendRequest("federicomolinachavez@gmail.com");
        setContentView(R.layout.activity_augmented__faces);
        final ViewRenderable[] modelFuture = {null};
        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        fragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate) ;
        Handler handler = new Handler();
        int delay = 2000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                takePhoto();
                handler.postDelayed(this, delay);
            }
        },delay);





    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onSceneUpdate(FrameTime frameTime) {
        fragment.onUpdate(frameTime);
        // If there is no frame then don't process anything.

        if (this.fragment.getArSceneView().getArFrame() == null) {
            return;
        }

        // If ARCore is not tracking yet, then don't process anything.
        if (this.fragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }


            ViewRenderable.builder()
                    .setView(this, R.layout.menupatient)
                    .build()
                    .thenAccept(renderable -> {
                        this.modelFuture = renderable;
                        if(checkProfile.getY()[0] != null){
                            TextView testView = this.modelFuture.getView().findViewById(R.id.TextViewMain);
                            testView.setText(checkProfile.getY()[0].toString());
                        }

                        //TextView testView = (TextView) findViewById(R.id.TextViewMain);
                       // testView.setText("other text");

                        if(this.node == null){
                            Vector3 cameraPos = this.fragment.getArSceneView().getScene().getCamera().getWorldPosition();
                            Vector3 cameraForward = this.fragment.getArSceneView().getScene().getCamera().getForward();
                            Vector3 position = Vector3.add(cameraPos,cameraForward.scaled(0.5f));
                            Pose pose = Pose.makeTranslation(position.x,position.y,position.z);
                            Anchor anchor = this.fragment.getArSceneView().getSession().createAnchor(pose);
                            AnchorNode anchorNode = new AnchorNode(anchor);
                            anchorNode.setParent(this.fragment.getArSceneView().getScene());
                            this.node = new Node();
                            this.node.setParent(anchorNode);
                            this.node.setRenderable(this.modelFuture);
                        }


                            /*this.node = new Node();
                            this.node.setParent(this.fragment.getArSceneView().getScene());
                            this.node.setRenderable(this.modelFuture);
                            Camera camera = this.fragment.getArSceneView().getScene().getCamera();

                            Log.d("ARCORE","putting");
                            Ray ray = camera.screenPointToRay(900/2f, 900/2f);
                            Vector3 newPosition = ray.getPoint(1f);
                            this.node.setLocalPosition(newPosition);*/

                    });








    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Bitmap takePhoto() {
        final Bitmap newBitmap;
        Frame frame = fragment.getArSceneView().getArFrame();
        if(frame == null){
            return null;
        }
        try (Image image = frame.acquireCameraImage()) {
            if(image.getFormat() != ImageFormat.YUV_420_888){
                throw new IllegalArgumentException("Expected image in YUV_420_888 formate, got format" + image.getFormat());
            }
           // ByteBuffer processedImageBytesGrayScale =
            byte[] data = null;
            data = NV21toJPEG(YUV_420_888toNV21(image),
                    image.getWidth(), image.getHeight());

            PhotoSender sender = new PhotoSender(data,"http://192.168.1.4/fatialRecog/consultImage2",getApplicationContext());
            sender.sendImage();
            String userToken = sender.getAsn();
            if(userToken != null){
                Log.i("IMAGERESPONSE", userToken);
                checkProfile.sendRequest(userToken);
            }



        } catch (NotYetAvailableException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }
    private static byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        nv21 = new byte[ySize + uSize + vSize];

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }
}
