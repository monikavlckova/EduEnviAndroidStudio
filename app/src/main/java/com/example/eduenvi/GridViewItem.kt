package com.example.eduenvi

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class GridViewItem : RelativeLayout{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
  @Override
  override fun onMeasure(widthMeasureSpec:Int, heightMeasureSpec:Int) {
      super.onMeasure(widthMeasureSpec, widthMeasureSpec)
  }
}