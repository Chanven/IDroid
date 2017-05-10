package com.andy.demo.ui;

public interface IDrawerViewGroup {
    public interface OnDrawerStateChangedListener {
        public void onLeftClosed();

        public void onLeftOpen();

        public void onRightClosed();

        public void onRightOpen();
    }

    public void openLeftContent(OnDrawerStateChangedListener listener);

    public void closeLeftContent(OnDrawerStateChangedListener listener);

    public void openRightContent(OnDrawerStateChangedListener listener);

    public void closeRightContent(OnDrawerStateChangedListener listener);

    public boolean isLeftOpen();

    public boolean isRightOpen();
}
