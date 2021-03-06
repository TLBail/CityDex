package com.tlbail.ptuts3androidapp.Model.PanneauVersVille;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import com.tlbail.ptuts3androidapp.Model.OCR.CityNameOCRDetector;
import com.tlbail.ptuts3androidapp.Model.OCR.OcrResultListener;
import com.tlbail.ptuts3androidapp.Model.ObjectDetection.NoSignInImageException;
import com.tlbail.ptuts3androidapp.Model.ObjectDetection.ObjectDetector;

public class CityDetectorInPhoto {

    private CityNameOCRDetector ocrDetector;
    private ObjectDetector objectDetector;

    public CityDetectorInPhoto(Context context, OcrResultListener ocrResultListener){
        objectDetector = ObjectDetector.getInstance(context);
        ocrDetector = new CityNameOCRDetector(ocrResultListener);
    }

    public void start(Bitmap imageToProcess) throws NoSignInImageException {
        RectF signLocationInImage = objectDetector.runObjectDetection(imageToProcess);
        ocrDetector.runOcrResult(imageToProcess, signLocationInImage);
    }
}
