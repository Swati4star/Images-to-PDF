package swati4star.createpdf.fragment.texttopdf;

import android.app.Activity;

import swati4star.createpdf.interfaces.Enhancer;

/**
 * The {@link Enhancers} represent a list of enhancers for the Text-to-PDF feature.
 */
public enum Enhancers {
    FONT_COLOR {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view) {
            return new PageColorEnhancer(activity);
        }
    },
    FONT_FAMILY {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view) {
            return new FontFamilyEnhancer(activity, view);
        }
    },
    FONT_SIZE {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view) {
            return new FontSizeEnhancer(activity, view);
        }
    },
    PAGE_COLOR {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view) {
            return new PageColorEnhancer(activity);
        }
    },
    PAGE_SIZE {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view) {
            return new PageSizeEnhancer(activity);
        }
    },
    PASSWORD {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view) {
            return new PasswordEnhancer(activity, view);
        }
    };

    /**
     * @param activity The {@link Activity} context.
     * @param view The {@link TextToPdfContract.View} that needs the enhancement.
     * @return An instance of the {@link Enhancer}.
     */
    abstract Enhancer getEnhancer(Activity activity, TextToPdfContract.View view);
}
