package ir.app.ensan.component.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import ir.app.ensan.EnsanApp;
import ir.app.ensan.R;

/**
 * Created by Khashayar on 21/09/2017.
 */

public class CustomTextView extends TextView {

  public CustomTextView(Context context) {
    super(context);
  }

  public CustomTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context ,attrs);
  }

  public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context ,attrs);
  }

  private void init(Context context, AttributeSet attrs) {

    TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
    FontType fontType = FontType.values()[attributes.getInt(R.styleable.CustomTextView_font_type, 0)];

    setTypeface(EnsanApp.getTypeFace(fontType));
    setIncludeFontPadding(false);
  }
}
