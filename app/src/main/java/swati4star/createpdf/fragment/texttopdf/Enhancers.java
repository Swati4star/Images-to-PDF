package swati4star.createpdf.fragment.texttopdf;

import android.app.Activity;

import swati4star.createpdf.interfaces.Enhancer;
import swati4star.createpdf.model.TextToPDFOptions;

/**
 * The {@link Enhancers} represent a list of enhancers for the Text-to-PDF feature.
 */
public enum Enhancers {
    FONT_COLOR {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view,
                             TextToPDFOptions.Builder builder) {
            return new FontColorEnhancer(activity, builder);
        }
    },
    FONT_FAMILY {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view,
                             TextToPDFOptions.Builder builder) {
            return new FontFamilyEnhancer(activity, view, builder);
        }
    },
    FONT_SIZE {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view,
                             TextToPDFOptions.Builder builder) {
            return new FontSizeEnhancer(activity, view, builder);
        }
    },
    PAGE_COLOR {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view,
                             TextToPDFOptions.Builder builder) {
            return new PageColorEnhancer(activity, builder);
        }
    },
    PAGE_SIZE {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view,
                             TextToPDFOptions.Builder builder) {
            return new PageSizeEnhancer(activity);
        }
    },
    PASSWORD {
        @Override
        Enhancer getEnhancer(Activity activity, TextToPdfContract.View view,
                             TextToPDFOptions.Builder builder) {
            return new PasswordEnhancer(activity, view, builder);
        }
    };

    /**
     * @param activity The {@link Activity} context.
     * @param view The {@link TextToPdfContract.View} that needs the enhancement.
     * @param builder The builder for {@link TextToPDFOptions}.
     * @return An instance of the {@link Enhancer}.
     */
    abstract Enhancer getEnhancer(Activity activity, TextToPdfContract.View view,
                                  TextToPDFOptions.Builder builder);
}
