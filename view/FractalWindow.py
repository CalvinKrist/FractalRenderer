from PyQt5.QtWidgets import QWidget
from PyQt5.QtGui import QPainter, QColor, QImage, QPixmap


class FractalRenderer(QWidget):

    def __init__(self, fract):
        super().__init__()

        self.fract = fract
        self.initUI()

    def initUI(self):
        self.setMinimumSize(800, 650)
        self.fract.width = 800
        self.fract.height = 650
        self.qp = QPainter()

    def paintEvent(self, event):
        self.fract.width = self.width()
        self.fract.height = self.height()
        image = self.fract.render()

        self.qp.begin(self)
        image = QImage(bytes(image), self.fract.width, self.fract.height, self.fract.width * 3, QImage.Format_RGB888)
        pix = QPixmap(image)

        self.qp.drawPixmap(self.rect(), pix)
        self.qp.end()
