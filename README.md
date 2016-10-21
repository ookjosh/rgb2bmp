# rgb2bmp
Convert Raspberry Pi Camera's RGB output to a bitmap file.

Unable to find a convenient source of information on how to process the Raspberry Pi Camera's RGB output
through the program raspiyuv, this program was born! It will take the resulting file from ``raspiyuv -rgb``
and convert it into a ``.bmp`` bitmap file.

##Usage

Download the jar release (preferably) or compile this to a .class file.
Cd to the directory of the jar/class

As Jar:
Then run ``java -jar rgb2bmp PATH/TO/IMAGE/`` and let it go! Use the complete path to the **folder**, not a
specific image. This will create an ``output`` directory in that path and save the images there.

As class:
Then run ``java rgb2bmp PATH/TO/IMAGE``. Otherwise the same as above.
