from PyQt5.QtWidgets import QApplication, QWidget, QGridLayout, QLabel
from PyQt5.QtGui import QPainter, QColor
import sys
from gradient import *

class FractalRenderer(QWidget):

    def __init__(self):
        super().__init__()

        self.initUI()

    def initUI(self):
        self.setMinimumSize(800, 600)

    def paintEvent(self, event):
        qp = QPainter()
        qp.begin(self)
        print("draw event")
        qp.fillRect(0, 0, 800, 600, QColor(0, 0, 0))
        qp.end()


class Window(QWidget):

    def __init__(self):
        super().__init__()

        self.initUI()

    def initUI(self):

        grid = QGridLayout()
        self.setLayout(grid)

        fractal = FractalRenderer()
        grid.addWidget(fractal, 0, 0, 1, 1)
        options = QLabel("hey there")
        grid.addWidget(options, 0, 1, 1, 1)
        gradient = Gradient()
        grid.addWidget(gradient, 1, 0, 1, 2)

        self.setLayout(grid)
        self.setWindowTitle('FractalFun')
        self.show()

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = Window()
    sys.exit(app.exec_())