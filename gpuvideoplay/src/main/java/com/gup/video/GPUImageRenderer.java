/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gup.video;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.co.cyberagent.android.gpuimage.BaseGPUImageRenderer;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageNativeLibrary;
import jp.co.cyberagent.android.gpuimage.OpenGlUtils;
import jp.co.cyberagent.android.gpuimage.Rotation;
import jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil;

import static jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil.TEXTURE_NO_ROTATION;

@TargetApi(11)
public class GPUImageRenderer implements Renderer, PreviewCallback {
    public static final int NO_IMAGE = -1;
    public static final float[] CUBE = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };
    public final Object mSurfaceChangedWaiter = new Object();
    private FloatBuffer mGLCubeBuffer;
    private FloatBuffer mGLTextureBuffer;
    private final Queue<Runnable> mRunOnDraw;
    private final Queue<Runnable> mRunOnDrawEnd;
    boolean transformSetted = false;
    MediaPlayer mediaPlayer;
    private GPUImageFilter mFilter;
    private int mGLTextureId = NO_IMAGE;
    private SurfaceTexture mSurfaceTexture = null;
    private IntBuffer mGLRgbBuffer;
    private int mOutputWidth;
    private int mOutputHeight;
    private int mImageWidth = 480;
    private int mImageHeight = 360;
    private int mAddedPadding;
    private Rotation mRotation;
    private boolean mFlipHorizontal;
    private boolean mFlipVertical;
    private GPUImage.ScaleType mScaleType = GPUImage.ScaleType.CENTER_INSIDE;
    private float mBackgroundRed = 0;
    private float mBackgroundGreen = 0;
    private float mBackgroundBlue = 0;
    private float[] videoTextureTransform = new float[16];
    private Surface surface;

    public GPUImageRenderer(final GPUImageFilter filter) {
        //Log.e("TAG", "init---------->>>> init  BaseGPUImageRenderer");
        mFilter = filter;
        mRunOnDraw = new LinkedList<>();
        mRunOnDrawEnd = new LinkedList<>();
        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
       // mGLCubeBuffer.put(CUBE).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
       // mGLTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
//
//        mGLCubeBuffer.clear();
//        mGLTextureBuffer.clear();

        int texture[] = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        mSurfaceTexture = new SurfaceTexture(texture[0]);
        surface = new Surface(mSurfaceTexture);
        Log.e("BaseGPUImageRenderer", ": mSurfaceTexture " + mSurfaceTexture);
        //GLES20.glClearColor(mBackgroundRed, mBackgroundGreen, mBackgroundBlue, 1);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mFilter.init();


    }
    /**
     * 创建离屏buffer
     */
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;

//        GLES20.glDeleteFramebuffers(1, fFrame, 0);
//        GLES20.glDeleteTextures(1, fTexture, 0);
//
//        GLES20.glGenFramebuffers(1, fFrame, 0);
//        EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, mOutputWidth, mOutputHeight);


        // Log.e("BaseGPUImageRenderer",   ": onSurfaceChanged " + mSurfaceTexture);
        // Log.e("BaseGPUImageRenderer",   ": onSurfaceChanged " + mediaPlayer);
        GLES20.glViewport(0, 0, width, height);
        GLES20.glUseProgram(mFilter.getProgram());
        mFilter.onOutputSizeChanged(width, height);
        adjustImageScaling();
        synchronized (mSurfaceChangedWaiter) {
            mSurfaceChangedWaiter.notifyAll();
        }
//        if (mediaPlayer != null) {
//            setUpSurfaceTexture(mediaPlayer);
//        }
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        // 设置白色为清屏
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // 清除屏幕和深度缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll(mRunOnDraw);
        if (mSurfaceTexture != null) {
            // Log.e("onDrawFrame  onDrawFrame","mSurfaceTexture"+mSurfaceTexture);
            mSurfaceTexture.updateTexImage();
        }
        mSurfaceTexture.getTransformMatrix(videoTextureTransform);
        if (videoTextureTransform[0] == 1 && videoTextureTransform[5] == -1) {
            setRotation(Rotation.NORMAL, false, false);
        } else if (videoTextureTransform[1] == 1 && videoTextureTransform[4] == 1) {
            setRotation(Rotation.ROTATION_270, false, false);
        } else if (videoTextureTransform[1] == -1 && videoTextureTransform[4] == -1) {
            setRotation(Rotation.ROTATION_90, false, false);
        } else if (videoTextureTransform[0] == -1 && videoTextureTransform[5] == 1) {
            setRotation(Rotation.ROTATION_180, false, false);
        }
        //EasyGlUtils.unBindFrameBuffer();
        mFilter.onDraw(mGLTextureId, mGLCubeBuffer, mGLTextureBuffer);
        runAll(mRunOnDrawEnd);
    }

    public Surface getSurface() {
        return surface;
    }

    public SurfaceTexture getmSurfaceTexture() {
        return mSurfaceTexture;
    }

    /**
     * Sets the background color
     *
     * @param red   red color value
     * @param green green color value
     * @param blue  red color value
     */
    public void setBackgroundColor(float red, float green, float blue) {
        mBackgroundRed = red;
        mBackgroundGreen = green;
        mBackgroundBlue = blue;
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        final Size previewSize = camera.getParameters().getPreviewSize();
        Log.e("adjustImageScaling","onPreviewFrame"+previewSize);
        if (mGLRgbBuffer == null) {
            mGLRgbBuffer = IntBuffer.allocate(previewSize.width * previewSize.height);
        }
        if (mRunOnDraw.isEmpty()) {
            runOnDraw(new Runnable() {
                @Override
                public void run() {
                    GPUImageNativeLibrary.YUVtoRBGA(data, previewSize.width, previewSize.height,
                            mGLRgbBuffer.array());
                    mGLTextureId = OpenGlUtils.loadTexture(mGLRgbBuffer, previewSize, mGLTextureId);
                    camera.addCallbackBuffer(data);

                    if (mImageWidth != previewSize.width) {
                        mImageWidth = previewSize.width;
                        mImageHeight = previewSize.height;
                        adjustImageScaling();
                    }
                }
            });
        }
    }

    public void setUpSurfaceTexture(final Camera camera) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                try {
                    camera.setPreviewTexture(mSurfaceTexture);
                    camera.setPreviewCallback(GPUImageRenderer.this);
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * added by baidu, for re-use
     *
     * @param mp
     */
    public void setUpSurfaceTexture(final MediaPlayer mp) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = mp;
                try {
                    
                    mp.setSurface(surface);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * need set source Size, add by Baidu (since we don't setBitmap, so we need set these params)
     *
     * @param imageWidth
     * @param imageHeight
     */
    public void setSourceSize(int imageWidth, int imageHeight,Rotation rotation) {
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
//        if (rotation == Rotation.NORMAL || rotation == Rotation.ROTATION_180) {
//            mImageWidth = imageWidth;
//            mImageHeight = imageHeight;
//        } else {
//            mImageWidth = imageHeight;
//            mImageHeight = imageWidth;
//        }
        // Log.e("BaseGPUImageRenderer", "mImageWidth  :" + mImageWidth + ": mImageHeight " + mImageHeight);
    }


    public void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("BaseGPUImageRenderer", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public void setFilter(final GPUImageFilter filter) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                final GPUImageFilter oldFilter = mFilter;
                mFilter = filter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                mFilter.init();
                GLES20.glUseProgram(mFilter.getProgram());
                mFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            }
        });
    }

    public void deleteImage() {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glDeleteTextures(1, new int[]{
                        mGLTextureId
                }, 0);
                mGLTextureId = NO_IMAGE;
            }
        });
    }

    public void setImageBitmap(final Bitmap bitmap) {
      //  setImageBitmap(bitmap, true);
    }

    public void setImageBitmap(final Bitmap bitmap, final boolean recycle) {
        if (bitmap == null) {
            return;
        }

        runOnDraw(new Runnable() {

            @Override
            public void run() {
                Bitmap resizedBitmap = null;
                if (bitmap.getWidth() % 2 == 1) {
                    resizedBitmap = Bitmap.createBitmap(bitmap.getWidth() + 1, bitmap.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas can = new Canvas(resizedBitmap);
                    can.drawARGB(0x00, 0x00, 0x00, 0x00);
                    can.drawBitmap(bitmap, 0, 0, null);
                    mAddedPadding = 1;
                } else {
                    mAddedPadding = 0;
                }

                mGLTextureId = OpenGlUtils.loadTexture(
                        resizedBitmap != null ? resizedBitmap : bitmap, mGLTextureId, recycle);
                if (resizedBitmap != null) {
                    resizedBitmap.recycle();
                }
                mImageWidth = bitmap.getWidth();
                mImageHeight = bitmap.getHeight();
                adjustImageScaling();
            }
        });
    }

    public void setScaleType(GPUImage.ScaleType scaleType) {
        mScaleType = scaleType;
    }

    protected int getFrameWidth() {
        return mOutputWidth;
    }

    protected int getFrameHeight() {
        return mOutputHeight;
    }

    private void adjustImageScaling() {
        float outputWidth = mOutputWidth;
        float outputHeight = mOutputHeight;
        Log.e("adjustImageScaling","outputHeight"+outputHeight);
        Log.e("adjustImageScaling","outputWidth"+outputHeight);
        Log.e("adjustImageScaling","mRotation"+mRotation);
        if (mRotation == Rotation.ROTATION_270 || mRotation == Rotation.ROTATION_90) {
            outputWidth = mOutputHeight;
            outputHeight = mOutputWidth;
        }

        float ratio1 = outputWidth / mImageWidth;
        float ratio2 = outputHeight / mImageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        float ratioWidth = imageWidthNew / outputWidth;
        float ratioHeight = imageHeightNew / outputHeight;

        float[] cube = CUBE;
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, mFlipHorizontal, mFlipVertical);
        if (mScaleType == GPUImage.ScaleType.CENTER_CROP) {
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical),
                    addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical),
                    addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical),
                    addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical),
            };
        } else {
            Log.e("adjustImageScaling","ratioHeight"+ratioHeight);
            Log.e("adjustImageScaling","ratioWidth"+ratioWidth);
            cube = new float[]{
                    CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
                    CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
                    CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
                    CUBE[6] / ratioHeight, CUBE[7] / ratioWidth,
            };
        }

        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public void setRotationCamera(final Rotation rotation, final boolean flipHorizontal,
                                  final boolean flipVertical) {
        setRotation(rotation, flipVertical, flipHorizontal);
    }

    public void setRotation(final Rotation rotation,
                            final boolean flipHorizontal, final boolean flipVertical) {
        mFlipHorizontal = flipHorizontal;
        mFlipVertical = flipVertical;
        setRotation(rotation);
    }

    public Rotation getRotation() {
        return mRotation;
    }

    public void setRotation(final Rotation rotation) {
        mRotation = rotation;
        adjustImageScaling();
    }

    public boolean isFlippedHorizontally() {
        return mFlipHorizontal;
    }

    public boolean isFlippedVertically() {
        return mFlipVertical;
    }

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }

    public void runOnDraw1(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }


    protected void runOnDrawEnd(final Runnable runnable) {
        synchronized (mRunOnDrawEnd) {
            mRunOnDrawEnd.add(runnable);
        }
    }
}
