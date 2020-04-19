package swati4star.createpdf.fragment.texttopdf;

/**
 * The {@link TextToPdfContract} is a contract used by the fragment to communicate with its
 * enhancements.
 */
public interface TextToPdfContract {
    /**
     * Represents the view (the fragment in this case).
     */
    interface View {
        /**
         * Update the view when enhancement is changed.
         */
        void updateView();
    }
}
