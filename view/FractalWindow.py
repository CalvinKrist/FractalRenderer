from PyQt5.QtWidgets import QWidget
from PyQt5.QtGui import QPainter, QColor


class FractalRenderer(QWidget):

    def __init__(self, fract):
        super().__init__()

        self.fract = fract
        self.initUI()

    def initUI(self):
        self.setMinimumSize(800, 650)
        self.fract.width = 800
        self.fract.height = 650

    def paintEvent(self, event):

        image = self.fract.render()
        cols = self.fract.width
        rows = self.fract.height

        qp = QPainter()
        qp.begin(self)
        for y in range(rows):
            for x in range(cols):
                color = image[y * cols + x]
                qp.fillRect(x, y, 1, 1, QColor(color[0], color[1], color[2]))
        qp.end()