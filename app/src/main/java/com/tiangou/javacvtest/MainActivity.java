package com.tiangou.javacvtest;

import android.Manifest;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    private static final String TAG = "MainActivity";
    private Button mergeButton;

    private Button addBgmButton;

    private TextView textView;

    private long startTime;
    private long endTime;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Bundle data = msg.getData();
            if (data != null && data.get("result") != null) {

                Result result1 = (Result) data.get("result");

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(result1.succeed ? "已完成" : "有异常发生");
                stringBuilder.append(" 耗时: " + result1.timeUsed/1000 + "s");

                //Toast.makeText(MainActivity.this,  result, Toast.LENGTH_LONG).show();

                textView.setText(stringBuilder.toString());

            }


            return false;
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        PermissionUtil.checkAndRequestPermission(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, true, PermissionUtil.CHECK_STORAGE_PERMISSION);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mergeButton = findViewById(R.id.button1);
        addBgmButton = findViewById(R.id.button2);

        textView = findViewById(R.id.text1);

        avutil.av_log_set_level(avutil.AV_LOG_ERROR);//  AV_LOG_ERROR


        mergeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText("视频处理中...");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        startTime = SystemClock.elapsedRealtime();

                        boolean b = mergeVideos();

                        endTime = SystemClock.elapsedRealtime();

                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("result", new Result(b, endTime - startTime));

                        message.setData(bundle);

                        handler.sendMessage(message);

                        Log.d(TAG, "mergeVideos result : >>>>>>> " + b  + "  time used : >>>>>>> " + (endTime - startTime));

                    }
                }).start();

            }
        });

        addBgmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText("视频处理中...");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        startTime = SystemClock.elapsedRealtime();

                        boolean b = addBgm();

                        endTime = SystemClock.elapsedRealtime();

                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("result", new Result(b, endTime - startTime));

                        message.setData(bundle);

                        handler.sendMessage(message);

                        Log.d(TAG, "addBgm result : >>>>>>> " + b  + "  time used : >>>>>>> " + (endTime - startTime));

                    }
                }).start();

            }
        });
    }

    private void printInfo(FFmpegFrameGrabber fFmpegFrameGrabber, String path) {

        Log.d(TAG, "printInfo: start >>>>>>  " + path);

        Log.d(TAG, "printInfo: getFormat >>> " + fFmpegFrameGrabber.getFormat());
        Log.d(TAG, "printInfo: getAspectRatio >>> " + fFmpegFrameGrabber.getAspectRatio());

        Log.d(TAG, "printInfo: getImageWidth >>> " + fFmpegFrameGrabber.getImageWidth());
        Log.d(TAG, "printInfo: getImageHeight >>> " + fFmpegFrameGrabber.getImageHeight());

        Log.d(TAG, "printInfo: getVideoCodec >>> " + fFmpegFrameGrabber.getVideoCodec());
        Log.d(TAG, "printInfo: getVideoBitrate >>> " + fFmpegFrameGrabber.getVideoBitrate());
        Log.d(TAG, "printInfo: getFrameRate >>> " + fFmpegFrameGrabber.getFrameRate());
        Log.d(TAG, "printInfo: getFrameNumber >>> " + fFmpegFrameGrabber.getFrameNumber());
        Log.d(TAG, "printInfo: getLengthInVideoFrames >>> " + fFmpegFrameGrabber.getLengthInVideoFrames());
        Log.d(TAG, "printInfo: getLengthInAudioFrames >>> " + fFmpegFrameGrabber.getLengthInAudioFrames());
        Log.d(TAG, "printInfo: getLengthInTime >>> " + fFmpegFrameGrabber.getLengthInTime());
        Log.d(TAG, "printInfo: getLengthInFrames >>> " + fFmpegFrameGrabber.getLengthInFrames());

        Log.d(TAG, "printInfo: getAudioCodec >>> " + fFmpegFrameGrabber.getAudioCodec());
        Log.d(TAG, "printInfo: getAudioBitrate >>> " + fFmpegFrameGrabber.getAudioBitrate());
        Log.d(TAG, "printInfo: getAudioChannels >>> " + fFmpegFrameGrabber.getAudioChannels());
        Log.d(TAG, "printInfo: getSampleRate >>> " + fFmpegFrameGrabber.getSampleRate());

        Log.d(TAG, "printInfo: end >>>>>> " + path);

    }

    private boolean mergeVideos() {

        final String path1 = "/storage/emulated/0/Movies/1.mp4";
        final String path2 = "/storage/emulated/0/Movies/2.mp4";
        final String path3 = "/storage/emulated/0/Movies/3.mp4";

        final String outputPath = "/storage/emulated/0/Movies/output_" + System.currentTimeMillis() + ".mp4";


        List<FFmpegFrameGrabberWrapper> fgs = new ArrayList<>();

        try {
            //第一段

            FFmpegFrameGrabber videoGrabber1 = new FFmpegFrameGrabber(path1);
            FFmpegFrameGrabber audioGrabber1 = new FFmpegFrameGrabber(path1);

            FFmpegFrameGrabberWrapper fFmpegFrameGrabberWrapper1 = new FFmpegFrameGrabberWrapper(videoGrabber1, audioGrabber1);

            videoGrabber1.start();
            audioGrabber1.start();

            //printInfo(videoGrabber1, path1);


            //第二段
            FFmpegFrameGrabber videoGrabber2 = new FFmpegFrameGrabber(path2);
            FFmpegFrameGrabber audioGrabber2 = new FFmpegFrameGrabber(path2);

            FFmpegFrameGrabberWrapper fFmpegFrameGrabberWrapper2 = new FFmpegFrameGrabberWrapper(videoGrabber2, audioGrabber2);


            videoGrabber2.start();
            audioGrabber2.start();

            //printInfo(videoGrabber2, path2);

//            //第三段
//            FFmpegFrameGrabber videoGrabber3 = new FFmpegFrameGrabber(path3);
//            FFmpegFrameGrabber audioGrabber3 = new FFmpegFrameGrabber(path3);
//
//            FFmpegFrameGrabberWrapper fFmpegFrameGrabberWrapper3 = new FFmpegFrameGrabberWrapper(videoGrabber3, audioGrabber3);
//
//
//            videoGrabber3.start();
//            audioGrabber3.start();



            //添加处理段落到集合中
            fgs.add(fFmpegFrameGrabberWrapper1);
            fgs.add(fFmpegFrameGrabberWrapper2);

            //fgs.add(fFmpegFrameGrabberWrapper3);


            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                    outputPath,
                    fgs.get(0).videoGrabber.getImageWidth(),
                    fgs.get(0).videoGrabber.getImageHeight());

            recorder.setFormat("mp4");

            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

            //recorder.setFrameRate(Math.min(grabber1.getFrameRate(), grabber2.getFrameRate()));

            recorder.setVideoBitrate(fgs.get(1).videoGrabber.getVideoBitrate());

            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

            recorder.setSampleRate(fgs.get(0).audioGrabber.getSampleRate());

            recorder.setAudioBitrate(fgs.get(0).audioGrabber.getAudioBitrate());

            recorder.setAudioChannels(fgs.get(0).audioGrabber.getAudioChannels());

            recorder.start();

            Frame frame;


            double videoWriteTimeStamp = 0;
            Frame videoLastFrame = null;
            long videoLastFrameReadTimeStamp = 0;


            double audioWriteTimeStamp = 0;
            Frame audioLastFrame = null;
            long audioLastFrameReadTimeStamp = 0;

            for (int index = 0; index < fgs.size(); index++) {

                FFmpegFrameGrabberWrapper fFmpegFrameGrabberWrapper = fgs.get(index);

                long videoFrameCount = 1;
                long audioFrameCount = 1;
                videoLastFrameReadTimeStamp = 0;
                videoLastFrame = null;

                //单独处理每段视频的  音轨 和 视轨


                //处理视轨
                while ((frame = fFmpegFrameGrabberWrapper.videoGrabber.grabFrame(false, true, true, false)) != null) {


                    //Log.d(TAG, "第 " + (index+1) + " 段视频 视轨 " + "第 " + videoFrameCount + " 帧 " + "readTimeStamp >>> " + fFmpegFrameGrabberWrapper.videoGrabber.getTimestamp());

                    if (videoLastFrame == null) {
                        //没有前一帧 说明是第一帧
                        //videoWriteTimeStamp = 0;

                    } else {

                        videoWriteTimeStamp = videoWriteTimeStamp + (fFmpegFrameGrabberWrapper.videoGrabber.getTimestamp() - videoLastFrameReadTimeStamp);

                    }


                    Log.d(TAG, "第 " + (index+1) + " 段视频 视轨 " + "第 " + videoFrameCount + " 帧 " + "PTS >>>  " + videoWriteTimeStamp +  " DTS >>>  " + recorder.getTimestamp());
                    //Log.d(TAG, "第 " + (index+1) + " 段视频 视轨 " + "第 " + videoFrameCount + " 帧 " + "DTS >>>  " + recorder.getTimestamp());


                    if ((long) videoWriteTimeStamp >= recorder.getTimestamp()) {

                        recorder.setTimestamp((long) videoWriteTimeStamp);

                    }

                    recorder.record(frame);

                    videoFrameCount += 1;
                    videoLastFrame = frame;
                    videoLastFrameReadTimeStamp = fFmpegFrameGrabberWrapper.videoGrabber.getTimestamp();


                }

                //处理音轨

                while ((frame = fFmpegFrameGrabberWrapper.audioGrabber.grabFrame(true, false, true, false)) != null) {


                    //Log.d(TAG, "第 " + (index+1) + " 段视频 音轨 " + "第 " + audioFrameCount + " 帧 " + "readTimeStamp >>> " + fFmpegFrameGrabberWrapper.audioGrabber.getTimestamp());

                    if (audioLastFrame == null) {
                        //没有前一帧 说明是第一帧
                        //audioWriteTimeStamp = 0;

                    } else {

                        audioWriteTimeStamp = audioWriteTimeStamp + (fFmpegFrameGrabberWrapper.audioGrabber.getTimestamp() - audioLastFrameReadTimeStamp);

                    }


                    //Log.d(TAG, "第 " + (index+1) + " 段视频 音轨 " + "第 " + audioFrameCount + " 帧 " + "writeTimeStamp >>>  " + audioWriteTimeStamp);

                    //recorder.setTimestamp((long)audioWriteTimeStamp);
                    //frame.timestamp = 0;

                    //frame.timestamp = (long) audioWriteTimeStamp;
                    //recorder.setTimestamp(audioFrameCount);

                    recorder.record(frame);


                    audioFrameCount += 1;
                    audioLastFrame = frame;
                    audioLastFrameReadTimeStamp = fFmpegFrameGrabberWrapper.audioGrabber.getTimestamp();


                }

                fFmpegFrameGrabberWrapper.videoGrabber.stop();
                fFmpegFrameGrabberWrapper.audioGrabber.stop();


            }

            recorder.stop();

        } catch (FrameGrabber.Exception e) {

            e.printStackTrace();
            return false;
        } catch (FrameRecorder.Exception e) {

            e.printStackTrace();

            return false;

        } catch (Exception e){

            e.printStackTrace();

            return false;

        }

        return true;

    }



    private boolean addBgm() {

        final String path1 = "/storage/emulated/0/Movies/1.mp4";

        final String path2 = "/storage/emulated/0/Movies/bgm.mp3";// longer then the video clip

        //final String path2 = "/storage/emulated/0/Movies/bgm_4_second.mp3";// shorted then the video clip

        final String outputPath = "/storage/emulated/0/Movies/output_" + System.currentTimeMillis() + ".mp4";


        List<FFmpegFrameGrabberWrapper> fgs = new ArrayList<>();

        try {

            FFmpegFrameGrabber videoGrabber1 = new FFmpegFrameGrabber(path1);

            FFmpegFrameGrabber audioGrabber2 = new FFmpegFrameGrabber(path2);

            FFmpegFrameGrabberWrapper fFmpegFrameGrabberWrapper1 = new FFmpegFrameGrabberWrapper(videoGrabber1, audioGrabber2);

            videoGrabber1.start();
            audioGrabber2.start();


            //添加处理段落到集合中
            fgs.add(fFmpegFrameGrabberWrapper1);


            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                    outputPath,
                    fgs.get(0).videoGrabber.getImageWidth(),
                    fgs.get(0).videoGrabber.getImageHeight());

            recorder.setFormat("mp4");

            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

            recorder.setVideoBitrate(fgs.get(0).videoGrabber.getVideoBitrate());

            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);

            recorder.setSampleRate(fgs.get(0).audioGrabber.getSampleRate());

            recorder.setAudioBitrate(fgs.get(0).audioGrabber.getAudioBitrate());

            recorder.setAudioChannels(fgs.get(0).audioGrabber.getAudioChannels());

            recorder.start();

            Frame frame;


            double videoWriteTimeStamp = 0;
            Frame videoLastFrame = null;
            long videoLastFrameReadTimeStamp = 0;


            double audioWriteTimeStamp = 0;
            Frame audioLastFrame = null;
            long audioLastFrameReadTimeStamp = 0;

            for (int index = 0; index < fgs.size(); index++) {

                FFmpegFrameGrabberWrapper fFmpegFrameGrabberWrapper = fgs.get(index);

                long videoFrameCount = 1;
                long audioFrameCount = 1;
                videoLastFrameReadTimeStamp = 0;
                videoLastFrame = null;

                //单独处理每段视频的  音轨 和 视轨

                if (fFmpegFrameGrabberWrapper.videoGrabber != null) {

                    //处理视轨
                    while ((frame = fFmpegFrameGrabberWrapper.videoGrabber.grabFrame(false, true, true, false)) != null) {


                        //Log.d(TAG, "第 " + (index+1) + " 段视频 视轨 " + "第 " + videoFrameCount + " 帧 " + "readTimeStamp >>> " + fFmpegFrameGrabberWrapper.videoGrabber.getTimestamp());

                        if (videoLastFrame == null) {
                            //没有前一帧 说明是第一帧
                            //videoWriteTimeStamp = 0;

                        } else {

                            videoWriteTimeStamp = videoWriteTimeStamp + (fFmpegFrameGrabberWrapper.videoGrabber.getTimestamp() - videoLastFrameReadTimeStamp);

                        }


                        Log.d(TAG, "第 " + (index+1) + " 段视频 视轨 " + "第 " + videoFrameCount + " 帧 " + "PTS >>>  " + videoWriteTimeStamp +  " DTS >>>  " + recorder.getTimestamp());
                        //Log.d(TAG, "第 " + (index+1) + " 段视频 视轨 " + "第 " + videoFrameCount + " 帧 " + "DTS >>>  " + recorder.getTimestamp());


                        if ((long) videoWriteTimeStamp >= recorder.getTimestamp()) {

                            recorder.setTimestamp((long) videoWriteTimeStamp);

                        }

                        recorder.record(frame);

                        videoFrameCount += 1;
                        videoLastFrame = frame;
                        videoLastFrameReadTimeStamp = fFmpegFrameGrabberWrapper.videoGrabber.getTimestamp();


                    }
                }



                //处理音轨
                if (fFmpegFrameGrabberWrapper.audioGrabber != null && fFmpegFrameGrabberWrapper.audioGrabber.hasAudio()) {


                    long audioInternal = (long)(1000000 / fFmpegFrameGrabberWrapper.audioGrabber.getAudioFrameRate());

                    while(audioWriteTimeStamp < videoWriteTimeStamp) {

                        frame = fFmpegFrameGrabberWrapper.audioGrabber.grabFrame(true, false, true, false);


                        if (frame == null) {

                            fFmpegFrameGrabberWrapper.audioGrabber.restart();

                            continue;

                        } else {

                            if (audioLastFrame == null) {
                                //没有前一帧 说明是第一帧
                                //audioWriteTimeStamp = 0;

                            } else {

                                audioWriteTimeStamp = audioWriteTimeStamp + audioInternal;

                            }

                            Log.d(TAG, "第 " + (index+1) + " 段视频 音轨 " + "第 " + audioFrameCount + " 帧 " + " PTS >>>  " + audioWriteTimeStamp);


                            recorder.record(frame);


                            audioFrameCount += 1;
                            audioLastFrame = frame;
                        }



                    }
                }


                if (fFmpegFrameGrabberWrapper.videoGrabber != null) {

                    fFmpegFrameGrabberWrapper.videoGrabber.stop();

                }


                if (fFmpegFrameGrabberWrapper.audioGrabber != null) {

                    fFmpegFrameGrabberWrapper.audioGrabber.stop();

                }


            }

            recorder.stop();

        } catch (FrameGrabber.Exception e) {

            e.printStackTrace();
            return false;
        } catch (FrameRecorder.Exception e) {

            e.printStackTrace();

            return false;

        } catch (Exception e){

            e.printStackTrace();

            return false;

        }

        return true;

    }

}
