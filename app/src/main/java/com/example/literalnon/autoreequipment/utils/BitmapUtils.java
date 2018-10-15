package com.example.literalnon.autoreequipment.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public final class BitmapUtils {
	private static final String TAG = "BitmapUtils";

	private static final int WATERMARK_MARGIN = 50;
	private static final int WATERMARK_ALPHA = 50;
	//private static final int WATERMARK_WIDTH = 118;
	// private static final int WATERMARK_HEIGHT = 90;


	private BitmapUtils() {
	}

	public static Bitmap getBitmapFromFile(File file) {
		InputStream istr = null;
		try {
			istr = new FileInputStream(file);
			return BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			return null;
		} finally {
			if (istr != null) {
				try {
					istr.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		int width = drawable.getIntrinsicWidth();
		width = width > 0 ? width : 1;
		int height = drawable.getIntrinsicHeight();
		height = height > 0 ? height : 1;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
		float ratio = Math.min(
				maxImageSize / realImage.getWidth(),
				maxImageSize / realImage.getHeight()
		);
		if (ratio >= 1.0) {
			return realImage;
		}
		final int width = Math.round(ratio * realImage.getWidth());
		final int height = Math.round(ratio * realImage.getHeight());

		return Bitmap.createScaledBitmap(realImage, width, height, filter);
	}

	public static boolean isCorruptedImageFile(File file) {
		if (file == null) {
			return true;
		}
		String filePath = file.getAbsolutePath();
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 1, 1);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options) == null;
	}

	@Nullable
	public static File compressImage(Context context, File file, int imageMaxSize,
	                                 String newFileName, @IntRange(from = 1, to = 100) int quality) {
		Bitmap scaled = decodeSampledBitmapFromFile(file.getAbsolutePath(), imageMaxSize, imageMaxSize);
		if (scaled == null) {
			return null;
		}
		//write new image to temp file
		final File output = new File(context.getExternalCacheDir(), newFileName);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(output);
			scaled.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
			return output;
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Error while compress image for file: " + file.getAbsolutePath());
			return file;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					|| (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		Log.w(TAG, "In sample size: " + String.valueOf(options.inSampleSize));

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	@Nullable
	public static File getOutputMediaFile(Context context, String fileName) {
		final File cacheDir = context.getExternalCacheDir();
		Log.d(TAG + "file", "fileName = " + fileName);
		return cacheDir == null ? null : new File(cacheDir, fileName);
	}

	public static File createFileFromBitmap(Context context, Bitmap bitmap) {
		return createFileFromBitmap(bitmap, getOutputMediaFile(context, "tempFile" + System.currentTimeMillis() + ".jpg"));
	}

	public static File createTempFileFromBitmap(Context context, Bitmap bitmap) {
		return createFileFromBitmap(bitmap, getOutputMediaFile(context, "tempFile.jpg"));
	}

	public static File createFileFromBitmap(Bitmap bitmap, File destination) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		if (bitmap != null) {
			Log.d(TAG, "bitmap.isRecycled() : " + (bitmap.isRecycled()));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		}
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(destination);
			fo.write(bytes.toByteArray());
			return destination;
		} catch (IOException | NullPointerException e) {
			Log.e(TAG, "failed createFileFromBitmap", e);
		} finally {
			if (fo != null) {
				try {
					fo.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static File getCompressFileFromGallery(Context context, String path, int reqWidth, int reqHeight) {
		Bitmap bitmap = decodeSampledBitmapFromFile(path, reqWidth, reqHeight);
		return createFileFromBitmap(context, bitmap);
	}

	private static int getOrientation(String path) {
		try {
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			Log.d(TAG, "orientationToSend = " + orientation);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					return 270;
				case ExifInterface.ORIENTATION_ROTATE_180:
					return 180;
				case ExifInterface.ORIENTATION_ROTATE_90:
					return 90;
				case ExifInterface.ORIENTATION_NORMAL:
					return 0;
				default:
					return -1;
			}
		} catch (IOException e) {
			return -1;
		}
	}

	/**
	 * @param file is compressed file
	 * @param path is path of not compressed file
	 **/

	public static File rotateToCorrectOrientation(Context context, File file, String path) {
		//String path = file.getAbsolutePath();
		int orientation = getOrientation(path);
		Log.d(TAG, "rotate to = " + orientation);
		if (orientation > 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(orientation);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap newBitmap = null;
			try {
				newBitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			// Bitmap newBitmap = BitmapFactory.decodeFile(path, options);
			Bitmap rotatedBitmap = Bitmap.createBitmap(newBitmap, 0, 0, newBitmap.getWidth(),
					newBitmap.getHeight(), matrix, true);
			return createFileFromBitmap(context, rotatedBitmap);
		} else {
			return file;
		}
	}
}
