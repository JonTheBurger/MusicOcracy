adb -d shell "run-as com.musicocracy.fpgk.musicocracy cp /data/data/com.musicocracy.fpgk.musicocracy/databases/MusicOcracy.sqlite /sdcard/musicOcracy.sqlite"
adb pull /sdcard/musicOcracy.sqlite
