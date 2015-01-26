package com.oit_sergei.KRUGIS.checking_resource;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by oit-sergei on 05.01.2015.
 */
public class audio_check {
    private AudioRecord audioRecord;

    public audio_check()
    {
        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int minInternalBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig,
                audioFormat);
        int internalBufferSize = minInternalBufferSize * 4;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, internalBufferSize);

    }

    public int audio_checking_process()
    {

        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
        {
            try
            {
                audioRecord.startRecording();
                if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                {
                    return 0;
                }
            } catch (Exception e)
            {
                return -2;
            }
            return -3;

        } else return -1;

    }

    public boolean audio_close()
    {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
           return true;
        } else return false;
    }

}
