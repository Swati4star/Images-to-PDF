package swati4star.createpdf.fragment.texttopdf;

import android.app.Activity;

import swati4star.createpdf.interfaces.Enhancer;

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

    abstract Enhancer getEnhancer(Activity activity, TextToPdfContract.View view);
}
