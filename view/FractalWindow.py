from PyQt5.QtWidgets import QWidget
from PyQt5.QtGui import QPainter, QColor
import fractal

class FractalRenderer(QWidget):

    def __init__(self):
        super().__init__()

        self.initUI()

    def initUI(self):
        self.setMinimumSize(1000, 800)

    def paintEvent(self, event):
        qp = QPainter()
        qp.begin(self)
        print("draw event")
        qp.fillRect(0, 0, self.width(), self.height(), QColor(0, 0, 0))
        qp.end()