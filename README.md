# Mandelbrot Fractal

![A black and white image of the Mandelbrot Fractal](https://github.com/CalvinKrist/FractalRenderer/raw/master/legacy-java/client/tutorialImages/Black%20and%20White%20Mandelbrot.png "A black and white image of the Mandelbrot Fractal")

 A Pyhton-based tool that allows one to explore and edit Mandelbrot Fractals. You can add layers, change the rendering types, and modify the palettes used. Create your perfect fractal!
 
 If you are looking for the legacy Java version that supported distributed rendering across a LAN network, please [see here](legacy-java).

## How it Works

The GUI is implemented using Python, and can be found in the [view](view) directory. The rendering code is done in using Python modules implemented in other languages so they can be faster. The [c module](modules/c) is implemented by [Calvin Krist](https://github.com/CalvinKrist) while the [rust module](modules/rust) is implemented by [David Smith](https://github.com/DavidSmith166).

Unit tests that verify the functionality of the modules can be found [here](tests/unit_tests.py), and the results of performance tests can be found [here](tests/speed_results/README.md).

At this moment, the project does not support distributed computing. However, as this is a re-implementation of the legacy Java version, that is an intended feature for the future.