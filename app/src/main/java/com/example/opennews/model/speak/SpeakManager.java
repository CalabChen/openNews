package com.example.opennews.model.speak;

import android.content.Context;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpeakManager {
    private final Context context;
    private TextToSpeech textToSpeech;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public SpeakManager(Context context) {
        this.context = context;
        initializeTextToSpeech();
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });
    }

    public void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(executorService, text -> {
                    StringBuilder extractedText = new StringBuilder();
                    for (Text.TextBlock block : text.getTextBlocks()) {
                        if (block.getCornerPoints() != null && block.getCornerPoints().length == 4) {
                            extractedText.append(block.getText()).append(" ");
                        }
                    }
                    String finalText = extractedText.toString().trim();
                    speakOut(finalText);
                })
                .addOnFailureListener(e -> Log.e("OCR", "Failed to process image.", e));
    }

    // 将 speakOut 改为 public
    public void speakOut(String text) {
        if (textToSpeech != null && !text.isEmpty()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    // 添加 isSpeaking 方法
    public boolean isSpeaking() {
        if (textToSpeech != null) {
            return textToSpeech.isSpeaking();
        }
        return false;
    }

    public void stopSpeaking() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        executorService.shutdown();
    }
}