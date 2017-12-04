package com.duke.dkskin.common;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duke.dkskin.R;

/**
 * Author: duke
 * DateTime: 2017-10-24 09:52
 * Description: 带图片的文本控件
 */
public class ImageTextView extends LinearLayout {

    /**
     * 图片相对于文本的方向
     */
    public enum Position {
        LEFT(1),
        TOP(2),
        RIGHT(3),
        BOTTOM(4);

        Position(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    private Bitmap imageBitmap;//优先于imageResId
    private int imageResId;//图片资源
    private int imageWidth;//px
    private int imageHeight;//px

    private float imageAngle;//旋转角度

    private int imagePadding;//图片与文本之间的间距 px
    private int imagePosition = Position.LEFT.getValue();//图片位置

    public static final int TEXT_SIZE = 16;//sp
    public static final int TEXT_COLOR = Color.BLACK;
    public static final int TEXT_BG_COLOR = Color.TRANSPARENT;

    private String imageText;//文本
    private int imageTextSize;//字体大小 pt
    private int imageTextColor = TEXT_COLOR;//字体颜色
    private int imageTextBgColor = TEXT_BG_COLOR;//文本控件背景颜色

    private boolean imageTextSingleLine;//文本是否单行
    private int imageTextMaxLine;//非单行时，文本最大行数


    /**
     * 上面提供了部分属性，如果还有详细的属性要设置，直接获取childView
     */
    private TextView textView;
    private ImageView imageView;

    public TextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    private int getDimenIdValue(int dimenId) {
        int val = -1;
        try {
            val = getResources().getDimensionPixelSize(dimenId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return val;
    }

    public float getImageAngle() {
        return imageAngle;
    }

    public void setImageAngle(float imageAngle) {
        if (imageView == null || imageView.getDrawable() == null) {
            return;
        }
        this.imageAngle = imageAngle;
        int[] location = new int[2];
        //获取控件在父容器中的左上角位置
        getLocationInWindow(location);
        //获取控件在手机屏幕中的左上角位置
        //getLocationOnScreen(location);

        if (imageWidth > 0) {
            imageView.setPivotX(location[0] + imageWidth / 2);
        }
        if (imageHeight > 0) {
            imageView.setPivotY(location[1] + imageHeight / 2);
        }
        if (imageAngle != 0) {
            imageView.setRotation(imageAngle);
        }
    }

    public int getImagePadding() {
        return imagePadding;
    }

    public void setImagePaddingDimenId(int dimenId) {
        setImagePadding(getDimenIdValue(dimenId));
    }

    /**
     * 设置图片与文本的间距，px
     *
     * @param imagePadding
     */
    public void setImagePadding(int imagePadding) {
        if (getChildCount() != 2 || imagePadding <= 0) {
            return;
        }
        this.imagePadding = imagePadding;
        //设置子孩子之间的间距
        View child1 = getChildAt(0);
        MarginLayoutParams layoutParams = (MarginLayoutParams) child1.getLayoutParams();
        if (layoutParams == null) {
            return;
        }
        if (imagePosition == Position.LEFT.getValue()
                || imagePosition == Position.RIGHT.getValue()) {
            layoutParams.rightMargin = imagePadding;
        } else {
            layoutParams.bottomMargin = imagePadding;
        }
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        if (imageView == null || imageBitmap == null) {
            return;
        }
        this.imageBitmap = imageBitmap;
        imageView.setImageBitmap(imageBitmap);
        this.imageWidth = imageBitmap.getWidth();
        this.imageHeight = imageBitmap.getHeight();
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        if (imageView == null) {
            return;
        }
        this.imageResId = imageResId;
        imageView.setImageResource(imageResId);
        if (imageView.getDrawable() == null) {
            return;
        }
        this.imageWidth = imageView.getDrawable().getMinimumWidth();
        this.imageHeight = imageView.getDrawable().getMinimumHeight();
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidthDimenId(int dimenId) {
        setImageWidth(getDimenIdValue(dimenId));
    }

    public void setImageWidth(int imageWidth) {
        setImageSize(imageWidth, 0);
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeightDimenId(int dimenId) {
        setImageHeight(getDimenIdValue(dimenId));
    }

    public void setImageHeight(int imageHeight) {
        setImageSize(0, imageHeight);
    }

    public void setImageSizeDimenId(int dimenIdWidth, int dimenIdHeight) {
        setImageSize(getDimenIdValue(dimenIdWidth), getDimenIdValue(dimenIdHeight));
    }

    public void setImageSize(int imageWidth, int imageHeight) {
        if (imageView == null || imageView.getLayoutParams() == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        boolean isNeedUpdate = false;
        if (imageWidth > 0) {
            this.imageWidth = imageWidth;
            layoutParams.width = imageWidth;
            isNeedUpdate = true;
        }
        if (imageHeight > 0) {
            this.imageHeight = imageHeight;
            layoutParams.height = imageHeight;
            isNeedUpdate = true;
        }
        if (isNeedUpdate) {
            imageView.setLayoutParams(layoutParams);
        }
        setImageAngle(imageAngle);
    }

    public int getImagePosition() {
        return imagePosition;
    }

    public void setImagePosition(Position position) {
        if (position == null) {
            position = Position.LEFT;
        }
        this.imagePosition = position.getValue();
        initViewsWithOrientation();
    }

    public String getImageText() {
        return imageText;
    }

    public void setImageText(String imageText) {
        if (textView == null) {
            return;
        }
        this.imageText = imageText;
        textView.setText(imageText);
    }

    public int getImageTextSize() {
        return imageTextSize;
    }

    public void setImageTextSizeDimenId(int dimenId) {
        setImageTextSize(getDimenIdValue(dimenId));
    }

    public void setImageTextSize(int imageTextSizePT) {
        if (textView == null || imageTextSizePT <= 0) {
            return;
        }
        this.imageTextSize = imageTextSizePT;
        //注意设置字体大小，是设置画笔的字体大小
        textView.getPaint().setTextSize(imageTextSize);
    }

    public int getImageTextColor() {
        return imageTextColor;
    }

    public void setImageTextColor(int imageTextColor) {
        if (textView == null) {
            return;
        }
        this.imageTextColor = imageTextColor;
        textView.setTextColor(imageTextColor);
    }

    public int getImageTextBgColor() {
        return imageTextBgColor;
    }

    public void setImageTextBgColor(int imageTextBgColor) {
        if (textView == null) {
            return;
        }
        this.imageTextBgColor = imageTextBgColor;
        textView.setBackgroundColor(imageTextBgColor);
    }

    public boolean isImageTextSingleLine() {
        return imageTextSingleLine;
    }

    public void setImageTextSingleLine(boolean imageTextSingleLine) {
        if (textView == null) {
            return;
        }
        this.imageTextSingleLine = imageTextSingleLine;
        if (imageTextSingleLine) {
            setMaxLinesAndEllipsize(1, TextUtils.TruncateAt.END);
        } else {
            setMaxLinesAndEllipsize(Integer.MAX_VALUE, null);
        }
    }

    public int getImageTextMaxLine() {
        return imageTextMaxLine;
    }

    public void setImageTextMaxLine(int imageTextMaxLine) {
        if (textView == null || imageTextMaxLine <= 0 || imageTextSingleLine) {//如果是单行，则不设置最大行数
            return;
        }
        this.imageTextMaxLine = imageTextMaxLine;
        setMaxLinesAndEllipsize(imageTextMaxLine, TextUtils.TruncateAt.END);
    }

    private void setMaxLinesAndEllipsize(int maxLines, TextUtils.TruncateAt truncateAt) {
        textView.setMaxLines(maxLines);
        textView.setEllipsize(truncateAt);
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public ImageTextView(Context context) {
        this(context, null, 0);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化属性
        this.imageTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE, getResources().getDisplayMetrics());

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView, defStyleAttr, 0);
        int size = array.getIndexCount();
        for (int i = 0; i < size; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.ImageTextView_imageResId:
                    //图片资源
                    imageResId = array.getResourceId(attr, 0);
                    break;
                case R.styleable.ImageTextView_imageWidth:
                    //图片宽度
                    imageWidth = array.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ImageTextView_imageHeight:
                    //图片高度
                    imageHeight = array.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ImageTextView_imageAngle:
                    //图片旋转角度
                    imageAngle = array.getFloat(attr, 0);
                    break;
                case R.styleable.ImageTextView_imagePadding:
                    //图片与文本之间的间距
                    imagePadding = array.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ImageTextView_imagePosition:
                    //图片位置
                    imagePosition = array.getInt(attr, Position.LEFT.getValue());
                    break;
                case R.styleable.ImageTextView_imageText:
                    //文本
                    imageText = array.getString(attr);
                    break;
                case R.styleable.ImageTextView_imageTextSize:
                    //字体大小
                    imageTextSize = array.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ImageTextView_imageTextColor:
                    //字体颜色
                    imageTextColor = array.getColor(attr, TEXT_COLOR);
                    break;
                case R.styleable.ImageTextView_imageTextBgColor:
                    //文本控件背景颜色
                    imageTextBgColor = array.getColor(attr, TEXT_BG_COLOR);
                    break;
                case R.styleable.ImageTextView_imageTextSingleLine:
                    //文本是否单行
                    imageTextSingleLine = array.getBoolean(attr, false);
                    break;
                case R.styleable.ImageTextView_imageTextMaxLine:
                    //非单行时，文本最大行数
                    imageTextMaxLine = array.getInt(attr, 0);
                    break;
            }
        }
        array.recycle();
        //初始化
        initViewsWithOrientation();
    }

    /**
     * 创建或更新TextView
     *
     * @return 是否需要添加到容器中
     */
    private boolean updateTextView() {
        boolean isNeedAdd = false;
        if (textView == null) {
            textView = new TextView(getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //初次创建，需要添加到容器中
            isNeedAdd = true;
        }
        setImageText(imageText);
        setImageTextSize(imageTextSize);
        setImageTextColor(imageTextColor);
        setImageTextBgColor(imageTextBgColor);
        //设置单行
        setImageTextSingleLine(imageTextSingleLine);
        //注意setImageTextMaxLine方法对是佛单行做了判断
        setImageTextMaxLine(imageTextMaxLine);
        return isNeedAdd;
    }

    /**
     * 创建或更新ImageView
     *
     * @return 是否需要添加到容器中
     */
    private boolean updateImageView() {
        boolean isNeedAdd = false;
        if (imageView == null) {
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //初次创建，需要添加到容器中
            isNeedAdd = true;
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(getResources(), imageResId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (imageBitmap != null) {
            bitmap = imageBitmap;
        }
        setImageBitmap(bitmap);
        setImageSize(this.imageWidth, this.imageHeight);
        return isNeedAdd;
    }

    private void initViewsWithOrientation() {
        //移除所有的views，避免重复添加或人为xml中添加
        removeAllViews();
        //更新子孩子
        updateTextView();
        updateImageView();
        //确定子孩子添加顺序
        if (imagePosition == Position.LEFT.getValue()) {
            setOrientation(LinearLayout.HORIZONTAL);
            addView(imageView);
            addView(textView);
        } else if (imagePosition == Position.TOP.getValue()) {
            setOrientation(LinearLayout.VERTICAL);
            addView(imageView);
            addView(textView);
        } else if (imagePosition == Position.RIGHT.getValue()) {
            setOrientation(LinearLayout.HORIZONTAL);
            addView(textView);
            addView(imageView);
        } else if (imagePosition == Position.BOTTOM.getValue()) {
            setOrientation(LinearLayout.VERTICAL);
            addView(textView);
            addView(imageView);
        }
        setImagePadding(imagePadding);
    }
}