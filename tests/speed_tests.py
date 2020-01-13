import fractal
from time import process_time
from view.FractalWindow import FractalRenderer
from PyQt5.QtGui import QPainter, QColor, QPixmap, QImage
from PyQt5.QtWidgets import QApplication
import sys
import PIL
from PIL import Image


def verify(width, height, value):
    return True


def test_c_by_size(layer, width, height, num=10):
    frac = fractal.Fractal()
    frac.width = width
    frac.height = height

    frac.insert_layer(0, layer)

    times = []
    result = None

    for i in range(num):
        start = process_time()
        result = frac.render()
        end = process_time()
        times.append(end - start)

    average = sum(times) / len(times)
    print("Average time " + str(width) + "x" + str(height) + " for " + str(num) + " trials: " + str(average))
    print("Correct: " + str(verify(width, height, result)))


class TimedFractalRenderer(FractalRenderer):
    def __init__(self, frac):
        super().__init__(frac)
        self.result = self.fract.render()
        self.elapsed = 0

    def paintEvent(self, event):
        start = process_time()
        qp = QPainter()
        qp.begin(self)

        image = QImage(bytes(self.result), self.fract.width, self.fract.height, self.fract.width * 3, QImage.Format_RGB888)
        pix = QPixmap(image)

        qp.drawPixmap(self.rect(), pix)
        qp.end()
        end = process_time()
        self.elapsed = end - start


def main():
    print("Testing SIMPLE BAND LAYER")

    print("C calculations: ")
    test_c_by_size(fractal.SimpleBandsLayer(), 800, 650, 100)
    print("-----------------------------")
    test_c_by_size(fractal.SimpleBandsLayer(), 1000, 1000, 50)
    print("-----------------------------")
    test_c_by_size(fractal.SimpleBandsLayer(), 5000, 5000, 15)
    print("-----------------------------")
    test_c_by_size(fractal.SimpleBandsLayer(), 10000, 10000, 5)

    print()

    print("Testing SMOOTH BAND LAYER")

    print("C calculations: ")
    test_c_by_size(fractal.SmoothBandsLayer(), 800, 650, 100)
    print("-----------------------------")
    test_c_by_size(fractal.SmoothBandsLayer(), 1000, 1000, 50)
    print("-----------------------------")
    test_c_by_size(fractal.SmoothBandsLayer(), 5000, 5000, 15)
    print("-----------------------------")
    test_c_by_size(fractal.SmoothBandsLayer(), 10000, 10000, 5)

    print()

    print("Testing HISTOGRAM LAYER")

    print("C calculations: ")
    test_c_by_size(fractal.HistogramLayer(), 800, 650, 100)
    print("-----------------------------")
    test_c_by_size(fractal.HistogramLayer(), 1000, 1000, 50)
    print("-----------------------------")
    test_c_by_size(fractal.HistogramLayer(), 5000, 5000, 15)
    print("-----------------------------")
    test_c_by_size(fractal.HistogramLayer(), 10000, 10000, 5)

    print()

    print("Python Render Time")
    app = QApplication(sys.argv)
    frac = fractal.Fractal()
    frac.insert_layer(0, fractal.SimpleBandsLayer())
    fractRenderer = TimedFractalRenderer(frac)

    times = []

    for i in range(300):
        fractRenderer.paintEvent(None)
        times.append(fractRenderer.elapsed)
    average = sum(times) / len(times)
    print("Average time rendered in PyQt5 over 300 trials: " + str(average))


if __name__ == '__main__':
    main()