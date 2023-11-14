package com.example.crittercalls;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;

import androidx.core.os.HandlerCompat;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AudioHelperActivity extends ClassificationActivity {
    float probabilityThreshold = 0.3f;
    private AudioClassifier audioClassifier;
    private TensorAudio tensorAudio;
    private TimerTask timerTask;

    private static final String MODEL_FILE = "yamnet_classification.tflite";
    private String[] permissions = {android.Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final float MINIMUM_DISPLAY_THRESHOLD = 0.3f;
    private AudioRecord audioRecord;
    private TensorAudio audioTensor;
    private int bufferSize;
    private short[] audioBuffer;
    private boolean isRecording = false;
    private Interpreter yamNetInterpreter;

    private AudioClassifier mAudioClassifier;
    private AudioRecord mAudioRecord;
    private long classficationInterval = 500;       // 0.5 sec
    private Handler mHandler;
    private boolean permissionToRecordAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        HandlerThread handlerThread = new HandlerThread("backgroundThread");
//        handlerThread.start();
//        mHandler = HandlerCompat.createAsync(handlerThread.getLooper());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        // Loading the model from the assets folder
        try {
            audioClassifier = AudioClassifier.createFromFile(this, MODEL_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioTensor = audioClassifier.createInputTensorAudio();
//        audioRecord = audioClassifier.createAudioRecord();

    }
    public void onStartRecording(View view) {
        super.onStartRecording(view);

        // Check if the required audio recording permission is granted
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//            // Calculate the buffer size for audio recording
//            int sampleRate = 44100;
//            int channelConfig = AudioFormat.CHANNEL_IN_MONO;
//            int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
//            bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
//            audioBuffer = new short[bufferSize];

            // Initialize AudioRecord
//            audioRecord = new AudioRecord(
//                    MediaRecorder.AudioSource.MIC,
//                    sampleRate,
//                    channelConfig,
//                    audioFormat,
//                    bufferSize
//            );

            // Start recording
            AudioRecord audioRecord = audioClassifier.createAudioRecord();
            audioRecord.startRecording();
            isRecording = true;
            this.audioRecord = audioRecord;
        }

//        if(mAudioClassifier != null) return;
//
//        try {
//            AudioClassifier classifier = AudioClassifier.createFromFile(this, MODEL_FILE);
//            TensorAudio audioTensor = classifier.createInputTensorAudio();
//
//            AudioRecord record = classifier.createAudioRecord();
//            record.startRecording();
//
//            Runnable run = new Runnable() {
//                @Override
//                public void run() {
//                    audioTensor.load(record);
//                    List<Classifications> output = classifier.classify(audioTensor);
//                    List<Category> filterModelOutput = output.get(0).getCategories();
//                    for(Category c : filterModelOutput) {
//                        if (c.getScore() > MINIMUM_DISPLAY_THRESHOLD)
//                            outputTextView.setText(" label : " + c.getLabel() + " score : " + c.getScore());
////                            Log.d("tensorAudio_java", " label : " + c.getLabel() + " score : " + c.getScore());
//                    }
//
//                    mHandler.postDelayed(this,classficationInterval);
//                }
//            };
//
//            mHandler.post(run);
//            mAudioClassifier = classifier;
//            mAudioRecord = record;
//        }catch (IOException e){
//            e.printStackTrace();
//        }

//        // showing the audio recorder specification
//        TensorAudio.TensorAudioFormat format = audioClassifier.getRequiredTensorAudioFormat();
//        String specs = "Number of channels: " + format.getChannels() + "\n"
//                + "Sample Rate: " + format.getSampleRate();
//        specsTextView.setText(specs);
//
//        // Creating and start recording
//        audioRecord = audioClassifier.createAudioRecord();
//        audioRecord.startRecording();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                // Classifying audio data
//                List<Classifications> output = audioClassifier.classify(tensorAudio);
//
//                // Filtering out classifications with low probability
//                List<Category> finalOutput = new ArrayList<>();
//                for (Classifications classifications : output) {
//                    for (Category category : classifications.getCategories()) {
//                        if (category.getScore() > probabilityThreshold) {
//                            finalOutput.add(category);
//                        }
//                    }
//                }
//                // Sorting the results
//                Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));
//
//                // Creating a multiline string with the filtered results
//                StringBuilder outputStr = new StringBuilder();
//                for (Category category : finalOutput) {
//                    outputStr.append(category.getLabel())
//                            .append(": ").append(category.getScore()).append("\n");
//                }
//                // Updating the UI
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (finalOutput.isEmpty()) {
//                            outputTextView.setText("Could not classify");
//                        } else {
//                            outputTextView.setText(outputStr.toString());
//                        }
//                    }
//                });
//            }
//        };
//        new Timer().scheduleAtFixedRate(timerTask, 1, 500);
    }
    public void onStopRecording(View view) {
        super.onStopRecording(view);

        // Stop recording and release resources
        audioRecord.stop();
        classifyAudio();

        audioRecord.release();
        isRecording = false;



//        mHandler.removeCallbacksAndMessages(null);
//        mAudioRecord.stop();
//        mAudioRecord = null;
//        mAudioClassifier = null;

//        timerTask.cancel();
//        audioRecord.stop();
    }

    private void classifyAudio() {
        audioTensor.load(audioRecord);

//      Filtering out classifications with low probability
        List<Classifications> output = audioClassifier.classify(audioTensor);
        List<Category> finalOutput = new ArrayList<>();
        for (Classifications classifications : output) {
            for (Category category : classifications.getCategories()) {
                if (category.getScore() > MINIMUM_DISPLAY_THRESHOLD) {
                    finalOutput.add(category);
                }
            }
        }

        // Sorting the results
        Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));

        // Creating a multiline string with the filtered results
        StringBuilder outputStr = new StringBuilder();
        for (Category category : finalOutput) {
            outputStr.append(category.getLabel())
                    .append(": ").append(category.getScore()).append("\n");
        }

        if (finalOutput.isEmpty()) {
            outputTextView.setText("Could not classify");
        } else {
            outputTextView.setText(outputStr.toString());
        }



//        List<Classifications> output = audioClassifier.classify(audioTensor);
//        List<Category> filterModelOutput = output.get(0).getCategories();
//        for(Category c : filterModelOutput) {
//            if (c.getScore() > MINIMUM_DISPLAY_THRESHOLD)
//                outputTextView.setText(" label : " + c.getLabel() + " score : " + c.getScore());
//            else
//                outputTextView.setText("Could not classify");
//        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_RECORD_AUDIO_PERMISSION:
//                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                break;
//        }
//        if (!permissionToRecordAccepted) finish();
//    }
}
