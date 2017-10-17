package mvp.data.store.glide.transform;  //----------------------旋转---------------------------

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
     *旋转
     */
    public class RotateTransformation extends BitmapTransformation {
        private static final String ID = "mvp.data.cache.glide.GlideUtils$RotateTransformation";
        private float rotateRotationAngle = 0f;
        public RotateTransformation(float rotateRotationAngle) {
            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }


        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
                messageDigest.update(ID.getBytes(CHARSET));
        }
    }
    //--------------------------------------------------
   