package com.core.gsbaseandroid.ui.view.toasty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.core.gsbaseandroid.R;

@SuppressLint("InflateParams")
public class Toasty {
    private static final Typeface LOADED_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
    private static Typeface currentTypeface = LOADED_TOAST_TYPEFACE;
    private static boolean tintIcon = true;
    private static boolean allowQueue = true;

    private static Toast lastToast = null;
    public static final int NORMAL = 1;
    public static final int WARNING = 2;
    public static final int INFO = 3;
    public static final int SUCCESS = 4;
    public static final int ERROR = 5;
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    private static Toast sToast;

    private Toasty() {
        // avoiding instantiation
    }

    public static void showToast(@NonNull Context context, @StringRes int message, int type) {
        try {
            if (sToast != null) {
                sToast.cancel();
            }
            switch (type) {
                case WARNING:
                    sToast = warning(context, message);
                    break;
                case INFO:
                    sToast = info(context, message);
                    break;
                case SUCCESS:
                    sToast = success(context, message);
                    break;
                case ERROR:
                    sToast = error(context, message);
                    break;
                case NORMAL:
                default:
                    sToast = normal(context, message);
                    break;
            }
            sToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideToast() {
        try {
            if (sToast != null) {
                sToast.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(@NonNull Context context, @NonNull CharSequence message, int type) {
        try {
            if (sToast != null) {
                sToast.cancel();
            }
            switch (type) {
                case WARNING:
                    sToast = warning(context, message);
                    break;
                case INFO:
                    sToast = info(context, message);
                    break;
                case SUCCESS:
                    sToast = success(context, message);
                    break;
                case ERROR:
                    sToast = error(context, message);
                    break;
                case NORMAL:
                default:
                    sToast = normal(context, message);
                    break;
            }
            sToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message) {
        return normal(context, context.getString(message), LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message) {
        return normal(context, message, LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message, Drawable icon) {
        return normal(context, context.getString(message), LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, Drawable icon) {
        return normal(context, message, LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message, int duration) {
        return normal(context, context.getString(message), duration, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return normal(context, message, duration, null, false);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message, int duration,
                               Drawable icon) {
        return normal(context, context.getString(message), duration, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                               Drawable icon) {
        return normal(context, message, duration, icon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @StringRes int message, int duration,
                               Drawable icon, boolean withIcon) {
        return custom(context, context.getString(message), icon, ToastyUtils.getColor(context, R.color.toast_normalColor),
                ToastyUtils.getColor(context, R.color.toast_defaultTextColor), duration, withIcon, true);
    }

    @CheckResult
    public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                               Drawable icon, boolean withIcon) {
        return custom(context, message, icon, ToastyUtils.getColor(context, R.color.toast_normalColor),
                ToastyUtils.getColor(context, R.color.toast_defaultTextColor), duration, withIcon, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @StringRes int message) {
        return warning(context, context.getString(message), LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @NonNull CharSequence message) {
        return warning(context, message, LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @StringRes int message, int duration) {
        return warning(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return warning(context, message, duration, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), ToastyUtils.getDrawable(context, R.drawable.toast_ic_error_outline_white),
                ToastyUtils.getColor(context, R.color.toast_warningColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast warning(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, ToastyUtils.getDrawable(context, R.drawable.toast_ic_error_outline_white),
                ToastyUtils.getColor(context, R.color.toast_warningColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @StringRes int message) {
        return info(context, context.getString(message), LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull CharSequence message) {
        return info(context, message, LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @StringRes int message, int duration) {
        return info(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return info(context, message, duration, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), ToastyUtils.getDrawable(context, R.drawable.toast_ic_info_outline_white),
                ToastyUtils.getColor(context, R.color.toast_infoColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast info(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, ToastyUtils.getDrawable(context, R.drawable.toast_ic_info_outline_white),
                ToastyUtils.getColor(context, R.color.toast_infoColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @StringRes int message) {
        return success(context, context.getString(message), LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message) {
        return success(context, message, LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @StringRes int message, int duration) {
        return success(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return success(context, message, duration, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), ToastyUtils.getDrawable(context, R.drawable.toast_ic_check_white),
                ToastyUtils.getColor(context, R.color.toast_successColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast success(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, ToastyUtils.getDrawable(context, R.drawable.toast_ic_check_white),
                ToastyUtils.getColor(context, R.color.toast_successColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @StringRes int message) {
        return error(context, context.getString(message), LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message) {
        return error(context, message, LENGTH_SHORT, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @StringRes int message, int duration) {
        return error(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return error(context, message, duration, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), ToastyUtils.getDrawable(context, R.drawable.toast_ic_clear_white),
                ToastyUtils.getColor(context, R.color.toast_errorColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast error(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, ToastyUtils.getDrawable(context, R.drawable.toast_ic_clear_white),
                ToastyUtils.getColor(context, R.color.toast_errorColor), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, true);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @StringRes int message, Drawable icon,
                               int duration, boolean withIcon) {
        return custom(context, context.getString(message), icon, -1, ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, false);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                               int duration, boolean withIcon) {
        return custom(context, message, icon, -1, ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, false);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @StringRes int message, @DrawableRes int iconRes,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, context.getString(message), ToastyUtils.getDrawable(context, iconRes),
                ToastyUtils.getColor(context, tintColorRes), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, @DrawableRes int iconRes,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, message, ToastyUtils.getDrawable(context, iconRes),
                ToastyUtils.getColor(context, tintColorRes), ToastyUtils.getColor(context, R.color.toast_defaultTextColor),
                duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @StringRes int message, Drawable icon,
                               @ColorRes int tintColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, context.getString(message), icon, ToastyUtils.getColor(context, tintColorRes),
                ToastyUtils.getColor(context, R.color.toast_defaultTextColor), duration, withIcon, shouldTint);
    }

    @CheckResult
    public static Toast custom(@NonNull Context context, @StringRes int message, Drawable icon,
                               @ColorRes int tintColorRes, @ColorRes int textColorRes, int duration,
                               boolean withIcon, boolean shouldTint) {
        return custom(context, context.getString(message), icon, ToastyUtils.getColor(context, tintColorRes),
                ToastyUtils.getColor(context, textColorRes), duration, withIcon, shouldTint);
    }

    @SuppressLint("ShowToast")
    @CheckResult
    public static Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                               @ColorInt int tintColor, @ColorInt int textColor, int duration,
                               boolean withIcon, boolean shouldTint) {
        final Toast currentToast = Toast.makeText(context, "", duration);
        currentToast.setGravity(Gravity.CENTER, 0, -100);
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.toast_layout, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint)
            drawableFrame = ToastyUtils.tint9PatchDrawableFrame(context, tintColor);
        else
            drawableFrame = ToastyUtils.getDrawable(context, R.drawable.toast_frame);
        ToastyUtils.setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null)
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            ToastyUtils.setBackground(toastIcon, tintIcon ? ToastyUtils.tintIcon(icon, textColor) : icon);
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setText(message);
        toastTextView.setTextColor(textColor);
        toastTextView.setTypeface(currentTypeface);

        currentToast.setView(toastLayout);

        if (!allowQueue) {
            if (lastToast != null)
                lastToast.cancel();
            lastToast = currentToast;
        }

        return currentToast;
    }

    public static class Config {
        private Typeface typeface = Toasty.currentTypeface;

        private boolean tintIcon = Toasty.tintIcon;
        private boolean allowQueue = true;

        private Config() {
            // avoiding instantiation
        }

        @CheckResult
        public static Config getInstance() {
            return new Config();
        }

        public static void reset() {
            Toasty.currentTypeface = LOADED_TOAST_TYPEFACE;
            Toasty.tintIcon = true;
            Toasty.allowQueue = true;
        }

        @CheckResult
        public Config setToastTypeface(@NonNull Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        @CheckResult
        public Config setTextSize(int sizeInSp) {
            return this;
        }

        @CheckResult
        public Config tintIcon(boolean tintIcon) {
            this.tintIcon = tintIcon;
            return this;
        }

        @CheckResult
        public Config allowQueue(boolean allowQueue) {
            this.allowQueue = allowQueue;
            return this;
        }

        public void apply() {
            Toasty.currentTypeface = typeface;
            Toasty.tintIcon = tintIcon;
            Toasty.allowQueue = allowQueue;
        }
    }
}
