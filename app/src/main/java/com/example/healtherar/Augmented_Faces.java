package com.example.healtherar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
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

import java.nio.ByteBuffer;


public class Augmented_Faces extends AppCompatActivity {
    private ArFragment fragment;
    private Node node;
    private Session arSession;
    private ViewRenderable modelFuture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augmented__faces);
        final ViewRenderable[] modelFuture = {null};
        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        fragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate) ;





    }




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
        takePhoto();

            ViewRenderable.builder()
                    .setView(this, R.layout.menupatient)
                    .build()
                    .thenAccept(renderable -> {
                        this.modelFuture = renderable;
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
              newBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ALPHA_8);
            newBitmap.copyPixelsFromBuffer(image.getPlanes()[0].getBuffer());
            Log.i("IMG", newBitmap.toString());
        } catch (NotYetAvailableException e) {
            e.printStackTrace();
        }
        return null;
    }
}
