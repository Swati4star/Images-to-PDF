package swati4star.createpdf.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import swati4star.createpdf.R;

public class ImageHandler {
    public ImageHandler() {
    }

    /**
     * Checks if images are received in the intent
     *
     * @param fragment - instance of current fragment
     */
    public void handleReceivedImagesIntent(Fragment fragment, Activity activity, Context context) {
        Intent intent = activity.getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (type == null || !type.startsWith("image/"))
            return;

        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            handleSendMultipleImages(intent, fragment, context); // Handle multiple images
        } else if (Intent.ACTION_SEND.equals(action)) {
            handleSendImage(intent, fragment, context); // Handle single image
        }
    }

    public boolean areImagesRecevied(Activity activity) {
        Intent intent = activity.getIntent();
        String type = intent.getType();
        return type != null && type.startsWith("image/");
    }

    /**
     * Get image uri from intent and send the image to homeFragment
     *
     * @param intent   - intent containing image uris
     * @param fragment - instance of homeFragment
     */
    public void handleSendImage(Intent intent, Fragment fragment, Context context) {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        imageUris.add(uri);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(context.getString(R.string.bundleKey), imageUris);
        fragment.setArguments(bundle);
    }

    /**
     * Get ArrayList of image uris from intent and send the image to homeFragment
     *
     * @param intent   - intent containing image uris
     * @param fragment - instance of homeFragment
     */
    public void handleSendMultipleImages(Intent intent, Fragment fragment, Context context) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(context.getString(R.string.bundleKey), imageUris);
            fragment.setArguments(bundle);
        }
    }
}