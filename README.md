# RotationLock
This app is kind of a <b>lockscreen</b>: It prevents accidental screen touches when the screen is on. It uses the devices rotation sensor to automatically lock or unlock the device: In portrait mode, you can use your device as usual but when tilted for more then 75° (in both directions), it will overlay the current screen with a touch-consuming fullscreen layer.

The intention of this app were augmented reality games: Some of these apps need to run in the foreground and keep the display on while the user keeps moving. However, putting your device in your pocket with the display kept on might trigger all kind of accidental screen touches...

<b>Note:</b> The overlay might not cover the status bar or navigation buttons (but it tries to). Therefore it might still be possible to accidentally press the home button or interact with the notifications.

A notification is shown to quickly enable or disable the app
