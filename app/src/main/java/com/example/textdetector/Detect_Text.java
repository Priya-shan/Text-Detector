package com.example.textdetector;

import static android.Manifest.permission_group.CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class Detect_Text extends AppCompatActivity {
    Button scan,detect;
    TextView textDetected;
    ImageView capturedImg;
    private Bitmap imageBitmap;
//    static final int REQUEST_IMAGE_CAPTURE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_text);

        scan=findViewById(R.id.scanBtn);
        detect=findViewById(R.id.detectBtn);
        textDetected=findViewById(R.id.textDetected);
        capturedImg=findViewById(R.id.capturedImg);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectText(capturedImg);
            }
        });
    }
    static final int REQUEST_IMAGE_CAPTURE=1;
    private void captureImage(){
        Intent takePicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extras =data.getExtras();
            imageBitmap=(Bitmap) extras.get("data");
            capturedImg.setImageBitmap(imageBitmap);
        }
    }

    public void detectText(ImageView capturedImg){
        InputImage image= InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer recognizer =TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result=recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuffer result=new StringBuffer();
                for(Text.TextBlock block:text.getTextBlocks()){
                    String blockText=block.getText();
                    Point[] blockCornerPoint=block.getCornerPoints();
                    Rect blockFrame=block.getBoundingBox();
                    for(Text.Line line:block.getLines()){
                        String lineText=line.getText();
                        Point[] lineCornerPoint=line.getCornerPoints();
                        Rect lineRect =line.getBoundingBox();
                        for(Text.Element elemnent:line.getElements()){
                            String elementText= elemnent.getText();
                            result.append(elementText);
                        }
                        textDetected.setText(blockText);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Detect_Text.this,"Failed to detect text fromimage"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}