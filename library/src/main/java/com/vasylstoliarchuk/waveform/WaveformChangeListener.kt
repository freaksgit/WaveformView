package com.vasylstoliarchuk.waveform

interface WaveformChangeListener {

    /**
     * Notification that the progress level has changed. Clients can use the fromUser parameter
     * to distinguish user-initiated changes from those that occurred programmatically.
     *
     * @param waveformView The WaveformView whose progress has changed
     * @param progress The current progress level.
     * @param fromUser True if the progress change was initiated by the user.
     */
    fun onProgressChanged(waveformView: WaveformView, progress: Float, fromUser: Boolean){}

    /**
     * Notification that the user has started a touch gesture.
     * @param waveformView The WaveformView in which the touch gesture began
     */
    fun onStartTrackingTouch(waveformView: WaveformView){}

    /**
     * Notification that the user has finished a touch gesture.
     * @param waveformView The WaveformView in which the touch gesture began
     */
    fun onStopTrackingTouch(waveformView: WaveformView){}
}