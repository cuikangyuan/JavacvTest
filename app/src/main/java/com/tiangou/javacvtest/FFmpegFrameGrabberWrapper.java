package com.tiangou.javacvtest;

import org.bytedeco.javacv.FFmpegFrameGrabber;

public class FFmpegFrameGrabberWrapper {


    public FFmpegFrameGrabberWrapper(FFmpegFrameGrabber videoGrabber, FFmpegFrameGrabber audioGrabber) {
        this.videoGrabber = videoGrabber;
        this.audioGrabber = audioGrabber;
    }

    public FFmpegFrameGrabber videoGrabber;
    public FFmpegFrameGrabber audioGrabber;
}
