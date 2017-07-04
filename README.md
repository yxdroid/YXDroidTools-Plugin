# YXDroidTools-Plugin

Android Studio plugin,to improve efficiency
 
## How to install
1. File->Setting->Plugin->Browse repositories, search "YXDroid Tools" in Android Studio
2. download the YXDroidTools-Plugiun-1.0.0.zip， File->Setting->Plugin->Install plugin from disk

## Generate Selector
generate selector by selected drawable resources order
~~~
<?xml version="1.0" encoding="utf-8" ?>
   <selector xmlns:android="http://schemas.android.com/apk/res/android">
   <item android:state_selected="false" android:drawable="${item1}"/>
   <item android:state_selected="true" android:drawable="${item2}"/>
   <item android:state_focused="false" android:drawable="${item2}"/>
   <item android:state_focused="true" android:drawable="${item2}"/>
</selector>
~~~
![](https://github.com/yxdroid/YXDroidTools-Plugin/blob/master/selector.gif?raw=true)

## Convert Style
convert style by selected text content

![](https://github.com/yxdroid/YXDroidTools-Plugin/blob/master/style.gif?raw=true)

## Generate JNI
generate jni file by selected native java class

![](https://github.com/yxdroid/YXDroidTools-Plugin/blob/master/jni.gif?raw=true)

