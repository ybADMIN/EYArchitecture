package mvp.data.store.glide.transform; //-----------------------------图片模糊----------------------------------

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
     *图片模糊
     */
    public class BlurTransformation extends BitmapTransformation {
        private static final String ID = "mvp.data.cache.glide.GlideUtils$GlideAppCircleTransform";
    private final int mRadius;
    private RenderScript rs;

        public BlurTransformation(Context context,int radius) {
            rs = RenderScript.create( context );
            this.mRadius = radius;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int USAGE_SHARED = 0x0080;
            Bitmap blurredBitmap = toTransform.copy( Bitmap.Config.ARGB_8888, true);
            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(
                rs,
                blurredBitmap,
                Allocation.MipmapControl.MIPMAP_FULL,
                USAGE_SHARED
            );
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(mRadius);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);
//            toTransform.recycle();
            return blurredBitmap;
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update(ID.getBytes(CHARSET));
        }
    }
  